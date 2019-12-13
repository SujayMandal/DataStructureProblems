/**
 * 
 */
package com.ca.umg.file.scheduler;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import com.ca.umg.file.processor.FileRequestProcessor;

/**
 * This class responsible for processing the polled file from the san base at scheduled interval.
 * 
 * @author kamathan
 *
 */
@Named
public class FileRequestProcessingScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileRequestProcessingScheduler.class);

    @Inject
    private FileRequestProcessor fileRequestProcessor;

    @Scheduled(fixedDelayString = "${scheduler.rate}")
    public void schedule() {
        LOGGER.info("Scheduling started for processing model request files.");
        fileRequestProcessor.processAllFiles();
    }

}
