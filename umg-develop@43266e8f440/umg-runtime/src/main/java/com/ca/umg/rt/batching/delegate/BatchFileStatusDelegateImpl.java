package com.ca.umg.rt.batching.delegate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.rt.batching.bo.BatchFileStatusBOImpl;
import com.ca.umg.rt.batching.data.BatchFileStatusInput;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;

/**
 * @author basanaga
 * 
 */
@Named
public class BatchFileStatusDelegateImpl implements BatchFileStatusDelegate {

    @Inject
    private BatchFileStatusBOImpl batchFileStatusBOImpl;

    @Override
    public List<Map<String, Object>> getBatchFileStatus(BatchFileStatusInput batchFileStatusInput) throws BusinessException {        
        long dateinMillis = 0;
        try {
            if (batchFileStatusInput.getDate() != null) {
                DateFormat dateFormat = new SimpleDateFormat(RuntimeConstants.DATE_FORMAT, Locale.getDefault());
                dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
                Date searchDate = dateFormat.parse(batchFileStatusInput.getDate());
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                Date gmtDate = dateFormat.parse(dateFormat.format(searchDate));
                dateinMillis = gmtDate.getTime();
            }
        } catch (ParseException exception) {
            throw new BusinessException(RuntimeExceptionCode.RSE000509, new Object[] { exception.getMessage() });// NOPMD
        }
        return batchFileStatusBOImpl.getBatcheStatusByFileNameAndStartTime(prepareFileName(batchFileStatusInput.getFileName()),
                dateinMillis);
    }

    private String prepareFileName(String batchFileName) {
        String extension = FilenameUtils.getExtension(batchFileName);
        String baseName = FilenameUtils.getBaseName(batchFileName);
        StringBuffer fileName = new StringBuffer();
        fileName.append(RuntimeConstants.PERCENTILE).append(baseName).append(RuntimeConstants.PERCENTILE)
                .append(RuntimeConstants.CHAR_UNDERSCORE)
                .append(RuntimeConstants.CHAR_DOT).append(extension);
        return fileName.toString();
    }

}
