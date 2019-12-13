package com.ca.umg.rt.cache.listener;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.pool.PoolObjectsLoader;
import com.ca.umg.rt.batching.delegate.BatchingDelegate;
import com.ca.umg.rt.cache.event.LruEvent;
import com.ca.umg.rt.timer.BatchLru;
import com.ca.umg.rt.timer.LruContainer;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * @author raddibas
 * 
 */

public class LruEventListener implements MessageListener<Object> {

    @Inject
    private LruContainer container;

    private static final Logger LOGGER = LoggerFactory.getLogger(LruEventListener.class);
    private static final Executor MESSAGEEXECUTER = Executors.newSingleThreadExecutor();
    public static final String LRU_BATCH_TOPIC = "lru-batch";
    private CacheRegistry cacheRegistry;

    @Inject
    private BatchingDelegate batchingDelegate;
    
    @Inject
    private PoolObjectsLoader poolObjectsLoader;

    private ITopic<Object> topic;

    public void init() {
        topic = cacheRegistry.getTopic(LRU_BATCH_TOPIC);
        topic.addMessageListener(this);
    }

    @Override
    public void onMessage(final Message<Object> message) {
        final LruEvent event = (LruEvent) message.getMessageObject();
        MESSAGEEXECUTER.execute(new Runnable() {
            @Override
            public void run() {
                RequestContext reqeustContext = null;
                BatchLru batchLru = container.removeBatchLRU(event.getBatchId());
                try {
                    Properties properties = new Properties();
                    properties.put(RequestContext.TENANT_CODE, batchLru.getTenantCode());
                    reqeustContext = new RequestContext(properties);
                    String status= StringUtils.EMPTY;
                    if (event.getStatus().equalsIgnoreCase(LruEvent.SUCCESS)) {
                    	if(batchLru.getBatchCnt()==batchLru.getSuccessCount()){
                    		status = TransactionStatus.SUCCESS.getStatus();                    		
                    	}else if(batchLru.getBatchCnt()==batchLru.getFailureCount()){
                    		status = TransactionStatus.FAILURE.getStatus();                     		
                    	}else{
                    		status = TransactionStatus.PARTIAL_SUCCESS.getStatus();                   		
                    	}
                        batchingDelegate.updateBatch(event.getBatchId(), batchLru.getBatchCnt(), batchLru.getSuccessCount(),
                                batchLru.getFailureCount(), status);
                        updatePoolBatchStatus(batchLru.getPoolName(), status);
                        LOGGER.info("in lru batch listener success for batch id : " + event.getBatchId() + " processed count : "
                                + event.getProcessedCount());
                    } else if (event.getStatus().equalsIgnoreCase(LruEvent.TIMEOUT)) {
                        batchingDelegate.updateBatch(event.getBatchId(), batchLru.getBatchCnt(), batchLru.getSuccessCount(),
                                batchLru.getFailureCount(), TransactionStatus.TIMEOUT.getStatus());
                        LOGGER.info("in lru batch listener invalid for batch id : " + event.getBatchId() + " processed count : "
                                + event.getProcessedCount());
                        updatePoolBatchStatus(batchLru.getPoolName(), TransactionStatus.TIMEOUT.getStatus());
                    } else if (event.getStatus().equalsIgnoreCase(LruEvent.TERMINATED)) {
                        batchingDelegate.updateBatch(event.getBatchId(), batchLru.getBatchCnt(), batchLru.getSuccessCount(),
                                batchLru.getFailureCount(), TransactionStatus.TERMINATED.getStatus());
                        LOGGER.info("in lru batch listener terminated for batch id : " + event.getBatchId() + " processed count : "
                                + event.getProcessedCount());
                        updatePoolBatchStatus(batchLru.getPoolName(), TransactionStatus.TERMINATED.getStatus());
                    }
                } catch (SystemException | BusinessException exp) {
                    LOGGER.error(String.format("Batch Id :: %s, Error occured while updating batch status. Exception msg :: %s",
                            event.getBatchId(), exp));
                } finally {
                	LOGGER.error("reqeustContext :"+reqeustContext);
                	if(reqeustContext != null){
                		reqeustContext.destroy();
                	}
                }
            }
        });
    }

    public CacheRegistry getCacheRegistry() {
        return cacheRegistry;
    }

    public void setCacheRegistry(CacheRegistry cacheRegistry) {
        this.cacheRegistry = cacheRegistry;
    }

    public ITopic<Object> getTopic() {
        return topic;
    }
    
    public void updatePoolBatchStatus(final String poolName, final String batchPoolStatus) {
    	poolObjectsLoader.updatePoolStatus(poolName, batchPoolStatus);
    }
}