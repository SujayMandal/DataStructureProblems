package com.fa.dp.business.sop.weekN.bo;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fa.dp.business.sop.weekN.entity.DPSopWeekNParam;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.week0.info.DashboardFilterInfo;
import com.fa.dp.core.exception.SystemException;

public interface DPSopWeekNParamBO {
	/**
	 * Get the most recent list end date from DB
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	String findMostRecentListEndDate() throws SystemException;

	/**
	 * @return List<DPSopWeekNParamInfo>
	 *
	 * @throws SystemException
	 */
	List<DPSopWeekNParamInfo> getSOPWeekNData() throws SystemException;

	/**
	 * Search in sop weekn for given asset number and empty delivery date and model version
	 *
	 * @param assetNumber
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	List<DPSopWeekNParamInfo> searchSopWeekNParamSuccesfulUnderRiview(String assetNumber) throws SystemException;

	/**
	 * save sop weekn param data
	 *
	 * @param paramEntry
	 *
	 * @throws SystemException
	 */
	void saveSopWeekNParamInfo(DPSopWeekNParamInfo paramEntry) throws SystemException;

	/**
	 * save sop weekn param list data
	 *
	 * @param sopWeekNInfoList
	 *
	 * @throws SystemException
	 */
	void saveSopWeekNParamInfoList(List<DPSopWeekNParamInfo> sopWeekNInfoList) throws SystemException;

	/**
	 * Fetch sop weekn param by id
	 *
	 * @param id
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	DPSopWeekNParamInfo findSopWeekNParamById(String id) throws SystemException;

	/**
	 * @param sopWeekNParamEntryInfo
	 * @param userSelectedDate
	 * @param response
	 *
	 * @throws SystemException
	 */
	void createAndDownloadSopWeekNExcel(DPSopWeekNParamEntryInfo sopWeekNParamEntryInfo, Long userSelectedDate, HttpServletResponse response)
			throws SystemException;

	/**
	 * @param id
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	List<DPSopWeekNParam> retrieveSopWeekNFilesDetailsById(String id) throws SystemException;

	/**
	 *
	 * @param sysGnrtdInputFileName
	 * @param zipFileName
	 * @param response
	 * @return
	 * @throws SystemException
	 */
	boolean findSopWeekNReports(String sysGnrtdInputFileName, String zipFileName, HttpServletResponse response) throws SystemException;

	/**
	 * @return
	 *
	 * @throws SystemException
	 */
	List<DPSopWeekNParam> findAllSopWeekNForDeliveryDateAndPropSoldDateNull() throws SystemException;

	/**
	 *
	 * @param mapOfOCNPHHandNRZListings
	 * @param response
	 * @param zipFileName
	 * @param sysGnrtdInputFileName
	 * @throws SystemException
	 */
	void generateSopWeekNZipFile(Map<String, List<DPSopWeekNParam>> mapOfOCNPHHandNRZListings, HttpServletResponse response, String zipFileName,
			String sysGnrtdInputFileName, String type) throws SystemException;
	
	/**
	 * @param fileId
	 * @param type
	 * @return
	 * @throws SystemException
	 */
	List<DPSopWeekNParamInfo> getAssetDetails(String fileId, String type) throws SystemException;
	
	/**
	 * @param assetNumber
	 * @return
	 */
	List<DPSopWeekNParam> searchByAssetNumber(String assetNumber);

	/**
	 *
	 * @param id
	 * @return
	 * @throws SystemException
	 */
	List<String> findFailedStepCommands(String id) throws SystemException;
	
	/**
	 * @param dashboardFilterInfo
	 * @return
	 * @throws SystemException
	 */
	List<DPSopWeekNParamInfo> getWeekNFilteredFiles(DashboardFilterInfo dashboardFilterInfo) throws SystemException;
	
	/**
	 * @param columnEntries
	 */
	void filterSopQAReportAssignment(List<DPSopWeekNParamInfo> columnEntries);
	
	/**
	 * @param columnEntries
	 */
	void filterSopQAReportState(List<DPSopWeekNParamInfo> columnEntries);
	
	/**
	 * @param infoObject
	 */
	void filterSopQAReportSSPmiFlag(DPSopWeekNParamEntryInfo infoObject);
}
