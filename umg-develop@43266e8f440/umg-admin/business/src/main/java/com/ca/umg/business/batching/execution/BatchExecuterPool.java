package com.ca.umg.business.batching.execution;

import static java.lang.Boolean.TRUE;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.hazelcast.core.IMap;

@Named
public final class BatchExecuterPool {

	private static final int POOL_SIZE = 2;//TODO Pick from umgproperties
	private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(POOL_SIZE);
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchExecuterPool.class);
	
	private BatchExecuterPool() {
	}
	
	public static void shutDown() {
		EXECUTOR_SERVICE.shutdown();
		//executorService.awaitTermination(1000, TimeUnit.SECONDS);
	}

	public void runTask(List<? extends Runnable> tasks, final String batchId) {
		for (Runnable task : tasks) {
			EXECUTOR_SERVICE.execute(task);
		}
	}

	public <T> List<Future<T>> getTaskHandle(List<Callable<T>> tasks)
			throws InterruptedException {
		return EXECUTOR_SERVICE.invokeAll(tasks);
	}
	
    
	public static boolean isBatchTerminated(final CacheRegistry cr, final String batchId) {
		boolean batchTerminated = false;
		
        final IMap<String, Boolean> batchTeminateMap = cr.getMap(CacheRegistry.BATCH_TERMINATE_MAP);
		if (batchTeminateMap.containsKey(batchId) && batchTeminateMap.get(batchId)) {
			batchTerminated = true;
		}

		LOGGER.info("Batch {}'s terminated status is {}", batchId, batchTerminated);

		return batchTerminated;
	}
	
	public static void removeTerminatedBatchFromCache(final CacheRegistry cr, final String batchId) {
        final IMap<String, Boolean> batchTeminateMap = cr.getMap(CacheRegistry.BATCH_TERMINATE_MAP);
		if (batchTeminateMap.containsKey(batchId)) {
			LOGGER.info("Batch {}'s terminated status is removed from Cache", batchId);
			batchTeminateMap.remove(batchId);
		}
	}
	
	public static void putTerminatedBatchIntoCache(final CacheRegistry cr, final String batchId) {
		LOGGER.info("Batch {}'s terminated status is put into Cache", batchId);
        final IMap<String, Boolean> batchTeminateMap = cr.getMap(CacheRegistry.BATCH_TERMINATE_MAP);
		batchTeminateMap.put(batchId, TRUE);
	}
}