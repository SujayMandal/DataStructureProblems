package com.fa.dp.business.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ThreadPoolExecutorUtil {

	private static final int DEFAULT_POOL_SIZE = 10;

	public static ExecutorService getFixedSizeThreadPool(int size) {

		return Executors.newFixedThreadPool(size > 0 ? size : DEFAULT_POOL_SIZE);

	}

}
