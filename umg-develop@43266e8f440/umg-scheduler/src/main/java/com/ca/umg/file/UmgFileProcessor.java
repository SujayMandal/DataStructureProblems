/**
 * 
 */
package com.ca.umg.file;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.file.event.processor.FileEventProcessor;

/**
 * @author kamathan
 *
 */
@Named
public class UmgFileProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmgFileProcessor.class);

    @Inject
    private UmgFilePoller umgFilePoller;

    @Inject
    private FileEventProcessor fileEventProcessor;

    /**
     * This method initializes the polling on the bulk/input folder of every tenant
     * 
     * @throws SystemException
     */
    public void initialize() throws SystemException {

        LOGGER.info("Initializing Polling on bulk/input folders of sanpath");

        // register san folders for file changes
        // umgFilePoller.registerDirectoriesForPolling();

        // initialize file pooling
        // initializeFilePoller();

        // initialize event processing
        initializeEventProcessing();
    }

    /*
     * Initializes the polling on san folder "bulk/input" for bulk model request json files.
     */
    /*private void initializeFilePoller() {
        Thread filePollingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    umgFilePoller.poll();
                } catch (SystemException e) {
                    LOGGER.error("An error occurred while starting poller");
                }
            }
        });
        filePollingThread.start();
    }*/

    /*
     * Initializes thread to process file events registered from the file watcher service
     */
    private void initializeEventProcessing() {
        Thread eventProcessorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    fileEventProcessor.processEvent();
                } catch (SystemException e) {
                    LOGGER.error("An error occurred while initilizing event processor.");
                }
            }
        });
        eventProcessorThread.start();
    }
}
