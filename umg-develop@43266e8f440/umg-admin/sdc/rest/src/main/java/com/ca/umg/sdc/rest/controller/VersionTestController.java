package com.ca.umg.sdc.rest.controller;

import static org.apache.commons.io.FilenameUtils.getExtension;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.batching.delegate.BatchingDelegate;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.delegate.MappingDelegate;
import com.ca.umg.business.mapping.info.TestBedOutputInfo;
import com.ca.umg.business.mapping.info.VersionTestContainer;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.tenant.delegate.TenantDelegate;
import com.ca.umg.business.transaction.delegate.TransactionDelegate;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.delegate.VersionDelegate;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionAPIContainer;
import com.ca.umg.business.versiontest.delegate.VersionTestDelegate;
import com.ca.umg.plugin.commons.excel.reader.ExcelReader;
import com.ca.umg.report.model.ModelReportStatusInfo;
import com.ca.umg.report.model.ModelReportTemplateDefinition;
import com.ca.umg.report.model.ReportInfo;
import com.ca.umg.report.service.ReportService;
import com.ca.umg.sdc.rest.constants.RestConstants;
import com.ca.umg.sdc.rest.utils.RestResponse;

@Controller
@RequestMapping("/versiontest")
@SuppressWarnings({"PMD.AvoidDeeplyNestedIfStmts" , "PMD.CyclomaticComplexity" })
//@SuppressWarnings({"PMD.CyclomaticComplexity"})
public class VersionTestController {
    private static final String ZIP_FILE_NAME = "zipFileName";
    private static final String JSON_TYPE = ".json";
    private static final Logger LOGGER = LoggerFactory.getLogger(VersionTestController.class);
    private static final String TID_NAME = "tidName";
    
    private static final String DOT = ".";
    private static final String HYPHEN = "-";
	

	
    @Inject
    private MappingDelegate mappingDelegate;
    @Inject
    private TenantDelegate tenantDelegate;
    @Inject
    private VersionDelegate versionDelegate;
    @Inject
    private VersionTestDelegate versionTestDelegate;
    @Inject
    private ExcelReader excelReader;
    @Inject
    private BatchingDelegate batchingDelegate;
    
    @Inject
    private TransactionDelegate transactionDelegate;
    
    @Inject
    private ReportService reportService; 
    
    @Inject
    private ModelDelegate modelDelegate;

    
    @Inject
    private CacheRegistry cacheRegistry;

    @RequestMapping(value = "/loadTestVersion/{tidName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<VersionTestContainer> loadTestVersion(@PathVariable(TID_NAME) String tidName) {
        RestResponse<VersionTestContainer> response = new RestResponse<>();
        VersionTestContainer versionTestContainer = new VersionTestContainer();
        try {
            versionTestContainer.setTidIoDefinitions(mappingDelegate.getTidIoDeFnsTestBed(tidName, true));
            versionTestContainer.setAsOnDate(AdminUtil.getDateFormatMillisForEst(new Date().getTime(),
                    BusinessConstants.UMG_EST_DATE_FORMAT));

            response.setResponse(versionTestContainer);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }
        return response;
    }

    @RequestMapping(value = "/executeVersion", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<TestBedOutputInfo> executeVersion(@RequestBody VersionTestContainer versionTestContainer) {
        RestResponse<TestBedOutputInfo> response = new RestResponse<>();
        try {
        	String asOnDateFormatted = AdminUtil.convertDateFormat(BusinessConstants.UMG_EST_DATE_FORMAT, 
        			BusinessConstants.UMG_EST_DATE_TIME_FORMAT, versionTestContainer.getAsOnDate());
            String runtimeJson = mappingDelegate.createRuntimeInputJson(versionTestContainer.getTidIoDefinitions(),
                    versionTestContainer.getModelName(), versionTestContainer.getMajorVersion(),
                    versionTestContainer.getMinorVersion(), asOnDateFormatted, Boolean.FALSE,versionTestContainer.getHasModelOpValidation(),versionTestContainer.getHasAcceptableValuesValidation(),versionTestContainer.getStoreRLogs());
            String tenantUrl = VersionControllerHelper.getTenantBaseUrl(tenantDelegate);
            String tenantAuthToken = VersionControllerHelper.getTenantAuthToken(cacheRegistry);
            TestBedOutputInfo testResult = versionDelegate.versionTest(runtimeJson, tenantUrl, tenantAuthToken, versionTestContainer.getVersionId());
            if (testResult != null) {
                response.setError(testResult.isError());
                response.setResponse(testResult);
                response.setErrorCode(testResult.getErrorCode());
                response.setMessage(testResult.getErrorMessage());
                
                if (!testResult.isError() && versionTestContainer.getGenerateReport()) {
                	LOGGER.info("Generate Generate Report");
                    try {

                    	final JSONObject jsonObject = new JSONObject(testResult.getOutputJson());
                    	final String transactionId = jsonObject.getJSONObject("header").getString("umgTransactionId");
                    	
                    	final String fullVersion = versionTestContainer.getMajorVersion() + "." + versionTestContainer.getMinorVersion();
                    	LOGGER.info("Version name:" + versionTestContainer.getModelName() + ", Full Version :" + fullVersion + ", TransactionId" + transactionId);
                    	final Version versionInfo = transactionDelegate.getVersionInfo(versionTestContainer.getModelName(), fullVersion);
                    	final ModelReportTemplateDefinition reportTemplate = modelDelegate.getModelReportTemplate(versionInfo.getId());
                    	
                    	if (reportTemplate != null) {
                        	final ModelReportStatusInfo reoprtStatusInfo = ModelReportStatusInfo.buildMRStatusInfo(reportTemplate);
                        	reoprtStatusInfo.setModelName(versionInfo.getMapping().getModel().getName());
                        	reoprtStatusInfo.setUmgTransactionId(transactionId);
                        	final TransactionDocument td = modelDelegate.getTransactionDocumentByTxnId(transactionId);
                        	reoprtStatusInfo.setReportJsonString(TransactionDocument.createJsonString(td));
                        	reoprtStatusInfo.setTransactionCreatedDate(td.getCreatedDate());
                        	reoprtStatusInfo.setClientTransactionId(td.getClientTransactionID());
                        	
                        	LOGGER.info("ModelReportStatusInfo object is :" + reoprtStatusInfo.toString());
                        	reportService.generateReport(reoprtStatusInfo);                      		
                        	
                        	final ReportInfo reportInfo= reportService.getTransactionReportURL(reoprtStatusInfo);
                        	testResult.setReportInfo(reportInfo);
                    	}
                    } catch (BusinessException | SystemException e) {
                        LOGGER.error(e.getLocalizedMessage(), e);
                        response.setErrorCode(e.getCode());
                        response.setError(true);
                        response.setMessage(e.getLocalizedMessage());
                    } catch (JSONException e) {
                    	LOGGER.error(e.getLocalizedMessage(), e);
                        response.setError(true);
                        response.setMessage(e.getLocalizedMessage());
                    }
                }
            }
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            TestBedOutputInfo testResult = new TestBedOutputInfo();
            response.setResponse(testResult);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        } catch (ParseException e) {
        	 LOGGER.error("Error while converting the date format VersionTestController::executeVersion", e);
             TestBedOutputInfo testResult = new TestBedOutputInfo();
             response.setResponse(testResult);
             response.setError(true);
             response.setErrorCode(BusinessExceptionCodes.BSE000024);
             response.setMessage(e.getLocalizedMessage());
		}
        return response;
    }

    @RequestMapping(value = "/markastested/{tidName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<String> markAsTested(@PathVariable(TID_NAME) String tidName) {
        RestResponse<String> response = new RestResponse<>();
        response.setError(false);
        return response;
    }

    /**
     * This method used to get the data from TenantInput (Based on Txnid) and populate into TestBed
     * 
     * @param txnId
     * @return response
     */
    @RequestMapping(value = "/loadTestBed/{txnId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<VersionTestContainer> populateTenantInputDatatoTestBed(@PathVariable("txnId") String txnId) {
        RestResponse<VersionTestContainer> response = new RestResponse<>();
        try {
            VersionTestContainer versionTestContainer = versionTestDelegate.getVersionTestContainer(txnId);
            versionTestContainer.setHasReportTemplate(modelDelegate.hasModelReportTemplate(versionTestContainer.getVersionId()));
            response.setResponse(versionTestContainer);
            response.setError(false);
            response.setMessage(RestConstants.CONTROLLER_DONE_MESSAGE);
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        }

        return response;
    }

    /**
     * This method used to get the data from TenantInput file and populate into TestBed
     * 
     * @param tenantInputfile
     * @return response
     * @throws IOException
     */
    @RequestMapping(value = "/parseFileData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<VersionTestContainer> populateTenantInputFile(@RequestPart MultipartFile tenantInputfile)
            throws IOException {
        RestResponse<VersionTestContainer> response = new RestResponse<>();
        try {
            InputStream tenantIS = tenantInputfile.getInputStream();
            byte[] tenantInput = IOUtils.toByteArray(tenantIS);
            VersionTestContainer versionTestContainer = versionTestDelegate.getVersionTestContainerFromFile(tenantInput);
            versionTestContainer.setHasReportTemplate(modelDelegate.hasModelReportTemplate(versionTestContainer.getVersionId()));
            response.setResponse(versionTestContainer);
            response.setError(false);
            response.setMessage("Upload Successful.");
        } catch (BusinessException | SystemException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(e.getCode());
            response.setMessage(e.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            response.setError(true);
            response.setErrorCode(BusinessExceptionCodes.BSE000089);
            response.setMessage(e.getLocalizedMessage());
        }

        return response;
    }

    @RequestMapping(value = "/downloadAPI/{versionId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void downloadVersionAPI(@PathVariable("versionId") String versionId, HttpServletResponse response) {
        try {
            VersionAPIContainer versionAPIContainer = versionTestDelegate.getVersionAPI(versionId);
            setResponseHeader(response, versionAPIContainer);
            createVersionZipAndFlush(response, versionAPIContainer);
        } catch (BusinessException | SystemException | IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }

    private void setResponseHeader(HttpServletResponse response, VersionAPIContainer versionAPIContainer) {
        String fileName = versionAPIContainer.getTenantInputSchemaName().trim().replace("INPUT", "API") + ".zip";
        String headerValue = String.format("attachment; filename=\"%s\"", fileName);
        response.setHeader("Content-Disposition", headerValue);
    }

    private void createVersionZipAndFlush(HttpServletResponse response, VersionAPIContainer versionAPIContainer)
            throws IOException, BusinessException {
        ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
        zos.putNextEntry(new ZipEntry(versionAPIContainer.getTenantInputSchemaName() + JSON_TYPE));
        zos.write(versionAPIContainer.getTenantInputSchema());
        zos.closeEntry();
        zos.putNextEntry(new ZipEntry(versionAPIContainer.getTenantOutputSchemaName() + JSON_TYPE));
        zos.write(versionAPIContainer.getTenantOutputSchema());
        zos.closeEntry();
        zos.putNextEntry(new ZipEntry("SampleInput" + JSON_TYPE));
        zos.write(versionAPIContainer.getSampleTenantInputJson());
        zos.closeEntry();
        zos.flush();
        zos.close();
    }

    /**
     * Excel file executor for creation of zip.
     * 
     * @param syndicateDataFile
     * @return
     */
    @RequestMapping(value = "/parseExcelFile", method = RequestMethod.POST,  produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<String> parseExcelFile(@RequestPart MultipartFile excelFile,  @RequestParam("downloadSingleFile") boolean downloadSingleFile, HttpServletResponse response) {
        List<Map<String,Object>> jsonList = null;
        String fileName = null;
        RestResponse<String> restResponse = new RestResponse<>();
        try {
            if (isNotExcelFile(excelFile.getOriginalFilename())) {
                throw new BusinessException(BusinessExceptionCodes.BSE000096,
                        new String[] { "not an Excel file, file extension is not matching " });
            }
            jsonList = excelReader.parseExcel(excelFile.getInputStream(),excelFile.getOriginalFilename());
            if(CollectionUtils.isNotEmpty(jsonList)){
                fileName = versionTestDelegate.createZip(jsonList,downloadSingleFile);
                if(StringUtils.isNotBlank(fileName)){
                    restResponse.setResponse(fileName);
                }else{
                    restResponse.setError(true);
                    restResponse.setMessage("File could not be created!");
                }
            }else{
                restResponse.setError(true);
                restResponse.setErrorCode("EXPL000001");
                restResponse.setMessage("Improper data uploaded! Empty or not conferring to template.");
            }
        }catch (BusinessException e) {
            LOGGER.error("Error while parsing the excel file : ", e);
            restResponse.setError(true);
            restResponse.setErrorCode(e.getCode());
            restResponse.setMessage(e.getLocalizedMessage());
            
        } catch (SystemException | IOException e) {
            LOGGER.error("Error while parsing the excel file : ", e);
            LOGGER.error("Error while parsing the excel file : ", e.getLocalizedMessage());
            restResponse.setError(true);
            restResponse.setMessage(e.getMessage());
        }
        return restResponse;
    }    

    @RequestMapping(value = "/downloadZipFile/{zipFileName}", method = RequestMethod.GET)
    @ResponseBody
    public void downloadZipFile(@PathVariable(ZIP_FILE_NAME) String fileName, HttpServletResponse response) {
        setResponseHeader(response, BusinessConstants.BATCH_REQUESTS + fileName + BusinessConstants.EXTN_ZIP);
        try {
            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
            versionTestDelegate.getZipFile(zos, fileName);
            zos.finish();
            zos.close();
            response.getOutputStream().flush();
        } catch (BusinessException | SystemException | IOException e) {
            LOGGER.error("Error while writing to data into outputstream : ", e);

        } finally {
            try {
                response.getOutputStream().close();
            } catch (IOException e) {
                LOGGER.error("Error while closing the ouput stream ",e);
            }
        }
    }

    private void setResponseHeader(HttpServletResponse response, String zipFileName) {
        response.setHeader("Content-Type", "application/zip");
        response.setHeader("Content-Disposition", "attachment;filename=" + zipFileName);
    }

    private boolean isNotExcelFile(String fileName) {
        return !(getExtension(fileName).equalsIgnoreCase("xls") || getExtension(fileName).equalsIgnoreCase("xlsx"));
    }
    
    private boolean isNotJsonFile(String fileName) {
        return !(getExtension(fileName).equalsIgnoreCase("json"));
    }
    
    private boolean isNotValidFileName(String fileName) {
    	boolean isNotAValidFileName = true;
    	try{
    	if(fileName.contains(DOT)){
    		String ext = fileName.substring(fileName.length() - JSON_TYPE.length(), fileName.length());
    		if(ext.equalsIgnoreCase(JSON_TYPE) && fileName.contains(HYPHEN)){
    				final String sequenceNo = fileName.substring(fileName.lastIndexOf(HYPHEN) + 1, fileName.length() - JSON_TYPE.length());
    				final int seqNo = Integer.valueOf(sequenceNo);
    				String withoutSequence = fileName.substring(0, fileName.lastIndexOf(HYPHEN));  
    				if(withoutSequence.contains(HYPHEN)){
    					final long dateTime = Long.valueOf(withoutSequence.substring(withoutSequence.lastIndexOf(HYPHEN) + 1 , withoutSequence.length()));
    					final String withoutDate = withoutSequence.substring(0, withoutSequence.lastIndexOf(HYPHEN)); 
    					final int version = Integer.valueOf(withoutSequence.substring(withoutDate.lastIndexOf(HYPHEN) + 1, withoutDate.length()));
    					if(withoutDate.contains(HYPHEN) && seqNo!=0  && dateTime != 0 && version != 0){
    						isNotAValidFileName = false;
    					}
    				}
    			}
    		}
    	}
    	catch (NumberFormatException e){
    		isNotAValidFileName = true;
    	}
    	return isNotAValidFileName;
    }

    /**
     * This method is called for test batch execution
     * The input file is put in batch/test folder
     * @param syndicateDataFile
     * @return
     */
    
    @RequestMapping(value = "/executeExcelFile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<String> executeExcelFile(@RequestPart MultipartFile excelFile) {
    	String batchId = null;
        String tenantUrl = null;
        RestResponse<String> restResponse = new RestResponse<>();
        try {
         
            tenantUrl = VersionControllerHelper.getTenantBaseUrl(tenantDelegate);
            
            
            StringBuffer fileName = new StringBuffer(FilenameUtils.getBaseName(excelFile.getOriginalFilename()));
            fileName.append(BusinessConstants.CHAR_HYPHEN)
            .append(DateTime.now().getMillis())
            .append(BusinessConstants.DOT)
            .append(getExtension(excelFile.getOriginalFilename()));
            batchingDelegate.saveExcelFile(excelFile, fileName.toString());
            batchId = batchingDelegate.executeBatchAsync(tenantUrl, fileName.toString(), excelFile.getInputStream());
            restResponse.setResponse(batchId);
        } catch (BusinessException e) {
            restResponse.setError(true);
            restResponse.setErrorCode(e.getCode());
            restResponse.setMessage(e.getLocalizedMessage());
            
        } catch (SystemException | IOException e) {
            LOGGER.error("Excel File execution failed : ", e);
            restResponse.setError(true);
            restResponse.setMessage(e.getMessage());
        }
        return restResponse;
    }  
    
    @RequestMapping(value = "/executeBulkJsonFile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RestResponse<String> executeJsonFile(@RequestPart MultipartFile jsonFile) {
        RestResponse<String> restResponse = new RestResponse<>();
        try {
        	
        	// Business
            if (isNotJsonFile(jsonFile.getOriginalFilename())) {
                throw new BusinessException(BusinessExceptionCodes.BSE0000151, new String[] { "not an Json file, file extension is not matching " });
            }
            if( isNotValidFileName(jsonFile.getOriginalFilename())){
            	 throw new BusinessException(BusinessExceptionCodes.BSE0000152, new String[] { "Bulk file name should be in format <MODEL-NAME>-<MAJOR-VERSION>-<Datetime>-<Seq-Num>.json" });
            }
            
            batchingDelegate.saveBulkFile(jsonFile, jsonFile.getOriginalFilename());
            restResponse.setError(false);
            restResponse.setMessage("File uploaded successfully");
        } catch (BusinessException e) {
            restResponse.setError(true);
            restResponse.setErrorCode(e.getCode());
            restResponse.setMessage(e.getLocalizedMessage());
            
        } catch (SystemException e) {
            LOGGER.error("Json File execution failed : ", e);
            restResponse.setError(true);
            restResponse.setMessage(e.getMessage());
        }
        return restResponse;
    }  
}