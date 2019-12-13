package com.fa.dp.business.sop.validator.delegate;

import com.fa.dp.business.sop.week0.input.info.DPSopParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNProcessStatusInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.business.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author misprakh
 * <p>
 * Interface for validation and file upload service for SOP system
 */
public interface DPSopFileDelegate {

	/**
	 * @param infoObject
	 *
	 * @return String
	 *
	 * @throws SystemException
	 */
	String setStatus(DPSopParamEntryInfo infoObject) throws SystemException;

	/**
	 * File validation operation
	 *
	 * @param file
	 * @param generatedFileName
	 * @param errorMessages
	 *
	 * @return
	 *
	 * @throws SystemException
	 * @throws IOException
	 */
	DPSopParamEntryInfo validateFile(MultipartFile file, String generatedFileName, List<String> errorMessages) throws SystemException;

	/**
	 * Find list of sop weekn process file list for particular status value
	 *
	 * @param fileStatus
	 *
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void checkForFileStatus(String fileStatus) throws BusinessException, SystemException;

	/**
	 * save sop weekn params
	 *
	 * @param dpSopWeekNParamEntryInfo
	 *
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void saveSopWeekNProcess(DPSopWeekNParamEntryInfo dpSopWeekNParamEntryInfo) throws BusinessException, SystemException;

	/**
	 * Fetch sop weekN params data for particular file proces by using process sattus id.
	 *
	 * @param fileId
	 *
	 * @return
	 *
	 * @throws BusinessException
	 * @throws SystemException
	 */
	List<DPSopWeekNParamInfo> findSopWeekNParamsData(String fileId) throws BusinessException, SystemException;

	/**
	 * fetch weekn sop params by excel file
	 *
	 * @param file
	 *
	 * @return
	 */
	List<DPSopWeekNParamInfo> getSOPWeekNParams(MultipartFile file) throws BusinessException;

	/**
	 * UPLOAD WEEKN EXCEL FILE BUSINESS LOGIC
	 *
	 * @param originalFilename
	 * @param listOfDPProcessWeekNParamInfos
	 *
	 * @return
	 *
	 * @throws BusinessException
	 */
	List<DPSopWeekNParamInfo> uploadSopWeekNExcel(String originalFilename, List<DPSopWeekNParamInfo> listOfDPProcessWeekNParamInfos)
			throws BusinessException;

	/**
	 * update sop weekn processing status
	 *
	 * @param dpSopWeekNProcessStatus
	 * @param fileStatus
	 *
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void updateSopWeeknRunningStatus(DPSopWeekNProcessStatusInfo dpSopWeekNProcessStatus, String fileStatus)
			throws BusinessException, SystemException;

	/**
	 * SOP WeekN command process
	 *
	 * @param dpSopWeekNParamEntryInfo
	 * @param id
	 */
	void sopWeekNProcessCommand(DPSopWeekNParamEntryInfo dpSopWeekNParamEntryInfo, String id) throws SystemException;

}
