/**
 * 
 */
package com.ca.umg.rt.file.filter;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.file.filters.AbstractFileListFilter;

import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;

/**
 * @author chandrsa
 * 
 */
public class BatchFileFilter extends AbstractFileListFilter<File> {

    private static final double MAX_ALLOWED_FILE_SIZE_BYTES = 128 * 1024 * 1024;
    private static final double MIN_ALLOWED_FILE_SIZE_BYTES = 512;
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchFileFilter.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.integration.file.filters.AbstractFileListFilter#accept(java.lang.Object)
     */
    @Override
    protected boolean accept(File file) {
        boolean accept = false;
        if (file != null
                && (file.getName().endsWith(".txt") || file.getName().endsWith(".json")
                        || file.getName().endsWith(RuntimeConstants.XLSX_EXTN) || file
                        .getName().endsWith(RuntimeConstants.XLS_EXTN))) {
        	double bytes = file.length();
        	if(bytes >= MIN_ALLOWED_FILE_SIZE_BYTES){
        		accept = true;
        		LOGGER.error(String.format("%s Accepted by filter", file.getName()));
        	} else {
        		LOGGER.error(String.format("%s Rejected by filter because file size is %s bytes.", file.getName(), bytes));
        	}
        } else {
            LOGGER.error(String.format("%s Rejected by filter", file.getName()));
        }
        return accept;
    }

    /**
     * If the file size is greater than the MAX allowed. Error notification have to be triggered and the file should be moved into
     * ERROR directory.
     * 
     * @param file
     * @return
     */
    protected boolean checkFileSize(File file) {
        // TODO Notification system.
        boolean acceptSize = false;
        double bytes = file.length();
        if (bytes <= MAX_ALLOWED_FILE_SIZE_BYTES) {
            acceptSize = true;
        }
        return acceptSize;
    }
}
