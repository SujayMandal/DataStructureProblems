package com.ca.umg.rt.util;

import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;

/**
 * @author basanaga
 * 
 */
public final class RuntimeBatchUtil {

    private RuntimeBatchUtil() {
    }

    /**
     * This method used to remove the batchmode and timestamp and provide the original filename
     * 
     * @param messageName
     * @return
     */
    public static String getRemoteFileName(String messageName) {
        String withoutTrsnp = null;
        String withoutTime = null;
        StringBuffer fileName = null;
        if (messageName != null) {
            withoutTime = messageName.substring(0, StringUtils.lastIndexOf(messageName, RuntimeConstants.CHAR_UNDERSCORE));
            withoutTrsnp = withoutTime.substring(0, StringUtils.lastIndexOf(withoutTime, RuntimeConstants.CHAR_UNDERSCORE));
            fileName = new StringBuffer(withoutTrsnp).append(RuntimeConstants.CHAR_DOT).append(
                    FilenameUtils.getExtension(messageName));
        }
        return fileName != null ? fileName.toString() : null;
    }
    
    public static String getOutputFileName(String messageName) {
        String withoutTrsnp = null;
        String withoutTime = null;
        StringBuffer fileName = null;
        String withoutErrSucc = null;
        if (messageName != null) {
        	withoutErrSucc = substringBeforeLast(messageName, RuntimeConstants.CHAR_UNDERSCORE);
            withoutTime = withoutErrSucc.substring(0, StringUtils.lastIndexOf(withoutErrSucc, RuntimeConstants.CHAR_UNDERSCORE));
            withoutTrsnp = withoutTime.substring(0, StringUtils.lastIndexOf(withoutTime, RuntimeConstants.CHAR_UNDERSCORE));
            fileName = new StringBuffer(withoutTrsnp).append(RuntimeConstants.CHAR_DOT).append(
                    FilenameUtils.getExtension(messageName));
        }
        return fileName != null ? fileName.toString() : null;
    }


}
