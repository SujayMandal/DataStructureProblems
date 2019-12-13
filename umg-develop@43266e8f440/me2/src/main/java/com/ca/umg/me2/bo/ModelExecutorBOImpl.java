/**
 *
 */
package com.ca.umg.me2.bo;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.bo.AbstractBusinessObject;
import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.connection.ConnectorType;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.entity.ModeletRestartInfo;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterConstants;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.modelet.ModeletClientInfo;
import com.ca.modelet.client.ModeletClient;
import com.ca.pool.ModeletAllocationAlgorithm;
import com.ca.pool.ModeletStatus;
import com.ca.pool.PoolManager;
import com.ca.pool.PoolManagerImpl;
import com.ca.pool.TransactionMode;
import com.ca.pool.manager.ModeletHelper;
import com.ca.pool.manager.ModeletManager;
import com.ca.pool.model.ExecutionLanguage;
import com.ca.pool.model.Pool;
import com.ca.pool.model.PoolAllocationInfo;
import com.ca.pool.model.PoolStatus;
import com.ca.pool.model.PoolStatusStats;
import com.ca.pool.model.RequestMode;
import com.ca.pool.model.TransactionCriteria;
import com.ca.pool.util.TransactionsDetailMap;
import com.ca.umg.me2.dao.MongoTransactionLogDAO;
import com.ca.umg.me2.dao.TransactionLogDAO;
import com.ca.umg.me2.exception.codes.ModelExecutorExceptionCodes;
import com.ca.umg.me2.util.ModelExecutionDistributor;
import com.ca.umg.me2.util.ModeletResult;
import com.ca.umg.modelet.common.ModelRequestInfo;
import com.ca.umg.modelet.constants.ErrorCodes;
import com.ca.umg.modelet.exception.ModeletExceptionCodes;
import com.ca.umg.notification.model.NotificationHeaderEnum;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static com.ca.framework.core.exception.SystemException.newSystemException;
import static com.ca.framework.core.exception.codes.FrameworkExceptionCodes.RSE0000507;
import static com.ca.framework.core.exception.codes.FrameworkExceptionCodes.RSE000930;
import static com.ca.umg.me2.exception.codes.ModelExecutorExceptionCodes.MSE0000001;
import static com.ca.umg.me2.exception.codes.ModelExecutorExceptionCodes.MSE0000003;
import static com.ca.umg.modelet.constants.ErrorCodes.ME0007;
import static com.ca.umg.modelet.constants.ErrorCodes.ME0009;
import static com.ca.umg.modelet.constants.ErrorCodes.ME0030;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

import static com.ca.framework.core.constants.PoolConstants.ALL_MODELET_MAP;

/**
 * @author kamathan
 *
 */
@SuppressWarnings("PMD")
@Named
public class ModelExecutorBOImpl extends AbstractBusinessObject implements ModelExecutorBO {

    private static final long serialVersionUID = -6791206159333014650L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelExecutorBOImpl.class);

    @Inject
    private ModelExecutionDistributor modelExecDistrbtr;

    @Inject
    private ModeletManager modeletManager;

    @Inject
    private ModeletHelper modeletHelper;

    @Inject
    private CacheRegistry cacheRegistry;

    @Inject
    private PoolManager poolManager;

    @Inject
    private TransactionsDetailMap localCacheMap;

    @Inject
    private TransactionLogDAO txnLogDAO;

    @Inject
    private MongoTransactionLogDAO mongoTransactionLogDAO;

    @Inject
    private SystemParameterProvider systemParameterProvider;

    @Inject
    private PoolManagerImpl poolManagerImpl;

    private final ObjectMapper mapper = new ObjectMapper();

    public static final Map<String, String> errorCodeMap = new HashMap<>();

    private ExecutorService executorService = new ThreadPoolExecutor(2, 10, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    static {
        errorCodeMap.put("MOSE000001", "RSE008041");
        errorCodeMap.put("MOSE000002", "RSE008042");
        errorCodeMap.put("MOSE000003", "RSE008043");
        errorCodeMap.put("MOSE000004", "RSE008044");
        errorCodeMap.put("MOSE000005", "RSE008045");
        errorCodeMap.put("MOSE000006", "RSE008046");
        errorCodeMap.put("MOSE000007", "RSE008047");
        errorCodeMap.put("MOSE000008", "RSE008048");
        errorCodeMap.put("ME0030", "RSE000930");
    }

    @Override
    public Map<String, Object> executeModel(final String modelInfo, final ModeletResult modeletResult) throws SystemException, BusinessException {
        final Map<String, Object> modeletResponse = execute(modelInfo, 0, modeletResult);
        modeletResult.setModeletResponse(modeletResponse);
        return modeletResponse;
    }

    private Map<String, Object> execute(final String modelInput, final int attempt, final ModeletResult modeletResult)
            throws SystemException, BusinessException {
        long completeModeletTime = 0;
        ModeletClient modeletClient = null;
        Map<String, Object> result = null;
        int norOfAttempts = attempt;
        boolean reRegisterModelet = true;
        KeyValuePair<ModeletClientInfo, ModeletClient> modeletDetails = null;
        String key = null;
        TransactionCriteria txnCriteria = null;
        try {
            LOGGER.error("Geting Modelet, Attempt : {}", attempt);
            txnCriteria = modeletResult.getTransactionCriteria();
            modeletDetails = modelExecDistrbtr.getAvailableFreeModelet(txnCriteria);
            if (modeletDetails != null && modeletDetails.getValue() != null) {
                if (txnCriteria.getClientID() != null && !StringUtils.isEmpty(txnCriteria.getClientID())) {
                    cacheRegistry.getTopic("MODELET_FOUND").publish(txnCriteria.getClientID() + "@" + "MODELET_FOUND");
                }
                modeletResult.setModeletClientInfo(modeletDetails.getKey());
                modeletClient = modeletDetails.getValue();
                if (modeletDetails.getKey() != null) {
                    MDC.put(FrameworkConstant.MDC_MODELET_KEY, modeletDetails.getKey().getHost() + "-" + modeletDetails.getKey().getPort());
                }
                completeModeletTime = System.currentTimeMillis();
                long modeletCreateConTime = System.currentTimeMillis();
                modeletClient.createConnection();
                LOGGER.error("Create connection time :: {}", System.currentTimeMillis() - modeletCreateConTime);
                // execute model using modelet client
                long sendDataTime = System.currentTimeMillis();
                key = modeletDetails.getKey().getHost() + "-" + modeletDetails.getKey().getPort() + "-" + sendDataTime + "-" + modeletDetails.getKey()
                        .getPoolName();
                localCacheMap.getExcesiveRuntimeMap().put(key, txnCriteria);
                updateModeletStatusInMap(modeletDetails.getKey(), ModeletStatus.BUSY.getStatus());
                String txnReqMode = txnCriteria.getTransactionRequestMode();
                if (StringUtils.equalsIgnoreCase(txnReqMode, RequestMode.BATCH.getMode()) || StringUtils
                        .equalsIgnoreCase(txnReqMode, TransactionMode.BULK.getMode())) {
                    LOGGER.error("Updating status of batch : {}", txnCriteria.getBatchId());
                    txnLogDAO.updateBatchStatus(TransactionStatus.IN_EXECUTION.getStatus(), txnCriteria.getBatchId(), txnReqMode);
                }
                txnLogDAO.updateTransactionStatus(TransactionStatus.IN_EXECUTION.getStatus(),
                        modeletResult.getTransactionCriteria().getUmgTransactionId());
                mongoTransactionLogDAO
                        .upsertRequestTransactionLogToMongo(modeletResult.getTransactionCriteria().getUmgTransactionId(), modeletDetails.getKey());
                String modeletResponse = modeletClient.sendData(modelInput);
                // set tenant code of the previous request to modelet client info
                modeletDetails.getKey().setTenantCode(txnCriteria.getTenantCode());
                modeletDetails.getKey().setRequestMode(txnCriteria.getTransactionRequestType());
                LOGGER.error("Send data time : {}", System.currentTimeMillis() - sendDataTime);
                result = parseModelResponseJson(modeletResponse);

                if (StringUtils.equalsIgnoreCase(modeletDetails.getKey().getExecutionLanguage(), ExecutionLanguage.R.getValue())) {
                    KeyValuePair<Boolean, Map<String, Object>> modelResponse = parseModeletResponse(result, modeletDetails, txnCriteria);
                    reRegisterModelet = modelResponse.getKey();
                    result = modelResponse.getValue();
                }

                // set model name and version details in case of successful execution

                if (reRegisterModelet) {
                    modeletDetails.getKey().setLoadedModel(txnCriteria.getModelName());
                    modeletDetails.getKey().setLoadedModelVersion(txnCriteria.getModelVersion());
                    final ModelRequestInfo modelRequestInfo = transformRequestToJSON(modelInput);
                    modeletDetails.getKey().setModelLibraryVersionName(modelRequestInfo.getHeaderInfo().getModelLibraryVersionName());
                }
            } else {
                LOGGER.error("Modelet is not avaiable in pool, re attempting");
                Pool pool = poolManager.getPoolByCriteria(txnCriteria);
                Integer poolWaitTimeout = pool.getWaitTimeout();
                double timeOut = modelExecDistrbtr.getModeletAvaiableTimeoutInSec();
                if (poolWaitTimeout != null && poolWaitTimeout > 0) {
                    timeOut = poolWaitTimeout / 1000;
                }
                throw newSystemException(MSE0000001, new Object[]{timeOut, norOfAttempts});
            }
        } catch (SystemException sysExc) {
            // do not try if error is due to unavailability of modelet in
            // registry
            LOGGER.error("An error occurred while executing request.", sysExc.getCode());
            if (equalsIgnoreCase(ME0030, sysExc.getCode())) {
                reRegisterModelet = false;
                LOGGER.error("Starting  modelet as errorCode is ==" + sysExc.getCode());
                final ModeletClientInfo clientInfo = modeletDetails != null ? modeletDetails.getKey() : null;
                if (localCacheMap.getTxnTimeOutMap().get(key) != null) {
                    if (checkErrorCodeForRestart(sysExc.getCode())) {
                        Thread startModeletThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // modeletManager.startRServe(clientInfo);
                                    modeletManager.startModelet(clientInfo, ConnectorType.SSH.getType());
                                } catch (SystemException | BusinessException e) {
                                    LOGGER.error("An error occurred while starting modelet {}.", clientInfo, e);
                                }
                            }
                        });
                        startModeletThread.start();

                    }
                    sysExc = new SystemException(RSE000930, new Object[]{
                            cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER).get(SystemParameterConstants.MODELET_EXEC_TIME_LIMIT)});

                    localCacheMap.getTxnTimeOutMap().remove(key);
                    LOGGER.error("Txn Timeout map size is :" + localCacheMap.getTxnTimeOutMap().size());
                    throw sysExc;
                } else if (localCacheMap.getTxnTimeOutMap().get(key) == null) {
                    throw sysExc;
                }
            }

            // if an error occurs while loading dependencies to modelet runtime then restart the modelet
            if (modeletDetails != null && modeletDetails.getKey() != null && StringUtils
                    .equalsIgnoreCase(modeletDetails.getKey().getExecutionLanguage(), ExecutionLanguage.R.getValue())) {
                restartModeletIfRequired(sysExc.getCode(), modeletDetails.getKey(), modeletDetails, modeletResult.getTransactionCriteria());
            }

            if (equalsIgnoreCase(RSE0000507, sysExc.getCode()) || equalsIgnoreCase(ModelExecutorExceptionCodes.MSE0000203, sysExc.getCode()) || sysExc
                    .getCode().startsWith("RSE00804")) {
                reRegisterModelet = false;
                throw sysExc;
            } else {
                if (norOfAttempts < poolManager.getRetryCount()) {
                    if (!equalsIgnoreCase(ME0007, sysExc.getCode()) || !equalsIgnoreCase(ErrorCodes.ME0005, sysExc.getCode())) {
                        reRegisterModelet = false;
                        LOGGER.info("Exception received from modelet client Retrying.");
                        // increment retry count and try to execute model again
                        norOfAttempts++;
                        result = execute(modelInput, norOfAttempts, modeletResult);
                    }
                } else {
                    if (!equalsIgnoreCase(ME0007, sysExc.getCode()) || !equalsIgnoreCase(ErrorCodes.ME0005, sysExc.getCode())) {
                        reRegisterModelet = false;
                    }

                    if (equalsIgnoreCase(MSE0000001, sysExc.getCode())) {
                        if (checkIfPoolHasBusyModelets(modeletResult.getTransactionCriteria())) {
                            Pool pool = poolManager.getPoolByCriteria(modeletResult.getTransactionCriteria());
                            Integer poolWaitTimeout = pool.getWaitTimeout();
                            if (poolWaitTimeout != null && poolWaitTimeout > 0) {
                                poolWaitTimeout = poolWaitTimeout / 1000;
                            }
                            sysExc = new SystemException(ModelExecutorExceptionCodes.MSE0000217, new Object[]{Integer.toString(poolWaitTimeout)});
                        } else {
                            Pool pool = poolManager.getPoolByCriteria(modeletResult.getTransactionCriteria());
                            Integer poolWaitTimeout = pool.getWaitTimeout();
                            if (poolWaitTimeout != null && poolWaitTimeout > 0) {
                                poolWaitTimeout = poolWaitTimeout / 1000;
                            }
                            sysExc = new SystemException(ModelExecutorExceptionCodes.MSE0000218, new Object[]{pool.getPoolName(), poolWaitTimeout});
                        }
                    }
                    throw sysExc;
                }
            }
        } finally {
            if (result != null) {
                result.put("NO_OF_ATTEMPTS", norOfAttempts);
            }
            if (key != null && localCacheMap.getExcesiveRuntimeMap().containsKey(key)) {
                localCacheMap.getExcesiveRuntimeMap().remove(key);
            }
            if (modeletClient != null) {
                long shutdownTime = System.currentTimeMillis();
                modeletClient.shutdownConnection();
                LOGGER.debug("Shutdown time {}", System.currentTimeMillis() - shutdownTime);
                LOGGER.debug("Complete modelet time :: {}", System.currentTimeMillis() - completeModeletTime);
            }
            if (reRegisterModelet && modeletDetails != null && modeletDetails.getKey() != null) {
                modelExecDistrbtr.addModeletBackToRegistry(modeletDetails.getKey());
                updateModeletStatusInMap(modeletDetails.getKey(), ModeletStatus.REGISTERED.getStatus());
            }
            if (modeletResult.getModeletClientInfo() != null && modeletResult.getModeletClientInfo().getLoadedModel() != null) {
                if (modeletManager.isModeletRestartReq(modeletResult.getModeletClientInfo())) {
                    restartModeletIfRequired(PoolConstants.BREARCH_LIMIT, modeletResult.getModeletClientInfo(), modeletDetails, txnCriteria);
                }
            }
        }
        return result;
    }

    private Boolean checkIfPoolHasBusyModelets(final TransactionCriteria transactionCriteria) throws SystemException {
        Boolean poolHasBusyModelets = Boolean.FALSE;
        Pool pool = poolManager.getPoolByCriteria(transactionCriteria);
        final IMap<String, ModeletClientInfo> allModeletMap = cacheRegistry.getMap(PoolConstants.ALL_MODELET_MAP);
        final Set<String> keySet = allModeletMap.keySet();
        for (final String key : keySet) {
            final ModeletClientInfo clientInfo = allModeletMap.get(key);
            if (equalsIgnoreCase(clientInfo.getPoolName(), pool.getPoolName())) {
                poolHasBusyModelets = Boolean.TRUE;
            }
        }
        return poolHasBusyModelets;
    }

    private KeyValuePair<Boolean, Map<String, Object>> parseModeletResponse(Map<String, Object> data,
                                                                            KeyValuePair<ModeletClientInfo, ModeletClient> modeletDetails, TransactionCriteria transactionCriteria) throws SystemException {
        boolean reRegisterModelet = false;
        if (data != null) {
            Map<String, Object> responseHeaderInfo = (Map<String, Object>) data.get("responseHeaderInfo");
            if (responseHeaderInfo != null) {
                String errorCode = (String) responseHeaderInfo.get("errorCode");
                Boolean error = Boolean.valueOf((String) responseHeaderInfo.get("error"));

                reRegisterModelet = restartModeletIfRequired(errorCode, modeletDetails.getKey(), modeletDetails, transactionCriteria);
            }
        }
        return new KeyValuePair<Boolean, Map<String, Object>>(!reRegisterModelet, data);

    }

    private Map<String, Object> parseModelResponseJson(String jsonResponse) throws SystemException {
        Map<String, Object> data = null;
        try {
            data = mapper.readValue(jsonResponse, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (JsonParseException | JsonMappingException e) {
            SystemException.newSystemException("", new String[]{""}, e);
        } catch (IOException ioe) {
            SystemException.newSystemException("", new String[]{""}, ioe);
        }
        return data;
    }

    private boolean restartModeletIfRequired(String code, final ModeletClientInfo modeletClientInfo,
                                             final KeyValuePair<ModeletClientInfo, ModeletClient> modeletDetails, TransactionCriteria transactionCriteria) {
        boolean restartRequested = Boolean.FALSE;
        try {
            if (checkErrorCodeForRestart(code)) {
                restartRequested = Boolean.TRUE;
                LOGGER.error(
                        "Modelet {} will be restarted as it failed to execute transacction in modelet. Error code : {}. Please check modelet logs for more details",
                        modeletClientInfo, code);
                Map<String, String> info = setNotificationHeaders(modeletClientInfo, transactionCriteria, ModeletManager.RESTART_REASON_2);
                modeletManager.restartModelet(modeletClientInfo, info);
            } else if (StringUtils.equalsIgnoreCase(code, ME0009)) {
                LOGGER.error("Modelet {},{} might have crashed.", modeletClientInfo.getHost(), modeletClientInfo.getPort());
                // check if modelet client is available
                ModeletClient modeletClient = modeletHelper.buildModeletClient(modeletClientInfo);
                try {
                    LOGGER.error("Trying to connect to modelet {}, {}.", modeletClientInfo.getHost(), modeletClientInfo.getPort());
                    modeletClient.createConnection();
                } catch (SystemException e) {
                    LOGGER.error("Modelet is not available, starting the modelet.");
                    modelExecDistrbtr.removeModeletFromPoolManager(modeletDetails.getKey());
                    Thread startModeletThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String delay = (String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER)
                                        .get(SystemParameterConstants.MODELET_RESTART_DELAY);
                                LOGGER.error("Sleeping for {} before calling start modelet in " + "ModelExecutorBOImpl:: restartModeletIfRequired ",
                                        delay);
                                Thread.sleep(StringUtils.isNotBlank(delay) ? Integer.parseInt(delay) : 30000l);
                                LOGGER.error(
                                        "Modelet is requested to start as something went wrong in Modelet during exection ot transactions, please check Modelet logs, Modelet Details : {}",
                                        modeletClientInfo);
                                // modeletManager.startRServe(modeletClientInfo);
                                modeletManager.startModelet(modeletClientInfo, ConnectorType.SSH.getType());
                            } catch (InterruptedException ioex) {
                                LOGGER.error("An error occurred while restarting the modelet.", ioex);
                            } catch (SystemException | BusinessException e) {
                                LOGGER.error("An error occurred while starting modelet {}.", modeletClientInfo, e);
                            }
                        }
                    });
                    startModeletThread.start();
                } finally {
                    if (modeletClient != null) {
                        modeletClient.shutdownConnection();
                    }
                }
            } else if (StringUtils.equals(code, PoolConstants.BREARCH_LIMIT)) {
                LOGGER.error(
                        "Modelet {} will be restarted as it failed to execute transacction in modelet. Error code : {}. Please check modelet logs for more details",
                        modeletClientInfo, ModeletExceptionCodes.MOSE000001);
                Map<String, String> info = setNotificationHeaders(modeletClientInfo, transactionCriteria, ModeletManager.RESTART_REASON_5);

                modeletManager.restartModelet(modeletClientInfo, info);
            }
        } catch (SystemException e) {
            LOGGER.error("An error occurred while restarting modelet {}.", modeletClientInfo, e);
        }
        return restartRequested;
    }

    private Map<String, String> setNotificationHeaders(final ModeletClientInfo modeletClientInfo, TransactionCriteria transactionCriteria,
                                                       String restartReason) {
        Map<String, String> info = new HashMap<>();
        info.put(NotificationHeaderEnum.TRANSACTION_ID.getHeaderName(), transactionCriteria.getUmgTransactionId());
        info.put(NotificationHeaderEnum.TRANSACTION_RUN_DATE.getHeaderName(), transactionCriteria.getRunAsData());
        info.put(NotificationHeaderEnum.REASON.getHeaderName(), restartReason);
        info.put(NotificationHeaderEnum.LOADED_MODEL.getHeaderName(), modeletClientInfo.getLoadedModel());
        info.put(NotificationHeaderEnum.LOADED_MODEL_VERSION.getHeaderName(), modeletClientInfo.getLoadedModelVersion());
        info.put(NotificationHeaderEnum.MODEL_TO_LOAD.getHeaderName(), transactionCriteria.getModelName());
        info.put(NotificationHeaderEnum.MODEL_VERSION_TO_LOAD.getHeaderName(), transactionCriteria.getModelVersion());
        ModeletRestartInfo modeletRestartInfo = modeletManager.getRestartAndExecCount(modeletClientInfo);
        if (modeletRestartInfo != null) {
            info.put(NotificationHeaderEnum.EXEC_COUNT.getHeaderName(), String.valueOf(modeletRestartInfo.getExecCount()));
            info.put(NotificationHeaderEnum.RESTART_COUNT.getHeaderName(), String.valueOf(modeletRestartInfo.getRestartCount()));
        } else {
            info.put(NotificationHeaderEnum.EXEC_COUNT.getHeaderName(), ModeletManager.EXEC_RESTART_COUNT_NOT_PRESENT);
            info.put(NotificationHeaderEnum.RESTART_COUNT.getHeaderName(), ModeletManager.EXEC_RESTART_COUNT_NOT_PRESENT);
        }
        modeletManager.setExecCounttoZero(modeletClientInfo);

        return info;
    }

    @Override
    public void getAllModeletInfo() throws SystemException {
        modelExecDistrbtr.getAllModeletinfo();
    }

    @Override
    public List<PoolStatusStats> getPoolStatusStats(final List<String> poolNames) throws SystemException {
        return modelExecDistrbtr.getPoolStatusStats(poolNames);
    }

    private ModelRequestInfo transformRequestToJSON(final String requestJSON) throws SystemException {
        ModelRequestInfo modelRequestInfo = null;
        try {
            modelRequestInfo = mapper.readValue(requestJSON, ModelRequestInfo.class);
        } catch (JsonParseException | JsonMappingException e) {
            LOGGER.error("Exception occured while parsing request json", e);
            newSystemException(MSE0000003, new String[]{"request", e.getMessage()}, e);
        } catch (final IOException e) {
            LOGGER.error("Exception occured while parsing response json", e);
            newSystemException(MSE0000003, new String[]{"request", e.getMessage()}, e);
        }
        return modelRequestInfo;
    }

    @Override
    public void allocateModelets(List<PoolAllocationInfo> poolAllocationInfoInfoList) throws SystemException {
        final IMap<String, ModeletClientInfo> allModeletMap = cacheRegistry.getMap(PoolConstants.ALL_MODELET_MAP);
        Map<String, String> criteriaValueMap = new HashMap<String, String>();
        for (PoolAllocationInfo poolAllocationInfo : poolAllocationInfoInfoList) {
            final List<ModeletClientInfo> modeletClientInfoList = poolAllocationInfo.getModeletClientInfoList();

            for (ModeletClientInfo modeletClientInfo : modeletClientInfoList) {
                ModeletClientInfo clientInfo = allModeletMap.get(modeletClientInfo.getHostKey());

                if (!StringUtils.equals(clientInfo.getPoolName(), poolAllocationInfo.getPool().getPoolName()) && StringUtils
                        .equalsIgnoreCase(clientInfo.getExecEnvironment(), poolAllocationInfo.getPool().getExecutionEnvironment()) && StringUtils
                        .equalsIgnoreCase(clientInfo.getExecutionLanguage(), poolAllocationInfo.getPool().getExecutionLanguage())) {

                    // remove modelet from source pool
                    boolean removed = false;
                    IQueue<Object> pool = cacheRegistry.getDistributedPoolQueue(clientInfo.getPoolName());
                    LOGGER.debug("Trying to remove modelet from existing pool if it is avaiable , Pool is : {}, removing modeletg is : {}",
                            clientInfo.getPoolName(), clientInfo.getLogMessage());
                    removed = poolManagerImpl.removeModeletClientFromPoolQueue(pool, clientInfo);
                    LOGGER.info("Modelet {}:{} removal from pool {} {}.", modeletClientInfo.getHost(), modeletClientInfo.getPort(),
                            modeletClientInfo.getPoolName(), removed ? "successful" : "failed");
                    clientInfo.setPoolName(poolAllocationInfo.getPool().getPoolName());
                    if (StringUtils.equalsIgnoreCase(clientInfo.getExecutionLanguage(), ExecutionLanguage.R.getValue()) && !StringUtils
                            .equalsIgnoreCase(clientInfo.getModeletStatus(), ModeletStatus.BUSY.getStatus()) && StringUtils
                            .isBlank(clientInfo.getLoadedModelVersion())) {
                        LOGGER.error("Modelet {}:{} restart not required as no model is still loaded.", modeletClientInfo.getHost(),
                                modeletClientInfo.getPort());
                        ModeletAllocationAlgorithm.loadModel(clientInfo, poolManager);
                    }
                    cacheRegistry.getMap(PoolConstants.ALL_MODELET_MAP).put(clientInfo.getHostKey(), clientInfo);
                    cacheRegistry.getMap(PoolConstants.RA_SYSTEM_MODELETS).put(clientInfo.getHostKey(), clientInfo);

                    // add modelet to new pool
                    if (StringUtils.isNotBlank(clientInfo.getModeletStatus()) && !StringUtils
                            .equalsIgnoreCase(clientInfo.getModeletStatus(), ModeletStatus.UNREGISTERED.getStatus()) && !StringUtils
                            .equalsIgnoreCase(clientInfo.getModeletStatus(), ModeletStatus.FAILED.getStatus()) && removed) {
                        boolean added = cacheRegistry.getDistributedPoolQueue(clientInfo.getPoolName()).add(clientInfo);
                        LOGGER.info("Modelet {}:{} registration to pool {} {}.", modeletClientInfo.getHost(), modeletClientInfo.getPort(),
                                added ? "successful" : "failed");
                    } else {
                        LOGGER.info("Modelet {}:{} is currently unavailable for allocation to pool queue {}.", modeletClientInfo.getHost(),
                                modeletClientInfo.getPort(), clientInfo.getPoolName());
                    }

                }

                if (ModeletAllocationAlgorithm.isSpecificRModel(clientInfo, poolManager, criteriaValueMap)) {
                    // this block handles use case where model information in pool definition is changed and no modelets are added
                    // or removed to the pool
                    ModeletClientInfo modeletInfoFromCache = (ModeletClientInfo) cacheRegistry.getMap(PoolConstants.ALL_MODELET_MAP)
                            .get(clientInfo.getHostKey());
                    if (modeletInfoFromCache != null && StringUtils
                            .equalsIgnoreCase(ModeletStatus.REGISTERED.getStatus(), modeletInfoFromCache.getModeletStatus())) {
                        // we can only restart modelets which are free. Busy modelets will be restarted when executing next
                        // request.
                        Map<String, String> info = createInfo(clientInfo.getLoadedModel(), clientInfo.getLoadedModelVersion());
                        modeletManager.restartModelet(clientInfo, info);
                    }
                }
            }
        }
    }

    public Map<String, String> createInfo(String loadedModel, String loadedModelVersion) {
        Map<String, String> info = new HashMap<>();
        info.put(NotificationHeaderEnum.REASON.getHeaderName(), ModeletManager.RESTART_REASON_6);
        info.put(NotificationHeaderEnum.LOADED_MODEL.getHeaderName(), loadedModel);
        info.put(NotificationHeaderEnum.LOADED_MODEL_VERSION.getHeaderName(), loadedModelVersion);
        return info;
    }

    @Override
    public PoolStatus getModeletPoolandCount(TransactionCriteria transactionCriteria) throws SystemException {
        PoolStatus poolStatus = null;
        KeyValuePair<String, Integer> poolDetails = poolManager.getModeletPoolAndCount(transactionCriteria);
        if (poolDetails != null && poolDetails.getKey() != null && poolDetails.getValue() != null) {
            poolStatus = new PoolStatus();
            poolStatus.setPoolname(poolDetails.getKey());
            poolStatus.setAvailablemodelets(poolDetails.getValue());
        }
        return poolStatus;

    }

    @Override
    public void startModelet(final ModeletClientInfo modeletClientInfo) throws SystemException, BusinessException {
        LOGGER.info("Received request to start modelet {}, {}.", modeletClientInfo.getHost(), modeletClientInfo.getPort());
        // if (StringUtils.equalsIgnoreCase(modeletClientInfo.getExecutionLanguage(), ExecutionLanguage.R.getValue())) {
        // modeletManager.startRServe(modeletClientInfo);
        // } else {
        modeletManager.startModelet(modeletClientInfo, ConnectorType.SSH.getType());
        // }
        LOGGER.info("started modelet {}, {} successfully.", modeletClientInfo.getHost(), modeletClientInfo.getPort());
    }

    @Override
    public void stopModelet(final ModeletClientInfo modeletClientInfo) throws SystemException {
        LOGGER.error("Received request to stop modelet {}, {}.", modeletClientInfo.getHost(), modeletClientInfo.getPort());
        modeletManager.stopModelet(modeletClientInfo);
        LOGGER.error("Initiated to stop modelet {}, {} successfully.", modeletClientInfo.getHost(), modeletClientInfo.getPort());
        LOGGER.info("Started refresh modelet allocation");
        // refreshModeletAllocation();
        LOGGER.info("Refresh modelet allocation done successfully");
    }

    private void updateModeletStatusInMap(final ModeletClientInfo mClientInfo, final String status) {
        final IMap<String, ModeletClientInfo> allModeletMap = cacheRegistry.getMap(PoolConstants.ALL_MODELET_MAP);
        final Set<String> keySet = allModeletMap.keySet();
        for (final String key : keySet) {
            final ModeletClientInfo clientInfo = allModeletMap.get(key);
            if (clientInfo.getMemberHost().equals(mClientInfo.getMemberHost()) && clientInfo.getExecutionLanguage()
                    .equals(mClientInfo.getExecutionLanguage()) && clientInfo.getHostKey().equals(mClientInfo.getHostKey())) {
                clientInfo.setModeletStatus(status);
                cacheRegistry.getMap(PoolConstants.ALL_MODELET_MAP).put(key, clientInfo);
                return;
            }
        }
    }

    private boolean checkErrorCodeForRestart(String errorCode) {
        boolean restartRequired = Boolean.FALSE;
        String modeletRestartCodes = systemParameterProvider.getParameter(SystemParameterConstants.R_MODELET_RESTART_ERROR_CODES);
        if (StringUtils.isNotBlank(errorCode) && StringUtils.isNotBlank(modeletRestartCodes)) {
            String[] restartCodes = StringUtils.split(modeletRestartCodes, FrameworkConstant.COMMA);
            if (restartCodes != null) {
                for (String restartCode : restartCodes) {
                    if (StringUtils.equalsIgnoreCase(errorCodeMap.get(StringUtils.trim(errorCode)), restartCode)) {
                        restartRequired = Boolean.TRUE;
                        break;
                    }
                }
            }
        }
        return restartRequired;
    }

    @Override
    public void startRserveProcess(ModeletClientInfo modeletClientInfo) throws SystemException {
        LOGGER.info("Received request to start modelet {}, {}.", modeletClientInfo.getHost(), modeletClientInfo.getPort());
        modeletManager.startRServe(modeletClientInfo);
        LOGGER.info("started modelet {}, {} successfully.", modeletClientInfo.getHost(), modeletClientInfo.getPort());

    }

    @Override
    public String fetchModeletResponse(ModeletClientInfo modeletClientInfo) throws SystemException, BusinessException {
        LOGGER.info("Received request to fetch modelet {}, {}. command result", modeletClientInfo.getHost(), modeletClientInfo.getPort());
        String result = modeletManager.fetchModeletResponse(modeletClientInfo, ConnectorType.SSH.getType());
        LOGGER.info("fetch modelet {}, {} command successfully.", modeletClientInfo.getHost(), modeletClientInfo.getPort());
        return result;
    }

    @Override
    public void restartModelets(List<ModeletClientInfo> modeletClientInfoList) {
        if (CollectionUtils.isNotEmpty(modeletClientInfoList)) {
            for (final ModeletClientInfo modeletClientInfo : modeletClientInfoList) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String modeletStatus = getModeletStatus(modeletClientInfo);
                            LOGGER.debug("Current status of the modelet  is : {}", modeletStatus);
                            if (StringUtils.equalsAny(modeletStatus, ModeletStatus.REGISTERED.getStatus(),
                                    ModeletStatus.STARTED.getStatus(), ModeletStatus.REGISTERED_WITH_SYSTEM_DEFAULT_POOL.getStatus())) {
                                modeletManager.stopModelet(modeletClientInfo);
                                final long delay = modeletManager.getModeletStartDelay();
                                LOGGER.debug("Modelet has been stopped successfully. Now sleeping for {} ms", delay);
                                Thread.sleep(delay);
                                LOGGER.debug("Going to start modelet now. After sleeping for {} ms", delay);
                                modeletManager.startModelet(modeletClientInfo, ConnectorType.SSH.getType());
                                LOGGER.debug("Modelet has been successfully started.");
                            } else if (StringUtils.equalsAny(modeletStatus, ModeletStatus.STOPPED.getStatus(),
                                    ModeletStatus.UNREGISTERED.getStatus(), ModeletStatus.REGISTRATION_INPROGRESS.getStatus(),
                                    ModeletStatus.FAILED.getStatus(), null)) {
                                LOGGER.debug("Going to start modelet directly now.");
                                modeletManager.startModelet(modeletClientInfo, ConnectorType.SSH.getType());
                                LOGGER.debug("Modelet has been successfully started.");
                            }
                        } catch (SystemException | BusinessException | InterruptedException exception) {
                            LOGGER.error("An error occurred while restarting modelet {}.", modeletClientInfo, exception);
                        }
                    }
                });
            }
        }
    }

    private String getModeletStatus(ModeletClientInfo modeletClientInfo) {
        final IMap<String, ModeletClientInfo> allModeletMap = cacheRegistry.getMap(ALL_MODELET_MAP);
        String key = StringUtils.join(modeletClientInfo.getHost(), FrameworkConstant.HYPHEN, modeletClientInfo.getPort());
        ModeletClientInfo fetchedModeletClientInfo = allModeletMap.get(key);
        LOGGER.info("Modelet fetched from cache : {}", fetchedModeletClientInfo.getString());
        return fetchedModeletClientInfo.getModeletStatus();
    }

    @Override
    public String fetchModeletLogs(ModeletClientInfo modeletClientInfo) throws SystemException {
        LOGGER.info("Received request to fetch modelet logs {}, {}. command result", modeletClientInfo.getHost(), modeletClientInfo.getPort());
        String result = modeletManager.fetchModeletLogs(modeletClientInfo, ConnectorType.SSH.getType());
        LOGGER.info("Fetch modelet logs {}, {} command executed successfully.", modeletClientInfo.getHost(), modeletClientInfo.getPort());
        return result;
    }
}