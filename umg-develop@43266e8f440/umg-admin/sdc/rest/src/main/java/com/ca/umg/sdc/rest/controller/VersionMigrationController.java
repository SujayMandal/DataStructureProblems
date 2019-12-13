/**
 * 
 */
package com.ca.umg.sdc.rest.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.constants.EncodingTypes;
import com.ca.umg.business.encryption.EncryptionProvider;
import com.ca.umg.business.encryption.info.EncryptionDataInfo;
import com.ca.umg.business.migration.MigrationAdapter;
import com.ca.umg.business.migration.MigrationAdapterFactory;
import com.ca.umg.business.migration.audit.info.MigrationAuditInfo;
import com.ca.umg.business.migration.audit.info.VersionData;
import com.ca.umg.business.migration.delegate.VersionMigrationDelegate;
import com.ca.umg.business.migration.info.VersionDetail;
import com.ca.umg.business.migration.info.VersionImportInfo;
import com.ca.umg.business.migration.info.VersionMigrationWrapper;
import com.ca.umg.business.util.EncryptionUtil;
import com.ca.umg.business.util.ZipUtil;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.sdc.rest.utils.RestResponse;

/**
 * @author nigampra
 * 
 */


@SuppressWarnings("PMD")
@Controller
@RequestMapping("/version")
public class VersionMigrationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionMigrationController.class);

    @Inject
    private VersionMigrationDelegate versionMigrationDelegate;

    @Inject
    private MigrationAdapterFactory factory;

    @Inject
    private EncryptionProvider encryptionProvider;

    private static final String IMPORT_FILE_NAME = "importFileName";

    @RequestMapping(value = "/export/{tenantModelName}/{version}/{id}")
    public void exportVersion(@PathVariable("tenantModelName") String tenantModelName, @PathVariable("version") String version,
            @PathVariable("id") String id, HttpServletResponse response) {
        LOGGER.info("Entered exportVersion method");
        String fileName = tenantModelName.trim().replaceAll(" ", "_") + '_' + version.trim() + ".zip";
        String tempFileName = fileName + new Random().nextLong();
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", fileName);
        MigrationAuditInfo migrationAuditInfo = new MigrationAuditInfo();
        try {
            migrationAuditInfo = versionMigrationDelegate.logVersionExport(id);
            VersionMigrationWrapper versionMigrationWrapper = versionMigrationDelegate.exportVersion(id, migrationAuditInfo);
            VersionData versionData = ConversionUtil.convertJson(migrationAuditInfo.getVersionData(), VersionData.class);
            byte[] migrationLog = getMigrationLog(migrationAuditInfo, versionData);
            versionMigrationWrapper.setZipChecksumAlgo(EncodingTypes.SHA256.getName());
            response.setHeader(headerKey, headerValue);
            ZipUtil.createWrapperZipFile(tempFileName, versionMigrationWrapper);
            byte[] zippedFileByteArray = ZipUtil.readZipFile(tempFileName);
            EncryptionDataInfo encryptionDataInfo = encryptArtifacts(zippedFileByteArray);
            createVersionZipAndFlush(response, fileName, encryptionDataInfo, migrationLog);
        } catch (BusinessException | SystemException se) {
            versionMigrationDelegate.markExportAsFailed(migrationAuditInfo.getId());
            LOGGER.error(se.getLocalizedMessage(), se);
        } catch (IOException e) {
            LOGGER.error("", e);
        } finally {
            ZipUtil.deleteFile(tempFileName);
        }
    }

    private EncryptionDataInfo encryptArtifacts(byte[] zippedFileByteArray) throws BusinessException, SystemException {
        EncryptionDataInfo encryptionDataInfo = new EncryptionDataInfo();
        encryptionDataInfo.setDecryptedData(zippedFileByteArray);
        encryptionProvider.encrypt(encryptionDataInfo);
        return encryptionDataInfo;
    }

    private void createVersionZipAndFlush(HttpServletResponse response, String fileName, EncryptionDataInfo encryptionDataInfo,
            byte[] migrationLog) throws IOException, BusinessException {
        ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
        zos.putNextEntry(new ZipEntry(fileName));
        zos.write(encryptionDataInfo.getEncryptedData());
        zos.closeEntry();
        zos.putNextEntry(new ZipEntry("exportinfo.json"));
        zos.write(migrationLog);
        zos.closeEntry();
        zos.putNextEntry(new ZipEntry("umg.key"));
        zos.write(EncryptionUtil.applyKeyEncryption(encryptionDataInfo.getEncryptedKey()));
        zos.closeEntry();
        zos.flush();
        zos.close();
    }

    private byte[] getMigrationLog(MigrationAuditInfo mai, VersionData versionData) {
        byte[] exportData = null;
        Properties prop = new Properties();
        try {
            prop.setProperty("Instance_Name", versionData.getInstanceName());
            prop.setProperty("Release", versionData.getReleaseVersion());
            prop.setProperty("Exported_By", mai.getCreatedBy());
            prop.setProperty("Exported_On", mai.getCreatedDate().toString());
            exportData = ConversionUtil.convertToJsonString(prop).getBytes();
        } catch (SystemException se) {
            LOGGER.error(se.getLocalizedMessage(), se);
        }
        return exportData;
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public @ResponseBody RestResponse<List<String>> importVersion(@RequestParam("versionInfo") String versionInfoJSON,
            @RequestParam("zipFile") MultipartFile zipFile) {
        RestResponse<List<String>> restResponse = new RestResponse<List<String>>();
        try {
            VersionDetail versionDetail = ConversionUtil.convertJson(versionInfoJSON, VersionDetail.class);
            MigrationAdapter migrationAdapter = factory.getMigrationAdapter(zipFile.getInputStream(),
                    zipFile.getOriginalFilename());
            VersionImportInfo versionImportInfo = migrationAdapter.extractVersionPackage();
            if (versionImportInfo == null || versionImportInfo.getVersionMigrationWrapper() == null) {
                restResponse.setError(true);
                restResponse
                        .setMessage("An error occurred while extracting the zip file. Please upload valid zip file to import version.");
            } else if (versionImportInfo.isValidZipCheckSum()) {
                KeyValuePair<VersionInfo, KeyValuePair<List<String>, List<String>>> result = migrationAdapter
                        .importVersion(versionDetail);
                if (result.getKey() != null) {
                    VersionInfo versionInfo = result.getKey();
                    versionMigrationDelegate.logVersionImport(versionInfo.getId(), versionImportInfo.getVersionMigrationWrapper()
                            .getMigrationAuditInfo());
                    LOGGER.info("Imported version {} successfully.", versionInfo.getName());
                    restResponse.setMessage("Version imported successfully with following details.");
                    restResponse.setResponse(result.getValue().getKey());
                } else {
                    restResponse.setMessage("Version import failed with following errors.");
                    restResponse.setError(true);
                    restResponse.setResponse(result.getValue().getValue());
                }
            } else {
                restResponse.setError(true);
                restResponse.setMessage("Zip checksum is invalid.");
            }
        } catch (BusinessException | SystemException e) {
            restResponse.setError(true);
            restResponse.setResponse(Arrays.asList(e.getLocalizedMessage()));
            restResponse.setErrorCode(e.getCode());
            restResponse.setMessage(e.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.error("An error occurred while closing the zip input stream. {}", e.getMessage());
        }
        if (restResponse.isError()) {
            try {
                versionMigrationDelegate.markImportAsFailed();
            } catch (BusinessException | SystemException e) {
                LOGGER.error("An error occurred while writing import failure log. {}", e.getMessage());
            }
        }
        return restResponse;
    }

    @RequestMapping(value = "/getVersionFromImportFile", method = RequestMethod.POST)
    public @ResponseBody RestResponse<VersionImportInfo> getVersionFromImportFile(
            @RequestParam("versionZipFile") MultipartFile versionZipFile) {
        final RestResponse<VersionImportInfo> restResponse = new RestResponse<VersionImportInfo>();
        VersionImportInfo versionImportInfo;
        String importFilePath = null;
        String importFileName = null;
        try {
            final MigrationAdapter migrationAdapter = factory.getMigrationAdapter(versionZipFile.getInputStream(), versionZipFile.getOriginalFilename());
            versionImportInfo = migrationAdapter.extractVersionPackage();
            if (versionImportInfo == null || versionImportInfo.getVersionMigrationWrapper() == null) {
                restResponse.setError(true);
                restResponse.setMessage("An error occurred while extracting the zip file. Please upload valid zip file to import version.");
            } else if (versionImportInfo.isValidZipCheckSum()) {
                importFileName = versionMigrationDelegate.getImportFileName(versionZipFile);
                importFilePath = versionMigrationDelegate.getImportFilePath(importFileName);
                versionImportInfo.setImportFileName(importFileName);
                versionMigrationDelegate.storeImportFileIntoSan(importFilePath, versionZipFile);
                versionMigrationDelegate.logVersionImport(versionImportInfo.getVersionMigrationWrapper().getMigrationAuditInfo(), importFilePath);
                versionImportInfo.getVersionMigrationWrapper().setModelIODefinition(null);
                versionImportInfo.getVersionMigrationWrapper().setModelExcelDefinition(null);
                versionImportInfo.getVersionMigrationWrapper().setModelDoc(null);
                versionImportInfo.getVersionMigrationWrapper().setModelLibraryJar(null);

                restResponse.setError(false);
                restResponse.setMessage("Version imported successfully with following details.");
                restResponse.setResponse(versionImportInfo);
            } else {
                restResponse.setError(true);
                restResponse.setMessage("Zip checksum is invalid.");
            }
        } catch (BusinessException | SystemException e) {
            restResponse.setError(true);
            restResponse.setErrorCode(e.getCode());
            restResponse.setMessage(e.getLocalizedMessage());
            if (importFilePath != null) {
                versionMigrationDelegate.deleteImportFilefromSan(importFilePath);
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred while closing the zip input stream. {}", e.getMessage());
        }

        if (restResponse.isError()) {
            try {
                versionMigrationDelegate.markImportAsFailed();
            } catch (BusinessException | SystemException e) {
                LOGGER.error("An error occurred while writing import failure log. {}", e.getMessage());
            }
        }
        return restResponse;
    }

    @RequestMapping(value = "/deleteImportFile/{importFileName}")
    @ResponseBody
    public RestResponse<String> deleteImportFile(@PathVariable(IMPORT_FILE_NAME) final String importFileName) {
        LOGGER.info("Request reached for deleting Import File:" + importFileName);
        final RestResponse<String> response = new RestResponse<>();
        try {
            final String importFilePath = versionMigrationDelegate.getImportFilePath(importFileName);
            versionMigrationDelegate.deleteImportFilefromSan(importFilePath);
            LOGGER.info("Import File is deleted succefully. Deleted File name is : {}", importFileName);
            response.setError(false);
            response.setResponse("Import File is deleted succefully. Deleted File name is : " + importFileName);
        } catch (SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }

        return response;
    }

    @RequestMapping(value = "/importNew", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody RestResponse<List<String>> importVersionNew(@RequestBody final VersionDetail versionDetail) {
        RestResponse<List<String>> restResponse = new RestResponse<List<String>>();
        FileInputStream fileInputStream = null;
        try {
//            VersionDetail versionDetail = ConversionUtil.convertJson(versionInfoJSON, VersionDetail.class);

            final String importFilePath = versionMigrationDelegate.getImportFilePath(versionDetail.getImportFileName());
            final File importFileDirectory = new File(importFilePath);
            final String[] files = importFileDirectory.list();       
            
            final StringBuffer completeFileName = new StringBuffer();
            completeFileName.append(importFileDirectory);
            completeFileName.append(File.separatorChar).append(files[0]);
            
            fileInputStream = new FileInputStream(new File(completeFileName.toString()));

            MigrationAdapter migrationAdapter = factory.getMigrationAdapter(fileInputStream, files[0]);
            VersionImportInfo versionImportInfo = migrationAdapter.extractVersionPackage();
            if (versionImportInfo == null || versionImportInfo.getVersionMigrationWrapper() == null) {
                restResponse.setError(true);
                restResponse.setMessage("An error occurred while extracting the zip file. Please upload valid zip file to import version.");
            } else if (versionImportInfo.isValidZipCheckSum()) {
                KeyValuePair<VersionInfo, KeyValuePair<List<String>, List<String>>> result = migrationAdapter.importVersion(versionDetail);
                if (result.getKey() != null) {
                    VersionInfo versionInfo = result.getKey();
                    versionMigrationDelegate.logVersionImport(versionInfo.getId(), versionImportInfo.getVersionMigrationWrapper().getMigrationAuditInfo());
                    LOGGER.info("Imported version {} successfully.", versionInfo.getName());
                    restResponse.setMessage("Version imported successfully with following details.");
                    restResponse.setResponse(result.getValue().getKey());
                } else {
                    restResponse.setMessage("Version import failed with following errors.");
                    restResponse.setError(true);
                    restResponse.setResponse(result.getValue().getValue());
                }
            } else {
                restResponse.setError(true);
                restResponse.setMessage("Zip checksum is invalid.");
            }
        } catch (BusinessException | SystemException e) {
            restResponse.setError(true);
            restResponse.setResponse(Arrays.asList(e.getLocalizedMessage()));
            restResponse.setErrorCode(e.getCode());
            restResponse.setMessage(e.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.error("An error occurred while closing the zip input stream. {}", e.getMessage());
        } finally {
        	if (fileInputStream != null) {
        		try {
        			fileInputStream.close();
        		} catch (IOException ioe) {
        			restResponse.setError(true);
        			restResponse.setMessage("Zip File Input Stream is not getting closed successfully");
        		}
        	}
        	deleteImportFile(versionDetail.getImportFileName());
        }
        if (restResponse.isError()) {
            try {
            	if (versionDetail.getMigrationId() == null) {
            		versionMigrationDelegate.markImportAsFailed();
            	} else {
            		versionMigrationDelegate.markImportAsFailed(versionDetail.getMigrationId());
            	}
            } catch (BusinessException | SystemException e) {
                LOGGER.error("An error occurred while writing import failure log. {}", e.getMessage());
            }
        }
        return restResponse;
    }
}