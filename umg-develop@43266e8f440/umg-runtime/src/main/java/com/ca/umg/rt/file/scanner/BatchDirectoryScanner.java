/**
 * 
 */
package com.ca.umg.rt.file.scanner;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.MessagingException;
import org.springframework.integration.file.DirectoryScanner;
import org.springframework.integration.file.FileLocker;
import org.springframework.integration.file.filters.FileListFilter;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.rt.batching.delegate.BatchingDelegate;

/**
 * @author chandrsa
 * 
 */
public class BatchDirectoryScanner implements DirectoryScanner {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchDirectoryScanner.class);

    private FileListFilter<File> filter;

    private FileLocker locker;

    private UmgFileProxy umgFileProxy;

    private SystemParameterProvider systemParameterProvider;
    
    private CacheRegistry cacheRegistry;
    
    private BatchingDelegate batchingDelegate;

    /* (non-Javadoc)
     * @see org.springframework.integration.file.DirectoryScanner#listFiles(java.io.File)
     */
    @Override
    public final List<File> listFiles(File directory) throws IllegalArgumentException {
        File files[] = null;
        File inputDirectory = directory;
        try {
            String sanPath = umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE));
            inputDirectory = new File(inputDirectory.getPath().replace(
                    inputDirectory.getParentFile().getParentFile().getParentFile().getPath(), sanPath));
            files = listEligibleFiles(inputDirectory);
            if (files == null) {
                throwException(inputDirectory);
            } else {
            	RequestContext reqeustContext = null;
            	Properties properties = new Properties();
                properties.put(RequestContext.TENANT_CODE, inputDirectory.getParentFile().getParentFile().getName());
                reqeustContext = new RequestContext(properties);
                try {
                	for(File file : files){
                		Map<String, Map<String, String>> batchInputFilesMap = cacheRegistry.getMap(FrameworkConstant.BATCH_INPUT_FILES_MAP);
                		final Lock lock = cacheRegistry.getDistributedLock(FrameworkConstant.LOCK_FOR_BATCH_INPUT_FILE);
                		lock.lock();
                		try {
                			if(MapUtils.isEmpty(batchInputFilesMap) || MapUtils.isEmpty(batchInputFilesMap.get(inputDirectory.getParentFile().getParentFile().getName())) || 
                					!batchInputFilesMap.get(inputDirectory.getParentFile().getParentFile().getName()).containsKey(file.getName())){
                				String batchId = batchingDelegate.createBatchEntry(file.getName(), inputDirectory.getParentFile().getParentFile().getName(), Boolean.FALSE);
                				Map<String, String> batchFiles = new HashMap<>();
                				if(!MapUtils.isEmpty(batchInputFilesMap) && !MapUtils.isEmpty(batchInputFilesMap.get(inputDirectory.getParentFile().getParentFile().getName()))){
                					batchFiles = batchInputFilesMap.get(inputDirectory.getParentFile().getParentFile().getName());
                				}
                				batchFiles.put(file.getName(), batchId);
                				batchInputFilesMap.put(inputDirectory.getParentFile().getParentFile().getName(), batchFiles);
                				cacheRegistry.getMap(FrameworkConstant.BATCH_INPUT_FILES_MAP).putAll(batchInputFilesMap);
                			}
                		} finally {
                			lock.unlock();
                		}
                	}
    			} catch (SystemException | BusinessException e) {
    				throwNewException(inputDirectory, e);
    			} finally {
    	            if (reqeustContext != null) {
    	                reqeustContext.destroy();
    	            }
    			}
            }

        } catch (SystemException e) {
            throwException(inputDirectory);
        }
        return (this.filter != null) ? this.filter.filterFiles(files) : Arrays.asList(files);
    }

    private void throwException(File directory) {
        LOGGER.error("The path [" + systemParameterProvider.getParameter(SystemConstants.SAN_BASE)
                + "] does not denote a properly accessible directory.");
            throw new MessagingException("The path [" + directory + "] does not denote a properly accessible directory.");
    }
    
    private void throwNewException(File directory, Exception e) {
        LOGGER.error("Exception during creating batch entery of files from " + directory + ". Exception : " + e.getLocalizedMessage());
            throw new MessagingException("Exception during creating batch entery of files from " + directory + ". Exception : " + e.getLocalizedMessage());
    }

    /**
     * Subclasses may refine the listing strategy by overriding this method. The files returned here are passed onto the filter.
     * 
     * @param directory
     *            root directory to use for listing
     * @return the files this scanner should consider
     */
    protected File[] listEligibleFiles(File directory) {
        return directory.listFiles();
    }

    @Override
    public void setFilter(FileListFilter<File> fileListFilter) {
        this.filter = fileListFilter;
    }

    @Override
    public void setLocker(FileLocker fileLocker) {
        this.locker = fileLocker;
    }
    
    public UmgFileProxy getUmgFileProxy() {
        return umgFileProxy;
    }

    public void setUmgFileProxy(UmgFileProxy umgFileProxy) {
        this.umgFileProxy = umgFileProxy;
    }

    public SystemParameterProvider getSystemParameterProvider() {
        return systemParameterProvider;
    }

    public void setSystemParameterProvider(SystemParameterProvider systemParameterProvider) {
        this.systemParameterProvider = systemParameterProvider;
    }
    
    public CacheRegistry getCacheRegistry() {
        return cacheRegistry;
    }

    public void setCacheRegistry(CacheRegistry cacheRegistry) {
        this.cacheRegistry = cacheRegistry;
    }
    
    public BatchingDelegate getBatchingDelegate() {
		return batchingDelegate;
	}

	public void setBatchingDelegate(BatchingDelegate batchingDelegate) {
		this.batchingDelegate = batchingDelegate;
	}

    @Override
    public boolean tryClaim(File file) {
        LOGGER.error(String.format("Trying claim on file :: %s.", file.getName()));
        return (this.locker == null) || this.locker.lock(file);
    }

}
