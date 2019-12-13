package com.ca.umg.sdc.rest.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.batching.info.BatchFileInfo;
import com.ca.umg.business.tenant.delegate.TenantDelegate;
import com.ca.umg.sdc.rest.exception.RESTExceptionCodes;
import com.ca.umg.sdc.rest.utils.RestResponse;

@Controller
@RequestMapping("/batch")
public class BatchFileUploadController {
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

	@Inject
	private TenantDelegate tenantDelegate;

	@RequestMapping(value = {
			"/uploadRequestJSON" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<String> uploadJsonFile(@RequestBody MultipartFile excelFile) {
		RestResponse<String> response = new RestResponse<String>();
		try {
			BatchFileInfo batchFileInfo = new BatchFileInfo();
			batchFileInfo.setFileInputStream(excelFile.getInputStream());
			batchFileInfo.setFileName(excelFile.getOriginalFilename());
			tenantDelegate.fileUpload(batchFileInfo);
			response.setError(false);
			response.setMessage("File uploaded successfully");
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(RESTExceptionCodes.RSE0000001);
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		} catch (SystemException | BusinessException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}
		return response;
	}

	@RequestMapping(value = { "/uploadFile" }, method = RequestMethod.POST)
	@ResponseBody
	public RestResponse<String> uploadFile(@RequestBody MultipartFile files) {
		RestResponse<String> response = new RestResponse<String>();
		String path = "D:\\Temp\\";
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		try {
			byte[] fileBytes = files.getBytes();

			fos = new FileOutputStream(new File(path + files.getOriginalFilename()));
			bos = new BufferedOutputStream(fos);
			bos.write(fileBytes);
			bos.flush();
		} catch (FileNotFoundException e) {
			LOGGER.debug("Global Error message", e.getLocalizedMessage(), e);
		} catch (IOException e) {
			LOGGER.debug("Global Error message", e.getLocalizedMessage(), e);
		} finally {
			if (fos != null) {
				IOUtils.closeQuietly(fos);
			}
				if(bos != null) {
				IOUtils.closeQuietly(bos);
			}
		}
		return response;
	}

}