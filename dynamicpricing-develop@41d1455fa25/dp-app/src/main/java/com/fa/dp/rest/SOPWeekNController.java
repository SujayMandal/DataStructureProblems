package com.fa.dp.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fa.dp.business.command.master.CommandMaster;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.sop.validator.delegate.DPSopFileDelegate;
import com.fa.dp.business.sop.weekN.delegate.DPSopWeekNParamDelegate;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.sop.weekN.response.SOPWeekNUploadResponse;
import com.fa.dp.business.sop.weekN.util.SopWeekNFileUtil;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.business.BusinessException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.systemparam.util.AppType;
import com.fa.dp.core.util.RAClientConstants;
import com.fa.dp.localization.MessageContainer;
import com.fa.dp.rest.response.RestResponse;

/**
 * @author misprakh
 *
 */

@RestController
@Slf4j
public class SOPWeekNController {

	@Inject
	@Named("dpCommandMaster")
	private CommandMaster commandMaster;

	@Inject
	private DPSopWeekNParamDelegate dpSopWeekNParamDelegate;

	@Inject
	private SopWeekNFileUtil sopWeekNFileUtil;

	@Inject
	private DPSopFileDelegate sopFileDelegate;

	/**
	 *
	 * @param selectedDateMillis
	 * @return
	 */
	@GetMapping(value = "/fetchSopWeekNHubzu")
	public RestResponse<DPSopWeekNParamEntryInfo> fetchSopWeekNFromHubzu(@RequestParam(required = true) Long selectedDateMillis) {
		RestResponse<DPSopWeekNParamEntryInfo> response = new RestResponse<>();
		try {
			MDC.put(RAClientConstants.APP_CODE, AppType.DPA.getAppCode());
			MDC.put(RAClientConstants.PRODUCT_TYPE, DPAConstants.SOP_WEEKN);
			DPSopWeekNParamEntryInfo sopWeekNParamEntryInfo = dpSopWeekNParamDelegate.fetchSopWeekNFromHubzu(selectedDateMillis);
			//calling the weekn flow for step1
			if(!CollectionUtils.isEmpty(sopWeekNParamEntryInfo.getColumnEntries())){
				sopWeekNParamEntryInfo.setFetchProcess(Boolean.TRUE);
				commandMaster.prepareSopWeekN(sopWeekNParamEntryInfo);
				response.setSuccess(Boolean.TRUE);
				response.setResponse(sopWeekNParamEntryInfo);
			} else {
				log.error("No record found in hubzu for the selected date");
				sopWeekNParamEntryInfo.setFetchProcess(Boolean.TRUE);
				response.setSuccess(Boolean.FALSE);
				response.setMessage("No record found in hubzu for the selected date");
			}
		} catch (SystemException se) {
			log.error("System exception - SOP week N :  While retrieving records from hubzu");
			response.setSuccess(Boolean.FALSE);
			response.setMessage(se.getLocalizedMessage());
			response.setErrorCode(se.getCode());
		} catch (Exception e) {
			log.error("Exception - SOP week N :  While retrieving records from hubzu", e);
			response.setSuccess(Boolean.FALSE);
			response.setMessage("Exception - SOP week N :  While retrieving records from hubzu");
		} finally {
			MDC.remove(RAClientConstants.APP_CODE);
			MDC.remove(RAClientConstants.PRODUCT_TYPE);
		}
		return response;
	}

	@PostMapping(value = "/uploadSopWeekNFile")
	public RestResponse<List<DPSopWeekNParamInfo>> uploadFile(@RequestParam(value = "file") MultipartFile file) throws BusinessException {
		log.info("Upload Week n sop downloaded excel file  begin");
		MDC.put(RAClientConstants.APP_CODE, AppType.DPA.getAppCode());
		MDC.put(RAClientConstants.PRODUCT_TYPE, DPAConstants.SOP_WEEKN);
		RestResponse<List<DPSopWeekNParamInfo>> response = new RestResponse<>();
		List<DPSopWeekNParamInfo> listOfDPProcessWeekNParamInfos;
		List<DPSopWeekNParamInfo> savedRecords;
		Long startTime;
		String successMessage = null;
		try {
			startTime = DateTime.now().getMillis();
			listOfDPProcessWeekNParamInfos = sopFileDelegate.getSOPWeekNParams(file);
			savedRecords = sopFileDelegate.uploadSopWeekNExcel(file.getOriginalFilename(), listOfDPProcessWeekNParamInfos);
			log.info("Time taken for week n sop file upload : " + (DateTime.now().getMillis() - startTime) + " millis");
			successMessage = "Save Successful, " + savedRecords.size() + " records inserted";
			response.setSuccess(true);
			response.setMessage(successMessage);
			response.setResponse(savedRecords);
		} catch (IllegalStateException e) {
			log.error("Exception caught while reading the file.", e);
			response.setSuccess(false);
			response.setMessage(MessageContainer.getMessage(CoreExceptionCodes.DPSOPWKN028, StringUtils.substringBetween(e.getLocalizedMessage(), "key ", "-")));
		} catch (Exception e) {
			log.error("Exception caught while reading the file.", e);
			response.setSuccess(false);
			response.setMessage(MessageContainer.getMessage(CoreExceptionCodes.DPSOPWKN029));
		}  finally {
			MDC.remove(RAClientConstants.APP_CODE);
			MDC.remove(RAClientConstants.PRODUCT_TYPE);
		}
		log.info("Upload Week n sop downloaded excel file  ends");
		return response;
	}

	/**
	 * @param dpSopWeekNParamEntryInfo
	 * @param fileId
	 *
	 * @return
	 *
	 * @throws BusinessException
	 * @throws SystemException
	 */
	@PostMapping(value = "/processSopWeekNFile")
	public RestResponse<SOPWeekNUploadResponse> processFile(@RequestBody(required = false) DPSopWeekNParamEntryInfo dpSopWeekNParamEntryInfo,
			@RequestParam(required = false) String fileId) throws BusinessException, SystemException {
		RestResponse<SOPWeekNUploadResponse> response = new RestResponse<>();

		if(ObjectUtils.isEmpty(dpSopWeekNParamEntryInfo) && StringUtils.isEmpty(fileId)) {
			response.setSuccess(false);
			response.setMessage(DPAConstants.EMPTY_FILE);
			return response;
		} else if(dpSopWeekNParamEntryInfo.getDpSopWeekNProcessStatus() == null && !ObjectUtils.isEmpty(dpSopWeekNParamEntryInfo.getColumnEntries())){
			dpSopWeekNParamEntryInfo.setDpSopWeekNProcessStatus(dpSopWeekNParamEntryInfo.getColumnEntries().get(0).getSopWeekNProcessStatus());
		}

		MDC.put(RAClientConstants.PRODUCT_TYPE, DPAConstants.SOP_WEEKN);
		MDC.put(RAClientConstants.APP_CODE, AppType.DPA.getAppCode());

		//checking if any file in progress status
		sopFileDelegate.checkForFileStatus(DPFileProcessStatus.IN_PROGRESS.getFileStatus());
		log.info("SOP Weekn file processing statrted.");

		if(ObjectUtils.isEmpty(dpSopWeekNParamEntryInfo) && StringUtils.isNotEmpty(fileId)) {
			List<DPSopWeekNParamInfo> columnEntries = sopFileDelegate.findSopWeekNParamsData(fileId);
			dpSopWeekNParamEntryInfo = new DPSopWeekNParamEntryInfo();
			dpSopWeekNParamEntryInfo.setColumnEntries(columnEntries);
			dpSopWeekNParamEntryInfo.setDpSopWeekNProcessStatus(columnEntries.get(0).getSopWeekNProcessStatus());
		}
		if(!dpSopWeekNParamEntryInfo.isReprocess()) {
			sopFileDelegate.saveSopWeekNProcess(dpSopWeekNParamEntryInfo);
		}
		final String id = StringUtils.equalsIgnoreCase(fileId, DPAConstants.NULL) ? dpSopWeekNParamEntryInfo.getDpSopWeekNProcessStatus().getId() : fileId;
		sopFileDelegate.sopWeekNProcessCommand(dpSopWeekNParamEntryInfo, id);

		log.info("SOP Weekn file processing completed.");
		response.setSuccess(true);
		response.setMessage(MessageContainer.getMessage(CoreExceptionCodes.DP041, new Object[] {}));

		return response;
	}

	/**
	 * @param sopWeekNParamEntryInfo
	 * @param userSelectedDate
	 * @param response
	 */
	@PostMapping(value = "/sopWeekNDownloadFromHubzu")
	public void sopWeekNDownloadFromHubzu(@RequestBody DPSopWeekNParamEntryInfo sopWeekNParamEntryInfo, @RequestParam Long userSelectedDate,
			HttpServletResponse response) {
		log.info("Download sop week n step 1 begin");
		try {
			MDC.put(RAClientConstants.APP_CODE, AppType.DPA.getAppCode());
			MDC.put(RAClientConstants.PRODUCT_TYPE, DPAConstants.SOP_WEEKN);
			dpSopWeekNParamDelegate.sopWeekNDownloadFromHubzu(sopWeekNParamEntryInfo, userSelectedDate, response);
		} catch (SystemException se) {
			log.error("System exception Exception occurred while downloading excel file. : ", se);
			sopWeekNFileUtil.writeErrorData(response, se.getLocalizedMessage());

		} catch (Exception e) {
			log.error("Exception occurred while downloading excel file. :", e);
			sopWeekNFileUtil.writeErrorData(response, DPAConstants.UNABLE_TO_DOWNLOAD_FILE);
		} finally {
			try {
				if(response.getOutputStream() != null) {
					response.getOutputStream().close();
				}
			} catch (IOException e) {
				log.error(DPAConstants.OUTPUTSTREAM_ERROR);
			}
			MDC.remove(RAClientConstants.APP_CODE);
			MDC.remove(RAClientConstants.PRODUCT_TYPE);
		}
		log.info("Download sop week n step 1 end");
	}

	/**
	 * @param id
	 * @param type
	 *
	 * @return
	 */
	@GetMapping(value = "/getSopWeekNZipDownload")
	public void getSopWeekNZipDownload(String id, String type, HttpServletResponse response) {
		log.info("getSopWeekNZipDownload api begins");
		MDC.put(RAClientConstants.APP_CODE, AppType.DPA.getAppCode());
		MDC.put(RAClientConstants.PRODUCT_TYPE, DPAConstants.SOP_WEEKN);
		try {
			dpSopWeekNParamDelegate.downloadSopWeekNZip(id, null, response, type);
			log.info("getSopWeekNZipDownload report execution success.");
		} catch (SystemException se) {
			log.error("SystemException occurred while downloading File", se);
			sopWeekNFileUtil.writeErrorData(response, se.getLocalizedMessage());
		} catch (Exception e) {
			log.error("Exception occurred while downloading File", e);
			sopWeekNFileUtil.writeErrorData(response, DPAConstants.UNABLE_TO_DOWNLOAD_FILE);
		} finally {
			try {
				if(response.getOutputStream() != null)
					response.getOutputStream().close();
			} catch (IOException e) {
				log.error(DPAConstants.OUTPUTSTREAM_ERROR);
			}
			MDC.remove(RAClientConstants.APP_CODE);
			MDC.remove(RAClientConstants.PRODUCT_TYPE);
		}
		log.info("getSopWeekNZipDownload api ends");
	}
	
	/**
	 * @param fileId
	 * @param weekType
	 *
	 * @return
	 */
	@GetMapping(value = "/getSopWeekNAssetDetails")
    public RestResponse getSopWeekNAssetDetails(String fileId, String weekType) {
        log.info("SOP Week N Asset details  begins");
        RestResponse<List<DPSopWeekNParamInfo>> response = new RestResponse<>();
        List<DPSopWeekNParamInfo> listOfDPSOPWeekNProcessParamInfo = new ArrayList<>();
        try {
        	listOfDPSOPWeekNProcessParamInfo = dpSopWeekNParamDelegate.getAssetDetails(fileId, weekType);
            response.setMessage("SOP Week N Asset details  successful.");
            response.setResponse(listOfDPSOPWeekNProcessParamInfo);
            log.info("SOP Week N Asset details execution success.");
        } catch (SystemException se) {
            log.error("SOP Week N  Asset details  failed with exception : " , se);
            response.setSuccess(false);
            response.setMessage(MessageContainer.getMessage(CoreExceptionCodes.DPSOPWKN025));
            response.setErrorCode(se.getCode());
        } catch (Exception e) {
            log.error("SOP Week N Asset details  failed with exception : " , e);
            response.setSuccess(false);
            response.setMessage(MessageContainer.getMessage(CoreExceptionCodes.DPSOPWKN026, e.getMessage()));
        }
        log.info("SOP Week N Asset details controller ends");
        return response;
    }

}
