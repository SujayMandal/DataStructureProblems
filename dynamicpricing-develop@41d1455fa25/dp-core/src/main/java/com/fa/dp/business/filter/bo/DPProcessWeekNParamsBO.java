package com.fa.dp.business.filter.bo;

import com.fa.dp.business.info.SSPMIInfo;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.KeyValue;
import java.time.LocalDate;

import java.util.List;

public interface DPProcessWeekNParamsBO {

	/**
	 * Save DPProcessWeekNParam
	 *
	 * @param dpProcessParam
	 * @return
	 */
	DPProcessWeekNParam saveDPProcessWeekNParam(DPProcessWeekNParam dpProcessParam);

	/**
	 * Save DPProcessWeekNParamInfo
	 *
	 * @param dpProcessParamInfo
	 * @return
	 */
	DPProcessWeekNParamInfo saveDPProcessWeekNParamInfo(DPProcessWeekNParamInfo dpProcessParamInfo);

	/**
	 * Save list of DPProcessWeekNParam
	 *
	 * @param dpProcessParam
	 * @return
	 */
	List<DPProcessWeekNParam> saveDPProcessParams(List<DPProcessWeekNParam> dpProcessParam);

	/**
	 * Save list of DPProcessWeekNParamInfo
	 *
	 * @param dpProcessParamInfoList
	 * @return
	 */
	List<DPProcessWeekNParamInfo> saveDPProcessParamInfos(List<DPProcessWeekNParamInfo> dpProcessParamInfoList);

	/**
	 * Fetch list of weekn param object for recommendation list
	 *
	 * @param infoObject
	 * @param classification
	 * @return
	 */
	List<DPProcessWeekNParamInfo> fetchRecommendationList(DPProcessWeekNParamEntryInfo infoObject, String classification);

	/**
	 * Fetch list of weekn param object for exclusion list
	 *
	 * @param infoObject
	 * @param classification
	 * @return
	 */
	List<DPProcessWeekNParamInfo> fetchExclusionList(DPProcessWeekNParamEntryInfo infoObject, String classification);

	/**
	 * Fetch list of weekn param object for past 12 cycle list
	 *
	 * @param infoObject
	 * @param classification
	 * @return
	 */
	List<DPProcessWeekNParamInfo> fetchPast12List(DPProcessWeekNParamEntryInfo infoObject, String classification);

	/**
	 * filter the records based on ss and pmi
	 * @param columnEntries
	 * @return
	 */
	//	KeyValue<List<DPProcessWeekNParamInfo>, List<DPProcessWeekNParamInfo>> filterRecords(List<DPProcessWeekNParamInfo> columnEntries);

	/**
	 * filter the records based on state and zipCode
	 *
	 * @param columnEntries
	 * @return
	 */
	KeyValue<List<DPProcessWeekNParamInfo>, List<DPProcessWeekNParamInfo>> filterRecordsOnZipCodeAndState(
			List<DPProcessWeekNParamInfo> columnEntries);

	/**
	 * filter the records based on sop
	 *
	 * @param columnEntries
	 * @return
	 */
	KeyValue<List<DPProcessWeekNParamInfo>, List<DPProcessWeekNParamInfo>> filterRecordsOnSop(List<DPProcessWeekNParamInfo> columnEntries);

	/**
	 * Fetch list of weekn param object for assetNumber
	 *
	 * @param assetNumber
	 * @param classification
	 * @return
	 */
	List<DPProcessWeekNParam> findByAssetNumberAndClassification(String assetNumber, String classification);

	/**
	 * Fetch list of weekn param where deleviry date is 1-1-y
	 *
	 * @param deliveryDate
	 * @return
	 */
	List<DPProcessWeekNParam> findByDeliveryDate(String deliveryDate);

	KeyValue<List<DPProcessWeekNParamInfo>, List<DPProcessWeekNParamInfo>> filterRecordsOnAssignment(List<DPProcessWeekNParamInfo> columnEntries,
			boolean isFetchProcess);

	List<DPProcessWeekNParam> searchByAssetNumber(String assetNumber);

	KeyValue<DPProcessWeekNParamEntryInfo, List<SSPMIInfo>> filterOnPMIFlag(DPProcessWeekNParamEntryInfo infoObject, List<SSPMIInfo> ssPmiInfos);

	KeyValue<DPProcessWeekNParamEntryInfo, List<SSPMIInfo>> filterOnInscComp(DPProcessWeekNParamEntryInfo infoObject, List<SSPMIInfo> ssPmiInfos);

	KeyValue<DPProcessWeekNParamEntryInfo, List<SSPMIInfo>> filterOnSpclServicing(DPProcessWeekNParamEntryInfo infoObject,
			List<SSPMIInfo> ssPmiInfos);

	List<DPProcessWeekNParam> searchByAssetDeliveryNull(String assetNumber);

	/**
	 * Filter PMI property as per the story #328
	 *
	 * @param infoObject
	 * @return DPProcessWeekNParamEntryInfo
	 */
	DPProcessWeekNParamEntryInfo filterOnNewPMIFlag(DPProcessWeekNParamEntryInfo infoObject) throws SystemException;

	/**
	 * Filter Pmi property for 0 flag.
	 *
	 * @param infoObject
	 * @return
	 * @throws SystemException
	 */
	DPProcessWeekNParamEntryInfo filterOnZeroPMIFlag(DPProcessWeekNParamEntryInfo infoObject) throws SystemException;

	/**
	 * Filter SS property as per the story #328
	 *
	 * @param infoObject
	 * @return DPProcessWeekNParamEntryInfo
	 */
	DPProcessWeekNParamEntryInfo filterOnNewSpclServicing(DPProcessWeekNParamEntryInfo infoObject);

	/**
	 * Fetch reduction data from weekn param
	 * @param rbidPropIdVcNn
	 * @param oldLoanNumber
	 * @param currentListEndDate
	 * @return
	 */
	DPProcessWeekNParamInfo checkReduction(String rbidPropIdVcNn, String oldLoanNumber, LocalDate currentListEndDate);

	/**
	 * Fetch latest weekn run date
	 * @return
	 */
	String getLatestWekNRunDate();

	void filterQAReportAssignment(List<DPProcessWeekNParamInfo> columnEntries);

	void filterQAReportState(List<DPProcessWeekNParamInfo> columnEntries);

	void filterQAReportSSPmiFlag(DPProcessWeekNParamEntryInfo infoObject);
}