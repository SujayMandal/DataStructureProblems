package com.fa.dp.rest;

import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.DateTime;
import org.slf4j.MDC;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fa.dp.business.command.master.CommandMaster;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.info.Response;
import com.fa.dp.business.sop.validator.delegate.DPSopFileDelegate;
import com.fa.dp.business.sop.week0.bo.DPSopProcessBOImpl;
import com.fa.dp.business.sop.week0.delegate.DPSopProcessFilterDelegate;
import com.fa.dp.business.sop.week0.delegate.DPSopProcessReportDelegate;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0ProcessStatus;
import com.fa.dp.business.sop.week0.input.info.DPSopParamEntryInfo;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.week0.input.mapper.DPSopWeek0ProcessStatusMapper;
import com.fa.dp.business.sop.week0.pojo.SOPWeek0UploadResponse;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.business.validation.file.util.InputFileValidationUtil;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.systemparam.provider.SystemParameterProvider;
import com.fa.dp.core.systemparam.util.SystemParameterConstant;
import com.fa.dp.core.util.RAClientConstants;
import com.fa.dp.localization.MessageContainer;
import com.fa.dp.rest.response.RestResponse;

@Slf4j
@RestController
public class SOPWeek0Controller {

	@Inject
	@Named("dpCommandMaster")
	private CommandMaster commandMaster;

	@Inject
	private DPSopProcessFilterDelegate dpSopProcessFilterDelegate;

	@Inject
	private DPSopProcessBOImpl dpSopFileProcessBO;

	@Inject
	private DPSopWeek0ProcessStatusMapper dpSopWeek0ProcessStatusMapper;
	
	@Inject
	private DPSopFileDelegate dpSopFileDelegate;
	
	@Inject
	private DPSopProcessReportDelegate dpSopProcessReportDelegate;
	
	@Inject
	private SystemParameterProvider systemParameterProvider;

	@PostMapping(value = "/processSopWeek0File")
	public RestResponse processFile(@RequestBody(required = false) DPSopParamEntryInfo dpSopParamEntryInfo, @RequestParam(required = false) String fileId) throws SystemException {
		RestResponse<Response> response = new RestResponse<Response>();
		DPSopParamEntryInfo sopParamEntryInfo = new DPSopParamEntryInfo();
		AsyncListenableTaskExecutor delegateExecutor = new SimpleAsyncTaskExecutor();
		DelegatingSecurityContextExecutor executor = new DelegatingSecurityContextExecutor(delegateExecutor, SecurityContextHolder.getContext());

		if (ObjectUtils.isEmpty(dpSopParamEntryInfo) && (null == fileId || fileId.isEmpty())) {
			response.setSuccess(false);
			response.setMessage(DPAConstants.EMPTY_FILE);
			return response;
		}
		try {
			MDC.put(RAClientConstants.PRODUCT_TYPE, DPAConstants.SOP_WEEK0);
			MDC.put(RAClientConstants.APP_CODE, "dpa");
			DPSopWeek0ProcessStatus sopWeek0ProcessStatus = dpSopProcessFilterDelegate.checkForFileStatus(DPFileProcessStatus.IN_PROGRESS.getFileStatus());
			if (!ObjectUtils.isEmpty(sopWeek0ProcessStatus)) {
				response.setSuccess(false);
				response.setMessage(MessageContainer.getMessage(CoreExceptionCodes.DP017, new Object[]{}));
				return response;
			}
			log.info("File Processing for Week0 started");
			if (null == dpSopParamEntryInfo && !fileId.isEmpty()) {
				try {
					List<DPSopWeek0ParamInfo> columnEntries = (List<DPSopWeek0ParamInfo>) dpSopFileProcessBO.getSopAssetsByFileId(fileId, DPAConstants.SOP_WEEK0);
					sopParamEntryInfo.setColumnEntries(columnEntries);
					sopParamEntryInfo.setDpSopWeek0ProcessStatusInfo(columnEntries.get(0).getSopWeek0ProcessStatus());
					dpSopParamEntryInfo = sopParamEntryInfo;
				} catch (SystemException e) {
					log.error("Exception while storing file : {}" , e);
					response.setSuccess(false);
					response.setMessage(e.getMessage());
					return response;
				}
			} else if (!dpSopParamEntryInfo.isReprocess()) {
				dpSopFileProcessBO.saveFileEntriesInDB(dpSopParamEntryInfo);
			}
			dpSopParamEntryInfo.getDpSopWeek0ProcessStatusInfo().setStatus(DPFileProcessStatus.IN_PROGRESS.getFileStatus());
			dpSopFileProcessBO.saveDPSopProcessStatus(dpSopWeek0ProcessStatusMapper.mapInfoToDomain(dpSopParamEntryInfo.getDpSopWeek0ProcessStatusInfo()));
			final Map<String, String> mdcContext = MDC.getCopyOfContextMap();
			final DPSopParamEntryInfo finalSopParamEntryInfo = dpSopParamEntryInfo;

			executor.execute(new Runnable() {
				@Override
				public void run() {
					if (mdcContext != null)
						MDC.setContextMap(mdcContext);
					try {
						commandMaster.prepareSopWeek0(finalSopParamEntryInfo);
					} catch (SystemException e) {
						log.error("Exception while processing file : {}" , e);
					}
				}
			});
			response.setSuccess(true);
			response.setMessage(MessageContainer.getMessage(CoreExceptionCodes.DP041, new Object[]{}));

		} catch (SystemException se) {
			log.error("System exception occurred while processing input file ");
			dpSopParamEntryInfo.getDpSopWeek0ProcessStatusInfo().setStatus(DPFileProcessStatus.ERROR.getFileStatus());
			dpSopFileProcessBO.saveDPSopProcessStatus(dpSopWeek0ProcessStatusMapper.mapInfoToDomain(dpSopParamEntryInfo.getDpSopWeek0ProcessStatusInfo()));
			response.setSuccess(false);
			response.setMessage("Exception occurred while processing Week0 input file ");
		} catch (Exception e) {
			log.error("System exception occurred while processing input file : ", e);
			dpSopParamEntryInfo.getDpSopWeek0ProcessStatusInfo().setStatus(DPFileProcessStatus.ERROR.getFileStatus());
			dpSopFileProcessBO.saveDPSopProcessStatus(dpSopWeek0ProcessStatusMapper.mapInfoToDomain(dpSopParamEntryInfo.getDpSopWeek0ProcessStatusInfo()));
			response.setSuccess(false);
			response.setMessage("Exception occurred while processing Week0 input file ");
		}
		return response;
	}
	
	@PostMapping(value = "/uploadSopFile")
	public RestResponse<SOPWeek0UploadResponse> uploadFile(@RequestParam(value = "file") MultipartFile file) {
		log.info("SOP Week0 Input file validation controller begins");
		RestResponse<SOPWeek0UploadResponse> response = new RestResponse<SOPWeek0UploadResponse>();
		SOPWeek0UploadResponse respObj = new SOPWeek0UploadResponse();
		List<String> errorMessages = new ArrayList<>();
		Long startTime;
		Long endTime;
		Boolean dataLevelError = false;

		if (!file.isEmpty()) {
			try {
				MDC.put(RAClientConstants.PRODUCT_TYPE, DPAConstants.SOP_WEEK0);
				MDC.put(RAClientConstants.APP_CODE, "dpa");
				String generatedFileName = InputFileValidationUtil.generateFileName(file.getOriginalFilename());
				log.info("SOP Week0 Input file validation controller. generated file name : " + generatedFileName);
				final DPSopParamEntryInfo dpSopParamEntryInfo;

				try {
					startTime = DateTime.now().getMillis();
					dpSopParamEntryInfo = dpSopFileDelegate.validateFile(file, generatedFileName, errorMessages);
					endTime = DateTime.now().getMillis() - startTime;
					log.info("Time taken for validation : " + endTime);

					if (null != dpSopParamEntryInfo) {
						respObj.setDpSopParamEntryInfo(dpSopParamEntryInfo);
						dataLevelError = dpSopParamEntryInfo.isDataLevelError();
					}
				} catch (SystemException e) {
					log.error("Exception while validating file : {}" , e);
					errorMessages.add(e.getLocalizedMessage());
				}

				if (errorMessages != null && errorMessages.size() > 0) {
					response.setSuccess(false);
				} else {
					try {
						startTime = DateTime.now().getMillis();
						String sanBase = systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_SAN_PATH);
						InputFileValidationUtil.createFile(file, generatedFileName, sanBase);
						endTime = DateTime.now().getMillis() - startTime;
						log.info("Time taken for file creation : " + endTime);
						response.setSuccess(true);
						response.setMessage(MessageContainer.getMessage(CoreExceptionCodes.DP040,
								  new Object[]{file.getOriginalFilename()}));
					} catch (SystemException e) {
						log.error("Exception while creating file : {}" , e);
						response.setSuccess(false);
						errorMessages.add(e.getLocalizedMessage());
					}
				}
			} catch (Exception e) {
				log.error("Exception while uploading file : {}" , e);
				response.setSuccess(false);
				response.setMessage("Exception occurred while uploading SOP Week0 input file");
			} finally {
				MDC.remove(RAClientConstants.APP_CODE);
				MDC.remove(RAClientConstants.PRODUCT_TYPE);
			}
		} else {
			response.setSuccess(false);
			errorMessages.add(MessageContainer.getMessage(CoreExceptionCodes.DP042, new Object[]{}));
		}

		respObj.setErrorMessages(errorMessages);
		respObj.setDataError(dataLevelError);
		response.setResponse(respObj);

		log.info("Input file validation controller ends");
		return response;
	}
	
	@GetMapping(value = "/downloadSOPWeek0Report")
    public void getSOPWeek0Report(String fileId, String type, HttpServletResponse response) {
        log.info("download SOP Week 0 Report controller begins");
        try {
        	dpSopProcessReportDelegate.downloadSOPWeek0Reports(fileId, type, response);
            log.info("Download report execution success.");
        } catch (SystemException se) {
            log.error("Exception occurred while downloading File " ,se);
            if (se.getLocalizedMessage() != null) {
                writeErrorData(response, fileId, se.getLocalizedMessage());
            } else {
                writeErrorData(response, fileId, se.getMessage());
            }
        } catch (Exception e) {
            log.error("Exception occurred while downloading File " , e);
            if (e.getLocalizedMessage() != null) {
                writeErrorData(response, fileId, DPAConstants.UNABLE_TO_DOWNLOAD_FILE);
            } else {
                writeErrorData(response, fileId, DPAConstants.UNABLE_TO_DOWNLOAD_FILE);
            }
        } finally {
            try {
                if (response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error(DPAConstants.OUTPUTSTREAM_ERROR + fileId , e);
            }
        }
        log.info("download Report controller ends");
    }
	
	@GetMapping(value = "/getSopWeek0AssetDetails")
    public RestResponse getSopWeek0AssetDetails(String fileId, String weekType) {
        log.info("SOP Week 0 Asset details  begins");
        RestResponse<List<DPSopWeek0ParamInfo>> response = new RestResponse<>();
        List<DPSopWeek0ParamInfo> listOfDPSOPWeek0ProcessParamInfo = new ArrayList<>();
        try {
        	listOfDPSOPWeek0ProcessParamInfo = dpSopProcessFilterDelegate.getAssetDetails(fileId, weekType);
            response.setSuccess(true);
            response.setMessage("SOP Week 0 Asset details  successful.");
            response.setErrorCode(null);
            response.setResponse(listOfDPSOPWeek0ProcessParamInfo);
            log.info("SOP Week 0 Asset details execution success.");
        } catch (SystemException se) {
            log.error("SOP Week 0  Asset details  failed with exception : " , se);
            response.setSuccess(false);
            response.setMessage("System Exception - SOP Week 0 Asset details  failed.");
            response.setErrorCode(se.getCode());
        } catch (Exception e) {
            log.error("SOP Week 0 Asset details  failed with exception : " , e);
            response.setSuccess(false);
            response.setMessage("Asset details  failed with exception : " + e.getMessage());
        }
        log.info("SOP Week 0 Asset details controller ends");
        return response;
    }

	private void writeErrorData(final HttpServletResponse response, final String fileId, final String msg) {
        try {
            final String headerValue = format("attachment; filename=\"%s\"", "error_" + fileId + ".txt");
            response.setHeader("Content-Disposition", headerValue);
            response.setHeader("Access-Control-Expose-Headers","Content-Disposition");
            String errorMsg = null;
            if (msg == null) {
                errorMsg = "No Data found for the fileId :" + fileId;
                response.getOutputStream().write(errorMsg.getBytes());
            } else {
                errorMsg = msg;
                response.getOutputStream().write(errorMsg.getBytes());
            }
        } catch (IOException excep) {
            log.error("Error while Writing error data  ", excep);
        }
    }
	
}