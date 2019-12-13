/**
 * 
 */
package com.ca.umg.rt.file.lock;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.file.locking.AbstractFileLockerFilter;

import com.ca.framework.core.cache.registry.CacheRegistry;

/**
 * @author chandrsa
 * 
 */
public class BatchFileLocker extends AbstractFileLockerFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchFileLocker.class);

    private final static String APPENDER = "-batchLock";

    private CacheRegistry cacheRegistry;
    
    /**
     * Tenant Name
     */
    private String name;

    /**
     * @return
     */
    public Map<Object, Object> getCache() {
        return cacheRegistry.getMap(this.getName() + APPENDER);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.integration.file.FileLocker#lock(java.io.File)
     */
    @Override
    public boolean lock(File fileToLock) {
        boolean locked = false;
        if (isLockable(fileToLock)) {
            getCache().put(fileToLock.getName(), true);
            locked = true;
            LOGGER.error(String.format("%s Locked for processing. Tenant Name : %s", fileToLock.getName(), this.getName()));
        }
        return locked;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.integration.file.FileLocker#isLockable(java.io.File)
     */
    @Override
    public boolean isLockable(File file) {
        LOGGER.error(String.format("%s Checking if lockable for processing. Tenant Name : %s. Lockable %s", file.getName(),
                this.getName(), !getCache().containsKey(file.getName())));
        return !getCache().containsKey(file.getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.integration.file.FileLocker#unlock(java.io.File)
     */
    @Override
    public void unlock(File fileToUnlock) {
        if (!isLockable(fileToUnlock)) {
            getCache().remove(fileToUnlock.getName());
            LOGGER.error(String.format("%s Unlocked after processing. Tenant Name : %s", fileToUnlock.getName(), this.getName()));
        }
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

}
