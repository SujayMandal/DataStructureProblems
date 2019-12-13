package com.fa.dp.business.sop.week0.bo;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0ProcessStatus;
import com.fa.dp.business.sop.week0.input.info.DPSopParamEntryInfo;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNProcessStatusInfo;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.info.DashboardFilterInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.KeyValue;

/**
 * @author misprakh
 * <p>
 * DP SOP process status BO interface
 */
public interface DPSopProcessBO {
	/**
	 * Save uploaded files  into SOP week 0 Process Status table
	 *
	 * @param processStatus
	 * @throws SystemException
	 */
	void saveDPSopProcessStatus(DPSopWeek0ProcessStatus processStatus) throws SystemException;

	/**
	 * Retrieve file in SOP week 0 table for given file id
	 *
	 * @param id
	 * @return
	 */
	DPSopWeek0ProcessStatus findDpSopWeek0ProcessStatusById(String id) throws SystemException;

	/**
	 * @param fileId
	 * @return
	 * @throws SystemException
	 */
	List<String> findFailedStepCommands(String fileId) throws SystemException;

	/**
	 * Retrieve Assets from SOP week 0 table for given file Id
	 *
	 * @param id
	 * @param sopWeek0
	 * @return
	 */
	List<?> getSopAssetsByFileId(String id, String sopWeek0) throws SystemException;

	/**
	 * @param dpSopParamEntryInfo
	 * @throws SystemException
	 */
	void saveFileEntriesInDB(DPSopParamEntryInfo dpSopParamEntryInfo) throws SystemException;

	/**
	 * @param fileStatus
	 * @return
	 * @throws SystemException
	 */
	DPSopWeek0ProcessStatus findFileByStatus(String fileStatus) throws SystemException;

	/**
	 * find sop weekn process status list for particular status
	 *
	 * @param fileStatus
	 * @return
	 * @throws SystemException
	 */
	List<DPSopWeekNProcessStatusInfo> findSopWeekNFileStatus(String fileStatus) throws SystemException;

	/**
	 * @param inputParamEntry
	 * @param filterName
	 * @return
	 * @throws SystemException
	 */
	KeyValue<List<DPSopWeek0ParamInfo>, List<DPSopWeek0ParamInfo>> filterOnAssetValue(DPSopParamEntryInfo inputParamEntry, String filterName)
			throws SystemException;

	/**
	 * @param in
	 * @return
	 */
	DPSopWeek0Param saveDPSopWeek0Param(DPSopWeek0Param in) throws SystemException;

	/**
	 * @param columnEntry
	 * @param dpProcessParams
	 * @param filterName
	 * @return
	 */
	DPSopWeek0ParamInfo filterOnDuplicates(DPSopWeek0ParamInfo columnEntry, List<DPSopWeek0ParamInfo> dpProcessParams, String filterName);

	/**
	 * @param assetNumber
	 * @return
	 */
	List<DPSopWeek0Param> searchByAssetNumber(String assetNumber) throws SystemException;

	/**
	 * @param state
	 * @param valueOf
	 * @param valueOf1
	 * @param value
	 * @param classification
	 * @param sopWeek0dbInitial
	 * @return
	 * @throws SystemException
	 */
	List<DPSopWeek0Param> countAssignmentByStateAndAssetNumber(String state, BigDecimal valueOf, BigDecimal valueOf1, String value,
			String classification, String sopWeek0dbInitial) throws SystemException;

	/**
	 * @param dpSopParamEntryInfo
	 * @throws SystemException
	 */
	void checkSopWeek0Migration(DPSopParamEntryInfo dpSopParamEntryInfo) throws SystemException;

	/**
	 * @param recordsToSave
	 * @return
	 * @throws SystemException
	 */
	List<DPSopWeek0ParamInfo> saveFailedRecord(List<DPSopWeek0ParamInfo> recordsToSave) throws SystemException;

	/**
	 * @return List<DPProcessWeekNParamInfo>
	 * @throws SystemException
	 */
	List<DPSopWeek0ParamInfo> getSOPWeek0Data() throws SystemException;

	/**
	 * @param id
	 * @return List of DPProcessParam for process status id
	 */
	List<DPSopWeek0Param> findDPSOPWeek0ProcessParamByProcessID(String id);

	/**
	 * @return String
	 * @throws SystemException
	 */
	void generateSOPWeek0OutputFile(List<DPSopWeek0Param> listOfDPSOPWeek0ProcessParamOCN, List<DPSopWeek0Param> listOfDPSOPWeek0ProcessParamNRZ,
			List<DPSopWeek0Param> listOfDPSOPWeek0ProcessParamPHH, HttpServletResponse httpResponse, String zipFileName) throws SystemException;

	/**
	 * @return List<DPSopWeek0ParamInfo>
	 * @throws SystemException
	 */
	List<DPSopWeek0ParamInfo> getAssetDetails(String fileId, String type) throws SystemException;

	List<DPSopWeek0ParamInfo> getWeekZeroFilteredFiles(DashboardFilterInfo dashboardFilterInfo) throws SystemException;

	/**
	 * Save sop weekn process status
	 *
	 * @param sopWeekNProcessStatusInfo
	 * @return
	 * @throws SystemException
	 */
	DPSopWeekNProcessStatusInfo saveSopWeekNProcessData(DPSopWeekNProcessStatusInfo sopWeekNProcessStatusInfo) throws SystemException;

	/**
	 * Save sop weekn param entry into DB
	 *
	 * @param columnEntries
	 * @return
	 * @throws SystemException
	 */
	List<DPSopWeekNParamInfo> saveSopWeekNParams(List<DPSopWeekNParamInfo> columnEntries) throws SystemException;

	/**
	 * fetch sop weekn param data from excel file
	 *
	 * @param file
	 * @return
	 * @throws SystemException
	 */
	List<DPSopWeekNParamInfo> fetchSopWeekNDataBySheet(MultipartFile file) throws SystemException;

	/**
	 * find sop weekn params data by file id.
	 *
	 * @param fileId
	 * @return
	 * @throws SystemException
	 */
	List<DPSopWeekNParamInfo> findSopWeekNParamsData(String fileId) throws SystemException;

	/**
	 * update sop weekn processing status
	 *
	 * @param id
	 * @param fileStatus
	 * @throws SystemException
	 */
	void updateSopWeeknRunningStatus(String id, String fileStatus) throws SystemException;
	
	/**
	 * @param selrPropIdVcNn
	 * @return
	 * @throws SystemException
	 */
	DPSopWeek0Param findInSOPWeek0ForAssetNumber(String selrPropIdVcNn) throws SystemException;

	DPSopWeek0Param findOcwenLoanByAssetNumber(String assetNumber);

	DPSopWeek0Param findOutOfScopeLoanByAssetNumber(String assetNumber);
}
