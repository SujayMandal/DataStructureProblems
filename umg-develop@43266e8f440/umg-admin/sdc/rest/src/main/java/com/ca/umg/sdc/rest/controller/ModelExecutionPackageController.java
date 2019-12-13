/**
 * 
 */
package com.ca.umg.sdc.rest.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.execution.delegate.ModelExecutionEnvironmentDelegate;
import com.ca.umg.business.execution.delegate.ModelExecutionPackageDelegate;
import com.ca.umg.business.model.bo.ModelArtifactBO;
import com.ca.umg.business.model.info.LargePackageInfo;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelExecutionEnvironmentInfo;
import com.ca.umg.business.model.info.ModelExecutionPackageInfo;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.sdc.rest.constants.ModelConstants;
import com.ca.umg.sdc.rest.exception.RESTExceptionCodes;
import com.ca.umg.sdc.rest.utils.RestResponse;

/**
 * @author nigampra
 *
 */
@Controller
@RequestMapping("/executionPackage")
public class ModelExecutionPackageController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModelExecutionPackageController.class);

	private static final int ONE = 1;

	@Inject
	private ModelExecutionPackageDelegate executionPackageDelegate;

	@Inject
	private ModelExecutionEnvironmentDelegate modelExecEnvironmentDelegate;

	@Inject
	private ModelArtifactBO modelArtifactBO;

	@RequestMapping(value = "/{modelType}/add", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody RestResponse<ModelExecutionPackageInfo> addModelExecutionPackage(
			@PathVariable(value = "modelType") String modelType,
			@RequestParam("supportPackage") MultipartFile supportPackage) throws SystemException {
		LOGGER.info("Request receieved to add Package");
		RestResponse<ModelExecutionPackageInfo> response = new RestResponse<>();
		ModelArtifact executionPkg = null;
		ModelExecutionPackageInfo modelExecutionPackageInfo = null;
		try {
			AdminUtil.setAdminAwareTrue();
			executionPkg = new ModelArtifact();
			executionPkg.setName(supportPackage.getOriginalFilename());
			executionPkg.setData(supportPackage.getInputStream());
			executionPkg.setDataArray(supportPackage.getBytes());
			executionPkg.setContentType(supportPackage.getContentType());
			modelExecutionPackageInfo = executionPackageDelegate.buildModelExecutionPackageInfo(modelType,
					executionPkg);
			modelExecutionPackageInfo = executionPackageDelegate.createModelExecutionPackage(modelExecutionPackageInfo);
			response.setResponse(modelExecutionPackageInfo);
			response.setError(false);
			response.setMessage("Saved successfully");
		} catch (IOException e) {
			LOGGER.error("IO exception occured : " + e);
			response.setErrorCode(RESTExceptionCodes.RSE0000001);
			response.setError(true);
			response.setMessage(supportPackage.getOriginalFilename());
		} catch (SystemException | BusinessException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		} finally {
			AdminUtil.setAdminAwareFalse();
			try {
				if (executionPkg.getData() != null) {
					executionPkg.getData().close();
				}

				if (supportPackage.getInputStream() != null) {
					supportPackage.getInputStream().close();
				}
				executionPkg.setDataArray(null);
			} catch (IOException e) {
				LOGGER.error("IO exception occured while closing the stream : " + e);

			}
		}

		return response;
	}

	/**
	 * 
	 */

	@RequestMapping(value = "/{modelType}/sync/moveDelete", method = POST, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody RestResponse<List<LargePackageInfo>> addSyncModelExecutionPackage(
			@PathVariable(value = "modelType") String modelType) throws SystemException {
		LOGGER.info("Request receieved to add Package");
		long startTime = System.currentTimeMillis();
		LOGGER.debug("Start Time : " + startTime);
		RestResponse<List<LargePackageInfo>> response = new RestResponse<>();
		ModelArtifact executionPkg = null;
		List<LargePackageInfo> successFailObj = new ArrayList<>();
		ModelExecutionPackageInfo modelExecutionPackageInfo = null;
		InputStream fileInputStream = null;
		byte[] fileDataArray = null;
		LargePackageInfo largepackageinfo;
		LargePackageInfo errorResponseObj = new LargePackageInfo();
		try {
			AdminUtil.setAdminAwareTrue();
			String getLargeFilesPath = modelArtifactBO.getLargeFilesPackage();
			File[] largesupportpackage = findTarGzFiles(getLargeFilesPath);

			if (!ArrayUtils.isEmpty(largesupportpackage)) {
				for (File child : largesupportpackage) {
					largepackageinfo = new LargePackageInfo();
					fileInputStream = new FileInputStream(child);
					fileDataArray = Files.readAllBytes(new File(child.getPath()).toPath());
					executionPkg = new ModelArtifact();
					executionPkg.setName(child.getName());
					executionPkg.setData(fileInputStream);
					executionPkg.setDataArray(fileDataArray);
					executionPkg.setContentType("application/x-gzip");
					largepackageinfo.setFileName(child.getName());
					largepackageinfo.setFileSize(child.length() / 1024);
					errorResponseObj = largepackageinfo;
					modelExecutionPackageInfo = executionPackageDelegate.buildModelExecutionPackageInfo(modelType,
							executionPkg);
					if (!executionPackageDelegate.isPackageAvailable(modelExecutionPackageInfo)) {
						modelExecutionPackageInfo = executionPackageDelegate
								.createModelExecutionPackage(modelExecutionPackageInfo);

						largepackageinfo.setUploadStatus("Package uploaded Successfully");

						child.delete();
					} else {
						largepackageinfo.setUploadStatus("Package with same name already exist.");

					}
					successFailObj.add(largepackageinfo);

				}
				response.setResponse(successFailObj);
				response.setError(false);
				response.setMessage("Saved successfully");

			} else {
				largepackageinfo = new LargePackageInfo();
				largepackageinfo.setFileName("Empty");
				largepackageinfo.setFileSize(0);
				largepackageinfo.setUploadStatus("No file Status found");
				successFailObj.add(largepackageinfo);
				response.setResponse(successFailObj);
				response.setError(false);
				response.setMessage("No file Found");

			}

		} catch (IOException e) {
			LOGGER.error("IO exception occured : " + e);
			response.setErrorCode(RESTExceptionCodes.RSE0000001);
			response.setError(true);
		} catch (SystemException | BusinessException e) {
			errorResponseObj.setUploadStatus(e.getLocalizedMessage());
			successFailObj.add(errorResponseObj);
			response.setResponse(successFailObj);
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		} finally {
			AdminUtil.setAdminAwareFalse();
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				LOGGER.error("IO exception occured while closing the fileInputStream : " + e);

			}
		}
		long endTime = System.currentTimeMillis();
		LOGGER.debug("Execution time for large package upload " + (endTime - startTime) + " milliseconds");
		return response;
	}

	@RequestMapping(value = "/count", method = GET, produces = APPLICATION_JSON_VALUE)
	public @ResponseBody RestResponse<Long> countLargeFiles() throws SystemException {
		LOGGER.info("Request receieved to count files in large Package");
		RestResponse<Long> response = new RestResponse<>();
		long tarGzFileCount = 0;

		try {
			AdminUtil.setAdminAwareTrue();
			File[] largesupportpackage = findTarGzFiles(modelArtifactBO.getLargeFilesPackage().toString());
			if (!ArrayUtils.isEmpty(largesupportpackage)) {
				tarGzFileCount = largesupportpackage.length;
			}
			response.setResponse(tarGzFileCount);
			response.setError(false);
			response.setMessage("Count Saved successfully");

		} catch (SystemException | BusinessException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		}
		return response;
	}

	private File[] findTarGzFiles(String dirName) {
		File dir = new File(dirName);

		return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".tar.gz");
			}
		});

	}

	@RequestMapping(value = "/listNames/{environment:.+}", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<List<String>> getNames(@PathVariable(value = "environment") String environment) {
		RestResponse<List<String>> response = new RestResponse<List<String>>();
		List<String> folderNames = null;
		try {
			ModelExecutionEnvironmentInfo modelExecutionEnvironmentInfo = getModelExecutionEnvironment(environment);
			if (modelExecutionEnvironmentInfo != null) {
				folderNames = executionPackageDelegate
						.getPackageFoldersByEnvironment(modelExecutionEnvironmentInfo.getName());
			} else {
				throw new SystemException(BusinessExceptionCodes.BSE000135,
						new Object[] {
								environment.substring(BusinessConstants.NUMBER_ZERO,
										environment.indexOf(BusinessConstants.HYPHEN)),
								environment.substring(environment.indexOf(BusinessConstants.HYPHEN)) });
			}
			response.setResponse(folderNames);
			response.setError(false);
		} catch (SystemException | BusinessException e) {
			LOGGER.error(e.getMessage());
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		} finally {
			AdminUtil.setAdminAwareFalse();
		}
		return response;
	}

	private ModelExecutionEnvironmentInfo getModelExecutionEnvironment(String environment)
			throws BusinessException, SystemException {
		ModelExecutionEnvironmentInfo modelExecutionEnvironmentInfo = null;
		AdminUtil.setAdminAwareTrue();
		modelExecutionEnvironmentInfo = modelExecEnvironmentDelegate.getModelExnEnvtListLibraries(
				environment.substring(BusinessConstants.NUMBER_ZERO, environment.indexOf(BusinessConstants.HYPHEN)),
				environment.substring(environment.indexOf(BusinessConstants.HYPHEN) + BusinessConstants.NUMBER_ONE));
		return modelExecutionEnvironmentInfo;

	}

	@RequestMapping(value = "/listAllSupportPackages/{environment:.+}/{packageFolder}", method = POST, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<PageRecord<ModelExecutionPackageInfo>> getPagedModelExecutionPackages(
			@PathVariable(value = "environment") String environment,
			@PathVariable(value = "packageFolder") String packageFolder,
			@RequestBody ModelExecutionPackageInfo pageInfo) {
		RestResponse<PageRecord<ModelExecutionPackageInfo>> response = new RestResponse<PageRecord<ModelExecutionPackageInfo>>();
		ModelExecutionEnvironmentInfo modelExecutionEnvironmentInfo = null;
		PageRecord<ModelExecutionPackageInfo> pageRecord = null;
		try {
			modelExecutionEnvironmentInfo = getModelExecutionEnvironment(environment);
			if (modelExecutionEnvironmentInfo != null) {
				pageRecord = executionPackageDelegate
						.getModelExecPkgByEnvAndName(modelExecutionEnvironmentInfo.getName(), packageFolder, pageInfo);
			} else {
				throw new SystemException(BusinessExceptionCodes.BSE000135,
						new Object[] {
								environment.substring(BusinessConstants.NUMBER_ZERO,
										environment.indexOf(BusinessConstants.HYPHEN)),
								environment.substring(environment.indexOf(BusinessConstants.HYPHEN)) });
			}
			response.setResponse(pageRecord);
		} catch (SystemException | BusinessException e) {
			LOGGER.error(e.getMessage());
			LOGGER.error(e.getLocalizedMessage(), e);
			response.setErrorCode(e.getCode());
			response.setError(true);
			response.setMessage(e.getLocalizedMessage());
		} finally {
			AdminUtil.setAdminAwareFalse();
		}
		return response;
	}

	@RequestMapping(value = "/downloadSupportPackage/{packageIds}", method = GET)
	public void downloadSupportPackage(@PathVariable("packageIds") String packageIds, HttpServletResponse response)
			throws SystemException, BusinessException {
		try {
			ModelExecutionPackageInfo modelExecutionPackageInfo = null;
			AdminUtil.setAdminAwareTrue();
			String[] ids = packageIds.split(BusinessConstants.CHAR_COMMA);
			if (ids.length == ONE) {
				modelExecutionPackageInfo = executionPackageDelegate.getModelExecutionPackageInfo(ids[0]);
				byte[] executionPackageBytes = executionPackageDelegate
						.getModelExecutionPackage(modelExecutionPackageInfo);
				response.setHeader("Content-Disposition",
						"attachment;filename=" + modelExecutionPackageInfo.getPackageName());
				response.getOutputStream().write(executionPackageBytes);
			} else if (ids.length > ONE) {
				response.setHeader("Content-Disposition",
						"attachment;filename=" + BusinessConstants.SUPPORT_PACKAGE_PARENT_FOLDER + ".zip");
				createZipForSupportPackages(ids, response);
			} else {
				LOGGER.info("No package id present for export.");
			}
		} catch (BusinessException | SystemException | IOException se) {
			writeErrorData(response);
		} finally {
			try {
				response.getOutputStream().flush();
				response.getOutputStream().close();
			} catch (IOException e) {
				LOGGER.error("Error while closing response outputstream.", e);
			}
			AdminUtil.setAdminAwareFalse();
		}

	}

	private void createZipForSupportPackages(String[] packageIds, HttpServletResponse response)
			throws SystemException, BusinessException {
		ModelExecutionPackageInfo execPackage = null;
		try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());) {
			for (String packageId : packageIds) {
				execPackage = executionPackageDelegate.getModelExecutionPackageInfo(packageId);
				LOGGER.info("Adding support package into zip :" + execPackage.getPackageName());
				zos.putNextEntry(new ZipEntry(execPackage.getPackageName()));
				zos.write(executionPackageDelegate.getModelExecutionPackage(execPackage));
				zos.closeEntry();
			}
		} catch (IOException e) {
			writeErrorData(response);
		}
	}

	private void writeErrorData(HttpServletResponse response) {
		try {
			String headerValue = String.format("attachment; filename=\"%s\"", "error.txt");
			response.setHeader(ModelConstants.CONTENT_DISPOSITION, headerValue);
			String errorMsg = "Package doesn't exist.";
			response.getOutputStream().write(errorMsg.getBytes());
			response.getOutputStream().flush();
		} catch (IOException excep) {
			LOGGER.error("Error while Writting error data  ", excep);
		}
	}

}
