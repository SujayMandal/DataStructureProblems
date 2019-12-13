package com.ca.umg.rt.timer;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.umg.rt.cache.event.LruEvent;
import com.ca.umg.rt.cache.listener.LruEventListener;

/**
 * Timer task which gets triggered only when the batch  
 * crosses the timeout set in system parameters and 
 * triggers the event for invalidating the batch
 * 
 * @author raddibas
 * 
 */

public class BatchTimer extends TimerTask {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BatchTimer.class);

	private final CacheRegistry cacheRegistry;
	private final String batchId;
	private final int prcsdBtchCnt;

	public BatchTimer(CacheRegistry cacheRgstry, String btchId, int counter) {
		super();
		cacheRegistry = cacheRgstry;
		batchId = btchId;
		prcsdBtchCnt = counter;
	}

	public void run() {
		LOGGER.info(String.format("invalidating the timer in runnable for batchid : %s", batchId));
		LruEvent event = new LruEvent();
		event.setBatchId(batchId);
        event.setStatus(event.TIMEOUT);
		event.setProcessedCount(prcsdBtchCnt);
		cacheRegistry.getTopic(LruEventListener.LRU_BATCH_TOPIC).publish(event);
	}
}
