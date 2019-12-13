package com.ca.umg.modelet.runtime.impl;

import java.text.NumberFormat;

import org.slf4j.Logger;

public class MemoryFoot {

    private final static NumberFormat FORMAT = NumberFormat.getInstance();

    private final long freeMemory;
    private final long totalMemory;
    private final long maxMemory;

    public MemoryFoot() {
        final Runtime runtime = Runtime.getRuntime();
        freeMemory = runtime.freeMemory();
        totalMemory = runtime.totalMemory();
        maxMemory = runtime.maxMemory();
    }

    public void printMemoryFootPrint(final String message, final Logger logger) {    	
        logger.error(message);
        logger.error("Free memory: " + FORMAT.format(freeMemory / (1024)));
        logger.error("Used memory: " + FORMAT.format((totalMemory-freeMemory) / (1024)));
        logger.error("Max memory: " + FORMAT.format(maxMemory / (1024)));
        logger.error("Total memory: " + FORMAT.format(totalMemory / (1024)));
    }
}