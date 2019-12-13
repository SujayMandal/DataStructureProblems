package com.ca.umg.file.event;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.event.EventBus;
import com.ca.umg.event.EventBusPoller;
import com.ca.umg.file.event.info.FileEvent;

@Named(FileEventBusPoller.BEAN_NAME)
public class FileEventBusPoller implements EventBusPoller<FileEvent> {

    public static final String BEAN_NAME = "fileEventBusPoller";

    @Inject
    @Named(FileEventBus.BEAN_NAME)
    private EventBus<FileEvent> fileEventBus;

    @Override
    public FileEvent take() throws SystemException {
        return fileEventBus.take();
    }

}
