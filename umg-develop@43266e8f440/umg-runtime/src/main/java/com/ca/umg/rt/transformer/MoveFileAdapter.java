package com.ca.umg.rt.transformer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.pool.Channel;
import com.ca.pool.TransactionMode;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.util.JsonDataUtil;
import com.ca.umg.rt.util.MessageVariables;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * handles file movement operations
 * 
 * @author raddibas
 *
 */
@Named
@SuppressWarnings("PMD.UseObjectForClearerAPI")
public class MoveFileAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoveFileAdapter.class);
    
    private static final String IO_TIMESTAMP="ioTimeStamp"; 
    
    @Inject
    private CacheRegistry cacheRegistry;

    /**
     * copies the file from input folder to archive
     * 
     * @param message
     * @throws SystemException
     */
    public void copyToArchive(Message<?> message) throws SystemException {
        Map<String, Object> msgHeader = (Map<String, Object>) message.getHeaders();
        String channel = (String) msgHeader.get(MessageVariables.CHANNEL);
        String sanBase = (String) msgHeader.get(MessageVariables.SAN_PATH);
        String fileName = (String) msgHeader.get(MessageVariables.FILE_NAME_HEADER);
        File srcFile = getFileObject(sanBase, fileName, MessageVariables.INPUT_FOLDER, channel);
        File destFolder = getFileObject(sanBase, MessageVariables.ARCHIVE_FOLDER, channel);
        try {
            // delete file from archive directory if exists
            deleteFileIfExists(new File(destFolder, fileName));
            // copy file to archive directory
            FileUtils.copyFileToDirectory(srcFile, destFolder, false);
        } catch (IOException e) {
            LOGGER.error("Error occured while copying file" + fileName + " from input folder to archive folder ", e);
            throw new SystemException(RuntimeExceptionCode.RVE000230,
                    new Object[] { "copying", fileName, MessageVariables.INPUT_FOLDER, MessageVariables.ARCHIVE_FOLDER }, e);
        }
    }

    /**
     * moves the file from input to inprogress and deletes the file from input folder
     * 
     * @param message
     * @throws SystemException
     */
    public void moveToInPrgrsAndDelInpt(Message<?> message) throws SystemException {
        Map<String, Object> msgHeader = (Map<String, Object>) message.getHeaders();
        String channel = (String) msgHeader.get(MessageVariables.CHANNEL);
        String sanBase = (String) msgHeader.get(MessageVariables.SAN_PATH);
        String fileName = (String) msgHeader.get(MessageVariables.FILE_NAME_HEADER);
        File srcFile = getFileObject(sanBase, fileName, MessageVariables.INPUT_FOLDER, channel);
        File destFolder = getFileObject(sanBase, MessageVariables.INPROGRESS_FOLDER, channel);
        try {
            // delete file in target folder if exists
            deleteFileIfExists(new File(destFolder, fileName));
            // copy the file to archive directory delete file from input directory
            if(!moveFile(srcFile, destFolder)){
            	LOGGER.error("failed to delete File '" + srcFile.getName() + "'");
            } else {
            	Map<String, Map<String, String>> bulkInputFilesMap = cacheRegistry.getMap(FrameworkConstant.BULK_INPUT_FILES_MAP);
                if (MapUtils.isNotEmpty(bulkInputFilesMap) && MapUtils.isNotEmpty(bulkInputFilesMap.get(RequestContext.getRequestContext().getTenantCode())) && 
                		bulkInputFilesMap.get(RequestContext.getRequestContext().getTenantCode()).containsKey(srcFile.getName())) {
                    Map<String, String> files = bulkInputFilesMap.get(RequestContext.getRequestContext().getTenantCode());
                    files.remove(srcFile.getName());
                    bulkInputFilesMap.put(RequestContext.getRequestContext().getTenantCode(), files);
                    cacheRegistry.getMap(FrameworkConstant.BULK_INPUT_FILES_MAP).putAll(bulkInputFilesMap);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error occured while moving file" + fileName + " from input folder to inprogress folder ", e);
            throw new SystemException(RuntimeExceptionCode.RVE000230,
                    new Object[] { "moving", fileName, MessageVariables.INPUT_FOLDER, MessageVariables.INPROGRESS_FOLDER }, e);
        }
    }

    private boolean deleteFileIfExists(File targetFile) {
        boolean deleted = true;
        if (targetFile.exists()) {
            deleted = targetFile.delete();
        }
        return deleted;
    }

    private boolean moveFile(File srcFile, File destFolder) throws IOException {
    	// remove unnecessary header  
    	ObjectMapper objectMapper=new ObjectMapper();
		Map<String,Object> fileData= objectMapper.readValue(FileUtils.readFileToString(srcFile),Map.class); 
    	removeAdditionalHeaderParam(fileData);
    	FileUtils.write(srcFile, objectMapper.writeValueAsString(fileData));
        // copy the file to archive directory
        FileUtils.copyFileToDirectory(srcFile, destFolder, false);
        // delete file from input directory
        return srcFile.delete();
    }

    /**
     * moves the file from input folder to archive folder and creates a response file
     * 
     * @param sanBase
     * @param tenantResponse
     * @throws SystemException
     */
    public void moveInptToArchvAndWriteErrRespns(String sanBase, Map<String, Object> tenantResponse, String channel)
            throws SystemException {
        Map<String, Object> tenantResponseHdr = (Map<String, Object>) tenantResponse.get(MessageVariables.HEADER);
        String fileName = (String) tenantResponseHdr.get(MessageVariables.FILE_NAME);
        if (fileName != null) {
            moveFromInputToArchive(fileName, sanBase, channel);
            createResponseFile(fileName, sanBase, tenantResponse, FrameworkConstant.ERROR, channel);
        }

    }

    /**
     * moves file from input folder to archive
     * 
     * @param fileName
     * @param sanBase
     * @throws SystemException
     */
    private void moveFromInputToArchive(String fileName, String sanBase, String channel) throws SystemException {
        File srcFile = getFileObject(sanBase, fileName, MessageVariables.INPUT_FOLDER, channel);
        File destFolder = getFileObject(sanBase, MessageVariables.ARCHIVE_FOLDER, channel);
        try {
            if (srcFile.exists()) {
                if(!moveFile(srcFile, destFolder)){
                	LOGGER.error("failed to delete File '" + fileName + "'");
                } else {
                	Map<String, Map<String, String>> bulkInputFilesMap = cacheRegistry.getMap(FrameworkConstant.BULK_INPUT_FILES_MAP);
                    if (MapUtils.isNotEmpty(bulkInputFilesMap) && MapUtils.isNotEmpty(bulkInputFilesMap.get(RequestContext.getRequestContext().getTenantCode())) && 
                    		bulkInputFilesMap.get(RequestContext.getRequestContext().getTenantCode()).containsKey(fileName)) {
                        Map<String, String> files = bulkInputFilesMap.get(RequestContext.getRequestContext().getTenantCode());
                        files.remove(fileName);
                        bulkInputFilesMap.put(RequestContext.getRequestContext().getTenantCode(), files);
                        cacheRegistry.getMap(FrameworkConstant.BULK_INPUT_FILES_MAP).putAll(bulkInputFilesMap);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error occured while moving file" + fileName + " from input folder to archive folder ", e);
            throw new SystemException(RuntimeExceptionCode.RVE000230,
                    new Object[] { "moving", fileName, MessageVariables.INPUT_FOLDER, MessageVariables.ARCHIVE_FOLDER }, e);
        }
    }

    /**
     * writes the tenant response to ouput folder
     * 
     * @param fileName
     * @param sanBase
     * @param tenantResponse
     * @param errSuccsString
     * @throws SystemException
     */
    public void createResponseFile(String fileName, String sanBase, Map<String, Object> tenantResponse, String errSuccsString,
            String channel) throws SystemException {
        String fileNameWithoutExtn = StringUtils.substringBeforeLast(fileName, FrameworkConstant.DOT);
        StringBuffer stringBuffer = new StringBuffer(fileNameWithoutExtn);
        stringBuffer.append(FrameworkConstant.HYPHEN).append(errSuccsString).append(FrameworkConstant.DOT)
                .append(StringUtils.substringAfterLast(fileName, FrameworkConstant.DOT));
        File destFile = getFileObject(sanBase, stringBuffer.toString(), MessageVariables.OUTPUT_FOLDER, channel);
        try {
            String jsonErrorResponse = JsonDataUtil.convertToJsonStringPrettyPrint(tenantResponse);
            FileUtils.writeStringToFile(destFile, jsonErrorResponse);
        } catch (IOException e) {
            LOGGER.error("Error occured while writing file" + stringBuffer.toString() + " to output folder ", e);
            throw new SystemException(RuntimeExceptionCode.RVE000231,
                    new Object[] { "writing", stringBuffer.toString(), " to" + MessageVariables.OUTPUT_FOLDER }, e);
        }
    }

    public void delInprogsFileAndWriteResponse(String fileName, String sanBase, Map<String, Object> tenantResponse,
            Boolean transactionSuccess, String channel) throws SystemException {
        deleteFileFromInprogress(fileName, sanBase, channel);
        if (!transactionSuccess) {
            createResponseFile(fileName, sanBase, tenantResponse, FrameworkConstant.ERROR, channel);
        } else {
            createResponseFile(fileName, sanBase, tenantResponse, FrameworkConstant.OUTPUT, channel);
        }
    }

    /**
     * deletes the file from inprogress folder
     * 
     * @param fileName
     * @param sanBase
     * @throws SystemException
     */
    private void deleteFileFromInprogress(String fileName, String sanBase, String channel) throws SystemException {
        File inProgressFile = getFileObject(sanBase, fileName, MessageVariables.INPROGRESS_FOLDER, channel);
        if (!inProgressFile.delete()) {
            LOGGER.error("Error occured while deleting file" + fileName + " from inprogress folder ");
            throw new SystemException(RuntimeExceptionCode.RVE000231,
                    new Object[] { "deleting", fileName, " from " + MessageVariables.INPROGRESS_FOLDER });
        }
    }

    /**
     * gets the file object for given filename in sanpath folder
     * 
     * @param sanBase
     * @param fileName
     * @param folder
     * @return
     * @throws SystemException
     */
    public static File getFileObjectForFolder(String sanBase, String fileName, String folder) throws SystemException {
        File file = null;
        if (StringUtils.isNotEmpty(sanBase)) {
            StringBuffer buffer = new StringBuffer(sanBase);
            buffer.append(File.separatorChar).append(RequestContext.getRequestContext().getTenantCode())
                    .append(File.separatorChar).append(folder).append(File.separatorChar).append(fileName);
            String absoluteFileName = buffer.toString();
            file = new File(absoluteFileName);
        } else {
            throw new SystemException(RuntimeExceptionCode.RVE000223, new Object[] { fileName });
        }
        return file;
    }

    /**
     * gets the file object for given filename in sanpath folder
     * 
     * @param sanBase
     * @param fileName
     * @param folder
     * @return
     * @throws SystemException
     */
    private static File getFileObjectforBulkHTTP(String sanBase, String fileName, String folder) throws SystemException {
        File file = null;
        if (StringUtils.isNotEmpty(sanBase)) {
            StringBuilder buffer = new StringBuilder(sanBase);
            buffer.append(File.separatorChar).append(RequestContext.getRequestContext().getTenantCode())
                    .append(File.separatorChar).append(MessageVariables.BULK_HTTP).append(File.separatorChar).append(folder)
                    .append(File.separatorChar).append(fileName);
            String absoluteFileName = buffer.toString();
            file = new File(absoluteFileName);
        } else {
            throw new SystemException(RuntimeExceptionCode.RVE000223, new Object[] { fileName });
        }
        return file;
    }

    /**
     * gets the file object for given folder name in sanpath
     * 
     * @param sanBase
     * @param fileName
     * @param folder
     * @return
     * @throws SystemException
     */
    private static File getFileObject(String sanBase, String fileName, String folder, String channel) throws SystemException {
        File file = null;
        if (StringUtils.isNotEmpty(sanBase)) {
            StringBuilder buffer = new StringBuilder(sanBase);
            buffer.append(File.separatorChar).append(RequestContext.getRequestContext().getTenantCode())
                    .append(File.separatorChar)
                    .append(StringUtils.equalsIgnoreCase(channel, Channel.FILE.getChannel()) ? MessageVariables.BULK_FILE
                            : MessageVariables.BULK_HTTP)
                    .append(File.separatorChar).append(folder).append(File.separatorChar).append(fileName);
            String absoluteFileName = buffer.toString();
            file = new File(absoluteFileName);
        } else {
            throw new SystemException(RuntimeExceptionCode.RVE000223, new Object[] { fileName });
        }
        return file;
    }

    /**
     * gets the folder object for given filename in sanpath folder
     * 
     * @param sanBase
     * @param fileName
     * @param folder
     * @return
     * @throws SystemException
     */
    private static File getFileObject(String sanBase, String folder, String channel) throws SystemException {
        File file = null;
        if (StringUtils.isNotEmpty(sanBase)) {
            StringBuilder buffer = new StringBuilder(sanBase);
            buffer.append(File.separatorChar).append(RequestContext.getRequestContext().getTenantCode())
                    .append(File.separatorChar).append(StringUtils.equalsIgnoreCase(channel, Channel.FILE.getChannel())
                            ? MessageVariables.BULK_FILE : MessageVariables.BULK_HTTP)
                    .append(File.separatorChar).append(folder);
            String absoluteFileName = buffer.toString();
            file = new File(absoluteFileName);
        } else {
            throw new SystemException(RuntimeExceptionCode.RVE000223, new Object[] { folder });
        }
        return file;
    }

    /**
     * writes the data into bulk folder in Online Bulk transaction is failed
     * 
     * @param fileName
     * @param sanBase
     * @param tenantResponse
     * @param errSuccsString
     * @throws SystemException
     */
    public void saveInBulkHttpFolder(String fileName, String sanBase, Map<String, Object> object, String folderName)
            throws SystemException {
    	Map<String, Object> updatedObject  = object;
        File destFile = getFileObjectforBulkHTTP(sanBase, fileName, folderName); 
        try {
        	 if(fileName.contains(RuntimeConstants.TENANT_INPUT))
             {
        		 updatedObject=removeAdditionalHeaderParam(updatedObject);  
             }
            String jsonErrorResponse = JsonDataUtil.convertToJsonStringPrettyPrint(updatedObject);
            FileUtils.writeStringToFile(destFile, jsonErrorResponse);
        } catch (IOException e) {
            LOGGER.error("Error occured while writing file" + fileName, e);
            throw new SystemException(RuntimeExceptionCode.RVE000231, new Object[] { "writing", fileName, " to " + destFile }, e);
        }
    }

    /**
     * Deleting the data into bulk folder in Online Bulk transaction is failed
     * 
     * @param fileName
     * @param sanBase
     * @param tenantResponse
     * @param errSuccsString
     * @throws SystemException
     */
    public void deleteInBulkHttpFolder(String fileName, String sanBase, String folderName) throws SystemException {
        File destFile = getFileObjectforBulkHTTP(sanBase, fileName, folderName);
        try {
            destFile.delete();
        } catch (Exception e) {// NOPMD
            LOGGER.error("Error occured while deleting a file " + fileName, e);
            throw new SystemException(RuntimeExceptionCode.RVE000231,
                    new Object[] { "deleting", fileName, " from folder " + destFile }, e);
        }
    }

    /**
     * saves the file into folder
     * 
     * @param fileName
     * @param sanBase
     * @param file
     * @param folderName
     * @throws SystemException
     */
    public void saveInFolder(String fileName, String sanBase, Map<String, Object> object, String folderName)
            throws SystemException {
        File destFile = getFileObjectForFolder(sanBase, fileName, folderName);
        try {
            String jsonObject = JsonDataUtil.convertToJsonStringPrettyPrint(object);
            FileUtils.writeStringToFile(destFile, jsonObject);
        } catch (IOException e) {
            LOGGER.error("Error occured while writing file" + fileName, e);
            throw new SystemException(RuntimeExceptionCode.RVE000231, new Object[] { "writing", fileName, " to " + destFile }, e);
        }
    }

    /**
     * Deletes the file from folder
     * 
     * @param fileName
     * @param sanBase
     * @param tenantResponse
     * @param errSuccsString
     * @throws SystemException
     */
    public void deleteFromFolder(String fileName, String sanBase, String folderName) throws SystemException {
        File destFile = getFileObjectForFolder(sanBase, fileName, folderName);
        try {
            destFile.delete();
        } catch (Exception e) {// NOPMD
            LOGGER.error("Error occured while deleting a file " + fileName, e);
            throw new SystemException(RuntimeExceptionCode.RVE000231,
                    new Object[] { "deleting", fileName, " from folder " + destFile }, e);
        }
    }
    private Map<String,Object> removeAdditionalHeaderParam(Map<String,Object> fileData) throws IOException { 
    	if(fileData!=null)
    	{ 
    		Map<String,Object> header= (Map<String,Object>)fileData.get(MessageVariables.HEADER);
    		if(header!=null)
    		{
    			header.remove(MessageVariables.CLIENT_ID);
    			header.remove(IO_TIMESTAMP);
    			header.remove(MessageVariables.CHANNEL);
    			String txnType=String.valueOf(header.get(MessageVariables.TRAN_MODE)); 
    			if(txnType==null) {
    				txnType=TransactionMode.ONLINE.getMode();
    			}
    			switch(txnType)
    			{
    				case  MessageVariables.TRAN_BULK:
    					header.remove(MessageVariables.BATCH_ID);
    					break;
    				case  MessageVariables.TRAN_BATCH:
    					header.remove(MessageVariables.FILE_NAME);
    					break; 
    				default: // MessageVariables.TRAN_ONLINE
    					header.remove(MessageVariables.BATCH_ID);
    					header.remove(MessageVariables.FILE_NAME);
    					break;
    			}
    			fileData.put(MessageVariables.HEADER, header); 
    		}
    	}  
    	return fileData;
    }
	
}
