package com.fa.dp.business.validator.bo;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.entity.DynamicPricingFilePrcsStatus;
import com.fa.dp.business.week0.entity.DynamicPricingIntgAudit;
import com.fa.dp.business.week0.info.DashboardFilterInfo;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author yogeshku
 * <p>
 * DP process status BO interface
 */
public interface DPFileProcessBO {

	/**
	 * @param id
	 * @return find DPProcessStatus by id
	 */
	DynamicPricingFilePrcsStatus findDPProcessStatusById(String id);

	/**
	 * @param dynamicPricingFilePrcsStatus
	 * @return save DPProcessStatus entity
	 */
	DynamicPricingFilePrcsStatus saveDPProcessStatus(DynamicPricingFilePrcsStatus dynamicPricingFilePrcsStatus);

	/**
	 * @param id
	 * @return find DPProcessParam by id
	 */
	DPProcessParam findDPProcessParamById(String id);

	/**
	 * @param dynamicPricingInputParam
	 * @return save DPProcessParam entity
	 */
	DPProcessParam saveDPProcessParam(DPProcessParam dynamicPricingInputParam);

	/**
	 * @param dynamicPricingInputParams
	 * @return save list of DPProcessParam entities
	 */
	List<DPProcessParam> saveAllDPProcessParam(List<DPProcessParam> dynamicPricingInputParams);

	/**
	 * @param dpIntgAudit
	 * @return
	 */
	DynamicPricingIntgAudit saveDPProcessIntgAudit(DynamicPricingIntgAudit dpIntgAudit);

	/**
	 * @param state
	 * @param lowerAssetValue
	 * @param higherAssetValue
	 * @param classification
	 * @return
	 */
	int countModeled(String state, int lowerAssetValue, int higherAssetValue, String classification);

	/**
	 * @param state
	 * @param lowerAssetValue
	 * @param higherAssetValue
	 * @param classification
	 * @return Total Benchmark for asset number and state
	 */
	int countBenchmark(String state, int lowerAssetValue, int higherAssetValue, String classification);

	/**
	 * @param status
	 * @return
	 */
	DynamicPricingFilePrcsStatus findDPProcessStatusByStatus(String status);

	/**
	 * @param id
	 * @return List of DPProcessParam for process status id
	 */
	List<DPProcessParam> findDPProcessParamByProcessID(String id);

	/**
	 * @param id
	 */
	void updateDPProcessParamEligibleByID(String id, String eligibility);

	/**
	 * @param id
	 * @param notes
	 */
	void saveDPProcessNotes(String id, String notes);

	/**
	 * @param id
	 * @param rtSource
	 */
	void saveDPProcessRTSource(String id, String rtSource);

	/**
	 * @param id
	 * @param error
	 */
	void saveDPProcessErrorDetail(String id, String error);

	/**
	 * @param id
	 * @param type
	 * @param errorDetail
	 * @param exception
	 * @return
	 */
	String saveDPProcessErrorDetail(String id, String type, String errorDetail, Exception exception);

	/**
	 * @return List<DPFileProcessStatusInfo>
	 * @throws SystemException
	 */
	List<DPProcessParamInfo> getWeekZeroAllUploadedFiles() throws SystemException;

	List<DPProcessParamInfo> getWeekZeroFilteredFiles(DashboardFilterInfo dashboardFilterInfo) throws SystemException;

	/**
	 * @return List<DPFileProcessStatusInfo>
	 * @throws SystemException
	 *//*
	List<DPProcessWeekNParamInfo> getWeekNAllUploadedFiles() throws SystemException;
*/

	/**
	 * @return List<Map   <   String   ,   DPDashboardParamInfo>>
	 * @throws SystemException
	 */
	List<DPDashboardParamInfo> getDashboardParams(List<DPDashboardParamInfo> listOfDPDashboardParamInfo, Map map) throws SystemException;

	/**
	 * @return List<Map < String , DPDashboardParamInfo>>
	 * @throws SystemException
	 */
/*
    List<DPDashboardParamInfo> getWeekNDashboardParams(List<DPDashboardParamInfo> listOfDPDashboardParamInfo) throws SystemException;
*/

	/**
	 * @return List<DPProcessParamInfo>
	 * @throws SystemException
	 */
	List<DPProcessParamInfo> getAssetDetails(String fileId, String type) throws SystemException;

	/**
	 * @return List<Command>
	 */
	List<Command> findfailedStepCommands(String fileId);

	/**
	 * @return List<DPProcessParam>
	 * @throws SystemException
	 */
	List<DPProcessParam> fetchFilesDetails() throws SystemException;

	/**
	 * @return String
	 * @throws SystemException
	 */
	void generateOutputFile(List<DPProcessParam> listOfDPProcessParamOCN, List<DPProcessParam> listOfDPProcessParamNRZ,
			List<DPProcessParam> listOfDPProcessParamPHH, HttpServletResponse httpResponse, String zipFileName) throws SystemException;

	/**
	 * @return List<DPProcessWeekNParamInfo>
	 * @throws SystemException
	 */
	List<DPProcessWeekNParamInfo> getWeekNData() throws SystemException;

	List<DPProcessWeekNParamInfo> getWeekNFilteredData(DashboardFilterInfo dashboardFilterInfo) throws SystemException;

	/**
	 * @param weekNId
	 * @param weekType
	 * @return List<DPProcessWeekNParamInfo>
	 * @throws SystemException List of DPProcessParam for process status id
	 */
	List<DPProcessWeekNParamInfo> getWeekNAssetDetails(String weekNId, String weekType) throws SystemException;

	/**
	 * @param id
	 * @return List of DPProcessParam for process status id
	 */
	List<DPProcessWeekNParam> findDPProcessWeekNParamByProcessID(String id) throws SystemException;

	/**
	 * @param id
	 * @return find DPWeekNProcessStatus by id
	 */
	DPWeekNProcessStatus findDPWeekNProcessStatusById(String id);

	DPWeekNProcessStatus saveDPProcessWeekNStatus(DPWeekNProcessStatus dpWeeknProcessStatus);

	DPWeekNProcessStatus findDPWeekNProcessById(String Id);

	void updateWeeknPrcsStatus(String status, String id);

	List<Command> findFailedStepCommandsWeekn(String processId);

	DPWeekNProcessStatus findWeeknPrcsStatusByStatus(String fileStatus);

	List<DPProcessParam> findByAssetNumberAndEligibleOrderByCreatedDateDesc(String assetNumber, String eligible);

	List<DPProcessParam> findLatestNonDuplicateInWeek0ForGivenAsset(String assetNumber);

	/**
	 * Fetch latest weekn run date
	 * @return
	 */
	LocalDate getLatestWekNRunDate();

	Map<String,DPProcessParam> findLatestNonDuplicateInWeek0ForGivenAssetList(List<String> assetNumbers);
}