/**
 * 
 */
package com.ca.umg.modelet.util;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.MDC;

import com.ca.umg.modelet.common.HeaderInfo;
import com.ca.umg.modelet.common.SystemInfo;
import com.mathworks.toolbox.javabuilder.MWException;
import com.sun.management.OperatingSystemMXBean;

/**
 * @author kamathan
 *
 */
public final class Modeletutil {
    
    public static final String MDC_LOGGING_RA_TRANSACTION_ID = "umgTransactionId";
    public static final String MDC_LOGGING_MODEL_NAME = "modelName";
    public static final String MDC_LOGGING_TENANT_CODE = "TENANT_CODE";
    public static final String MDC_LOGGING_MODEL_VERSION = "modelVersion";
    public static final String EXT_TXT = ".txt";


    private Modeletutil() {

    }

    public static String extractModelException(Throwable th) {
        String errorMessage = th.getMessage();
        List<Throwable> throwables = ExceptionUtils.getThrowableList(th);
        for (Throwable throwable : throwables) {
            if (throwable instanceof MWException) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                throwable.printStackTrace(printWriter);
                errorMessage = stringWriter.toString();
            }
        }
        return errorMessage;
    }
    
    public static  String  getCpuLoad() {
    	OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    	String cpuLoad = String.format("%.2f",bean.getSystemCpuLoad());
    	double cpuLoadInPer = Double.valueOf(cpuLoad) * 100 ;
    	return String.format("%.2f",cpuLoadInPer);
    }

    public static long usedMemory(){
    	return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }
    
    public static String freeMemory(){
    	long freeMemory =  Runtime.getRuntime().maxMemory() - usedMemory();
    	double freeMemoreyInMB = freeMemory/(1024 * 1024) ;
    	return  String.format("%.2f",freeMemoreyInMB);
    }
    public static File createLogFileLocation(SystemInfo systemInfo,HeaderInfo headerInfo,String fileName){
    	
    	String location = systemInfo.getSanPath() + File.separator + headerInfo.getTenantCode();
/*    	String fileName = MDC.get(TID);
*/    	File file = new File(location + File.separator  +"rLog" +  File.separator + fileName + EXT_TXT);
    	return file;
    }
}
