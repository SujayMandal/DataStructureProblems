package com.ca.umg.rt.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.ca.framework.core.bulk.BulkFileUtil;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.tranasction.ExecutionGroupEnum;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.pool.TransactionMode;
import com.ca.umg.rt.endpoint.http.Header;
import com.ca.umg.rt.endpoint.http.ModelRequest;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public final class RequestValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestValidator.class);
    
    private static final String STORE_RLOGS = "storeRLogs";
    private static final String VALUE_TRUE ="true";
    private static final String VALUE_FALSE ="false";
    @SuppressWarnings("unchecked")
    public static ModelRequest validateRequest(String request, String sanBase, KeyValuePair<String, byte[]> bytesOfFile)
            throws SystemException, BusinessException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        ModelRequest modelRequest = null;

        try {
            long startTime = System.currentTimeMillis();
            mapper.configure(Feature.ALLOW_COMMENTS, true);
            mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            LinkedHashMap<String, Object> obj = mapper.readValue(request, new TypeReference<LinkedHashMap<String, Object>>() {
            });
            LOGGER.debug("JSON transformation time: " + (System.currentTimeMillis() - startTime));
            validateData(obj);
            Map<String, Object> header = (Map<String, Object>) obj.get(MessageVariables.HEADER);
            if (header == null || header.isEmpty()) {
                throw new SystemException(RuntimeExceptionCode.RVE000202, new Object[] { "Request header not found" }); // NOPMD
            }
            modelRequest = new ModelRequest();
            Header headerObj = new Header();
            validateAndSetMdlNmMjrVrsn(headerObj, header);

            // code for validation of file
            String fileName = (String) header.get(MessageVariables.FILE_NAME);
            if (StringUtils.isNotEmpty(fileName)) {
                validateAndSetFileName(headerObj, fileName, sanBase);
                Map<String, Object> hdrAndDataMapFromFile = validateAndGetDataFromFile(fileName, sanBase, headerObj, bytesOfFile);
                if (hdrAndDataMapFromFile != null) {
                    header = (Map<String, Object>) hdrAndDataMapFromFile.get(MessageVariables.HEADER);
                }
            }

            final String batchId = (String) header.get(MessageVariables.BATCH_ID);
            validateAndSetMnrVrsnTranId(headerObj, header);

            // added this to fix umg-4251 to set versionCreationTest flag to true
            // if it is test transaction during version creation else the flag will be false
            Boolean isVersionCreationTest = Boolean.FALSE;
            if (header.get(MessageVariables.VERSION_CREATION_TEST) != null) {
                isVersionCreationTest = (Boolean) header.get(MessageVariables.VERSION_CREATION_TEST);
            }

            headerObj.setBatchId(batchId);
            headerObj.setVersionCreationTest(isVersionCreationTest);

            // added to fix UMG-4500 Additional variables in Transaction header
            // adding validation for UMG-4612
            setUsrTransTypExecGrp(header, headerObj);
            setHeaderObj(header, headerObj);
            if (StringUtils.isEmpty(headerObj.getModelName()) || headerObj.getMajorVersion() == null
                    || headerObj.getMajorVersion() == 0 || StringUtils.isEmpty(headerObj.getTransactionId())) {
                throw new SystemException(RuntimeExceptionCode.RVE000202,
                        new Object[] { String.format("%s, %s, %s  are mandatory header fields", MessageVariables.MODEL_NAME,
                                MessageVariables.MAJOR_VERSION, MessageVariables.TRANSACTION_ID) });
            }

            String date = (String) header.get(MessageVariables.DATE);
            if (!StringUtils.isEmpty(date)) {
                try {
                    DateTimeFormatter format = ISODateTimeFormat.dateHourMinuteSecondMillis().withZoneUTC();
                    DateTime dateTime = format.parseDateTime((String) date);
                    headerObj.setDate(dateTime);
                } catch (UnsupportedOperationException | IllegalArgumentException e) // NOPMD
                {
                    LOGGER.error("Error while converting request date", e);
                    throw new BusinessException(RuntimeExceptionCode.RVE000701, new Object[] { e.getMessage() });// NOPMD
                }
            }

            String channel = isVersionCreationTest ? MessageVariables.ChannelType.HTTP.getChannel()
                    : StringUtils.isBlank(fileName) ? MessageVariables.ChannelType.HTTP.getChannel()
                            : MessageVariables.ChannelType.FILE.getChannel();
            headerObj.setChannel(channel);

            modelRequest.setHeader(headerObj);
            modelRequest.setData((Map<Object, Object>) obj.get(MessageVariables.DATA));
        } catch (IOException objEx) {
            throw new SystemException(RuntimeExceptionCode.RVE000210, new Object[] { "Invalid JSON", objEx.getMessage() }, objEx);// NOPMD
        }
        return modelRequest;
    }

    private static void setHeaderObj(Map<String, Object> header, Header headerObj) throws SystemException {
    	 List<String> addOnValidations = null;
        if (header.get(FrameworkConstant.ADD_ON_VALIDATION) != null) {
        	if(header.get(FrameworkConstant.ADD_ON_VALIDATION) instanceof String){
        		String addOnValidation = (String) header.get(FrameworkConstant.ADD_ON_VALIDATION); 
        		String[] addOnValidationsStr = addOnValidation.substring(addOnValidation.indexOf('[')+2,addOnValidation.indexOf(']')-1).split(FrameworkConstant.COMMA);
        		addOnValidations = Arrays.asList(addOnValidationsStr);
        	}else{
        	       addOnValidations = (List<String>) header.get(FrameworkConstant.ADD_ON_VALIDATION);
        	}
            if (!CollectionUtils.isEmpty(addOnValidations) && !(addOnValidations.contains("ModelOutput") ||addOnValidations.contains("AcceptableValues")))  {
                throw new SystemException(RuntimeExceptionCode.RVE000202,
                        new Object[] { String.format(
                                "Incorrect value received for %s header field in Tenant Input. Acceptable values are ModelOutput,AcceptableValues and blank",
                                FrameworkConstant.ADD_ON_VALIDATION) });
            } else {
                headerObj.setAddonValidation(addOnValidations);
            }
        }

        if (header.get(MessageVariables.PAYLOAD_STORAGE)!=null && !(header.get(MessageVariables.PAYLOAD_STORAGE) instanceof String) && !(header.get(MessageVariables.PAYLOAD_STORAGE) instanceof Boolean)){      		
        		throw new SystemException(RuntimeExceptionCode.RVE000202,
                        new Object[] { "Accepted values for payloadStorage is true or false" });
        }

        if (header.get(MessageVariables.TRAN_MODE) != null) {
            String tranMode = (String) header.get(MessageVariables.TRAN_MODE);
            if (TransactionMode.BULK.getMode().equals(tranMode) || TransactionMode.ONLINE.getMode().equals(tranMode) || TransactionMode.BATCH.getMode().equals(tranMode)) {
                if (TransactionMode.ONLINE.getMode().equals(tranMode) && header.get(MessageVariables.FILE_NAME) != null) {
                    throw new SystemException(RuntimeExceptionCode.RVE000202,
                            new Object[] { "transactionMode and fileName should not give at a time" });
                }
                if (TransactionMode.BATCH.getMode().equals(tranMode) && header.get(MessageVariables.BATCH_ID)== null) {
                    throw new SystemException(RuntimeExceptionCode.RVE000202,
                            new Object[] { "not a valid batchId." });
                }
                headerObj.setTransactionMode(tranMode);
            } else {
                throw new SystemException(RuntimeExceptionCode.RVE000202,
                        new Object[] { "allowed transactionMode values are Online or Bulk" });
            }
            headerObj.setTransactionMode((String) header.get(MessageVariables.TRAN_MODE));
        } else {
            headerObj.setTransactionMode(TransactionMode.ONLINE.getMode());
        }
        
        if(headerObj.getFileName()!=null && header.get(MessageVariables.TRAN_MODE) !=null && 
        		StringUtils.equals((String)header.get(MessageVariables.TRAN_MODE),TransactionMode.ONLINE.getMode())){        	
        	   throw new SystemException(RuntimeExceptionCode.RVE000202,
                       new Object[] { "Bulk file with transactionMode as Online will not allow" });
        	
        }
    }

    /**
     * checks if file exists in input folder
     * 
     * @param headerObj
     * @param fileName
     * @param sanBase
     * @throws SystemException
     */
    private static void validateAndSetFileName(Header headerObj, String fileName, String sanBase) throws SystemException {
        BulkFileUtil.getBulkFileInfo(fileName);
        File file = getFileForBulk(sanBase, fileName);
        if (file != null && file.exists()) {
            headerObj.setFileName(fileName);
        } else {
            LOGGER.error("Unable to find the file - " + fileName + " in the input location");
            throw new SystemException(RuntimeExceptionCode.RVE000222, new Object[] { fileName });
        }
    }

    // checks if file content is -- empty/non-parsable/header/data empty
    // and request headers (model/version) matches model/version in header field of file
    private static Map<String, Object> validateAndGetDataFromFile(String fileName, String sanBase, Header headerObj,
            KeyValuePair<String, byte[]> bytesOfFile) throws SystemException {
        Map<String, Object> hdrAndDataMapFromFile = null;
        File file = getFileForBulk(sanBase, fileName);
        // File file = new File(sanBase);
        if (file != null) {
            try {
                byte[] bytesFromFile = Files.readAllBytes(file.toPath());
                if (bytesFromFile != null && bytesFromFile.length > 0) {
                    bytesOfFile.setKey("bytesOfFile");
                    bytesOfFile.setValue(bytesFromFile);
                    Map<String, Object> mapOfFile = getMapFromBytes(bytesFromFile);
                    if (headerAndDataExist(mapOfFile, fileName)
                            && modelnameAndVersionMatch(fileName, headerObj, (Map<String, Object>) mapOfFile.get("header"))) {
                        hdrAndDataMapFromFile = new HashMap<>();
                        hdrAndDataMapFromFile.put(MessageVariables.HEADER, (Map<String, Object>) mapOfFile.get("header"));
                        hdrAndDataMapFromFile.put(MessageVariables.DATA, (Map<Object, Object>) mapOfFile.get("data"));
                    }
                } else {
                    LOGGER.info("bulk file uploaded is empty");
                    throw new SystemException(RuntimeExceptionCode.RVE000225, new Object[] { fileName });
                }
            } catch (IOException e) {
                LOGGER.error("Error occured while reading the bulk file ", e);
                throw new SystemException(RuntimeExceptionCode.RVE000224, new Object[] {}, e);
            }
        }
        return hdrAndDataMapFromFile;
    }

    /**
     * checks is Model-name and version mentioned in request header match with header from file name
     * 
     * @param fileName
     * @param headerFromFile
     * @return
     * @throws SystemException
     */
    private static Boolean modelnameAndVersionMatch(String fileName, Header headerObj, Map<String, Object> headerFromFile)
            throws SystemException {
        Boolean matched = Boolean.TRUE;
        // BulkFileInfo bulkFileInfo = BulkFileUtil.getBulkFileInfo(fileName);
        if (!StringUtils.equalsIgnoreCase(headerObj.getModelName(), (String) headerFromFile.get(MessageVariables.MODEL_NAME))
                || !StringUtils.equals(Integer.toString(headerObj.getMajorVersion()),
                        (String) headerFromFile.get(MessageVariables.MAJOR_VERSION).toString())) {
            matched = Boolean.FALSE;
            LOGGER.info("Model-name and version mentioned in request header does not match with header from file name - ",
                    fileName);
            throw new SystemException(RuntimeExceptionCode.RVE000229, new Object[] {});
        }
        return matched;
    }

    /**
     * checks if the header and data sections of the file has any data in it
     * 
     * @param mapOfFile
     * @param fileName
     * @return
     * @throws SystemException
     */
    private static Boolean headerAndDataExist(Map<String, Object> mapOfFile, String fileName) throws SystemException {
        Boolean headerAndDataExist = Boolean.TRUE;
        if (mapOfFile != null) {
            Map<String, Object> headerFromFile = (Map<String, Object>) mapOfFile.get(MessageVariables.HEADER);
            Map<Object, Object> dataFromFile = (Map<Object, Object>) mapOfFile.get(MessageVariables.DATA);
            if (headerFromFile == null || headerFromFile.isEmpty() || dataFromFile == null || dataFromFile.isEmpty()) {
                headerAndDataExist = Boolean.FALSE;
                LOGGER.info("header or data is not specified in bulk input file - ", fileName);
                throw new SystemException(RuntimeExceptionCode.RVE000227, new Object[] { fileName });
            }
        } else {
            LOGGER.info("Json object created from file" + fileName + "is empty or null.");
            throw new SystemException(RuntimeExceptionCode.RVE000228, new Object[] { fileName });
        }
        return headerAndDataExist;
    }

    /**
     * gets the json object/map from bytes
     * 
     * @param dataBytes
     * @return
     * @throws SystemException
     */
    private static Map<String, Object> getMapFromBytes(byte[] dataBytes) throws SystemException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> dataMap = null;
        try {
            dataMap = mapper.readValue(dataBytes, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (IOException e) {
            LOGGER.error("Error while converting input data from file to json object", e);
            throw new SystemException(RuntimeExceptionCode.RVE000226, new Object[] {}, e);
        }
        return dataMap;
    }

    /**
     * gets the absolute path of file name and returns file object
     * 
     * @param sanBase
     * @param fileName
     * @return
     * @throws SystemException
     */
    private static File getFileForBulk(String sanBase, String fileName) throws SystemException {
        File file = null;
        if (StringUtils.isNotEmpty(sanBase)) {
            StringBuffer buffer = new StringBuffer(sanBase);
            buffer.append(File.separatorChar).append(RequestContext.getRequestContext().getTenantCode())
                    .append(File.separatorChar).append(MessageVariables.BULK_FILE).append(File.separatorChar)
                    .append(MessageVariables.INPUT_FOLDER).append(File.separatorChar).append(fileName);
            String absoluteFileName = buffer.toString();
            file = new File(absoluteFileName);
        } else {
            LOGGER.error("San base not available");
            throw new SystemException(RuntimeExceptionCode.RVE000223, new Object[] { fileName });
        }
        return file;
    }

    @SuppressWarnings("unchecked")
    private static void validateData(Map<String, Object> request) throws SystemException {
        if (!request.containsKey(MessageVariables.HEADER)) {
            throw new SystemException(RuntimeExceptionCode.RVE000211, new Object[] {});
        }
        Object header = request.get(MessageVariables.HEADER);
        if (!(header instanceof Map) || ((Map<String, Object>) header).isEmpty()) {
            throw new SystemException(RuntimeExceptionCode.RVE000212, new Object[] {});
        }
        Map<String, Object> headerMap = (Map<String, Object>) header;
        
        boolean isError=false;
    	StringJoiner invalidParams = new StringJoiner(FrameworkConstant.COMMA);
       String tempTranMode=headerMap.get(MessageVariables.TRAN_MODE)!=null?String.valueOf(headerMap.get(MessageVariables.TRAN_MODE)):null;
        if(StringUtils.isEmpty(tempTranMode) || StringUtils.equalsIgnoreCase(tempTranMode, TransactionMode.ONLINE.getMode()) ) {
        	 for (String key : headerMap.keySet()) {
                 if (!(key.equals(MessageVariables.TRANSACTION_ID) || key.equals(MessageVariables.MODEL_NAME)
                         || key.equals(MessageVariables.MAJOR_VERSION) || key.equals(MessageVariables.MINOR_VERSION)
                         || key.equals(MessageVariables.DATE) 
                         // added to fix UMG-4500 Additional variables in Transaction header
                         || key.equals(MessageVariables.USER) || key.equals(MessageVariables.TRANSACTION_TYPE) 
                         // added to fix UMG-4697
                         || key.equals(MessageVariables.EXECUTION_GROUP)  
                         || key.equals(FrameworkConstant.ADD_ON_VALIDATION) || key.equals(MessageVariables.PAYLOAD_STORAGE) 
                         || key.equals(MessageVariables.TRAN_MODE) ||  key.equals(STORE_RLOGS) || key.equals(MessageVariables.TENANT_TRAN_COUNT)
                         ||  key.equals(MessageVariables.CLIENT_ID) ||  key.equals(MessageVariables.VERSION_CREATION_TEST))) {
                	 isError=true;
                	 invalidParams.add(key); 
                 }
             } 
        }
        else {
	        for (String key : headerMap.keySet()) {
	            if (!(key.equals(MessageVariables.TRANSACTION_ID) || key.equals(MessageVariables.MODEL_NAME)
	                    || key.equals(MessageVariables.MAJOR_VERSION) || key.equals(MessageVariables.MINOR_VERSION)
	                    || key.equals(MessageVariables.DATE) || key.equals(MessageVariables.CORRELATION_ID)
	                    || key.equals(MessageVariables.BATCH_ID) || key.equals(MessageVariables.VERSION_CREATION_TEST) ||
	                    // added to fix UMG-4500 Additional variables in Transaction header
	                    key.equals(MessageVariables.USER) || key.equals(MessageVariables.TRANSACTION_TYPE) ||
	                    // added to fix UMG-4697
	                    key.equals(MessageVariables.EXECUTION_GROUP) || key.equals(MessageVariables.TENANT_TRAN_COUNT)
	                    || key.equals(MessageVariables.FILE_NAME) || key.equals(FrameworkConstant.ADD_ON_VALIDATION)
	                    || key.equals(MessageVariables.PAYLOAD_STORAGE) || key.equals(MessageVariables.TRAN_MODE)
	                    || key.equals(MessageVariables.CLIENT_ID) || key.equals(STORE_RLOGS))) {
	            	isError=true;
               	 	invalidParams.add(key);  
	            }
	        }
        }
        if(isError)
	   	{
	   		 throw new SystemException(RuntimeExceptionCode.RVE000213, new Object[] { invalidParams });
	   	}
        
        if (!request.containsKey(MessageVariables.DATA)) {
            throw new SystemException(RuntimeExceptionCode.RVE000214, new Object[] {});
        }
        Object data = request.get(MessageVariables.DATA);
        if (!(data instanceof Map)) {
            throw new SystemException(RuntimeExceptionCode.RVE000215, new Object[] {});
        }
    }

    private RequestValidator() {
    }

    private static void setUsrTransTypExecGrp(Map<String, Object> header, Header headerObj) throws BusinessException {
        String user = (String) header.get(MessageVariables.USER);
        if (StringUtils.isBlank(user)) {
            headerObj.setUser((String) header.get(MessageVariables.USER));
        } else {
            String regex = "^\\s*[@\\-_\\.\\w][@\\-_\\.\\w\\s]*$";
            if (!user.matches(regex)) {
                LOGGER.error("Only numbers, alphabets and special characters @ . - _ and space are allowed for User parameter");
                throw new BusinessException(RuntimeExceptionCode.RVE000202,
                        new Object[] { "Only numbers, alphabets and special characters @ . - _ and space are allowed for User parameter"});// NOPMD

            } 
            else {
                   headerObj.setUser((String) header.get(MessageVariables.USER));
            }
        }
        // adding validation for UMG-4611 & UMG-4610
        String transactionType = (String) header.get(MessageVariables.TRANSACTION_TYPE);
        if (StringUtils.isBlank(transactionType)) {
            headerObj.setTransactionType(transactionType);
        } else {
            transactionType = transactionType.trim().toLowerCase();
            if (StringUtils.equalsIgnoreCase(transactionType, "test") || StringUtils.equalsIgnoreCase(transactionType, "prod")) {
                headerObj.setTransactionType(transactionType);
            } else {
                LOGGER.error("Allowed values for Transaction_type - Test or Prod or blank. ");
                throw new BusinessException(RuntimeExceptionCode.RVE000202,
                        new Object[] { "Allowed values for Transaction_type - Test or Prod or blank" });// NOPMD
            }

        }

        // adding validation for UMG-4697
        String execGroup = (String) header.get(MessageVariables.EXECUTION_GROUP);
        if (StringUtils.isBlank(execGroup)) {
            headerObj.setExecutionGroup(MessageVariables.DEFAULT_EXECUTION_GROUP);
        } else {
            execGroup = execGroup.trim();
            if (ExecutionGroupEnum.isValid(execGroup)) {
                headerObj.setExecutionGroup(execGroup);
            } else {
                LOGGER.error("Execution group can be one of Benchmark, Modeled, Ineligible");
                throw new BusinessException(RuntimeExceptionCode.RVE000202,
                        new Object[] { "Execution group can be one of Benchmark, Modeled, Ineligible" });// NOPMD
            }

        }

        // adding validation for UMG-5348
        String fileName = (String) (headerObj != null ? headerObj.getFileName() : null);
        Integer tenantTranCount = (Integer) header.get(MessageVariables.TENANT_TRAN_COUNT);
        String transactionMode = (String)header.get(MessageVariables.TRAN_MODE);
        if ((tenantTranCount == null && !StringUtils.isBlank(fileName)) || (!StringUtils.isBlank(transactionMode) && StringUtils.equals(transactionMode, TransactionMode.BULK.getMode())  && tenantTranCount == null ) ) {
            LOGGER.error("tenantTranCount is Integer and mandatory for bulk transactions but optional for online/batch.");
            throw new BusinessException(RuntimeExceptionCode.RVE000202, new Object[] {
                    "tenantTranCount is Integer and mandatory for bulk transactions but optional for online/batch." });// NOPMD
        } else if (tenantTranCount != null) {
            headerObj.setTenantTranCount(tenantTranCount);
        }
    }

    private static boolean validateObjectType(Object toBeTested, Object expectedType) {
        boolean status = false;
        if (toBeTested == null) {
            status = true;
        } else if (toBeTested.getClass().toString().equals(expectedType.toString())) {
            status = true;
        }
        return status;
    }

    private static void validateAndSetMdlNmMjrVrsn(Header headerObj, Map<String, Object> header) throws SystemException {
        if (validateObjectType(header.get(MessageVariables.MODEL_NAME), String.class)) {
            String modelName = (String) header.get(MessageVariables.MODEL_NAME);
            headerObj.setModelName(modelName);
        } else {
            throw new SystemException(RuntimeExceptionCode.RVE000217, new Object[] { "Model Name should be a string" });
        }

        if (validateObjectType(header.get(MessageVariables.MAJOR_VERSION), Integer.class)) {
            Integer majorVersion = (Integer) header.get(MessageVariables.MAJOR_VERSION);
            headerObj.setMajorVersion(majorVersion);
        } else {
            throw new SystemException(RuntimeExceptionCode.RVE000217, new Object[] { "majorVersion should be an integer" });
        }
    }

    private static void validateAndSetMnrVrsnTranId(Header headerObj, Map<String, Object> header) throws SystemException {
        if (validateObjectType(header.get(MessageVariables.MINOR_VERSION), Integer.class)) {
            Integer minorVersion = (Integer) header.get(MessageVariables.MINOR_VERSION);
            headerObj.setMinorVersion(minorVersion);
        } else {
            throw new SystemException(RuntimeExceptionCode.RVE000217, new Object[] { "minorVersion should be an integer" });
        }

        if (validateObjectType(header.get(MessageVariables.TRANSACTION_ID), String.class)) {
        	String transactionId = (String) header.get(MessageVariables.TRANSACTION_ID);
        	if(!hasValidChars(transactionId)) {
        		throw new SystemException(RuntimeExceptionCode.RVE000202, new Object[] {"Special characters \" and / are not allowed in tenant transaction id"});
        	}
            headerObj.setTransactionId(transactionId);
        } else {
            throw new SystemException(RuntimeExceptionCode.RVE000217, new Object[] { "Transaction Id should be a string" });
        }
        
        if(header.containsKey(STORE_RLOGS)){
        	Object rLog = header.get(STORE_RLOGS);
        	if( ((rLog != null)  && (rLog instanceof String)))  {
        		if(!((StringUtils.equalsIgnoreCase(rLog.toString() , VALUE_TRUE)) || (StringUtils.equalsIgnoreCase(rLog.toString() , VALUE_FALSE)))){
        			throw new SystemException(RuntimeExceptionCode.RVE000217, new Object[] { "storeRLogs  should be a boolean" });
        		}
        	}
        	else if( ! (rLog instanceof Boolean)){
        		throw new SystemException(RuntimeExceptionCode.RVE000217, new Object[] { "storeRLogs  should be a boolean" });
        	}
        		boolean rLogstatus =  false;
        		if(rLog instanceof Boolean){
        			rLogstatus = (boolean) rLog;
        		} else {
        			if(StringUtils.equalsIgnoreCase(rLog.toString() , VALUE_TRUE)){
        				rLogstatus = true;
        			} else {
        				rLogstatus =  false;
        			}
        		}
        		headerObj.setStoreRLogs(rLogstatus);
        }
        putValueToMDC(headerObj);
    }

    private static void putValueToMDC(Header headerObj) {
        MDC.put(MessageVariables.MODEL_NAME, headerObj.getModelName());
        MDC.put(MessageVariables.MAJOR_VERSION,
                headerObj.getMajorVersion() != null ? String.valueOf(headerObj.getMajorVersion()) : "");
        MDC.put(MessageVariables.MINOR_VERSION,
                headerObj.getMinorVersion() != null ? String.valueOf(headerObj.getMinorVersion()) : "");
    }
    
    public static void validateHTMLTags(String request) throws SystemException {		
    	if (StringUtils.isEmpty(request)) {
			SystemException.newSystemException(RuntimeExceptionCode.RVE000705, new Object[] {});
		}
  		if(hasHTMLTags(request)){
  			SystemException.newSystemException(RuntimeExceptionCode.RVE000704, new String[] {});
  		}
  	}
      
      private static boolean hasHTMLTags(String text){
  		boolean flag =  false;
  		Matcher m;
  		for(Pattern p : patterns){
  			m = p.matcher(text);
  			if(m.matches()){
  				flag =  true;
  				break;
  			}  		
  		}
  		if(text.contains("<script>") || text.contains("<") || text.contains(">") ){
  			flag = true;
  		}
  	   
  	    return flag;
  	}

  	  
      @SuppressWarnings("PMD")
      private static Pattern[] patterns = new Pattern[]{
		// Script fragments
        Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
        // src='...'
        Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // lonely script tags
        Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // eval(...)
        Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // expression(...)
        Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // javascript:...
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        // vbscript:...
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        // onload(...)=...
        Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
  	};
      
      private static boolean hasValidChars(String value)
      {
    	  return (value!=null?(!(value.contains("\"") || value.contains("/"))):true) ;
      }
}
