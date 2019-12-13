package com.fa.dp.business.task.sop.week0;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.sop.week0.bo.DPSopProcessBO;
import com.fa.dp.business.sop.week0.delegate.DPSopProcessFilterDelegate;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.sop.week0.input.info.DPSopParamEntryInfo;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.week0.input.mapper.DPSopWeek0ParamMapper;
import com.fa.dp.business.task.sop.week0.filters.SopWeek0ModeledBenchmarkCriteria;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;

/**
 * @author misprakh
 */

@Slf4j
@Named
public abstract class AbstractSopWeek0ModeledBenchmarkCriteria implements SopWeek0ModeledBenchmarkCriteria, Command {

	@Inject
	private CacheManager cacheManager;

	@Inject
	private DPSopProcessFilterDelegate dpSopProcessFilterDelegate;

	@Inject
	private DPSopProcessBO dpSopProcessBO;

	@Inject
	private DPSopWeek0ParamMapper dpSopWeek0ParamMapper;

	/**
	 * This filter provide 80 and 20 criteria to process loans
	 *
	 * @param data
	 * @throws SystemException
	 */
	public void executeSopWeek0ModeledBenchmarkCriteria(Object data, String filterName) throws SystemException {

		log.info("SopWeek0ModeledBenchmarkCriteria -> processTask started.");
		Long startTime = DateTime.now().getMillis();
		DPSopParamEntryInfo sopParamEntryInfo = null;
		try {
			sopParamEntryInfo = ((DPSopParamEntryInfo) data);

			log.info("ModeledBenchmarkCriteriaImpl -> modeledBenchmarkRuleProcess started.");
			List<DPSopWeek0ParamInfo> sopParamEntryInfoColumnEntries = sopParamEntryInfo.getColumnEntries();
			int[][] avRange = getAVRange();
			List<DPSopWeek0ParamInfo> paramsProcessing = new ArrayList<DPSopWeek0ParamInfo>();

			for (int[] range : avRange) {
				List<DPSopWeek0ParamInfo> sopWeek0ParamInfosAV = filterByAVRange(sopParamEntryInfoColumnEntries, range[0], range[1]);
				sopWeek0ParamInfosAV = filterByModeledBenchMark(sopWeek0ParamInfosAV, range[0], range[1], true);
				paramsProcessing.addAll(sopWeek0ParamInfosAV);
			}

			List<DPSopWeek0ParamInfo> diff = sopParamEntryInfoColumnEntries.stream().filter(elem -> !paramsProcessing.contains(elem)).collect(Collectors.toList());

			sopParamEntryInfoColumnEntries.clear();
			sopParamEntryInfoColumnEntries.addAll(diff);
			sopParamEntryInfoColumnEntries.addAll(paramsProcessing);
			sopParamEntryInfo.setColumnEntries(sopParamEntryInfoColumnEntries);

			if (!CollectionUtils.isEmpty(sopParamEntryInfo.getColumnEntries())) {
				log.info("Adding Successfull entries into SOP Week 0 table after Modeled and Benchmark criteria");
				sopParamEntryInfo.getColumnEntries().forEach(entry -> {
					entry.setAssignmentDate(DateConversionUtil.getCurrentUTCTime().getMillis());
					entry.setEligible(DPProcessFilterParams.ELIGIBLE.getValue());
				});

			}
			dpSopProcessBO.saveFileEntriesInDB(sopParamEntryInfo);
			log.info("ModeledBenchmarkCriteriaImpl -> modeledBenchmarkRuleProcess ended.");
		} catch (Exception e) {
			log.error("Exception while executing SopWeek0ModeledBenchmarkCriteria filter : {}", e);
			sopParamEntryInfo.getDpSopWeek0ProcessStatusInfo().setStatus(DPFileProcessStatus.ERROR.getFileStatus());
		}
		log.info("Time taken for SopWeek0ModeledBenchmarkCriteria : {}ms", (DateTime.now().getMillis() - startTime));
		log.info("SopWeek0ModeledBenchmarkCriteria -> processTask ended.");
	}

	private List<DPSopWeek0ParamInfo> filterByModeledBenchMark(List<DPSopWeek0ParamInfo> sopWeek0ParamInfosAV, int lowerAssetValue, int higherAssetValue, boolean processEligible) throws SystemException {
		log.info("SOPWeek0ModeledBenchmarkCriteriaImpl -> filterByModeledBenchMark started.");
		List<DPSopWeek0ParamInfo> result = new ArrayList<DPSopWeek0ParamInfo>();

		if (sopWeek0ParamInfosAV != null && sopWeek0ParamInfosAV.size() > 0) {
			float nrzExpectedRatio = 0;
			float ocnExpectedRatio = 0;
			float phhExpectedRatio = 0;
			int nrzAVUpperSlab = Integer.parseInt((String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_AV_UPPER_SLAB));
			int nrzAVLowerSlab = Integer.parseInt((String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_AV_LOWER_SLAB));
			int ocnAVUpperSlab = Integer.parseInt((String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_AV_UPPER_SLAB));
			int ocnAVLowerSlab = Integer.parseInt((String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_AV_LOWER_SLAB));
			String phhUpperSlbStr = (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_AV_UPPER_SLAB);
			String phhAVLowerSlabStr = (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_AV_UPPER_SLAB);
			int phhAVUpperSlab = Integer.parseInt(StringUtils.isNotBlank(phhUpperSlbStr) ? phhUpperSlbStr : "0");
			int phhAVLowerSlab = Integer.parseInt(StringUtils.isNotBlank(phhAVLowerSlabStr) ? phhAVLowerSlabStr : "0");

			if (nrzAVUpperSlab > 0 && nrzAVLowerSlab > 0) {
				nrzExpectedRatio = nrzAVUpperSlab / nrzAVLowerSlab;
			}

			if (ocnAVUpperSlab > 0 && ocnAVLowerSlab > 0) {
				ocnExpectedRatio = ocnAVUpperSlab / ocnAVLowerSlab;
			}

			if (phhAVUpperSlab > 0 && phhAVLowerSlab > 0) {
				phhExpectedRatio = phhAVUpperSlab / phhAVLowerSlab;
			}

			log.info("nrzExpectedRatio : {} , ocnExpectedRatio :  {}, phhExpectedRatio : {}" , nrzExpectedRatio, ocnExpectedRatio, phhExpectedRatio);

			List<DPSopWeek0ParamInfo> nrzClassificationParams = sopWeek0ParamInfosAV.stream().filter(clf -> clf.getClassification().equals(DPAConstants.NRZ)).collect(Collectors.toList());
			List<DPSopWeek0ParamInfo> ocnClassificationParams = sopWeek0ParamInfosAV.stream().filter(clf -> clf.getClassification().equals(DPAConstants.OCN)).collect(Collectors.toList());
			List<DPSopWeek0ParamInfo> phhClassificationParams = sopWeek0ParamInfosAV.stream().filter(clf -> clf.getClassification().equals(DPAConstants.PHH)).collect(Collectors.toList());

			log.info("ocnClassificationParams count :{} , phhClassificationParams count : {}, nrzClassificationParams count : {}", ocnClassificationParams.size(), phhClassificationParams.size(), nrzClassificationParams.size());

			for (DPSopWeek0ParamInfo info : nrzClassificationParams) {
				MDC.put(RAClientConstants.LOAN_NUMBER, String.valueOf(info.getAssetValue()));
				if (StringUtils.isEmpty(info.getAssignment())) {
					sopWeek0AssignmentCalculate(lowerAssetValue, higherAssetValue, nrzExpectedRatio, nrzAVUpperSlab, nrzAVLowerSlab, info, DPAConstants.NRZ);
					saveParamInfo(lowerAssetValue, higherAssetValue, nrzAVUpperSlab, nrzAVLowerSlab, info, processEligible);
				}
				MDC.remove(RAClientConstants.LOAN_NUMBER);
			}
			for (DPSopWeek0ParamInfo info : ocnClassificationParams) {
				MDC.put(RAClientConstants.LOAN_NUMBER, info.getAssetNumber());
				if (StringUtils.isEmpty(info.getAssignment())) {
					sopWeek0AssignmentCalculate(lowerAssetValue, higherAssetValue, ocnExpectedRatio, ocnAVUpperSlab, ocnAVLowerSlab, info, DPAConstants.OCN);
					saveParamInfo(lowerAssetValue, higherAssetValue, ocnAVUpperSlab, ocnAVLowerSlab, info, processEligible);
				}
				MDC.remove(RAClientConstants.LOAN_NUMBER);
			}
			for (DPSopWeek0ParamInfo info : phhClassificationParams) {
				MDC.put(RAClientConstants.LOAN_NUMBER, info.getAssetNumber());
				if (StringUtils.isEmpty(info.getAssignment())) {
					sopWeek0AssignmentCalculate(lowerAssetValue, higherAssetValue, phhExpectedRatio, phhAVUpperSlab, phhAVLowerSlab, info, DPAConstants.PHH);
					saveParamInfo(lowerAssetValue, higherAssetValue, phhAVUpperSlab, phhAVLowerSlab, info, processEligible);
				}
				MDC.remove(RAClientConstants.LOAN_NUMBER);
			}
			result.addAll(nrzClassificationParams);
			result.addAll(ocnClassificationParams);
			result.addAll(phhClassificationParams);
		}
		log.info("SopWeek0ModeledBenchmarkCriteriaImpl -> filterByModeledBenchMark ended.");
		return result;
	}

	/**
	 * Perform AV range calculation
	 *
	 * @return
	 */
	private int[][] getAVRange() {
		String avRangeStr = (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_AV_RANGE);
		String[] avRangeArray = avRangeStr.split(",");
		int avRange[][] = new int[avRangeArray.length][2];
		for (int i = 0; i < avRangeArray.length; i++) {
			String[] temp = avRangeArray[i].split("\\|");
			avRange[i][0] = Integer.parseInt(temp[0]);
			avRange[i][1] = Integer.parseInt(temp[1]);
		}
		log.info("AV Range: {}", avRange);
		return avRange;
	}

	/**
	 * Filter DP process parameters by AV Range
	 *
	 * @param dpProcessParams
	 * @param minimumAV
	 * @param maximumAV
	 * @return
	 */
	private List<DPSopWeek0ParamInfo> filterByAVRange(List<DPSopWeek0ParamInfo> dpProcessParams, int minimumAV, int maximumAV) {
		List<DPSopWeek0ParamInfo> result = new ArrayList<DPSopWeek0ParamInfo>();
		for (DPSopWeek0ParamInfo info : dpProcessParams) {
			MDC.put(RAClientConstants.LOAN_NUMBER, info.getAssetNumber());
			log.debug("Filtering {} based on min Av - {} and max Av - {}", info.getAssetNumber(), minimumAV, maximumAV);
			if (info.getAssetValue().compareTo(BigDecimal.valueOf(minimumAV)) >= 0  && (info.getAssetValue().compareTo(BigDecimal.valueOf(maximumAV)) <= 0)) {
				result.add(info);
			}
			MDC.remove(RAClientConstants.LOAN_NUMBER);
		}
		return result;
	}

	/**
	 * @param lowerAssetValue
	 * @param higherAssetValue
	 * @param expectedRatio
	 * @param avUpperSlab
	 * @param avLowerSlab
	 */
	private void sopWeek0AssignmentCalculate(int lowerAssetValue, int higherAssetValue, float expectedRatio, int avUpperSlab, int avLowerSlab, DPSopWeek0ParamInfo paramInfo, String classification) throws SystemException {

		log.info("AssignmentCalculate() started for {} classification", classification);
		log.info("Loan number : state : lowerAssetValue : higherAssetValue : AVUpperSlab : AVLowerSlab : processEligible : ExpectedRatio ==>> "
				  + paramInfo.getAssetNumber() + " : " + paramInfo.getState() + ":" + lowerAssetValue + " : " + higherAssetValue + " : "
				  + avUpperSlab + " : " + avLowerSlab + " : " + expectedRatio);

		if (avUpperSlab == 0 && avLowerSlab == 0) {
			paramInfo.setAssignment(null);
		} else if (avUpperSlab == 0 || avLowerSlab == 0) {
			if (avUpperSlab > avLowerSlab) {
				paramInfo.setAssignment(DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue());
			} else {
				paramInfo.setAssignment(DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue());
			}
		} else {
			int modeledCount = countModeled(paramInfo, lowerAssetValue, higherAssetValue, classification);
			int benchmarkCount = countBenchMark(paramInfo, lowerAssetValue, higherAssetValue, classification);

			log.info("ModeledCount : BenchmarkCount ==>> {} : {}", modeledCount, benchmarkCount);

			//In case of OCN and PHH First entry should be Modeled instead of Benchmark.
			if (StringUtils.equals(classification, DPAConstants.NRZ)) {
				if (benchmarkCount == 0) {
					// put Benchmark
					paramInfo.setAssignment(DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue());
				} else if (modeledCount == 0) {
					// put Modeled
					paramInfo.setAssignment(DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue());
				} else {
					float calculatedRatio = (float) modeledCount / benchmarkCount;

					log.info("calculatedRatio : {}", calculatedRatio);
					log.info("calculatedRatio < nrzExpectedRatio : {}", (calculatedRatio < expectedRatio));

					if (calculatedRatio < expectedRatio) {
						paramInfo.setAssignment(DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue());
					} else {
						paramInfo.setAssignment(DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue());
					}

					log.info("ASSIGNMENT : {}", paramInfo.getAssignment());

				}
			} else {
				if (modeledCount == 0) {
					// put Modeled
					paramInfo.setAssignment(DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue());
				} else if (benchmarkCount == 0) {
					// put Benchmark
					paramInfo.setAssignment(DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue());
				} else {
					float calculatedRatio = (float) modeledCount / benchmarkCount;

					log.info("calculatedRatio : {}", calculatedRatio);
					log.info("calculatedRatio < nrzExpectedRatio : {}", (calculatedRatio < expectedRatio));

					if (calculatedRatio < expectedRatio) {
						paramInfo.setAssignment(DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue());
					} else {
						paramInfo.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
						paramInfo.setAssignment(DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue());
					}

					log.info("ASSIGNMENT : {}", paramInfo.getAssignment());

				}
			}

			paramInfo.setModeledCount(modeledCount);
			paramInfo.setBenchmarkCount(benchmarkCount);
		}

		log.info("AssignmentCalculate() ended.");

	}

	private int countBenchMark(DPSopWeek0ParamInfo paramInfo, int lowerAssetValue, int higherAssetValue, String classification) throws SystemException {
		return dpSopProcessFilterDelegate.countBenchmark(paramInfo.getState(), lowerAssetValue, higherAssetValue, classification);
	}

	private int countModeled(DPSopWeek0ParamInfo paramInfo, int lowerAssetValue, int higherAssetValue, String classification) throws SystemException {
		return dpSopProcessFilterDelegate.countModeled(paramInfo.getState(), lowerAssetValue, higherAssetValue, classification);
	}

	/**
	 * @param lowerAssetValue
	 * @param higherAssetValue
	 * @param ocnAVUpperSlab
	 * @param ocnAVLowerSlab
	 * @param info
	 */
	private void saveParamInfo(int lowerAssetValue, int higherAssetValue, int ocnAVUpperSlab, int ocnAVLowerSlab, DPSopWeek0ParamInfo info,
	                           boolean processEligible) throws SystemException {
		log.info("saveParamInfo() ");
		log.info("lowerAssetValue : higherAssetValue : AVUpperSlab : AVLowerSlab : processEligible ==>> " + lowerAssetValue + " : " + higherAssetValue
				  + " : " + ocnAVUpperSlab + " : " + ocnAVLowerSlab + " : " + processEligible);
		if (processEligible) {
			info.setEligible(DPProcessParamAttributes.ELIGIBLE.getValue());
		}
		info.setLowerAssetValue(lowerAssetValue);
		info.setHigherAssetValue(higherAssetValue);
		info.setLowerSlab(ocnAVLowerSlab);
		info.setHigherSlab(ocnAVUpperSlab);
		// update input param
		DPSopWeek0Param obj = dpSopWeek0ParamMapper.mapInfoToDomain(info);
		dpSopProcessFilterDelegate.saveDPSopWeek0Param(obj);
	}
}