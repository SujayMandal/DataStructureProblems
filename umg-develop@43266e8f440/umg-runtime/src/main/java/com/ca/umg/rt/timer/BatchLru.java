package com.ca.umg.rt.timer;

import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.rt.cache.event.LruEvent;
import com.ca.umg.rt.cache.listener.LruEventListener;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.hazelcast.core.IMap;

/**
 * @author raddibas TODO raddibas : Documentation Needed here.
 * 
 * starts the batch lru which starts resets the timer  
 */
public class BatchLru {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchLru.class);
    
    private BatchTimer task;
    private final Timer timer;
    private final String batchId;
    private final int batchCnt;
    private int counter;
    private final CacheRegistry cacheRegistry;
    private static final int COUNT_ONE = 1;
    private int successCount;
    private int failureCount;
    private final String tenantCode;
    private final SystemParameterProvider systemParameterProvider;
    private final Object lock = new Object();
    private final String poolName;

    public BatchLru(String batchId, int batchCnt, CacheRegistry cacheRgstry, String tenantCode, 
    		SystemParameterProvider systemParamProvider, final String poolName) {
        this.batchId = batchId;
        this.batchCnt = batchCnt;
        this.cacheRegistry = cacheRgstry;
        this.timer = new Timer(true);
        this.counter = 0;
        this.successCount = 0;
        this.failureCount = 0;
        this.tenantCode = tenantCode;
        this.systemParameterProvider = systemParamProvider;
        this.poolName = poolName;
        task = new BatchTimer(cacheRegistry, batchId, counter);
    }

    /**
     * starts the timer with timeout from system parameters 
     */
    private void startTimer() {
        LOGGER.info("IN START TIMER : ", batchId);
      //Reading the timeout from system properties
        String timeOut = systemParameterProvider.getParameter(RuntimeConstants.BATCH_TIMEOUT); 
        timer.schedule(task, Integer.parseInt(timeOut));
    }

    /**
     * stops the scheduled timer and resets it back to the the timeout
     */
    private void resetTimer() {
        LOGGER.info("IN RESET TIMER counter : ", batchId);
        task.cancel();
        task = new BatchTimer(cacheRegistry, batchId, counter);
        //Reading the timeout from system properties
        String timeOut = systemParameterProvider.getParameter(RuntimeConstants.BATCH_TIMEOUT); 
        timer.schedule(task, Integer.parseInt(timeOut));
        //timer.schedule(task, 100000);
    }


    /**
     * A thread safe implementation for updating counts.
     * 
     * @param success
     *            if <code>true</code> success count is incremented or else failure count.
     */
    public void incrementCounter(boolean success) {
        synchronized (lock) {
            counter += 1;
            if (success) {
                incrementSuccessCount();
            } else {
                incrementFailureCount();
            }
            if ((getSuccessCount() + getFailureCount()) == batchCnt) {
                LOGGER.info("Batch complete : ", batchId);
                task.cancel();
                // raise a event for success
                LruEvent event = new LruEvent();
                event.setBatchId(batchId);
                event.setStatus(LruEvent.SUCCESS);
                event.setProcessedCount(counter);
                cacheRegistry.getTopic(LruEventListener.LRU_BATCH_TOPIC).publish(event);
            } else if (counter == COUNT_ONE) {
                startTimer();
            } else {
                resetTimer();
            }
        }
    }
    
    public boolean isBatchTerminated() {
		final IMap<String, Boolean> batchTeminateMap = cacheRegistry.getMap(CacheRegistry.BATCH_TERMINATE_MAP);
		if (batchTeminateMap.containsKey(batchId) && batchTeminateMap.get(batchId)) {
			LruEvent event = new LruEvent();
            event.setBatchId(batchId);
            event.setStatus(LruEvent.TERMINATED);
            event.setProcessedCount(counter);
            cacheRegistry.getTopic(LruEventListener.LRU_BATCH_TOPIC).publish(event);
			return true;
		}
		return false;
	}

    public int getSuccessCount() {
        return successCount;
    }

    /**
     * Increment success count. Not thread safe.
     */
    public void incrementSuccessCount() {
        successCount += 1;
    }

    public int getFailureCount() {
        return failureCount;
    }

    /**
     * Increment failure count. Not thread safe.
     */
    public void incrementFailureCount() {
        failureCount += 1;
    }

    public int getBatchCnt() {
        return batchCnt;
    }

    public String getTenantCode() {
        return tenantCode;
    }
    
    public String getPoolName() {
    	return poolName;
    }
}
