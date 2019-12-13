package com.fa.dp.business.sop.weekN.delegate;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;

/**
 * @author misprakh
 */
public interface DPSopWeekNParamDelegate {
	/**
	 * @param selectedDateMillis
	 *
	 * @return DPSopWeekNParamEntryInfo
	 *
	 * @throws SystemException
	 */
	DPSopWeekNParamEntryInfo fetchSopWeekNFromHubzu(Long selectedDateMillis) throws SystemException;

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
	 * Search sop weekn record for given asset number and eligibility
	 *
	 * @param assetNumber
	 * @param eligible
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	List<DPSopWeek0ParamInfo> fetchSopWeek0ParamsRA(String assetNumber, String eligible) throws SystemException;

	/**
	 * fetch sop weekn param by id
	 *
	 * @param id
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	DPSopWeekNParamInfo findSopWeekNParamById(String id) throws SystemException;

	/**
	 * prepare sop weekN output data
	 *
	 * @param paramInfo
	 * @param response
	 *
	 * @throws SystemException
	 */
	void populateSopWeekNOutputParam(DPSopWeekNParamInfo paramInfo, Map response) throws SystemException;

	/**
	 *
	 * @param sopWeekNParamEntryInfo
	 * @param userSelectedDate
	 * @param response
	 * @throws SystemException
	 */
	void sopWeekNDownloadFromHubzu(DPSopWeekNParamEntryInfo sopWeekNParamEntryInfo, Long userSelectedDate, HttpServletResponse response) throws SystemException;

	/**
	 *
	 * @param id
	 * @param type
	 * @return
	 * @throws SystemException
	 */
	void downloadSopWeekNZip(String id, DPSopWeekNParamEntryInfo sopWeekNParamEntryInfo, HttpServletResponse response, String type) throws SystemException;

	/**
	 * @param infoObject
	 * @throws SystemException
	 */
	void getHubzuData(DPSopWeekNParamEntryInfo infoObject) throws SystemException;

	/**
	 * @param infoObject
	 * @throws SystemException
	 */
	void getStage5Data(DPSopWeekNParamEntryInfo infoObject) throws SystemException;
	
	/**
	 * @param fileId
	 * @param type
	 * @return
	 * @throws SystemException
	 */
	List<DPSopWeekNParamInfo> getAssetDetails(String fileId, String type) throws SystemException;
	
	/**
	 * @param infoObject
	 */
	void processSopQAReportHubzuData(DPSopWeekNParamEntryInfo infoObject);
	
	/**
	 * @param dPSopWeekNParamEntryInfo
	 */
	void filterSOPQAReportAssigment(DPSopWeekNParamEntryInfo dPSopWeekNParamEntryInfo);
	
	/**
	 * @param infoObject
	 */
	void filterSopQAReportState(DPSopWeekNParamEntryInfo infoObject);
	
	/**
	 * @param infoObject
	 */
	void filterSopQAReportSSPmi(DPSopWeekNParamEntryInfo infoObject);
}