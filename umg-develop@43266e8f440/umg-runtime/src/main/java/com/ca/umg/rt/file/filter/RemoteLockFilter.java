/**
 * 
 */
package com.ca.umg.rt.file.filter;

import java.io.File;
import java.util.Map;

import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.file.filters.AbstractFileListFilter;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.jcraft.jsch.ChannelSftp.LsEntry;

/**
 * @author chandrsa
 * 
 */
public class RemoteLockFilter<F> extends AbstractFileListFilter<F> {

    private static final double MAX_ALLOWED_FILE_SIZE_BYTES = 128 * 1024 * 1024;
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteLockFilter.class);

    private final static String APPENDER = "-remote-";

    private CacheRegistry cacheRegistry;
    /**
     * Tenant Name
     */
    private String name;

    private String transportType;

    /**
     * @return
     */
    public Map<Object, Object> getCache() {
        return cacheRegistry.getMap(this.getName() + APPENDER + this.getTransportType());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.integration.file.filters.AbstractFileListFilter#accept(java.lang.Object)
     */
    @Override
    protected boolean accept(F file) {
        boolean accept = false;
        String fileName = null;
        if (file instanceof File) {
            fileName = ((File) file).getName();
        } else if (file instanceof FTPFile) {// FTP and FTPS
            fileName = ((FTPFile) file).getName();
        } else if (file instanceof LsEntry) {// SFTP
            fileName = ((LsEntry) file).getFilename();
        }
        if (file != null && fileName != null 
                && (fileName.endsWith(".txt") || fileName.endsWith(".json") || fileName.endsWith(RuntimeConstants.XLSX_EXTN) || fileName
                        .endsWith(RuntimeConstants.XLS_EXTN)) && lock(fileName)) {
            accept = true;
            LOGGER.error(String.format("%s Accepted by filter", fileName));
        } else {
            LOGGER.error(String.format("%s Rejected by filter", fileName));
        }
        return accept;
    }

    private boolean lock(String fileName) {
        boolean locked = false;
        if (isLockable(fileName)) {
            getCache().put(fileName, true);
            locked = true;
            LOGGER.error(String.format("%s Locked for processing. Tenant Name : %s. Transport Type : %s", fileName,
                    this.getName(), this.getTransportType()));
        }
        return locked;
    }

    private boolean isLockable(String fileName) {
        LOGGER.error(String.format("%s Checking if lockable for processing. Tenant Name : %s. Lockable %s", fileName,
                this.getName(), !getCache().containsKey(fileName)));
        return !getCache().containsKey(fileName);
    }

    public void unlock(String fileName) {
        if (!isLockable(fileName)) {
            getCache().remove(fileName);
            LOGGER.error(String.format("%s Unlocked after processing. Tenant Name : %s", fileName, this.getName()));
        }
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

    public CacheRegistry getCacheRegistry() {
        return cacheRegistry;
    }

    public void setCacheRegistry(CacheRegistry cacheRegistry) {
        this.cacheRegistry = cacheRegistry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }
}
