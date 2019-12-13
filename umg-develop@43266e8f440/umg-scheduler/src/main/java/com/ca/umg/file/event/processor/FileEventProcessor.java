/**
 * 
 */
package com.ca.umg.file.event.processor;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.bulk.BulkFileUtil;
import com.ca.framework.core.bulk.info.BulkFileInfo;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.event.EventBusPoller;
import com.ca.umg.event.processor.EventProcessor;
import com.ca.umg.file.container.DataContainers;
import com.ca.umg.file.event.FileEventBusPoller;
import com.ca.umg.file.event.info.FileEvent;
import com.ca.umg.file.event.info.FileStatusInfo;
import com.ca.umg.file.event.util.FileStatus;
import com.ca.umg.util.UmgSchedulerConstants;

/**
 * @author kamathan
 */
@Named
public class FileEventProcessor implements EventProcessor<FileEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileEventProcessor.class);
    @Inject
    private DataContainers dataContainers;

    @Inject
    @Named(FileEventBusPoller.BEAN_NAME)
    private EventBusPoller<FileEvent> eventBusPoller;

    private String DEFAULT_MODEL_NAME = "DEFAULT_MODEL_NAME";
    private String DEFAULT_MAJOR_VERSION = "1";

    @Override
    public void processEvent() throws SystemException {

        LOGGER.info("Started processing event in event bus");
        while (true) {
            FileEvent fileEvent = eventBusPoller.take();

            if (StringUtils.isBlank(fileEvent.getEventType())) {
                LOGGER.error("Received event blank, exiting file event processor");
                break;
            }

            LOGGER.info("Received file {} and event type {} from event bus.", fileEvent.getFile(), fileEvent.getEventType());

            if (validateFile(fileEvent)) {
                // if valid file update the container
                updateDataContainer(fileEvent);
            } else {
                LOGGER.error("Validation failed on file {}, hence ignoring.", fileEvent.getFile());
            }
        }
    }

    /*
     * Validates the file for the .json extension
     */
    private boolean validateFile(FileEvent fileEvent) {
        boolean valid = true;
        try {
            BulkFileUtil.checkFileExtensionIsJson(fileEvent.getFile().toFile().getName());
        } catch (SystemException e) {
            valid = false;
        }
        return valid;
    }

    /*
     * Updates the file details in data container.
     * 
     * If the file is added in the folder then the same will be updated in the container and if any file is removed from the
     * system then the same will be removed from the container
     */
    private void updateDataContainer(FileEvent fileEvent) {
        LOGGER.info("Received the request for Updating container for file {} and event type {} from event bus.", fileEvent.getFile(), fileEvent.getEventType());
        FileStatusInfo fileStatusInfo = new FileStatusInfo();
        String tenantCode = fileEvent.getTenantCode();
        String fileName = fileEvent.getFile().toString();
        String filePath = fileEvent.getWatchedDirectory().toFile().getAbsolutePath();
        fileStatusInfo.setFilePath(filePath);
        fileStatusInfo.setName(fileName);

        fileStatusInfo.setTenantCode(tenantCode);

        Map<String, List<FileStatusInfo>> modelRequestFileMap = null;

        Map<String, Map<String, List<FileStatusInfo>>> tenantRequestFileMap = dataContainers.getRequestFilesMap();

        if (!tenantRequestFileMap.containsKey(tenantCode)) {
            modelRequestFileMap = new LinkedHashMap<String, List<FileStatusInfo>>();
            tenantRequestFileMap.put(tenantCode, modelRequestFileMap);
        }

        BulkFileInfo bulkFileInfo = null;
        try {
            bulkFileInfo = BulkFileUtil.getBulkFileInfo(fileName);
        } catch (SystemException e) {
            LOGGER.error("Could not identify model name and major version from file {}, hence setting default values.", fileName);
            bulkFileInfo = new BulkFileInfo();
            bulkFileInfo.setModelName(DEFAULT_MODEL_NAME);
            bulkFileInfo.setMajorVersion(DEFAULT_MAJOR_VERSION);
        }

        String modelDetail = bulkFileInfo.getModelName() + UmgSchedulerConstants.CHAR_HYPHEN + bulkFileInfo.getMajorVersion();

        modelRequestFileMap = tenantRequestFileMap.get(tenantCode);

        if (!modelRequestFileMap.containsKey(modelDetail)) {
            List<FileStatusInfo> fileStatusInfos = new LinkedList<FileStatusInfo>();
            modelRequestFileMap.put(modelDetail, fileStatusInfos);
        }

        LOGGER.info("Received the request file {} for model {} and tenant {}.", fileName, modelDetail, tenantCode);

        switch (fileEvent.getEventType()) {
        case "ENTRY_CREATE":
        case "ENTRY_MODIFY":
            fileStatusInfo.setStatus(FileStatus.ACK.getStatus());
            fileStatusInfo.setAckTime(DateTime.now().getMillis());
            if (!tenantRequestFileMap.get(tenantCode).get(modelDetail).contains(fileStatusInfo)) {
                tenantRequestFileMap.get(tenantCode).get(modelDetail).add(fileStatusInfo);
                LOGGER.info("Added the request file {} for model {} and tenant {} to data container.", fileName, modelDetail,
                        tenantCode);
            } else {
                LOGGER.info("Request not added for {} event - {} for model {} and tenant {} to data container.", fileEvent.getEventType(), fileName, modelDetail,
                        tenantCode);
            }
            break;
        case "ENTRY_DELETE":
            tenantRequestFileMap.get(tenantCode).get(modelDetail).remove(fileStatusInfo);
            LOGGER.info("Removed the request file {} for model {} and tenant {} from data container.", fileName, modelDetail,
                    tenantCode);
            break;
        default:
            break;
        }
    }
}
