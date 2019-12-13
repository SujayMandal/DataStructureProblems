/**
 * 
 */
package com.ca.umg.file.event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.event.EventBus;
import com.ca.umg.exception.UmgSchedulerExceptionCodes;

/**
 * @author kamathan
 *
 */
@Named(FileEventBus.BEAN_NAME)
public class FileEventBus<FileEvent> implements EventBus<FileEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileEventBus.class);

    public static final String BEAN_NAME = "fileEventBus";

    private BlockingQueue<FileEvent> eventQueue;

    @PostConstruct
    public void init() {
        eventQueue = new LinkedBlockingQueue<FileEvent>();
    }

    @Override
    public void add(FileEvent fileEvent) {
        if (eventQueue.contains(fileEvent)) {
            LOGGER.info("File event already exists, hence ignoring. {} ", fileEvent);
        } else {
            eventQueue.add(fileEvent);
            LOGGER.info("File has added to event bus successfully. {}", fileEvent);
        }
    }

    @Override
    public FileEvent take() throws SystemException {
        FileEvent fileEvent = null;
        try {
            fileEvent = eventQueue.take();
        } catch (InterruptedException e) {
            LOGGER.error("An error occurred while retrieving file event info from event bus.", e);
            SystemException.newSystemException(UmgSchedulerExceptionCodes.USC0000002, new Object[] {});
        }
        return fileEvent;
    }

}
