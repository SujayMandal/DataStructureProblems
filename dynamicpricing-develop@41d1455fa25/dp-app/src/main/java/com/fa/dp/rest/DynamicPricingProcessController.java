package com.fa.dp.rest;

import com.fa.dp.business.command.master.CommandMaster;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.filter.delegate.DPProcessWeekNFilterDelegate;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.delegate.DPProcessDelegate;
import com.fa.dp.business.week0.delegate.DPFileProcessDelegate;
import com.fa.dp.business.week0.entity.DynamicPricingFilePrcsStatus;
import com.fa.dp.business.weekn.delegate.WeekNDataDelegate;
import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.client.pojo.DPProcessResponse;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.util.RAClientConstants;
import com.fa.dp.localization.MessageContainer;
import com.fa.dp.rest.response.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.MDC;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class DynamicPricingProcessController {

	@Inject
	private WeekNDataDelegate weekNDataDelegate;

	@Inject
	private DPProcessDelegate dPProcessDelegate;

	@Inject
	private DPFileProcessDelegate dpFileProcessDelegate;

	@Inject
	private DPProcessWeekNFilterDelegate dpProcessWeekNFilterDelegate;

	@Inject
	@Named("dpCommandMaster")
	private CommandMaster commandMaster;

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	@ResponseBody
	public RestResponse<DPProcessResponse> uploadFile(@RequestParam(value = "file") MultipartFile file) {
		log.info("Input file validation controller begins");
		RestResponse<DPProcessResponse> response = new RestResponse<DPProcessResponse>();
		DPProcessResponse respObj = new DPProcessResponse();
		List<String> errorMessages = new ArrayList<>();

		Long startTime;
		Long endTime;
		Boolean dataLevelError = false;

		if (!file.isEmpty()) {
			try {
				String generatedFileName = dPProcessDelegate.generateFileName(file.getOriginalFilename());
				log.info("Input file validation controller. generated file name : " + generatedFileName);
				final DPProcessParamEntryInfo dpParamEntry;

				try {
					startTime = DateTime.now().getMillis();
					dpParamEntry = dPProcessDelegate.validateFile(file, generatedFileName, errorMessages);
					endTime = DateTime.now().getMillis() - startTime;
					log.info("Time taken for validation : " + endTime);

					if (null != dpParamEntry) {
						respObj.setDpProcessParamEntry(dpParamEntry);
						dataLevelError = dpParamEntry.isDataLevelError();
					}
				} catch (Exception e) {
					log.error(e.getLocalizedMessage(), e);
					errorMessages.add(e.getLocalizedMessage());
				}

				if (errorMessages != null && errorMessages.size() > 0) {
					response.setSuccess(false);
				} else {
					// copy file to server location
					try {
						startTime = DateTime.now().getMillis();
						dPProcessDelegate.createFile(file, generatedFileName);
						endTime = DateTime.now().getMillis() - startTime;
						log.info("Time taken for file creation : " + endTime);
						response.setSuccess(true);
						response.setMessage(MessageContainer.getMessage(CoreExceptionCodes.DP040, new Object[] { file.getOriginalFilename() }));
					} catch (IOException e) {
						log.error(e.getLocalizedMessage(), e);
						response.setSuccess(false);
						errorMessages.add(e.getLocalizedMessage());
					}
				}
			} catch (Exception e) {
				log.error(e.getLocalizedMessage(), e);
				response.setSuccess(false);
				response.setMessage("Exception occurred while uploading Week0 input file");
			}
		} else {
			response.setSuccess(false);
			errorMessages.add(MessageContainer.getMessage(CoreExceptionCodes.DP042, new Object[] {}));
		}

		respObj.setErrorMessages(errorMessages);
		respObj.setDataError(dataLevelError);
		response.setResponse(respObj);

		log.info("Input file validation controller ends");
		return response;
	}

	@RequestMapping(value = "/processFile", method = RequestMethod.POST)
	@ResponseBody
	public RestResponse<DPProcessResponse> processFile(@RequestBody(required = false) DPProcessParamEntryInfo dpParamEntry,
			@RequestParam(required = false) String fileId) {
		RestResponse<DPProcessResponse> response = new RestResponse<DPProcessResponse>();

		if (ObjectUtils.isEmpty(dpParamEntry) && (null == fileId || fileId.isEmpty())) {
			response.setSuccess(false);
			response.setMessage("Input cannot be null");
			return response;
		}

		try {
			MDC.put(RAClientConstants.PRODUCT_TYPE, DPAConstants.WEEK0);
			MDC.put(RAClientConstants.APP_CODE, "dpa");

			DynamicPricingFilePrcsStatus dpFilePrcsStatus = dPProcessDelegate.checkForPrcsStatus(DPFileProcessStatus.IN_PROGRESS.getFileStatus());
			if (!ObjectUtils.isEmpty(dpFilePrcsStatus)) {
				response.setSuccess(false);
				response.setMessage(MessageContainer.getMessage(CoreExceptionCodes.DP017, new Object[] {}));
				return response;
			}
			log.info("File Processing for Week0 started");
			if (null == dpParamEntry && !fileId.isEmpty()) {
				try {
					List<DPProcessParamInfo> columnEntries = dpFileProcessDelegate.getAssetDetails(fileId, DPAConstants.WEEK0);
					DPProcessParamEntryInfo dpProcessParamEntryInfo = new DPProcessParamEntryInfo();
					dpProcessParamEntryInfo.setColumnEntries(columnEntries);
					dpProcessParamEntryInfo.setDPFileProcessStatusInfo(columnEntries.get(0).getDynamicPricingFilePrcsStatus());
					dpParamEntry = dpProcessParamEntryInfo;
				} catch (SystemException e) {
					log.error(e.getLocalizedMessage(), e);
					response.setSuccess(false);
					response.setMessage(e.getMessage());
					return response;
				}
			} else if (!dpParamEntry.isReprocess()) {
				dPProcessDelegate.saveFileEntriesInDB(dpParamEntry);
			}
			dpParamEntry.getDPFileProcessStatusInfo().setStatus(DPFileProcessStatus.IN_PROGRESS.getFileStatus());
			dPProcessDelegate.saveDpPrcsStatus(dpParamEntry.getDPFileProcessStatusInfo());
			AsyncListenableTaskExecutor delegateExecutor = new SimpleAsyncTaskExecutor();
			DelegatingSecurityContextExecutor executor = new DelegatingSecurityContextExecutor(delegateExecutor, SecurityContextHolder.getContext());
			final Map<String, String> mdcContext = MDC.getCopyOfContextMap();
			final DPProcessParamEntryInfo dpProcessParamEntryInfo = dpParamEntry;

			executor.execute(new Runnable() {
				@Override
				public void run() {
					if (mdcContext != null)
						MDC.setContextMap(mdcContext);
					try {
						commandMaster.prepareWeek0(dpProcessParamEntryInfo);
					} catch (SystemException e) {
						log.error(e.getLocalizedMessage(), e);
					}
				}
			});
			response.setSuccess(true);
			response.setMessage(MessageContainer.getMessage(CoreExceptionCodes.DP041, new Object[] {}));

		} catch (Exception e) {
			log.error("Exception occurred while processing input file : ", e);
			dpParamEntry.getDPFileProcessStatusInfo().setStatus(DPFileProcessStatus.ERROR.getFileStatus());
			dPProcessDelegate.saveDpPrcsStatus(dpParamEntry.getDPFileProcessStatusInfo());
			response.setSuccess(false);
			response.setMessage("Exception occurred while processing Week0 input file ");
		} finally {
			MDC.remove(RAClientConstants.APP_CODE);
			MDC.remove(RAClientConstants.PRODUCT_TYPE);
		}
		return response;
	}

	@RequestMapping(value = "/processWeekN", method = RequestMethod.POST)
	@ResponseBody
	public RestResponse<DPProcessResponse> processWeekN(@RequestBody(required = false) DPProcessWeekNParamEntryInfo dpWeeknParamEntry,
			@RequestParam(required = false) String fileId) {
		RestResponse<DPProcessResponse> response = new RestResponse<DPProcessResponse>();
		boolean isFirstFetchProcess = false;

		if ((ObjectUtils.isEmpty(dpWeeknParamEntry) || dpWeeknParamEntry.getColumnEntries().isEmpty()) && StringUtils.isEmpty(fileId)) {
			response.setSuccess(false);
			response.setMessage("Input cannot be null");
			return response;
		}

		try {
			MDC.put(RAClientConstants.PRODUCT_TYPE, DPAConstants.WEEKN);
			MDC.put(RAClientConstants.APP_CODE, "dpa");
			if (null != dpWeeknParamEntry) {
				isFirstFetchProcess = dpWeeknParamEntry.isFetchProcess();
			}

			if (!isFirstFetchProcess) {
				DPWeekNProcessStatus dpWeekNProcessStatus = dpProcessWeekNFilterDelegate
						.checkForWeekNPrcsStatus(DPFileProcessStatus.IN_PROGRESS.getFileStatus());
				if (!ObjectUtils.isEmpty(dpWeekNProcessStatus)) {
					response.setSuccess(false);
					response.setMessage(MessageContainer.getMessage(CoreExceptionCodes.DP017, new Object[] {}));
					return response;
				} else {
					if (null != dpWeeknParamEntry && !dpWeeknParamEntry.isReprocess()) {
						dPProcessDelegate.saveWeekNFileEntriesInDB(dpWeeknParamEntry);
					}
					if (null == dpWeeknParamEntry && !fileId.isEmpty()) {
						List<DPProcessWeekNParamInfo> columnEntries = dpFileProcessDelegate.getWeekNAssetDetails(fileId, DPAConstants.WEEKN);
						DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo = new DPProcessWeekNParamEntryInfo();
						dpProcessParamEntryInfo.setColumnEntries(columnEntries);
						dpProcessParamEntryInfo.setDpWeeknProcessStatus(columnEntries.get(0).getDpWeekNProcessStatus());
						dpWeeknParamEntry = dpProcessParamEntryInfo;
					}
					// set current process to IN-PROGRESS
					dpProcessWeekNFilterDelegate.updateWeeknPrcsStatus(DPFileProcessStatus.IN_PROGRESS.getFileStatus(),
							dpWeeknParamEntry.getDpWeeknProcessStatus().getId());
				}
			}
			log.info("WeekN process started");

			// deriving file Id
			final String id = StringUtils.equalsIgnoreCase(fileId, DPAConstants.NULL) ? dpWeeknParamEntry.getDpWeeknProcessStatus().getId() : fileId;

			final DPProcessWeekNParamEntryInfo dpProcessParamEntryInfo = dpWeeknParamEntry;
			AsyncListenableTaskExecutor delegateExecutor = new SimpleAsyncTaskExecutor();
			DelegatingSecurityContextExecutor executor = new DelegatingSecurityContextExecutor(delegateExecutor, SecurityContextHolder.getContext());

			final Map<String, String> mdcContext = MDC.getCopyOfContextMap();

			executor.execute(() -> {
				if (mdcContext != null)
					MDC.setContextMap(mdcContext);
				try {
					commandMaster.prepareWeekN(dpProcessParamEntryInfo);
					weekNDataDelegate.downloadWeekNReports(id, null, null, dpProcessParamEntryInfo);
				} catch (SystemException e) {
					log.error("SystemException during weekN process. {}", e.getLocalizedMessage(), e);
				} catch (IOException e) {
					log.error("IOException during weekN process report generation. ", e);
				}
			});
			response.setSuccess(true);
			response.setMessage(MessageContainer.getMessage(CoreExceptionCodes.DP041, new Object[] {}));
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			if (!ObjectUtils.isEmpty(dpWeeknParamEntry.getDpWeeknProcessStatus()))
				dpProcessWeekNFilterDelegate
						.updateWeeknPrcsStatus(DPFileProcessStatus.ERROR.getFileStatus(), dpWeeknParamEntry.getDpWeeknProcessStatus().getId());
			response.setSuccess(false);
			response.setMessage("Exception occurred while processing WeekN ");
		} finally {
			MDC.remove(RAClientConstants.APP_CODE);
			MDC.remove(RAClientConstants.PRODUCT_TYPE);
		}

		log.info("Process weekN file controller ends");
		return response;
	}
}