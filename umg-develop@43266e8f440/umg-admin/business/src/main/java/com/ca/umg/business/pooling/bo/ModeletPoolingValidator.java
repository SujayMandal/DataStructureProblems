package com.ca.umg.business.pooling.bo;

import static com.ca.framework.core.constants.PoolConstants.CHANNEL;
import static com.ca.framework.core.constants.PoolConstants.EXECUTION_ENVIRONMENT;
import static com.ca.framework.core.constants.PoolConstants.EXECUTION_LANGUAGE;
import static com.ca.framework.core.constants.PoolConstants.MODEL;
import static com.ca.framework.core.constants.PoolConstants.MODEL_VERSION;
import static com.ca.framework.core.constants.PoolConstants.NUMBER_SIGN;
import static com.ca.framework.core.constants.PoolConstants.TENANT;
import static com.ca.framework.core.constants.PoolConstants.TRANSACTION_MODE;
import static com.ca.framework.core.constants.PoolConstants.TRANSACTION_TYPE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.Channel;
import com.ca.pool.PoolObjectsLoader;
import com.ca.pool.TransactionMode;
import com.ca.pool.model.DefaultPool;
import com.ca.pool.model.Pool;
import com.ca.pool.model.PoolCriteriaDetails;
import com.ca.pool.util.PoolCriteriaUtil;
import com.ca.umg.business.pooling.entity.PoolEntity;
import com.ca.umg.business.pooling.model.CompletePoolDetails;

@SuppressWarnings("PMD")
public class ModeletPoolingValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModeletPoolingValidator.class);

    public static final String POOL_NAME_EMPTY = "Pool Name cannot be null or empty";
    public static final String POOL_DESC_EMPTY = "Pool Description cannot be null or empty";
    public static final String POOL_ENV_EMPTY = "Pool Environment cannot be null or empty";
    public static final String POOL_MODELET_CAPA_EMPTY = "Pool  Modelet Capacity cannot be null or empty";
    public static final String POOL_MODELET_COUNT_EMPTY = "Pool  Modelet Count cannot be negative";
    public static final String POOL_STATUS_NOT_EMPTY = "Pool Status should be null";
    public static final String POOL_DEFAULT_CANNOT = "User defined pool cannot be default pool";
    public static final String POOL_PRIORITY_EMPTY = "Pool Priority cannot be zero or negative";
    public static final String POOL_CRITERIA_TENANT_EMPTY = "Tenant Name cannot be null or empty";
    public static final String POOL_CRITERIA_ENV_EMPTY = "Environment Name cannot be null or empty";
    public static final String POOL_CRITERIA_ENV_VER_EMPTY = "Environment Version cannot be null or empty";
    public static final String POOL_CRITERIA_TRAN_TYPE_EMPTY = "Transaction Type cannot be null or empty";
    public static final String POOL_CRITERIA_TRAN_MODE_EMPTY = "Transaction Mode cannot be null or empty";
    public static final String POOL_CRITERIA_CHANNEL_EMPTY = "Channel cannot be null or empty";
    public static final String POOL_CRITERIA_FILE_BATCH = "Channel cannot be File for Transaction Type Batch";
    public static final String POOL_CRITERIA_FILE_ONLINE = "Channel cannot be File for Transaction Type Online";
    public static final String POOL_CRITERIA_MODEL_EMPTY = "Model Name cannot be null or empty";
    public static final String POOL_CRITERIA_MODEL_VER_EMPTY = "Model Version cannot be null or empty";
    public static final String R_POOL_CRITERIA_NOT_UNIQUE = "Pool criteria cannot be duplicate ";
    public static final String MATLAB_POOL_CRITERIA_NOT_UNIQUE = "Pool criteria cannot be duplicate ";
    public static final String EXCEL_POOL_CRITERIA_NOT_UNIQUE = "Pool criteria cannot be duplicate ";
    public static final String NO_CHANGE_FOR_UPDATE = "No changes made to pool criteria to update the configurations";
    public static final String VALIDATION_PASSED = "Validation Passed";
    public static final String VALIDATION_FAILED = "Validation Failed";
    public static final String POOL_DELETE_MODEL_COUNT = "Pool with modelets (non-empty pools) cannot be deleted";
    public static final String POOL_CHECK_DEFAULT = "Default pool cannot be deleted";
    public static final String POOL_NAME_IS_NOT_UNIQUE = "Pool name ";
    public static final String R_MODEL_POOL_PROPITY_NOT_UNIQUE = "Pool priority cannot be duplicate for the same Environment Name";
    public static final String MATLAB_MODEL_POOL_PROPITY_NOT_UNIQUE = "Pool priority cannot be duplicate for the same Environment Name";
    public static final String EXCEL_MODEL_POOL_PROPITY_NOT_UNIQUE = "Pool priority cannot be duplicate for the same Environment Name";
    public static final String MODELEAT_SHIFTING_ERROR = "Error during shifting modelets between selected environments";
    public static final String LESS_TO_HIGH_PRIORITY = "Cannot shift modelet From higher priority pool To lower priority pool";
    public static final String DEFAULT_POOL_CANNOT_UPDATED = "Default pool cannot be updated";
    public static final String DEFAULT_POOL_CANNOT_EMPTY = "Default pool cannot be empty";

    public static List<String> validateNewPool(final CompletePoolDetails poolDetails,
            final List<CompletePoolDetails> poolDetailsList, final SystemParameterProvider spp) {
        final List<String> errorList = new ArrayList<>();
        final List<CompletePoolDetails> newPoolDetailsList = new ArrayList<>();

        newPoolDetailsList.add(0, poolDetails);
        newPoolDetailsList.addAll(poolDetailsList);

        validateCreatePoolDetails(newPoolDetailsList, spp, errorList,
                poolDetails.getPoolCriteriaDetails().getExecutionEnvironment());

        return errorList;
    }

    public static List<String> validateUpdatePools(final List<CompletePoolDetails> poolDetailsList,
            final SystemParameterProvider spp, final ModeletPoolingBO bo, final PoolObjectsLoader poolObjectLoader,
            final CacheRegistry cacheRegistry) {
        final List<String> errorList = new ArrayList<>();
        // validateIfNoChangeForUpdate(poolDetailsList, errorList);
        validateIfDefaultPool(poolDetailsList, errorList);
        // validateModeletShiftingCount(poolDetailsList, errorList);
        if (errorList.isEmpty()) {
            validatePoolDetails(poolDetailsList, spp, errorList);
        }

        return errorList;
    }

    public static List<String> validateDeleteUpdatePools(final PoolEntity existingPoolEntity, final SystemParameterProvider spp,
            final ModeletPoolingBO bo, final PoolObjectsLoader poolObjectLoader, final CacheRegistry cacheRegistry) {
        final List<String> errorList = new ArrayList<>();
        validateDeletePoolDetails(existingPoolEntity, spp, errorList);
        return errorList;
    }

    public static List<CompletePoolDetails> findupdatedPools(final List<CompletePoolDetails> poolDetailList,
            final CacheRegistry cacheRegistry) {
        final List<CompletePoolDetails> updatedPoolList = new ArrayList<>();
        for (final CompletePoolDetails poolDetails : poolDetailList) {
            if (!ModeletPoolingBOImpl.isSystemTempPool(poolDetails.getPool().getPoolName())) {
                boolean updated = false;
                if (!StringUtils.equalsIgnoreCase(poolDetails.getPoolCriteria(),
                        ModeletPoolingBOImpl.createPoolCriteriaValue(poolDetails))) {
                    updated = true;
                    LOGGER.error("Criteria updated for pool %s. Old criteria %s and new criteria %s.", poolDetails.getPool()
                            .getPoolName(), poolDetails.getPoolCriteria(), ModeletPoolingBOImpl
                            .createPoolCriteriaValue(poolDetails));
                }
                if (poolDetails.getPool().getOldWaitTimeout().intValue() != poolDetails.getPool().getWaitTimeout().intValue()) {
                    updated = true;
                    LOGGER.error("Wait timeout updated for pool %s. Old time %s and new time %s.", poolDetails.getPool()
                            .getPoolName(), poolDetails.getPool().getOldWaitTimeout(), poolDetails.getPool().getWaitTimeout());
                }
                for (final ModeletClientInfo modeletClientInfo : poolDetails.getModeletClientInfoList()){
                    if(!StringUtils.equalsIgnoreCase(modeletClientInfo.getPoolName(), poolDetails.getPool().getPoolName())){
                        updated = true;
                        LOGGER.error("Modelet %s moved from pool %s to pool %s.", modeletClientInfo.getHostKey(),
                                modeletClientInfo.getPoolName(), poolDetails.getPool().getPoolName());
                    }
                }
                if (poolDetails.getModeletClientInfoList().size() < poolDetails.getPool().getModeletCount()) {
                    updated = true;
                }
                if (updated) {
                    updatedPoolList.add(poolDetails);
                }
            }
        }
        return updatedPoolList;
    }

    /*
     * New Method adding For Validating The Pool at the time of pool creation UMG-4941
     */
    private static void validateCreatePoolDetails(final List<CompletePoolDetails> poolDetailsList,
            final SystemParameterProvider spp, final List<String> errorList, String executionEnvironment) {
        final CompletePoolDetails poolDetails = poolDetailsList.get(0);
        final Pool pool = poolDetails.getPool();
        final PoolCriteriaDetails criteriaDetails = poolDetails.getPoolCriteriaDetails();
        validatePoolName(pool.getPoolName(), errorList);
        validatePoolDescription(pool.getPoolDesc(), errorList);
        validatePoolEnvironment(pool.getExecutionLanguage(), errorList);
        validatePoolDefault(pool, errorList);
        validatePoolPriority(pool.getPriority(), errorList);
        validatePoolStatus(pool.getPoolStatus(), errorList);
        validateModeletCapacity(pool.getModeletCapacity(), errorList);
        if (!DefaultPool.isDefaultPool(pool)) {
            validateModeletCount(pool.getModeletCount(), errorList);
        }
        validateCriteriaEnvironment(criteriaDetails.getExecutionLanguage(), errorList);     
        validateCriteriaModel(criteriaDetails.getModelName(), errorList);
        validateCriteriaModelVersion(criteriaDetails.getModelVersion(), errorList);
        validateCriteriaTenant(criteriaDetails.getTenant(), errorList);
        validateCriteriaTranType(criteriaDetails.getTransactionType(), errorList);
        validateCriteriaTranMode(criteriaDetails.getTransactionMode(), errorList);
        validateCriteriaChannel(criteriaDetails.getChannel(), criteriaDetails.getTransactionMode(), errorList);
        validateMatlabModel(poolDetails, errorList);
        validatePoolDuplicateName(poolDetailsList, errorList);
        validateRModel(poolDetails, errorList);

        poolDetails.setPoolCriteria(buildPoolCriteriaValue(poolDetails));

        if (PoolCriteriaUtil.isMatlab(poolDetails.getPool().getExecutionLanguage())) {
            validateMatlabPoolPriority(poolDetailsList, errorList);
            validateMatlabPoolCriteria(poolDetailsList, errorList);
        } else if (PoolCriteriaUtil.isExcel(poolDetails.getPool().getExecutionLanguage())) {
            validateExcelPoolPriority(poolDetailsList, errorList);
            validateExcelPoolCriteria(poolDetailsList, errorList);
        } else {
            validateRPoolPriority(poolDetailsList, errorList, executionEnvironment);
            validateRPoolCriteria(poolDetailsList, errorList, executionEnvironment);
        }

        // validateRModeletCount(spp, poolDetailsList, errorList);
        // validateMatlabModeletCount(spp, poolDetailsList, errorList);
    }

    private static void validatePoolDetails(final List<CompletePoolDetails> poolDetailsList, final SystemParameterProvider spp,
            final List<String> errorList) {
        for (final CompletePoolDetails poolDetails : poolDetailsList) {
            final Pool pool = poolDetails.getPool();
            if (!pool.getPoolName().equals(PoolConstants.DEFAULT_POOL)
                    && !pool.getPoolName().equals(PoolConstants.INACTIVE_MODELETS_POOL)) {
                final PoolCriteriaDetails criteriaDetails = poolDetails.getPoolCriteriaDetails();

                validatePoolName(pool.getPoolName(), errorList);
                validatePoolDescription(pool.getPoolDesc(), errorList);
                validatePoolEnvironment(pool.getExecutionLanguage(), errorList);
                // validatePoolDefault(pool, errorList);
                validatePoolPriority(pool.getPriority(), errorList);
                LOGGER.error("Pool Name is :" + pool.getPoolName());
                validateModeletCapacity(pool.getModeletCapacity(), errorList);
                validateCriteriaEnvironment(criteriaDetails.getExecutionLanguage(), errorList);
              //  validateCriteriaEnvVersion(criteriaDetails.getExecutionLanguageVersion(), errorList);
                validateCriteriaModel(criteriaDetails.getModelName(), errorList);
                validateCriteriaModelVersion(criteriaDetails.getModelVersion(), errorList);
                validateCriteriaTenant(criteriaDetails.getTenant(), errorList);
                validateCriteriaTranType(criteriaDetails.getTransactionType(), errorList);
                validateCriteriaTranMode(criteriaDetails.getTransactionMode(), errorList);
                validateCriteriaChannel(criteriaDetails.getChannel(), criteriaDetails.getTransactionMode(), errorList);
                if (!DefaultPool.isDefaultPool(pool)) {
                    validateModeletCount(pool.getModeletCount(), errorList);
                }
                validateMatlabModel(poolDetails, errorList);
                validateRModel(poolDetails, errorList);
            }
        }

        validateMatlabPoolPriority(poolDetailsList, errorList);
        validateExcelPoolPriority(poolDetailsList, errorList);
        validateRPoolPriority(poolDetailsList, errorList, SystemConstants.LINUX_OS);
        validateRPoolPriority(poolDetailsList, errorList, SystemConstants.WINDOWS_OS);
        validateMatlabPoolUpdateCriteria(poolDetailsList, errorList);
        validateExcelPoolUpdateCriteria(poolDetailsList, errorList);
        validateRPoolUpdateCriteria(poolDetailsList, errorList, SystemConstants.LINUX_OS);
        validateRPoolUpdateCriteria(poolDetailsList, errorList, SystemConstants.WINDOWS_OS);
        validateModeleatShifting(poolDetailsList, errorList);
        // validateModeleatShiftingPriority(poolDetailsList, errorList);

        // validateRModeletCount(spp, poolDetailsList, errorList);
        // validateMatlabModeletCount(spp, poolDetailsList, errorList);
    }

    private static void validateDeletePoolDetails(final PoolEntity existingPoolEntity, final SystemParameterProvider spp,
            final List<String> errorList) {
        validateDefaultPool(existingPoolEntity, errorList);
        validateModeletCountInPool(existingPoolEntity, errorList);
    }

    private static void validatePoolName(final String poolName, final List<String> errorList) {
        if (poolName == null || poolName.trim().isEmpty()) {
            errorList.add(POOL_NAME_EMPTY);
        }
    }

    private static void validatePoolDescription(final String poolDescription, final List<String> errorList) {
        if (poolDescription == null || poolDescription.trim().isEmpty()) {
            errorList.add(POOL_DESC_EMPTY);
        }
    }

    private static void validatePoolStatus(final String poolStatus, final List<String> errorList) {
        if (poolStatus != null) {
            LOGGER.error("Pool Status is : " + poolStatus);
            errorList.add(POOL_STATUS_NOT_EMPTY);
        }
    }

    private static void validatePoolEnvironment(final String poolEnvironment, final List<String> errorList) {
        if (poolEnvironment == null || poolEnvironment.trim().isEmpty()) {
            errorList.add(POOL_ENV_EMPTY);
        }
    }

    private static void validatePoolDefault(final Pool pool, final List<String> errorList) {
        if (DefaultPool.isDefaultPool(pool)) {
            errorList.add(POOL_DEFAULT_CANNOT);
        }
    }

    private static void validateModeletCount(final int modeletCount, final List<String> errorList) {
        if (modeletCount < 0) {
            errorList.add(POOL_MODELET_COUNT_EMPTY);
        }
    }

    private static void validateModeletCapacity(final String modeletCapacity, final List<String> errorList) {
        if (modeletCapacity == null || modeletCapacity.trim().isEmpty()) {
            errorList.add(POOL_MODELET_CAPA_EMPTY);
        }
    }

    private static void validatePoolPriority(final int poolPriority, final List<String> errorList) {
        if (poolPriority < 1) {
            errorList.add(POOL_PRIORITY_EMPTY);
        }
    }

    private static void validateCriteriaTenant(final String tenant, final List<String> errorList) {
        if (tenant == null || tenant.trim().isEmpty()) {
            errorList.add(POOL_CRITERIA_TENANT_EMPTY);
        }
    }

    private static void validateCriteriaEnvironment(final String environment, final List<String> errorList) {
        if (environment == null || environment.trim().isEmpty()) {
            errorList.add(POOL_CRITERIA_ENV_EMPTY);
        }
    }

    private static void validateCriteriaEnvVersion(final String environmentVersion, final List<String> errorList) {
        if (environmentVersion == null || environmentVersion.trim().isEmpty()) {
            errorList.add(POOL_CRITERIA_ENV_VER_EMPTY);
        }
    }

    private static void validateCriteriaTranType(final String transactionType, final List<String> errorList) {
        if (transactionType == null || transactionType.trim().isEmpty()) {
            errorList.add(POOL_CRITERIA_TRAN_TYPE_EMPTY);
        }
    }

    private static void validateCriteriaTranMode(final String transactionMode, final List<String> errorList) {
        if (transactionMode == null || transactionMode.trim().isEmpty()) {
            errorList.add(POOL_CRITERIA_TRAN_MODE_EMPTY);
        }
    }

    private static void validateCriteriaChannel(final String channel, final String transactionMode,
            final List<String> errorList) {
        if (channel == null || channel.trim().isEmpty()) {
            errorList.add(POOL_CRITERIA_CHANNEL_EMPTY);
        } else if (transactionMode.equalsIgnoreCase(TransactionMode.ONLINE.getMode())
                && channel.equalsIgnoreCase(Channel.FILE.getChannel())) {
            errorList.add(POOL_CRITERIA_FILE_ONLINE);
        } else if (transactionMode.equalsIgnoreCase(TransactionMode.BATCH.getMode())
                && channel.equalsIgnoreCase(Channel.FILE.getChannel())) {
            errorList.add(POOL_CRITERIA_FILE_BATCH);
        }
    }

    private static void validateCriteriaModel(final String model, final List<String> errorList) {
        if (model == null || model.trim().isEmpty()) {
            errorList.add(POOL_CRITERIA_MODEL_EMPTY);
        }
    }

    private static void validateCriteriaModelVersion(final String modelVersion, final List<String> errorList) {
        if (modelVersion == null || modelVersion.trim().isEmpty()) {
            errorList.add(POOL_CRITERIA_MODEL_VER_EMPTY);
        }
    }

    /*
     * private static void validateMatlabModeletCount(final SystemParameterProvider spp, final List<CompletePoolDetails>
     * poolDetailsList, final List<String> errorList) { final int totalModeletCount = getMatlabModeletCount(poolDetailsList);
     * final int maxModelCount = Integer.valueOf(spp.getParameter(PoolConstants.MATLAB_MAX_MODELET_COUNT)); if (totalModeletCount
     * > maxModelCount) { errorList.add("Matlab Modelet Count is more than Matlab Maximum Count, Matlab Maximum Count is : " +
     * maxModelCount); } }
     */

    /*
     * private static void validateRModeletCount(final SystemParameterProvider spp, final List<CompletePoolDetails>
     * poolDetailsList, final List<String> errorList) { final int totalModeletCount = getRModeletCount(poolDetailsList); final int
     * maxModelCount = Integer.valueOf(spp.getParameter(PoolConstants.R_MAX_MODELET_COUNT)); if (totalModeletCount >
     * maxModelCount) { errorList.add("R Modelet Count is more than Matlab Maximum Count, Matlab Maximum Count is : " +
     * maxModelCount); } }
     */

    /*
     * private static int getMatlabModeletCount(final List<CompletePoolDetails> poolDetailsList) { int modeletCount = 0; for
     * (final CompletePoolDetails poolDetails : poolDetailsList) { if
     * (PoolCriteriaUtil.isMatlab(poolDetails.getPool().getEnvironment())) { modeletCount +=
     * poolDetails.getPool().getModeletCount(); } }
     * 
     * return modeletCount; }
     */
    /*
     * private static int getRModeletCount(final List<CompletePoolDetails> poolDetailsList) { int modeletCount = 0; for (final
     * CompletePoolDetails poolDetails : poolDetailsList) { if (PoolCriteriaUtil.isR(poolDetails.getPool().getEnvironment())) {
     * modeletCount += poolDetails.getPool().getModeletCount(); } }
     * 
     * return modeletCount; }
     */

    private static void validateMatlabPoolPriority(final List<CompletePoolDetails> poolDetailsList,
            final List<String> errorList) {
        boolean addStatus = false;
        final Set<Integer> prioritySet = new HashSet<>();
        for (final CompletePoolDetails poolDetails : poolDetailsList) {
            if (PoolCriteriaUtil.isMatlab(poolDetails.getPool().getExecutionLanguage())) {
                addStatus = prioritySet.add(Integer.valueOf(poolDetails.getPool().getPriority()));
                if (addStatus == false) {
                    errorList.add(MATLAB_MODEL_POOL_PROPITY_NOT_UNIQUE);
                    break;
                }
            }
        }
    }

    private static void validateExcelPoolPriority(final List<CompletePoolDetails> poolDetailsList, final List<String> errorList) {
        boolean addStatus = false;
        final Set<Integer> prioritySet = new HashSet<>();
        for (final CompletePoolDetails poolDetails : poolDetailsList) {
            if (PoolCriteriaUtil.isExcel(poolDetails.getPool().getExecutionLanguage())) {
                addStatus = prioritySet.add(Integer.valueOf(poolDetails.getPool().getPriority()));
                if (addStatus == false) {
                    errorList.add(EXCEL_MODEL_POOL_PROPITY_NOT_UNIQUE);
                    break;
                }
            }
        }
    }

    private static void validateRPoolPriority(final List<CompletePoolDetails> poolDetailsList, final List<String> errorList,
            String executionEnvironment) {
        boolean addStatus = false;
        final Set<Integer> prioritySet = new HashSet<>();
        for (final CompletePoolDetails poolDetails : poolDetailsList) {
            if (PoolCriteriaUtil.isR(poolDetails.getPool().getExecutionLanguage())
                    && poolDetails.getPoolCriteriaDetails().getExecutionEnvironment().equalsIgnoreCase(executionEnvironment)
                    && !(poolDetails.getPool().getPoolName().equals(PoolConstants.INACTIVE_MODELETS_POOL))) {
                addStatus = prioritySet.add(Integer.valueOf(poolDetails.getPool().getPriority()));
                if (addStatus == false) {
                    errorList.add(R_MODEL_POOL_PROPITY_NOT_UNIQUE);
                    break;
                }
            }
        }
    }

    private static void validateMatlabModel(final CompletePoolDetails poolDetails, final List<String> errorList) {
        final Pool pool = poolDetails.getPool();
        final PoolCriteriaDetails criteriaDetails = poolDetails.getPoolCriteriaDetails();

        if (PoolCriteriaUtil.isMatlab(pool.getExecutionLanguage())) {
            if (!PoolCriteriaUtil.isModelAny(criteriaDetails.getModelName())) {
                errorList.add("Matlab Model can not be Specific Model for Matlab Pools. Please select Any");
            }
        }
    }

    private static void validateRModel(final CompletePoolDetails poolDetails, final List<String> errorList) {
        final Pool pool = poolDetails.getPool();
        final PoolCriteriaDetails criteriaDetails = poolDetails.getPoolCriteriaDetails();

        if (PoolCriteriaUtil.isTenantAny(criteriaDetails.getTenant())) {
            if (PoolCriteriaUtil.isR(pool.getExecutionLanguage())) {
                if (!PoolCriteriaUtil.isModelAny(criteriaDetails.getModelName())) {
                    errorList.add("R Model can not be Specific Model when Tenant is Any");
                }
            }
        }
    }

    private static void validatePoolDuplicateName(final List<CompletePoolDetails> poolDetailsList, final List<String> errorList) {
        boolean addStatus = false;
        final Set<String> nameSet = new HashSet<>();

        for (final CompletePoolDetails poolDetails : poolDetailsList) {
            addStatus = nameSet.add(poolDetails.getPool().getPoolName());
            if (addStatus == false) {
                errorList.add(POOL_NAME_IS_NOT_UNIQUE + poolDetails.getPool().getPoolName() + " already exists.");
                break;
            }
        }
    }

    private static void validateDefaultPool(final PoolEntity existingPoolEntity, final List<String> errorList) {
        if (existingPoolEntity.isDefaultPool() == 1) {
            errorList.add(POOL_CHECK_DEFAULT);
        }
    }

    private static void validateModeletCountInPool(final PoolEntity existingPoolEntity, final List<String> errorList) {
        if (existingPoolEntity.getModeletCount() > 0) {
            errorList.add(POOL_DELETE_MODEL_COUNT);
        }
    }

    private static void validateRPoolCriteria(final List<CompletePoolDetails> poolDetailsList, final List<String> errorList,
            String executionEnvironment) {
        boolean addStatus = false;
        final Set<String> criteriaSet = new HashSet<>();

        for (final CompletePoolDetails poolDetails : poolDetailsList) {
            /*
             * if (poolDetails.getPoolCriteria() == null &&
             * !(poolDetails.getPool().getPoolName().equals(PoolConstants.DEFAULT_POOL))) { String criteriaString =
             * buildPoolCriteriaValue(poolDetails); poolDetails.setPoolCriteria(criteriaString); }
             */
            if (PoolCriteriaUtil.isR(poolDetails.getPool().getExecutionLanguage())
                    && poolDetails.getPoolCriteriaDetails().getExecutionEnvironment().equalsIgnoreCase(executionEnvironment)
                    && !(poolDetails.getPool().getPoolName().equals(PoolConstants.DEFAULT_POOL))
                    && !(poolDetails.getPool().getPoolName().equals(PoolConstants.INACTIVE_MODELETS_POOL))) {
                addStatus = criteriaSet.add(StringUtils.lowerCase(poolDetails.getPoolCriteria()));
                if (addStatus == false) {
                    errorList.add(
                            R_POOL_CRITERIA_NOT_UNIQUE + "(duplicates with pool " + poolDetails.getPool().getPoolName() + ")");
                    break;
                }
            }
        }
    }

    private static void validateMatlabPoolCriteria(final List<CompletePoolDetails> poolDetailsList,
            final List<String> errorList) {
        boolean addStatus = false;
        final Set<String> criteriaSet = new HashSet<>();

        for (final CompletePoolDetails poolDetails : poolDetailsList) {
            /*
             * if (poolDetails.getPoolCriteria() == null &&
             * !(poolDetails.getPool().getPoolName().equals(PoolConstants.DEFAULT_POOL))) { String criteriaString =
             * buildPoolCriteriaValue(poolDetails); poolDetails.setPoolCriteria(criteriaString); }
             */

            if (PoolCriteriaUtil.isMatlab(poolDetails.getPool().getExecutionLanguage())
                    && !(poolDetails.getPool().getPoolName().equals(PoolConstants.DEFAULT_POOL))
                    && !(poolDetails.getPool().getPoolName().equals(PoolConstants.INACTIVE_MODELETS_POOL))) {
                addStatus = criteriaSet.add(StringUtils.lowerCase(poolDetails.getPoolCriteria()));
                if (addStatus == false) {
                    errorList.add(MATLAB_POOL_CRITERIA_NOT_UNIQUE + "(duplicates with pool " + poolDetails.getPool().getPoolName()
                            + ")");
                    break;
                }
            }
        }
    }

    private static void validateExcelPoolCriteria(final List<CompletePoolDetails> poolDetailsList, final List<String> errorList) {
        boolean addStatus = false;
        final Set<String> criteriaSet = new HashSet<>();

        for (final CompletePoolDetails poolDetails : poolDetailsList) {
            /*
             * if (poolDetails.getPoolCriteria() == null &&
             * !(poolDetails.getPool().getPoolName().equals(PoolConstants.DEFAULT_POOL))) { String criteriaString =
             * buildPoolCriteriaValue(poolDetails); poolDetails.setPoolCriteria(criteriaString); }
             */

            if (PoolCriteriaUtil.isExcel(poolDetails.getPool().getExecutionLanguage())
                    && !(poolDetails.getPool().getPoolName().equals(PoolConstants.DEFAULT_POOL))
                    && !(poolDetails.getPool().getPoolName().equals(PoolConstants.INACTIVE_MODELETS_POOL))) {
                addStatus = criteriaSet.add(StringUtils.lowerCase(poolDetails.getPoolCriteria()));
                if (addStatus == false) {
                    errorList.add(EXCEL_POOL_CRITERIA_NOT_UNIQUE + "(duplicates with pool " + poolDetails.getPool().getPoolName()
                            + ")");
                    break;
                }
            }
        }
    }

    private static void validateRPoolUpdateCriteria(final List<CompletePoolDetails> poolDetailsList, final List<String> errorList,
            String executionEnvironment) {
        boolean addStatus = false;
        final Set<String> criteriaSet = new HashSet<>();

        for (final CompletePoolDetails poolDetails : poolDetailsList) {
            if (PoolCriteriaUtil.isR(poolDetails.getPool().getExecutionLanguage())
                    && poolDetails.getPoolCriteriaDetails().getExecutionEnvironment().equalsIgnoreCase(executionEnvironment)
                    && !(poolDetails.getPool().getPoolName().equals(PoolConstants.DEFAULT_POOL))
                    && !(poolDetails.getPool().getPoolName().equals(PoolConstants.INACTIVE_MODELETS_POOL))) {
                String criteriaString = buildPoolCriteriaValue(poolDetails);
                addStatus = criteriaSet.add(StringUtils.lowerCase(criteriaString));
                if (addStatus == false) {
                    errorList.add(
                            R_POOL_CRITERIA_NOT_UNIQUE + "(duplicates with pool " + poolDetails.getPool().getPoolName() + ")");
                }
            }
        }
    }

    private static void validateMatlabPoolUpdateCriteria(final List<CompletePoolDetails> poolDetailsList,
            final List<String> errorList) {
        boolean addStatus = false;
        final Set<String> criteriaSet = new HashSet<>();

        for (final CompletePoolDetails poolDetails : poolDetailsList) {
            if (PoolCriteriaUtil.isMatlab(poolDetails.getPool().getExecutionLanguage())
                    && !(poolDetails.getPool().getPoolName().equals(PoolConstants.DEFAULT_POOL))
                    && !(poolDetails.getPool().getPoolName().equals(PoolConstants.INACTIVE_MODELETS_POOL))) {
                String criteriaString = buildPoolCriteriaValue(poolDetails);
                addStatus = criteriaSet.add(StringUtils.lowerCase(criteriaString));
                if (addStatus == false) {
                    errorList.add(MATLAB_POOL_CRITERIA_NOT_UNIQUE + "(duplicates with pool " + poolDetails.getPool().getPoolName()
                            + ")");
                    break;
                }
            }
        }
    }

    private static void validateExcelPoolUpdateCriteria(final List<CompletePoolDetails> poolDetailsList,
            final List<String> errorList) {
        boolean addStatus = false;
        final Set<String> criteriaSet = new HashSet<>();

        for (final CompletePoolDetails poolDetails : poolDetailsList) {
            if (PoolCriteriaUtil.isExcel(poolDetails.getPool().getExecutionLanguage())
                    && !(poolDetails.getPool().getPoolName().equals(PoolConstants.DEFAULT_POOL))
                    && !(poolDetails.getPool().getPoolName().equals(PoolConstants.INACTIVE_MODELETS_POOL))) {
                String criteriaString = buildPoolCriteriaValue(poolDetails);
                addStatus = criteriaSet.add(StringUtils.lowerCase(criteriaString));
                if (addStatus == false) {
                    errorList.add(EXCEL_POOL_CRITERIA_NOT_UNIQUE + "(duplicates with pool " + poolDetails.getPool().getPoolName()
                            + ")");
                    break;
                }
            }
        }
    }

    private static void validateIfNoChangeForUpdate(final List<CompletePoolDetails> poolDetailsList,
            final List<String> errorList) {
        boolean status = false;
        for (final CompletePoolDetails poolDetails : poolDetailsList) {
            if (poolDetails.getPool().getPoolName().equals(PoolConstants.DEFAULT_POOL)
                    || poolDetails.getPool().getPoolName().equals(PoolConstants.INACTIVE_MODELETS_POOL)) {
                continue;
            }
            String criteriaString = buildPoolCriteriaValue(poolDetails);
            if (!(criteriaString.equalsIgnoreCase(poolDetails.getPoolCriteria()))) {
                status = true;
                break;
            }
            if (poolDetails.getPool().getWaitTimeout().intValue() != poolDetails.getPool().getOldWaitTimeout().intValue()) {
                status = true;
                break;
            }
            if (!(DefaultPool.isDefaultPool(poolDetails.getPool()) && poolDetails.getPoolCriteriaDetails()
                    .getExecutionEnvironment().equalsIgnoreCase(SystemConstants.LINUX_OS))) {
                if (!(poolDetails.getPool().getModeletAdded() == 0 && poolDetails.getPool().getModeletRemoved() == 0)) {
                    status = true;
                    break;
                }
            }
        }
        if (!status) {
            errorList.add(NO_CHANGE_FOR_UPDATE);
        }
    }

    private static void validateModeleatShifting(final List<CompletePoolDetails> poolDetailsList, final List<String> errorList) {
        for (final CompletePoolDetails poolDetails : poolDetailsList) {
            if (!(poolDetails.getModeletClientInfoList().isEmpty())
                    && !(poolDetails.getPool().getPoolName().equals(PoolConstants.DEFAULT_POOL))
                    && !(poolDetails.getPool().getPoolName().equals(PoolConstants.INACTIVE_MODELETS_POOL))) {
                for (ModeletClientInfo modeletClientInfo : poolDetails.getModeletClientInfoList()) {
                    if (!(modeletClientInfo.getExecutionLanguage()
                            .equalsIgnoreCase(poolDetails.getPool().getExecutionLanguage()))) {
                        errorList.add(MODELEAT_SHIFTING_ERROR);
                        break;
                    }
                }
            }
        }
    }

    private static void validateIfDefaultPool(final List<CompletePoolDetails> poolDetailsList, final List<String> errorList) {
        for (final CompletePoolDetails poolDetails : poolDetailsList) {
            if (poolDetails.getPool().getPoolName().equals(PoolConstants.DEFAULT_POOL)
                    || poolDetails.getPool().getPoolName().equals(PoolConstants.INACTIVE_MODELETS_POOL)) {
                continue;
            }
            if ((DefaultPool.isDefaultPool(poolDetails.getPool()))) {
                String criteriaString = buildPoolCriteriaValue(poolDetails);
                if (!(criteriaString.equalsIgnoreCase(poolDetails.getPoolCriteria()))) {
                    LOGGER.info("Existing Pool criteria String : " + poolDetails.getPoolCriteria());
                    LOGGER.info("Constructed Pool criteria String : " + criteriaString);
                    errorList.add(DEFAULT_POOL_CANNOT_UPDATED);
                    break;
                }
            }
        }
    }

    private static void validateModeletShiftingCount(final List<CompletePoolDetails> poolDetailsList,
            final List<String> errorList) {
        int countCheck = 0;
        for (CompletePoolDetails cDetails : poolDetailsList) {
            Integer i = cDetails.getPool().getModeletAdded() - cDetails.getPool().getModeletRemoved();
            if (i.intValue() == 1 || i.intValue() == -1) {
                countCheck++;
            }
            if (i.intValue() > 1 || i.intValue() < -1) {
                // errorList.add("Cannot move more then one modelet at a time");
                // break;
            }

            if (countCheck > 2) {
                // errorList.add("Cannot move more then one modelet at a time");
                // break;
            }
        }
    }

    /*
     * private static void validateModeleatShiftingPriority(final List<CompletePoolDetails> poolDetailsList, final List<String>
     * errorList) { for (final CompletePoolDetails poolDetails : poolDetailsList) { if
     * (!(poolDetails.getModeletClientInfoList().isEmpty()) &&
     * !(poolDetails.getPool().getPoolName().equals(PoolConstants.DEFAULT_POOL))) { for(ModeletClientInfo modeletClientInfo :
     * poolDetails.getModeletClientInfoList()){ if(modeletClientInfo.getPoolName()!=null){ for (final CompletePoolDetails
     * poolDetails1 : poolDetailsList) {
     * if(poolDetails1.getPool().getPoolName().equalsIgnoreCase(modeletClientInfo.getPoolName())){
     * if(poolDetails1.getPool().getPriority() < poolDetails.getPool().getPriority()){ errorList.add(LESS_TO_HIGH_PRIORITY);
     * break; } } } }
     * 
     * } } } }
     */

    private static String buildPoolCriteriaValue(final CompletePoolDetails poolDetails) {
        final String tenant = poolDetails.getPoolCriteriaDetails().getTenant();
        final String environment = poolDetails.getPoolCriteriaDetails().getExecutionLanguage();
       // final String environmentVersion = poolDetails.getPoolCriteriaDetails().getExecutionLanguageVersion();
        final String transactionType = poolDetails.getPoolCriteriaDetails().getTransactionType();
        final String model = poolDetails.getPoolCriteriaDetails().getModelName();
        final String modelVersion = poolDetails.getPoolCriteriaDetails().getModelVersion();
        final String transactionMode = poolDetails.getPoolCriteriaDetails().getTransactionMode();
        final String channel = poolDetails.getPoolCriteriaDetails().getChannel();
        final String executionEnvironment = poolDetails.getPoolCriteriaDetails().getExecutionEnvironment();

        final StringBuilder sb = new StringBuilder();
        sb.append(NUMBER_SIGN).append(TENANT).append(NUMBER_SIGN).append(" = ").append(tenant).append(" & ");
        sb.append(NUMBER_SIGN).append(EXECUTION_LANGUAGE).append(NUMBER_SIGN).append(" = ").append(environment).append(" & ");
       /* sb.append(NUMBER_SIGN).append(EXECUTION_LANGUAGE_VERSION).append(NUMBER_SIGN).append(" = ").append(environmentVersion)
                .append(" & ");*/
        sb.append(NUMBER_SIGN).append(TRANSACTION_TYPE).append(NUMBER_SIGN).append(" = ").append(transactionType).append(" & ");
        sb.append(NUMBER_SIGN).append(MODEL).append(NUMBER_SIGN).append(" = ").append(model).append(" & ");
        sb.append(NUMBER_SIGN).append(MODEL_VERSION).append(NUMBER_SIGN).append(" = ").append(modelVersion).append(" & ");
        sb.append(NUMBER_SIGN).append(TRANSACTION_MODE).append(NUMBER_SIGN).append(" = ").append(transactionMode).append(" & ");
        sb.append(NUMBER_SIGN).append(CHANNEL).append(NUMBER_SIGN).append(" = ").append(channel).append(" & ");
        sb.append(NUMBER_SIGN).append(EXECUTION_ENVIRONMENT).append(NUMBER_SIGN).append(" = ").append(executionEnvironment);

        return sb.toString();
    }

}
