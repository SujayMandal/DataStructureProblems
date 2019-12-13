package com.ca.umg.modelet.common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.lang.type.DataType;
import com.ca.umg.modelet.util.Modeletutil;

@SuppressWarnings("PMD")
public class RModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(RModel.class);
    private static final String TID = "tID";
    private static final String EXT_TXT = ".txt";
    private final REngineDelegator rEngineInvoker;
    private SystemInfo systemInfo;
    public RModel(final REngineDelegator rEngineInvoker, final SystemInfo systemInfo) {
        this.rEngineInvoker = rEngineInvoker;
        this.systemInfo = systemInfo;
    }

    public Map<String, Object> executeModel(final List<DataType> inputList , HeaderInfo headerInfo) throws SystemException {
        final long startTime = System.currentTimeMillis();
        String command = createRCommand(inputList);
        LOGGER.info("R command to be executed : {}", command);
        Map<String, Object> output = new HashMap<>();
        Object result = null;
        /* changes for UMG-5015 */
        try {
            result = rEngineInvoker.executeModel(command); 
            output.put("command", command); 
            output.put("result", result);
            output.put("rText", rEngineInvoker.getrText()); 
        } finally {
        	 if(headerInfo.isStoreRLogs()  || (headerInfo.getTransactionCriteria().getTransactionRequestType() != null && StringUtils.equalsIgnoreCase(headerInfo.getTransactionCriteria().getTransactionRequestType(), "test"))){
        	        try {
						FileUtils.writeStringToFile(Modeletutil.createLogFileLocation(systemInfo, headerInfo,MDC.get(TID)), rEngineInvoker.getrText());
						MDC.remove(TID);
					} catch (IOException e) {
						 LOGGER.error("ISSUE IN WRITING RJAVA LOG TO FILE");
					} 
        	 }
            final long endTime = System.currentTimeMillis();
            LOGGER.error("Actual R Model Execution time:" + (endTime - startTime));
        } 
        LOGGER.error("rText is ================= "+output.get("rText")); 
        return output;
    }

    private String createRCommand(final List<DataType> inputList) {
        final StringBuffer strCommand = new StringBuffer(); 
        final String tmp = rEngineInvoker.getModelKey().getModelMethod() + "("; 
        strCommand.append(tmp);

        if (inputList != null && !inputList.isEmpty()) { 
            for (int i = 0; i < inputList.size(); i++) {
                if (inputList.get(i) == null) {
                    strCommand.append("NA");
                } else {
                    strCommand.append(inputList.get(i).toNative());
                }
                if (i < inputList.size() - 1) {
                    strCommand.append(", ");
                }
            } 
        }

        strCommand.append(')'); 
        return strCommand.toString();
    }
    /*private File createLogFileLocation(SystemInfo systemInfo,HeaderInfo headerInfo){
    	
    	String location = systemInfo.getSanPath() + File.separator + headerInfo.getTenantCode();
    	String fileName = MDC.get(TID);
    	File file = new File(location + File.separator  +"rLog" +  File.separator + fileName + EXT_TXT);
    	return file;
    }*/
}