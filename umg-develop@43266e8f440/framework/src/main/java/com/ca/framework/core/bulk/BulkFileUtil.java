package com.ca.framework.core.bulk;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.bulk.info.BulkFileInfo;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;

public final class BulkFileUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkFileUtil.class);
    
    private BulkFileUtil () {
    }

    /**
     * Splits the model request file name and provides model name and major version of the bulk request.
     * 
     * @param fileName
     * @return
     * @throws SystemException
     */
    public static BulkFileInfo getBulkFileInfo (String fileName) throws SystemException {
        BulkFileInfo bulkFileInfo = null;
        checkFileExtensionIsJson (fileName);
        String[] fileSplitArray = StringUtils.splitByWholeSeparator(fileName, FrameworkConstant.HYPHEN);
        if (fileSplitArray.length != 4 ) {
            LOGGER.info("File format is not valid of file - ",fileName);
            throw new SystemException(FrameworkExceptionCodes.FVE0000201, new Object[] {fileName});
        } else {
            bulkFileInfo = new BulkFileInfo();
            bulkFileInfo.setModelName(fileSplitArray[0]);
            bulkFileInfo.setMajorVersion(fileSplitArray[1]);
        }
        return bulkFileInfo;
    }
    
    /**
     * Validates the file name extension, only .json files are allowed as bulk requests
     * 
     * @param fileName
     * @throws SystemException
     */
    public static void checkFileExtensionIsJson (String fileName) throws SystemException {
        String extn = StringUtils.substringAfterLast(fileName, FrameworkConstant.DOT);
        if (extn == null || StringUtils.isEmpty(extn) || !StringUtils.equals(extn, "json")) {
            LOGGER.info("File extension is not json for file - ",fileName);
            throw new SystemException(FrameworkExceptionCodes.FVE0000202, new Object[] {fileName});
        }
    }
}
