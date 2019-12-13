/**
 * 
 */
package com.ca.umg.file.container;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Named;

import org.apache.commons.collections.MapUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.umg.file.event.info.FileStatusInfo;
import com.ca.umg.file.event.util.FileStatus;
import com.google.common.collect.Maps;

/**
 * @author kamathan
 *
 */
@Named
public class DataContainers {

    private static final String STATUS_FAILED = "failed";

    private static final String STATUS_SUCCESSFUL = "successful";

    private static final Logger LOGGER = LoggerFactory.getLogger(DataContainers.class);

    /**
     * Holds the list of request file names against the model details
     */
    private Map<String, Map<String, List<FileStatusInfo>>> requestFilesMap = new ConcurrentHashMap<String, Map<String, List<FileStatusInfo>>>();

    public Map<String, Map<String, List<FileStatusInfo>>> getRequestFilesMap() {
        return requestFilesMap;
    }

    public Map<String, Map<String, List<FileStatusInfo>>> getRequestFilesMapClone() {
        Map<String, Map<String, List<FileStatusInfo>>> clonedMap = Maps.newConcurrentMap();
        clonedMap.putAll(requestFilesMap);
        return clonedMap;
    }

    public void updateRequestFilesMap(String tenantCode, String modelDetail, String status, FileStatusInfo fileStatusInfo) {
        LOGGER.info("Received request to update request file {} status for tenant {} and model {} as {}.",
                fileStatusInfo.getName(), tenantCode, modelDetail, status);
        Map<String, List<FileStatusInfo>> tenantModelDetailMap = getRequestFilesMap().get(tenantCode);
        if (MapUtils.isNotEmpty(tenantModelDetailMap)) {
            List<FileStatusInfo> files = tenantModelDetailMap.get(modelDetail);

            if (files == null) {
                files = new LinkedList<FileStatusInfo>();
                tenantModelDetailMap.put(modelDetail, files);
            }

            switch (FileStatus.valueOf(status)) {
            case ACK:
                if (!files.contains(fileStatusInfo)) {
                    fileStatusInfo.setStatus(status);
                    fileStatusInfo.setAckTime(DateTime.now().getMillis());
                    boolean added = files.add(fileStatusInfo);
                    LOGGER.info("Addition of file {} for tenant {} and model details {} {}.", fileStatusInfo.getName(),
                            tenantCode, modelDetail, added ? STATUS_SUCCESSFUL : STATUS_FAILED);
                }
                break;
            case POSTED:
                boolean deleted = files.remove(fileStatusInfo);
                LOGGER.info("Removal of file {} for tenant {} and model details {} {}.", fileStatusInfo.getName(), tenantCode,
                        modelDetail, deleted ? STATUS_SUCCESSFUL : STATUS_FAILED);
                break;
            default:
                break;
            }
        }
    }
}
