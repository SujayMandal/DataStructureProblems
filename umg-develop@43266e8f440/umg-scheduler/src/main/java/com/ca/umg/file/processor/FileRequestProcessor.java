/**
 * 
 */
package com.ca.umg.file.processor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.framework.event.BulkFilePollingEvent;
import com.ca.pool.TransactionMode;
import com.ca.pool.TransactionType;
import com.ca.pool.model.PoolStatus;
import com.ca.pool.model.TransactionCriteria;
import com.ca.umg.file.container.DataContainers;
import com.ca.umg.file.event.info.FileStatusInfo;
import com.ca.umg.file.event.util.FileStatus;
import com.ca.umg.file.rt.RuntimeClient;
import com.ca.umg.util.UmgSchedulerConstants;

/**
 * @author kamathan
 *
 */
@Named
public class FileRequestProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileRequestProcessor.class);

    @Inject
    private RuntimeClient runtimeClient;

    @Inject
    private CacheRegistry cacheRegistry;

    @Inject
    private DataContainers dataContainers;

    private ExecutorService executorService;

    @Value("${default.model.env.lang}")
    private String defaultModelEnvLang;

    @Value("${default.model.env.ver}")
    private String defaultModelEnvVersion;
    
    @Value("${default.model.env}")
    private String defaultModelEnv;

    private static final String CHANNEL_TYPE_FILE = "FILE";

    private static final String MODEL_NAME = "modelName".intern();
    private static final String EXEC_GROUP = "executionGroup".intern();
    private static final String FILE_NAME = "fileName".intern();
    private static final String HEADER = "header".intern();
    private static final String DATA = "data".intern();
    private static final String MAJOR_VERSION = "majorVersion".intern();

    private static final String EXEC_GRP_MODELED = "Modeled".intern();

    @PostConstruct
    public void init() {
        executorService = Executors.newCachedThreadPool();
    }

    @PreDestroy
    public void destroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * Responsible for processing bulk model request files from the san folder.
     * 
     */
    public void processAllFiles() {
        LOGGER.info("Initialized processing of file request");
        Map<String, Map<String, List<FileStatusInfo>>> tenantRequestFilesMap = dataContainers.getRequestFilesMapClone();

        if (MapUtils.isNotEmpty(tenantRequestFilesMap)) {
            for (Entry<String, Map<String, List<FileStatusInfo>>> tenantRequestFilesMapEntry : tenantRequestFilesMap.entrySet()) {
                LOGGER.info("Started processing files for tenant {}.", tenantRequestFilesMapEntry.getKey());
                Map<String, List<FileStatusInfo>> modelDetailsMap = tenantRequestFilesMapEntry.getValue();
                if (MapUtils.isNotEmpty(modelDetailsMap)) {
                    for (Entry<String, List<FileStatusInfo>> modelDetailsMapEntry : modelDetailsMap.entrySet()) {
                        String modelDetail = modelDetailsMapEntry.getKey();
                        List<FileStatusInfo> fileStatusInfos = new LinkedList<FileStatusInfo>(modelDetailsMapEntry.getValue());
                        if (CollectionUtils.isNotEmpty(fileStatusInfos)) {
                            LOGGER.info("Found {} files for model {}.", fileStatusInfos.size(), modelDetail);

                            // publish hazel-cast event 
                            for(FileStatusInfo fileStatusInfo : fileStatusInfos){
                            	BulkFilePollingEvent bulkFilePollingEvent = new BulkFilePollingEvent();
                            	bulkFilePollingEvent.setTenantCode(tenantRequestFilesMapEntry.getKey());
                            	bulkFilePollingEvent.setFileName(fileStatusInfo.getName());
                            	bulkFilePollingEvent.setEvent(BulkFilePollingEvent.BULK_FILE_ADDED_EVENT);
                            	cacheRegistry.getTopic(BulkFilePollingEvent.BULK_FILE_ADDED_EVENT).publish(bulkFilePollingEvent);
                            }
                            
                            // build transaction criteria to get probable pool and modelet count
                            TransactionCriteria transactionCriteria = buildPoolCriteria(modelDetail, fileStatusInfos.get(0));

                            PoolStatus poolStatus = runtimeClient.getProbablePoolAndCount(transactionCriteria);
                            LOGGER.error("probabale pool and probable pool count - {}", poolStatus);
                            if (poolStatus == null) {
                                LOGGER.info("No Pool found for criteria : {} for model {}.", transactionCriteria.toString(),
                                        modelDetail);
                                continue;
                            }

                            int availableModelets = getNumberOfModelets(poolStatus);
                            int invokedRequests = 0;

                            for (FileStatusInfo fileStatusInfo : fileStatusInfos) {
                                if (StringUtils.equalsIgnoreCase(FileStatus.ACK.getStatus(), fileStatusInfo.getStatus())) {
                                    executeRequest(transactionCriteria, fileStatusInfo);
                                    invokedRequests++;
                                }

                                if (invokedRequests >= availableModelets) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Identifies the model request file for a given tenant, model name and major version and submits the request for umg-runtime
     * for processing
     * 
     * @param tenantCode
     * @param modelName
     * @param majorVersion
     */
    public void processFileByModel(String tenantCode, String modelName, String majorVersion) {
        Map<String, Map<String, List<FileStatusInfo>>> tenantRequestFilesMap = dataContainers.getRequestFilesMapClone();
        Map<String, List<FileStatusInfo>> modelMap = tenantRequestFilesMap.get(tenantCode);
        if (MapUtils.isNotEmpty(modelMap)) {
            String modelDetail = modelName + UmgSchedulerConstants.CHAR_HYPHEN
                    + StringUtils.substringBefore(majorVersion, FrameworkConstant.DOT);
            List<FileStatusInfo> fileStatusInfos = new LinkedList<>(modelMap.get(modelDetail));
            if (CollectionUtils.isNotEmpty(fileStatusInfos)) {
                for (FileStatusInfo fileStatusInfo : fileStatusInfos) {
                    if (StringUtils.equalsIgnoreCase(fileStatusInfo.getStatus(), FileStatus.ACK.getStatus())) {
                        executeRequest(buildPoolCriteria(modelDetail, fileStatusInfo), fileStatusInfo);
                    }
                }
            }
        }
    }

    /*
     * Submits a task to execute runtime request
     */
    private void executeRequest(final TransactionCriteria transactionCriteria, final FileStatusInfo fileStatusInfo) {
     // update file status to "POSTED"
        dataContainers.updateRequestFilesMap(transactionCriteria.getTenantCode(),
                transactionCriteria.getModelName() + UmgSchedulerConstants.CHAR_HYPHEN
                        + StringUtils.substringBefore(transactionCriteria.getModelVersion(), UmgSchedulerConstants.CHAR_DOT),
                FileStatus.POSTED.getStatus(), fileStatusInfo);
        Runnable invokeRuntimeTask = new Runnable() {
            @Override
            public void run() {
                runtimeClient.executeRuntimeRequest(fileStatusInfo, transactionCriteria,
                        buildRuntimeEecutionRequest(transactionCriteria, fileStatusInfo));

            }
        };
        executorService.submit(invokeRuntimeTask);
    }

    /*
     * Build runtime request
     */
    private Map<String, Object> buildRuntimeEecutionRequest(TransactionCriteria transactionCriteria,
            FileStatusInfo fileStatusInfo) {
        Map<String, Object> request = new HashMap<String, Object>();
        Map<String, Object> header = new HashMap<String, Object>();
        header.put(MODEL_NAME, transactionCriteria.getModelName());
        header.put(EXEC_GROUP, EXEC_GRP_MODELED);
        header.put(FILE_NAME, fileStatusInfo.getName());
        String majorVersion = StringUtils.substringBefore(transactionCriteria.getModelVersion(), UmgSchedulerConstants.CHAR_DOT);
        header.put(MAJOR_VERSION, Integer.parseInt(majorVersion));

        Map<String, Object> data = new HashMap<String, Object>();

        request.put(HEADER, header);
        request.put(DATA, data);
        return request;
    }

    private int getNumberOfModelets(PoolStatus poolStatus) {
        if (poolStatus != null) {
            LOGGER.info("Probable pool for request execution is {} and available modelets is {}.", poolStatus.getPoolname(),
                    poolStatus.getAvailablemodelets());
        }
        return poolStatus != null ? poolStatus.getAvailablemodelets() : 0;
    }

    /*
     * Build a transaction criteria for a given model detail and file information.
     */
    private TransactionCriteria buildPoolCriteria(String modelDetail, FileStatusInfo fileStatusInfo) {
        TransactionCriteria transactionCriteria = null;

        String executionEnvironmentVersion = null;
        String executionEnvironment = null;
        String executionLanguage = null;
        Map<String, Map<String, VersionExecInfo>> tenantModelEnvMap = cacheRegistry
                .getMap(FrameworkConstant.MAJOR_VERSION_ENV_MAP);

        if (tenantModelEnvMap == null) {
            LOGGER.info("Not able to get the map from cacheRegistry, re-initializing the map");
            tenantModelEnvMap = new HashMap<String, Map<String, VersionExecInfo>>();
        }
        Map<String, VersionExecInfo> modelMap = tenantModelEnvMap.get(fileStatusInfo.getTenantCode());

        if (MapUtils.isNotEmpty(modelMap) && modelMap.containsKey(modelDetail)) {
            VersionExecInfo versionExecInfo = modelMap.get(modelDetail);
            executionEnvironment = versionExecInfo.getExecEnv();
            executionEnvironmentVersion = versionExecInfo.getExecLangVer();
            executionLanguage = versionExecInfo.getExecLanguage();
        } else {
            executionEnvironment = this.defaultModelEnv;
            executionEnvironmentVersion = this.defaultModelEnvVersion;
            executionLanguage = this.defaultModelEnvLang;
        }

        transactionCriteria = new TransactionCriteria();
        transactionCriteria.setExecutionLanguage(executionLanguage);
        transactionCriteria.setExecutionLanguageVersion(executionEnvironmentVersion);
        transactionCriteria.setExecutionEnvironment(executionEnvironment);
        String modelName = StringUtils.substringBeforeLast(modelDetail, UmgSchedulerConstants.CHAR_HYPHEN);
        transactionCriteria.setModelName(modelName);
        transactionCriteria.setTenantCode(fileStatusInfo.getTenantCode());
        String majorVersion = StringUtils.substringAfterLast(modelDetail, UmgSchedulerConstants.CHAR_HYPHEN);
        transactionCriteria.setModelVersion(majorVersion + ".0");
        transactionCriteria.setTransactionRequestMode(TransactionMode.BULK.getMode());        
        transactionCriteria.setTransactionRequestType(TransactionType.PROD.getType());
        transactionCriteria.setTransactionRequestChannel(CHANNEL_TYPE_FILE);
        return transactionCriteria;
    }
}
