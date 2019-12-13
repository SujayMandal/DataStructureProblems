/**
 *
 */
package com.fa.dp.business.task;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.RAClientConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author misprakh
 */

@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "week0ModeledBenchmarkCriteria")
public class Week0ModeledBenchmarkCriteria extends AbstractCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(Week0ModeledBenchmarkCriteria.class);

	@Inject
	private CacheManager cacheManager;

	@Inject
	private DPFileProcessBO dpFileProcessBO;

	@Override
	public void execute(Object data) throws SystemException {
		LOGGER.info("week0ModeledBenchmarkCriteria -> processTask started.");
		Long startTime = DateTime.now().getMillis();
		DPProcessParamEntryInfo dpProcessParamEntryInfo = null;
		try {
			if (checkData(data, DPProcessParamEntryInfo.class)) {
				dpProcessParamEntryInfo = ((DPProcessParamEntryInfo) data);

				LOGGER.info("ModeledBenchmarkCriteriaImpl -> modeledBenchmarkRuleProcess started.");
				List<DPProcessParamInfo> dpProcessParams = dpProcessParamEntryInfo.getColumnEntries();

				int[][] avRange = getAVRange();

				List<DPProcessParamInfo> paramsProcessing = new ArrayList<DPProcessParamInfo>();

				for (int[] range : avRange) {
					List<DPProcessParamInfo> dpProcessParamsAV = filterByAVRange(dpProcessParams, range[0], range[1]);

					dpProcessParamsAV = filterByModeledBenchMark(dpProcessParamsAV, range[0], range[1], true);

					paramsProcessing.addAll(dpProcessParamsAV);
				}

				List<DPProcessParamInfo> diff = dpProcessParams.stream().filter(elem -> !paramsProcessing.contains(elem))
						.collect(Collectors.toList());

				dpProcessParams.clear();
				dpProcessParams.addAll(diff);
				dpProcessParams.addAll(paramsProcessing);
				dpProcessParamEntryInfo.setColumnEntries(dpProcessParams);
				LOGGER.info("ModeledBenchmarkCriteriaImpl -> modeledBenchmarkRuleProcess ended.");
			}
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			dpProcessParamEntryInfo.getDPFileProcessStatusInfo().setStatus(DPFileProcessStatus.ERROR.getFileStatus());
		}
		log.info("Time taken for week0ModeledBenchmarkCriteria : " + (DateTime.now().getMillis() - startTime) + "ms");
		LOGGER.info("week0ModeledBenchmarkCriteria -> processTask ended.");
	}

	public void week0DBModeledBenchmarkRuleProcess(DPProcessParamEntryInfo dpProcessParamEntryInfo) {
		LOGGER.info("ModeledBenchmarkCriteriaImpl -> week0DBModeledBenchmarkRuleProcess started.");
		List<DPProcessParamInfo> dpProcessParams = dpProcessParamEntryInfo.getColumnEntries();

		int[][] avRange = getAVRange();

		List<DPProcessParamInfo> paramsProcessing = new ArrayList<DPProcessParamInfo>();

		for (int[] range : avRange) {
			List<DPProcessParamInfo> dpProcessParamsAV = filterByAVRange(dpProcessParams, range[0], range[1]);

			dpProcessParamsAV = filterByModeledBenchMark(dpProcessParamsAV, range[0], range[1], false);

			paramsProcessing.addAll(dpProcessParamsAV);
		}

		List<DPProcessParamInfo> diff = dpProcessParams.stream().filter(elem -> !paramsProcessing.contains(elem)).collect(Collectors.toList());

		dpProcessParams.clear();
		dpProcessParams.addAll(diff);
		dpProcessParams.addAll(paramsProcessing);
		dpProcessParamEntryInfo.setColumnEntries(dpProcessParams);
		LOGGER.info("ModeledBenchmarkCriteriaImpl -> week0DBModeledBenchmarkRuleProcess ended.");
	}

	/**
	 * Filter DP process parameters by AV Range
	 *
	 * @param dpProcessParams
	 * @param minimumAV
	 * @param maximumAV
	 * @return
	 */
	private List<DPProcessParamInfo> filterByAVRange(List<DPProcessParamInfo> dpProcessParams, int minimumAV, int maximumAV) {
		List<DPProcessParamInfo> result = new ArrayList<DPProcessParamInfo>();
		for (DPProcessParamInfo info : dpProcessParams) {
			MDC.put(RAClientConstants.LOAN_NUMBER, info.getAssetNumber());
			double assetValue = Double.parseDouble(info.getAssetValue());
			if (assetValue >= minimumAV && assetValue <= maximumAV) {
				result.add(info);
			}
			MDC.remove(RAClientConstants.LOAN_NUMBER);
		}
		return result;
	}

	/**
	 * Filter by classification
	 *
	 * @param dpProcessParams
	 * @param classification
	 * @return
	 */
	private List<DPProcessParamInfo> filterByClassification(List<DPProcessParamInfo> dpProcessParams, String classification) {
		List<DPProcessParamInfo> result = new ArrayList<>();
		for (DPProcessParamInfo info : dpProcessParams) {
			if (info.getClassification().equalsIgnoreCase(classification)) {
				result.add(info);
			}
		}
		return result;
	}

	/**
	 * Modeled Benchmark ratio filtering
	 *
	 * @param dpProcessParams
	 * @param higherAssetValue
	 * @param lowerAssetValue
	 * @return
	 */
	private List<DPProcessParamInfo> filterByModeledBenchMark(List<DPProcessParamInfo> dpProcessParams, int lowerAssetValue, int higherAssetValue,
			boolean processEligible) {
		LOGGER.info("ModeledBenchmarkCriteriaImpl -> filterByModeledBenchMark started.");
		LOGGER.info("DPProcessWeekZeroParamInfo size : " + dpProcessParams.size());
		List<DPProcessParamInfo> result = new ArrayList<DPProcessParamInfo>();

		if (dpProcessParams != null && dpProcessParams.size() > 0) {

			float nrzExpectedRatio = 0;
			float ocnExpectedRatio = 0;
			float phhExpectedRatio = 0;

			int nrzAVUpperSlab = Integer.parseInt((String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_AV_UPPER_SLAB));
			int nrzAVLowerSlab = Integer.parseInt((String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_AV_LOWER_SLAB));
			int ocnAVUpperSlab = Integer.parseInt((String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_AV_UPPER_SLAB));
			int ocnAVLowerSlab = Integer.parseInt((String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_AV_LOWER_SLAB));

			int phhAVUpperSlab = Integer.parseInt((String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_AV_UPPER_SLAB));
			int phhAVLowerSlab = Integer.parseInt((String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_AV_LOWER_SLAB));

			if (nrzAVUpperSlab > 0 && nrzAVLowerSlab > 0) {
				nrzExpectedRatio = nrzAVUpperSlab / nrzAVLowerSlab;
			}

			if (ocnAVUpperSlab > 0 && ocnAVLowerSlab > 0) {
				ocnExpectedRatio = ocnAVUpperSlab / ocnAVLowerSlab;
			}

			if (phhAVUpperSlab > 0 && phhAVLowerSlab > 0) {
				phhExpectedRatio = phhAVUpperSlab / phhAVLowerSlab;
			}

			LOGGER.info("nrzExpectedRatio : " + nrzExpectedRatio);
			LOGGER.info("ocnExpectedRatio : " + ocnExpectedRatio);
			LOGGER.info("phhExpectedRatio : " + phhExpectedRatio);

			List<DPProcessParamInfo> nrzClassificationParams = filterByClassification(dpProcessParams, DPProcessParamAttributes.NRZ.getValue());
			List<DPProcessParamInfo> ocnClassificationParams = filterByClassification(dpProcessParams, DPProcessParamAttributes.OCN.getValue());
			List<DPProcessParamInfo> phhClassificationParams = filterByClassification(dpProcessParams, DPProcessParamAttributes.PHH.getValue());

			LOGGER.info("ocnClassificationParams count : " + ocnClassificationParams.size());
			LOGGER.info("phhClassificationParams count : " + phhClassificationParams.size());
			LOGGER.info("nrzClassificationParams count : " + nrzClassificationParams.size());

			for (DPProcessParamInfo info : nrzClassificationParams) {
				MDC.put(RAClientConstants.LOAN_NUMBER, info.getAssetValue());
				if (StringUtils.isEmpty(info.getAssignment())) {
					nrzAssignmentCalculate(lowerAssetValue, higherAssetValue, nrzExpectedRatio, nrzAVUpperSlab, nrzAVLowerSlab, info);
					saveParamInfo(lowerAssetValue, higherAssetValue, nrzAVUpperSlab, nrzAVLowerSlab, info, processEligible);
				}
				MDC.remove(RAClientConstants.LOAN_NUMBER);
			}

			for (DPProcessParamInfo info : ocnClassificationParams) {
				MDC.put(RAClientConstants.LOAN_NUMBER, info.getAssetNumber());
				if (StringUtils.isEmpty(info.getAssignment())) {
					ocnAssignmentCalculate(lowerAssetValue, higherAssetValue, ocnExpectedRatio, ocnAVUpperSlab, ocnAVLowerSlab, info,
							DPProcessParamAttributes.OCN.getValue());
					saveParamInfo(lowerAssetValue, higherAssetValue, ocnAVUpperSlab, ocnAVLowerSlab, info, processEligible);
				}
				MDC.remove(RAClientConstants.LOAN_NUMBER);
			}

			for (DPProcessParamInfo info : phhClassificationParams) {
				MDC.put(RAClientConstants.LOAN_NUMBER, info.getAssetNumber());
				if (StringUtils.isEmpty(info.getAssignment())) {
					ocnAssignmentCalculate(lowerAssetValue, higherAssetValue, phhExpectedRatio, phhAVUpperSlab, phhAVLowerSlab, info,
							DPProcessParamAttributes.PHH.getValue());
					saveParamInfo(lowerAssetValue, higherAssetValue, phhAVUpperSlab, phhAVLowerSlab, info, processEligible);
				}
				MDC.remove(RAClientConstants.LOAN_NUMBER);
			}

			result.addAll(nrzClassificationParams);
			result.addAll(ocnClassificationParams);
			result.addAll(phhClassificationParams);
		}
		LOGGER.info("DPProcessWeekZeroParamInfo processed size : " + result.size());
		LOGGER.info("ModeledBenchmarkCriteriaImpl -> filterByModeledBenchMark ended.");
		return result;
	}

	/**
	 * @param lowerAssetValue
	 * @param higherAssetValue
	 * @param ocnAVUpperSlab
	 * @param ocnAVLowerSlab
	 * @param info
	 */
	private void saveParamInfo(int lowerAssetValue, int higherAssetValue, int ocnAVUpperSlab, int ocnAVLowerSlab, DPProcessParamInfo info,
			boolean processEligible) {
		LOGGER.info("saveParamInfo() ");
		LOGGER.info(
				"lowerAssetValue : higherAssetValue : AVUpperSlab : AVLowerSlab : processEligible ==>> " + lowerAssetValue + " : " + higherAssetValue
						+ " : " + ocnAVUpperSlab + " : " + ocnAVLowerSlab + " : " + processEligible);
		if (processEligible) {
			info.setEligible(DPProcessParamAttributes.ELIGIBLE.getValue());
		}
		info.setLowerAssetValue(lowerAssetValue);
		info.setHigherAssetValue(higherAssetValue);
		info.setLowerSlab(ocnAVLowerSlab);
		info.setHigherSlab(ocnAVUpperSlab);
		// update input param
		DPProcessParam obj = new DPProcessParam();
		obj = convert(info, DPProcessParam.class);
		dpFileProcessBO.saveDPProcessParam(obj);
	}

	/**
	 * @param lowerAssetValue
	 * @param higherAssetValue
	 * @param expectedRatio
	 * @param avUpperSlab
	 * @param avLowerSlab
	 */
	private void ocnAssignmentCalculate(int lowerAssetValue, int higherAssetValue, float expectedRatio, int avUpperSlab, int avLowerSlab,
			DPProcessParamInfo paramInfo, String classification) {

		LOGGER.info("AssignmentCalculate() started for {} classification", classification);

		LOGGER.info(
				"Loan number : state : lowerAssetValue : higherAssetValue : ocnAVUpperSlab : ocnAVLowerSlab : processEligible : ocnExpectedRatio ==>> "
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

			LOGGER.info("ModeledCount : BenchmarkCount ==>> " + modeledCount + " : " + benchmarkCount);

			//First entry should be Modeled instead of Benchmark.
			if (modeledCount == 0) {
				// put Modeled
				paramInfo.setAssignment(DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue());
			} else if (benchmarkCount == 0) {
				// put Benchmark
				paramInfo.setAssignment(DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue());
			} else {
				float calculatedRatio = (float) modeledCount / benchmarkCount;

				LOGGER.info("calculatedRatio : " + calculatedRatio);
				LOGGER.info("calculatedRatio < {}ExpectedRatio : {}" , classification, (calculatedRatio < expectedRatio));

				if (calculatedRatio < expectedRatio) {
					paramInfo.setAssignment(DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue());
				} else {
					paramInfo.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					paramInfo.setAssignment(DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue());
				}

				LOGGER.info("ASSIGNMENT : " + paramInfo.getAssignment());

			}
			paramInfo.setModeledCount(modeledCount);
			paramInfo.setBenchmarkCount(benchmarkCount);
		}

		LOGGER.info("ocnAssignmentCalculate() ended.");

	}

	/**
	 * @param lowerAssetValue
	 * @param higherAssetValue
	 * @param nrzExpectedRatio
	 * @param nrzAVUpperSlab
	 * @param nrzAVLowerSlab
	 * @param paramInfo
	 */
	private void nrzAssignmentCalculate(int lowerAssetValue, int higherAssetValue, float nrzExpectedRatio, int nrzAVUpperSlab, int nrzAVLowerSlab,
			DPProcessParamInfo paramInfo) {

		LOGGER.info("nrzAssignmentCalculate() started.");

		LOGGER.info(
				"lowerAssetValue : higherAssetValue : nrzAVUpperSlab : nrzAVLowerSlab : processEligible : nrzExpectedRatio ==>> " + lowerAssetValue
						+ " : " + higherAssetValue + " : " + nrzAVUpperSlab + " : " + nrzAVLowerSlab + " : " + nrzExpectedRatio);

		if (nrzAVUpperSlab == 0 && nrzAVLowerSlab == 0) {
			paramInfo.setAssignment(null);
		} else if (nrzAVUpperSlab == 0 || nrzAVLowerSlab == 0) {
			if (nrzAVUpperSlab > nrzAVLowerSlab) {
				paramInfo.setAssignment(DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue());
			} else {
				paramInfo.setAssignment(DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue());
			}
		} else {
			int nrzModeledCount = countModeled(paramInfo, lowerAssetValue, higherAssetValue, DPProcessParamAttributes.NRZ.getValue());
			int nrzBenchmarkCount = countBenchMark(paramInfo, lowerAssetValue, higherAssetValue, DPProcessParamAttributes.NRZ.getValue());

			LOGGER.info("nrzModeledCount : nrzBenchmarkCount ==>> " + nrzModeledCount + " : " + nrzBenchmarkCount);

			if (nrzBenchmarkCount == 0) {
				// put Benchmark
				paramInfo.setAssignment(DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue());
			} else if (nrzModeledCount == 0) {
				// put Modeled
				paramInfo.setAssignment(DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue());
			} else {
				float calculatedRatio = (float) nrzModeledCount / nrzBenchmarkCount;

				LOGGER.info("calculatedRatio : " + calculatedRatio);
				LOGGER.info("calculatedRatio < nrzExpectedRatio : " + (calculatedRatio < nrzExpectedRatio));

				if (calculatedRatio < nrzExpectedRatio) {
					paramInfo.setAssignment(DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue());
				} else {
					paramInfo.setAssignment(DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue());
				}

				LOGGER.info("ASSIGNMENT : " + paramInfo.getAssignment());

			}
			paramInfo.setModeledCount(nrzModeledCount);
			paramInfo.setBenchmarkCount(nrzBenchmarkCount);
		}

		LOGGER.info("nrzAssignmentCalculate() ended.");
	}

	/**
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
		LOGGER.info("AV Range: " + avRange);
		return avRange;
	}

	private int countBenchMark(DPProcessParamInfo paramInfo, int lowerAssetValue, int higherAssetValue, String classification) {
		return dpFileProcessBO.countBenchmark(paramInfo.getState(), lowerAssetValue, higherAssetValue, classification);
	}

	private int countModeled(DPProcessParamInfo paramInfo, int lowerAssetValue, int higherAssetValue, String classification) {
		return dpFileProcessBO.countModeled(paramInfo.getState(), lowerAssetValue, higherAssetValue, classification);
	}

}
