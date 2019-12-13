package com.ca.pool;

import static com.ca.framework.core.constants.PoolConstants.ALL_MODELET_MAP;
import static com.ca.framework.core.constants.PoolConstants.CRITERA_POOL_MAP;
import static com.ca.framework.core.constants.PoolConstants.MODEL;
import static com.ca.framework.core.constants.PoolConstants.MODEL_VERSION;
import static com.ca.framework.core.constants.PoolConstants.NUMBER_ZERO;
import static com.ca.framework.core.constants.PoolConstants.TENANT;
import static com.ca.framework.core.exception.SystemException.newSystemException;
import static com.ca.modelet.ModelCommands.LOAD_MODEL;
import static java.lang.Integer.valueOf;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.rmodel.info.SupportPackage;
import com.ca.modelet.ModeletClientInfo;
import com.ca.modelet.client.HttpModeletClient;
import com.ca.modelet.client.ModeletClient;
import com.ca.modelet.client.SocketModeletClient;
import com.ca.modelet.common.ServerType;
import com.ca.pool.manager.ModeletManager;
import com.ca.pool.model.ExecutionLanguage;
import com.ca.pool.model.TransactionCriteria;
import com.ca.pool.util.PoolCriteriaUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;

@SuppressWarnings("PMD")
public class ModeletAllocationAlgorithm {

    private static final Logger LOGGER = getLogger(ModeletAllocationAlgorithm.class);

    public static void loadModel(final ModeletClientInfo clientInfo, final PoolManager poolManager) throws SystemException {
        // TODO : Load model into Modelet, This is required only for R Models, and specific Model and Version, otherwise just
        // don't call load model
        // final IMap<String, ModeletClientInfo> allModeletMap = poolManager.getCacheRegistry().getMap(ALL_MODELET_MAP);
        // allModeletMap.put(clientInfo.getHostKey(), clientInfo);

        final Map<String, String> criteriaValueMap = new HashMap<String, String>();
        final boolean isSpecificRModel = clientInfo != null ? isSpecificRModel(clientInfo, poolManager, criteriaValueMap) : false;
        if (isSpecificRModel) {
            final Map<String, String> modelDetails = new HashMap<String, String>();
            final Map<String, Object> loadModelRequest = createLoadModelRequest(criteriaValueMap, poolManager, modelDetails);
            LOGGER.error("Load Model Request : {}", loadModelRequest.toString());
            final ModeletClient modeletClient = buildModeletClient(clientInfo);
            try {
            	modeletClient.createConnection();
            }catch(SystemException e) {
                LOGGER.error("Could not load the model as modelet {} seems to be down",clientInfo.getHostKey());
            	return;
            }
            final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String inputJson;
            try {
                inputJson = ow.writeValueAsString(loadModelRequest);
                modeletClient.sendData(inputJson);

                clientInfo.setLoadedModel(modelDetails.get("MODEL_NAME"));
                clientInfo.setLoadedModelVersion(modelDetails.get("MODEL_VERSION"));
                clientInfo.setModelLibraryVersionName(modelDetails.get("MODEL_LIBRARY_VERSION_NAME"));
            } catch (JsonProcessingException e) {
                LOGGER.error(" An error occurred while converting Model Load Request object into input JSON. {}", e);
                newSystemException("MSE0000200", new Object[] { e.getLocalizedMessage() });
            } catch (BusinessException be) {
                LOGGER.error(" An error occurred while senidng Model Load Request to Modelet. {}", be);
                newSystemException("MSE0000200", new Object[] { be.getLocalizedMessage() });
            } catch (SystemException sysExc) {
                LOGGER.error(" An error occurred while loading request.", sysExc);
                if (!equalsIgnoreCase("ME0007", sysExc.getCode()) || !equalsIgnoreCase("ME0005", sysExc.getCode())) {
                    LOGGER.error(" Exception received from modelet client.", sysExc);
                }

                throw sysExc;
            }
        }
    }

    private static Map<String, Object> createLoadModelRequest(final Map<String, String> criteriaValueMap,
            final PoolManager poolManager, final Map<String, String> modelDetailsToReturn) throws SystemException {
        final String modelName = criteriaValueMap.get(MODEL.toLowerCase());
        final String version = criteriaValueMap.get(MODEL_VERSION.toLowerCase());

        Integer majorVersion = valueOf(0);
        Integer minorVersion = valueOf(0);

        if (StringUtils.isNotBlank(version)) {
            try {
                String[] versionArr = version.split("\\.");
                if (versionArr.length > 2) {
                    throw newSystemException("MSE0000201", new Object[] { "Model Version has more than 2 numbers" });
                } else if (versionArr.length == 0) {
                    majorVersion = valueOf(version);
                } else {
                    majorVersion = Integer.valueOf(versionArr[0]);
                    if (versionArr.length == 2) {
                        minorVersion = Integer.valueOf(versionArr[1]);
                    }
                }
            } catch (NumberFormatException excp) {
                LOGGER.error(
                        " MSE0000201 : The Model Version data format is invalid. Valid Formats are : [Integer], [Integer.Integer]");
                throw newSystemException("MSE0000201", new Object[] { "Model Version is wrong" });
            }
        }

        final String tenantCode = criteriaValueMap.get(TENANT.toLowerCase());
        if (!tenantCode.equalsIgnoreCase("any")) {
            Map<String, Object> headerInfo = new LinkedHashMap<String, Object>();
            headerInfo.put("tenantCode", tenantCode);
            headerInfo.put("engine", ExecutionLanguage.R.getValue());
            headerInfo.put("commandName", LOAD_MODEL.getCommandName());

            headerInfo.put("libraries", getSupportPackageList(modelName, majorVersion, minorVersion, tenantCode, poolManager));
            LOGGER.error("Got Support Libraries");
            final Map<String, String> modelDetails = getModelPackageName(modelName, majorVersion, minorVersion, tenantCode,
                    poolManager);
            headerInfo.put("modelPackageName", modelDetails.get("PACKAGE_NAME"));
            headerInfo.put("modelLibraryName", modelDetails.get("MODEL_NAME"));
            headerInfo.put("jarName", modelDetails.get("JAR_NAME"));
            headerInfo.put("modelLibraryVersionName", modelDetails.get("MODEL_LIBRARY_VERSION_NAME"));
            TransactionCriteria transactionCriteria = new TransactionCriteria();
            transactionCriteria.setExecutionEnvironment(criteriaValueMap.get("execution_environment"));
            headerInfo.put("transactionCriteria", transactionCriteria);
            LOGGER.error("Got Model Details");

            modelDetailsToReturn.put("MODEL_NAME", modelDetails.get("VERSION_NAME"));
            modelDetailsToReturn.put("MODEL_VERSION", version);
            modelDetailsToReturn.put("MODEL_LIBRARY_VERSION_NAME", modelDetails.get("MODEL_LIBRARY_VERSION_NAME"));

            // Create model request
            final Map<String, Object> modelRequest = new LinkedHashMap<String, Object>();
            final List<Map<String, Object>> modelRequestBody = new ArrayList<>();

            modelRequest.put("headerInfo", headerInfo);
            modelRequest.put("payload", modelRequestBody);
            return modelRequest;
        } else {
            throw SystemException.newSystemException("MSE0000202",
                    new String[] { "Model Cannot be loaded into Modelet as Tenant is ANY or not know" });
        }
    }

    private static Map<String, String> getModelPackageName(final String modelName, final Integer majorVersion,
            final Integer minorVersion, final String tenantCode, final PoolManager poolManager) {
        return poolManager.getModelPackageName(modelName, majorVersion, minorVersion, tenantCode);
    }

    private static List<SupportPackage> getSupportPackageList(final String modelName, final Integer majorVersion,
            final Integer minorVersion, final String tenantCode, final PoolManager poolManager) {
        return poolManager.getSupportPackageList(modelName, majorVersion, minorVersion, tenantCode);
    }

    public static boolean isSpecificRModel(final ModeletClientInfo clientInfo, final PoolManager poolManager,
            final Map<String, String> criteriaValueMap) {
        boolean value = false;
        if (clientInfo.getExecutionLanguage().equalsIgnoreCase(ExecutionLanguage.R.getValue())) {
            final String poolCriteria = getPoolCriteria(clientInfo, poolManager.getCacheRegistry());
            PoolCriteriaUtil.getCriteraValues(poolCriteria, criteriaValueMap);
            if (criteriaValueMap.containsKey(MODEL.toLowerCase()) && criteriaValueMap.containsKey(MODEL_VERSION.toLowerCase())) {
                if (!criteriaValueMap.get(MODEL.toLowerCase()).equalsIgnoreCase("any")
                        && !criteriaValueMap.get(MODEL_VERSION.toLowerCase()).equalsIgnoreCase("any") && 
                        !(StringUtils.equalsIgnoreCase(criteriaValueMap.get(MODEL.toLowerCase()), clientInfo.getLoadedModel()) && 
                        		StringUtils.equalsIgnoreCase(criteriaValueMap.get(MODEL_VERSION.toLowerCase()), clientInfo.getLoadedModelVersion()))) {
                    value = true;
                    LOGGER.error("Pool Critera is : {}", poolCriteria);
                    LOGGER.error("This is for specific R Model, hence Modelet {} will be called to load Specific R Model",
                            clientInfo.getString());
                }
            }
        }

        return value;
    }

    private static String getPoolCriteria(final ModeletClientInfo clientInfo, final CacheRegistry cacheRegistry) {
        final IMap<String, String> criteriaPoolMap = cacheRegistry.getMap(CRITERA_POOL_MAP);
        final Set<String> criteriaSet = criteriaPoolMap.keySet();
        String poolCriteria = null;
        for (final String criteria : criteriaSet) {
            if (criteriaPoolMap.get(criteria) != null) {
                if (criteriaPoolMap.get(criteria).toString().equals(clientInfo.getPoolName())) {
                    poolCriteria = criteria;
                    LOGGER.error("{} pool criteria is {}", clientInfo.getPoolName(), poolCriteria);
                    break;
                }
            }
        }

        return poolCriteria;
    }

    private static ModeletClient buildModeletClient(final ModeletClientInfo modeletClientInfo) {
        ModeletClient modeletClient = null;
        LOGGER.error("Creating modelet client for modelel {}.", modeletClientInfo);
        switch (ServerType.valueOf(modeletClientInfo.getServerType())) {
        case HTTP:
            modeletClient = new HttpModeletClient(modeletClientInfo.getHost(), modeletClientInfo.getPort(),
                    modeletClientInfo.getContextPath());
            break;
        case SOCKET:
            modeletClient = new SocketModeletClient(modeletClientInfo.getHost(), modeletClientInfo.getPort());
            break;
        default:
            break;
        }
        LOGGER.error("Modelet client {} for modelet {} created successfully.", modeletClient, modeletClientInfo.getString());
        return modeletClient;
    }

    public static void addModeletsToDefault(final String deletedPoolName, final PoolManager poolManager,
            final ModeletManager modeletManager) throws SystemException {
        try {
            LOGGER.error(" Deleted Pool Name is :{}", deletedPoolName);
            if (deletedPoolName != null) {
                final IQueue<Object> deletedPoolQueue = poolManager.getPoolQueue(deletedPoolName);
                final int modeletCount = deletedPoolQueue.size();
                for (int count = 0; count < modeletCount; ++count) {
                    final Object object = deletedPoolQueue.poll(NUMBER_ZERO, MILLISECONDS);
                    if (object != null) {
                        final ModeletClientInfo clientInfo = (ModeletClientInfo) object;
                        final String execLanguage = clientInfo.getExecutionLanguage();
                        final String execEnvironment = clientInfo.getExecEnvironment();
                        LOGGER.error(" Deleted Pool execLanguage is :{}", execLanguage);
                        LOGGER.error(" Deleted Pool execEnvironment is :{}", execEnvironment);
                        final String envDefaultPoolName = poolManager.getEnvDefaultPoolName(execLanguage, execEnvironment);
                        LOGGER.error(" Adding deleted modelet to Environment Default Pool, Environment Default Pool  is :{}",
                                envDefaultPoolName);
                        clientInfo.setPoolName(envDefaultPoolName);
                        poolManager.addModeletToPoolQueue(clientInfo);

                        final IMap<String, ModeletClientInfo> allModeletMap = poolManager.getCacheRegistry()
                                .getMap(ALL_MODELET_MAP);
                        allModeletMap.put(clientInfo.getHostKey(), clientInfo);
                    }
                }
            }
        } catch (InterruptedException ie) {
            LOGGER.error(" An error occurred while retrieving modelet client.", ie);
            LOGGER.error("Modelet allocation is done with fail, please call getAllModeletInfo API");
            throw newSystemException("MSE0000200", new Object[] { ie.getLocalizedMessage() });
        }
    }

}