package com.fa.dp.business.sop.week0.delegate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.util.ObjectUtils;

import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.sop.week0.bo.DPSopProcessBO;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0ProcessStatus;
import com.fa.dp.business.sop.week0.input.info.DPSopParamEntryInfo;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.week0.input.mapper.DPSopWeek0ParamMapper;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.KeyValue;
import com.fa.dp.core.util.RAClientConstants;

/**
 * @author misprakh
 */

@Slf4j
@Named
public class DPSopProcessFilterDelegateImpl implements DPSopProcessFilterDelegate {

	@Inject
	private DPSopProcessBO dpSopProcessBO;

	@Inject
	private DPSopWeek0ParamMapper sopWeek0ParamMapper;


	/**
	 * Filter asset number based on asset value
	 *
	 * @param inputParamEntry
	 * @param filterName
	 * @throws SystemException
	 */
	@Override
	public void filterOnAssetValue(DPSopParamEntryInfo inputParamEntry, String filterName) throws SystemException {
		if (null != inputParamEntry.getColumnEntries() && !inputParamEntry.getColumnEntries().isEmpty()) {
			log.info("Filtering on Asset value started..");
			KeyValue<List<DPSopWeek0ParamInfo>, List<DPSopWeek0ParamInfo>> resultMap = dpSopProcessBO.filterOnAssetValue(inputParamEntry, filterName);
			// setting the successful records after filtering, back to input object
			inputParamEntry.setColumnEntries(resultMap.getKey());
			// save ineligible records to DPSopWeek0Param table
			if (CollectionUtils.isNotEmpty(resultMap.getValue())) {
				log.info("Saving all ineligible AssetValue entries to db..");
				dpSopProcessBO.saveFailedRecord(resultMap.getValue());
			}
			log.info("Filtering on Asset value ended..");
		}
	}

	/**
	 * Used to check the status of file If status is in-progress then process will not start
	 *
	 * @param fileStatus
	 * @return
	 * @throws SystemException
	 */
	@Override
	public DPSopWeek0ProcessStatus checkForFileStatus(String fileStatus) throws SystemException {
		return dpSopProcessBO.findFileByStatus(fileStatus);
	}

	/**
	 * @param dpSopParamEntryInfo
	 * @param filterName
	 */
	@Override
	public void filterOnDuplicates(DPSopParamEntryInfo dpSopParamEntryInfo, String filterName) throws SystemException {
		if (CollectionUtils.isNotEmpty(dpSopParamEntryInfo.getColumnEntries())) {
			log.info("Filtering on duplicate check started..");
			List<DPSopWeek0ParamInfo> successEntries = new ArrayList<>();

			for (DPSopWeek0ParamInfo columnEntry : dpSopParamEntryInfo.getColumnEntries()) {
				MDC.put(RAClientConstants.LOAN_NUMBER, columnEntry.getAssetNumber());
				DPSopWeek0ParamInfo filteredEntry = null;
				List<DPSopWeek0Param> dpProcessParams = dpSopProcessBO.searchByAssetNumber(columnEntry.getAssetNumber());
				if (CollectionUtils.isNotEmpty(dpProcessParams)) {
					List<DPSopWeek0ParamInfo> dpProcessParamsInfo = sopWeek0ParamMapper.mapDomainToInfoList(dpProcessParams);
					filteredEntry = dpSopProcessBO.filterOnDuplicates(columnEntry, dpProcessParamsInfo, filterName);
				}
				if (!ObjectUtils.isEmpty(filteredEntry)) {
					// saving record as ineligible to db
					log.info("Saving ineligible duplicate record to db ..");
					DPSopWeek0Param failedDpSopWeek0Param = sopWeek0ParamMapper.mapInfoToDomain(filteredEntry);
					dpSopProcessBO.saveDPSopWeek0Param(failedDpSopWeek0Param);
				} else {
					// saving non filtered out column entry as it is to db
					DPSopWeek0ParamInfo infoWithId = new DPSopWeek0ParamInfo();
					//Check for reprocess status
					if (dpSopParamEntryInfo.isReprocess()) {
						columnEntry.setFailedStepCommandName(null);
						columnEntry.setEligible(null);
						columnEntry.setErrorDetail(null);
						columnEntry.setAssignment(null);
						columnEntry.setAssignmentDate(null);
						columnEntry.setNotes(null);
						columnEntry.setErrorDetail(null);
					}
					DPSopWeek0Param successDpSopWeek0Param = sopWeek0ParamMapper.mapInfoToDomain(columnEntry);
					DPSopWeek0Param dpSopWeek0Param = dpSopProcessBO.saveDPSopWeek0Param(successDpSopWeek0Param);
					infoWithId = sopWeek0ParamMapper.mapDomainToInfo(dpSopWeek0Param);
					successEntries.add(infoWithId);
				}
				MDC.remove(RAClientConstants.LOAN_NUMBER);
			}
			dpSopParamEntryInfo.setColumnEntries(successEntries);
			log.info("Filtering on duplicate check ended.");
		}
	}

	/**
	 * @param state
	 * @param lowerAssetValue
	 * @param higherAssetValue
	 * @param classification
	 * @return
	 * @throws SystemException
	 */
	@Override
	public int countBenchmark(String state, int lowerAssetValue, int higherAssetValue, String classification) throws SystemException {
		return dpSopProcessBO.countAssignmentByStateAndAssetNumber(state, BigDecimal.valueOf(lowerAssetValue), BigDecimal.valueOf(higherAssetValue),
							 DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue(), classification, DPAConstants.SOP_WEEK0DB_INITIAL).size();
	}

	/**
	 * @param state
	 * @param lowerAssetValue
	 * @param higherAssetValue
	 * @param classification
	 * @return
	 */
	@Override
	public int countModeled(String state, int lowerAssetValue, int higherAssetValue, String classification) throws SystemException {
		return dpSopProcessBO.countAssignmentByStateAndAssetNumber(state, BigDecimal.valueOf(lowerAssetValue), BigDecimal.valueOf(higherAssetValue),
							 DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue(), classification, DPAConstants.SOP_WEEK0DB_INITIAL).size();
	}

	/**
	 * @param dpSopWeek0Param
	 * @return
	 */
	@Override
	public DPSopWeek0Param saveDPSopWeek0Param(DPSopWeek0Param dpSopWeek0Param) throws SystemException {
		return dpSopProcessBO.saveDPSopWeek0Param(dpSopWeek0Param);
	}

	private DPSopWeek0ParamInfo saveParam(DPSopWeek0ParamInfo recordToSave) throws SystemException {
		DPSopWeek0Param in = sopWeek0ParamMapper.mapInfoToDomain(recordToSave);
		in = dpSopProcessBO.saveDPSopWeek0Param(in);
		recordToSave.setId(in.getId());
		return recordToSave;
	}
	@Override
	public List<DPSopWeek0ParamInfo> getAssetDetails(String fileId, String type)
			throws SystemException {
		return dpSopProcessBO.getAssetDetails(fileId, type);
		}
}
