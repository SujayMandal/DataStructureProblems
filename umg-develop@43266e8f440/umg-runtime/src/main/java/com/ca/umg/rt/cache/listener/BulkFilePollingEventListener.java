/**
 * 
 */
package com.ca.umg.rt.cache.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.event.BulkFilePollingEvent; 
import com.ca.umg.rt.batching.delegate.BatchingDelegate;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * Listens to bulk file pooling events raised by scheduler when a proper file is added.
 * @author mandasuj
 *
 */

@Named
public class BulkFilePollingEventListener implements MessageListener<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BulkFilePollingEventListener.class);

    @Inject
    private CacheRegistry cacheRegistry;
    
    @Inject
    private BatchingDelegate batchingDelegate;
 

    @PostConstruct
    public void init() {
    	ITopic<Object> bulkFileAddedTopic = cacheRegistry.getTopic(BulkFilePollingEvent.BULK_FILE_ADDED_EVENT);
    	bulkFileAddedTopic.addMessageListener(this);
    }

    @Override
    public void onMessage(Message<Object> message) {
        LOGGER.info("Receveid event to add file into BULK_INPUT_FILES_MAP.");
        BulkFilePollingEvent event = (BulkFilePollingEvent) message.getMessageObject();
        if (StringUtils.upperCase(event.getEvent()).equals(BulkFilePollingEvent.BULK_FILE_ADDED_EVENT)) {
         
        	RequestContext reqeustContext = null;
        	Properties properties = new Properties();
            properties.put(RequestContext.TENANT_CODE, event.getTenantCode());
            reqeustContext = new RequestContext(properties);
            try {
            	Map<String, Map<String, String>> bulkInputFilesMap = cacheRegistry.getMap(FrameworkConstant.BULK_INPUT_FILES_MAP);
        		final Lock lock = cacheRegistry.getDistributedLock(FrameworkConstant.LOCK_FOR_BULK_INPUT_FILE);
        		lock.lock();
        		try {
        			if(MapUtils.isEmpty(bulkInputFilesMap) || MapUtils.isEmpty(bulkInputFilesMap.get(event.getTenantCode())) || 
        					!bulkInputFilesMap.get(event.getTenantCode()).containsKey(event.getFileName())){
        				String batchId = batchingDelegate.createBatchEntry(event.getFileName(), event.getTenantCode(), Boolean.TRUE);
        				Map<String, String> bulkFiles = new HashMap<>();
        				if(!MapUtils.isEmpty(bulkInputFilesMap) && !MapUtils.isEmpty(bulkInputFilesMap.get(event.getTenantCode()))){ // NOPMD
        					bulkFiles = bulkInputFilesMap.get(event.getTenantCode());
        				}
        				bulkFiles.put(event.getFileName(), batchId);
        				bulkInputFilesMap.put(event.getTenantCode(), bulkFiles);
        				cacheRegistry.getMap(FrameworkConstant.BULK_INPUT_FILES_MAP).putAll(bulkInputFilesMap);
        			}
				} finally {
        			lock.unlock();
        		}
            } catch (SystemException | BusinessException e) {
            	LOGGER.error(e.getLocalizedMessage(), e);
            } finally {
	            if (reqeustContext != null) {
	                reqeustContext.destroy();
	            }
			}
            
        }
    }
	
}
