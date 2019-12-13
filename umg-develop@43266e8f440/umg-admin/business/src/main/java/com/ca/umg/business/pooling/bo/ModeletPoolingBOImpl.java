package com.ca.umg.business.pooling.bo;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.entity.ModeletRestartInfo;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.Channel;
import com.ca.pool.ModeletPoolingResponse;
import com.ca.pool.ModeletPoolingStatus;
import com.ca.pool.ModeletStatus;
import com.ca.pool.PoolObjectsLoader;
import com.ca.pool.TransactionMode;
import com.ca.pool.TransactionType;
import com.ca.pool.model.DefaultPool;
import com.ca.pool.model.Pool;
import com.ca.pool.model.PoolAllocationInfo;
import com.ca.pool.model.PoolCriteriaDetails;
import com.ca.pool.util.PoolCriteriaUtil;
import com.ca.systemmodelet.SystemModeletConfig;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.integration.runtime.RuntimeIntegrationClient;
import com.ca.umg.business.modelexecenvs.ModelExecEnvironmentProvider;
import com.ca.umg.business.pooling.dao.ModeletRestartDAO;
import com.ca.umg.business.pooling.dao.PoolCriteriaDefMappingEntityDAO;
import com.ca.umg.business.pooling.dao.PoolEntityDAO;
import com.ca.umg.business.pooling.dao.PoolUsageOrderMappingEntityDAO;
import com.ca.umg.business.pooling.entity.ModeletRestartConfig;
import com.ca.umg.business.pooling.entity.PoolCriteriaDefMappingEntity;
import com.ca.umg.business.pooling.entity.PoolEntity;
import com.ca.umg.business.pooling.entity.PoolUsageOrderMappingEntity;
import com.ca.umg.business.pooling.info.ModeletRestartDetails;
import com.ca.umg.business.pooling.model.CompletePoolDetails;
import com.ca.umg.business.pooling.model.CompletePoolDetailsComparator;
import com.ca.umg.business.pooling.model.ModeletPoolingDetails;
import com.ca.umg.business.tenant.bo.TenantBO;
import com.ca.umg.business.tenant.delegate.AuthTokenDelegate;
import com.ca.umg.business.tenant.entity.Tenant;
import com.ca.umg.business.tenant.entity.TenantConfig;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.data.VersionDataContainer;
import com.hazelcast.core.IMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.ca.framework.core.constants.PoolConstants.ALL_MODELET_MAP;
import static com.ca.framework.core.constants.PoolConstants.CHANNEL;
import static com.ca.framework.core.constants.PoolConstants.EXECUTION_ENVIRONMENT;
import static com.ca.framework.core.constants.PoolConstants.EXECUTION_LANGUAGE;
import static com.ca.framework.core.constants.PoolConstants.MODEL;
import static com.ca.framework.core.constants.PoolConstants.MODEL_VERSION;
import static com.ca.framework.core.constants.PoolConstants.NUMBER_SIGN;
import static com.ca.framework.core.constants.PoolConstants.RA_SYSTEM_MODELETS;
import static com.ca.framework.core.constants.PoolConstants.TENANT;
import static com.ca.framework.core.constants.PoolConstants.TRANSACTION_MODE;
import static com.ca.framework.core.constants.PoolConstants.TRANSACTION_TYPE;
import static com.ca.framework.core.constants.SystemConstants.SYSTEM_KEY_TENANT_URL;
import static com.ca.framework.core.constants.SystemConstants.SYSTEM_KEY_TYPE_TENANT;
import static com.ca.framework.core.exception.BusinessException.newBusinessException;
import static com.ca.framework.core.exception.SystemException.newSystemException;
import static com.ca.pool.util.PoolCriteriaUtil.getModeleCapacity;

@SuppressWarnings("PMD")
@Named
public class ModeletPoolingBOImpl extends AbstractDelegate implements ModeletPoolingBO {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModeletPoolingBOImpl.class);

	@Autowired
	private PoolObjectsLoader poolObjectsLoader;

	@Autowired
	private PoolEntityDAO poolEntityDAO;

	@Autowired
	private PoolUsageOrderMappingEntityDAO poolUsageOrderDAO;

	@Autowired
	private PoolCriteriaDefMappingEntityDAO poolCriteriaDefDAO;

	@Inject
	private VersionDataContainer versionDataContainer;

	@Inject
	private ModelExecEnvironmentProvider modelExecEnvironmentProvider;

	@Inject
	private RuntimeIntegrationClient runtimeIntegrationClient;

	@Inject
	private TenantBO tenantBO;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	@Inject
	private AuthTokenDelegate authTokendelegate;

	@Inject
	private CacheRegistry cacheRegistry;

	@Inject
	private ModeletRestartDAO modeletRestartDAO;

	@Inject
	private SystemModeletConfig systemModeletConfig;

	@Override
	public List<CompletePoolDetails> getAllPoolDetails() throws SystemException {
		final List<Pool> poolList = poolObjectsLoader.getPoolList();
		final List<String> tenantSpecificModel = new ArrayList<>();
		tenantSpecificModel.add(PoolCriteriaUtil.getModelNameWithVersion(PoolConstants.ANY, PoolConstants.ANY));
		if(poolList != null && !poolList.isEmpty()) {
			final List<CompletePoolDetails> allCompletePoolDetails = new ArrayList<>();
			CompletePoolDetails systemPoolInfo = new CompletePoolDetails();
			CompletePoolDetails poolDetails = null;
			for (final Pool pool : poolList) {
				final String poolName = pool.getPoolName();
				poolDetails = new CompletePoolDetails();
				poolDetails.setPoolCriteria(poolObjectsLoader.getPoolCriteria(poolName));
				poolDetails.setModeletClientInfoList(poolObjectsLoader.getModeletClientInfo(poolName));
				pool.setModeletCount(poolDetails.getModeletClientInfoList().size());
				poolDetails.setPool(pool);
				if(!isSystemTempPool(poolName)) {
					poolDetails.setPoolUsageOrderMapping(poolObjectsLoader.getPoolUsageOrderList(poolName));
					poolDetails.setPoolCriteriaDetails(poolObjectsLoader.getPoolCriteriaDetails(poolName));
					poolDetails.setTenantSpecificModel(tenantSpecificModel);
					allCompletePoolDetails.add(poolDetails);
					Collections.sort(allCompletePoolDetails, new CompletePoolDetailsComparator());
				} else {
					systemPoolInfo.setPool(pool);
					systemPoolInfo.setPoolCriteria(poolObjectsLoader.getPoolCriteria(poolName));
					systemPoolInfo.setModeletClientInfoList(poolObjectsLoader.getModeletClientInfo(poolName));

				}

			}
			allCompletePoolDetails.add(systemPoolInfo);
			return allCompletePoolDetails;
		}
		return null;
	}

	@Override
	public List<ModeletClientInfo> fetchAllModeletClients() throws SystemException {
		return poolObjectsLoader.getAllModeletClientInfo();
	}

	@Override
	public void createPool(final CompletePoolDetails poolDetails) throws SystemException, BusinessException {
		String poolId = null;
		CompletePoolDetails defaultPoolDetails = null;
		boolean updatePoolGoingOn = false;

		try {
			if(!isModeletPoolingInProgress()) {
				updatePoolGoingOn = true;
				setModeletPoolingInProgress();
				parseEnvironment(poolDetails);
				parseModel(poolDetails);
				final List<CompletePoolDetails> poolDetailList = getAllPoolDetails();
				setDefaultPrority(poolDetails, poolDetailList);
				setModeletCapacity(poolDetails);
				setNotDefaultPoolFalg(poolDetails);

				// this is not required, will be removed from here
				// updateDefaultPoolCount(poolDetails, poolDetailList);

				final List<String> errorList = ModeletPoolingValidator.validateNewPool(poolDetails, poolDetailList, systemParameterProvider);

				if(!errorList.isEmpty()) {
					BusinessException.newBusinessException(BusinessExceptionCodes.BSE000301, new String[] {"Validation Issue" + errorList});
				}

				// Updating priority of default pool
				defaultPoolDetails = null;
				if(PoolCriteriaUtil.isMatlab(poolDetails.getPool().getExecutionLanguage())) {
					defaultPoolDetails = getMatlabDefaultPool(poolDetailList);
				} else if(PoolCriteriaUtil.isExcel(poolDetails.getPool().getExecutionLanguage())) {
					defaultPoolDetails = getExcelDefaultPool(poolDetailList);
				} else if(PoolCriteriaUtil.isR(poolDetails.getPool().getExecutionLanguage())) {
					defaultPoolDetails = getRDefaultPool(poolDetailList, poolDetails.getPoolCriteriaDetails().getExecutionEnvironment());
				}

				updatePool(defaultPoolDetails);

				final PoolEntity poolEntity = new PoolEntity();

				poolEntity.setPoolName(poolDetails.getPool().getPoolName());
				poolEntity.setPoolDesc(poolDetails.getPool().getPoolDesc());
				poolEntity.setPoolStatus(null);
				poolEntity.setDefaultPool(DefaultPool.NON_DEFAULT.getDefaultPool());
				poolEntity.setExecutionLanguage(poolDetails.getPool().getExecutionLanguage());
				poolEntity.setModeletCount(poolDetails.getPool().getModeletCount());
				poolEntity.setModeletCapacity(getModeleCapacity(poolDetails.getPool().getExecutionLanguage()));
				poolEntity.setPriority(poolDetails.getPool().getPriority());
				poolEntity.setWaitTimeout(poolDetails.getPool().getWaitTimeout());
				poolEntity.setExecutionEnvironment(poolDetails.getPoolCriteriaDetails().getExecutionEnvironment());

				LOGGER.info("Pool Entity is getting created. Pool Entity is : {}", poolEntity.toString());

				RequestContext requestContext = RequestContext.getRequestContext();
				requestContext.setAdminAware(true);

				final PoolEntity savedPoolEntity = poolEntityDAO.saveAndFlush(poolEntity);
				poolId = savedPoolEntity.getId();
				LOGGER.info("Pool Entity is created Successfully. Pool Entity is : {}", savedPoolEntity.toString());

				createPoolCriteriaDefMappingEntity(savedPoolEntity.getId(), poolDetails);
				createDefaultPoolUsageOrder(savedPoolEntity.getId(), poolDetails);

				requestContext.setAdminAware(false);

				// is this required?
				updatePool(
						getDefaultPool(poolDetails.getPool().getExecutionLanguage(), poolDetails.getPoolCriteriaDetails().getExecutionEnvironment(),
								poolDetailList));

              /*  final PoolRequestInfo requestInfo = new PoolRequestInfo();
                requestInfo.setRequest(PoolRequest.CREATE.getRequest());
                requestInfo.setDeletedPoolName(null);
                requestInfo.setMovedClientInfo(null);*/

				// refreshModeletAllocation(requestInfo);
				reloadPoolDetailsIntoCache();
			} else {
				newBusinessException(BusinessExceptionCodes.BSE000302,
						new String[] {"Modelet Pooling updates are in progress,  Please try again after some time."});
			}
		} catch (SystemException | BusinessException e) {
			if(poolId != null) {
				deletePool(poolId);

				if(defaultPoolDetails != null) {
					defaultPoolDetails.getPool().setPriority(defaultPoolDetails.getPool().getPriority() - 1);
					updatePool(defaultPoolDetails);
				}
			}

			newBusinessException(BusinessExceptionCodes.BSE000301, new String[] {e.getLocalizedMessage()});
		} catch (Exception ex) {
			LOGGER.error("Error occured while creation of pool.", ex);
			newBusinessException(BusinessExceptionCodes.BSE000304, new String[] {ex.getMessage()});
		} finally {
			if(updatePoolGoingOn) {
				setModeletPoolingDone();
			}
		}
	}

	private void createPoolCriteriaDefMappingEntity(final String poolId, final CompletePoolDetails poolDetails) {
		final PoolCriteriaDefMappingEntity entity = new PoolCriteriaDefMappingEntity();
		entity.setPoolId(poolId);
		entity.setPoolCriteriaValue(createPoolCriteriaValue(poolDetails));

		LOGGER.info("Pool Criteria Defination Mapping Entity is getting created. Entity is : {}", entity.toString());
		final PoolCriteriaDefMappingEntity savedEntity = poolCriteriaDefDAO.saveAndFlush(entity);
		LOGGER.info("Pool Criteria Defination Mapping Entity is created Successfully. Entity is : {}", savedEntity.toString());
	}

	private void createDefaultPoolUsageOrder(final String poolId, final CompletePoolDetails poolDetails) {
		final PoolUsageOrderMappingEntity entity = new PoolUsageOrderMappingEntity();
		entity.setPoolId(poolId);
		entity.setPoolUsageId(poolId);
		entity.setPoolTryOrder(1);

		LOGGER.info("Pool Usage Order Mapping Entity is getting created. Entity is : {}", entity.toString());
		final PoolUsageOrderMappingEntity savedEntity = poolUsageOrderDAO.saveAndFlush(entity);
		LOGGER.info("Pool Usage Order Mapping Entity is created Successfully. Entity is : {}", savedEntity.toString());
	}

	@Override
	public void updatePool(final List<CompletePoolDetails> poolDetailList) throws SystemException, BusinessException {
		boolean updatePoolGoingOn = false;

		try {
			if(!isModeletPoolingInProgress()) {
				updatePoolGoingOn = true;
				setModeletPoolingInProgress();
				parseEnvironmentAndModel(poolDetailList);
				final List<String> errorList = ModeletPoolingValidator
						.validateUpdatePools(poolDetailList, systemParameterProvider, this, poolObjectsLoader, cacheRegistry);
				if(CollectionUtils.isNotEmpty(errorList)) {
					setModeletPoolingDone();
					newBusinessException(BusinessExceptionCodes.BSE000305, new String[] {"Validation Issue" + errorList});
				}

				List<CompletePoolDetails> updatedPoolList = ModeletPoolingValidator.findupdatedPools(poolDetailList, cacheRegistry);

				for (final CompletePoolDetails updatedPool : updatedPoolList) {
					if(isSystemTempPool(updatedPool.getPool().getPoolName())) {
						continue;
					}
					updatePool(updatedPool);
				}

               /* final PoolRequestInfo requestInfo = new PoolRequestInfo();
                requestInfo.setRequest(PoolRequest.UPDATE.getRequest());
                requestInfo.setDeletedPoolName(null);
                requestInfo.setMovedClientInfo(null);*/

				// refreshModeletAllocation(requestInfo);
				poolObjectsLoader.loadPoolObjects();
				LOGGER.info("Allocate Modelets Process started, this is initiated from Modelet Poooling UI");
				allocateModelets(convertToList(updatedPoolList, PoolAllocationInfo.class));
				LOGGER.info("Allocate Modelets Process is done");
			} else {
				newBusinessException(BusinessExceptionCodes.BSE000302,
						new String[] {"Modelet Pooling updates are in progress,  Please try again after some time."});
			}
		} catch (SystemException | BusinessException e) {
			LOGGER.error("Error occured while updating pool", e);
			newBusinessException(BusinessExceptionCodes.BSE000302, new String[] {e.getLocalizedMessage()});
		} finally {
			if(updatePoolGoingOn) {
				setModeletPoolingDone();
			}
		}
	}

	private void allocateModelets(List<PoolAllocationInfo> poolAllocationInfo) throws BusinessException, SystemException {
		RequestContext requestContext = RequestContext.getRequestContext();
		requestContext.setAdminAware(true);
		final String authKey = authTokendelegate.getActiveAuthCode(getTenant(RequestContext.getRequestContext().getTenantCode()).getId());

		final String tenantBaseUrl = getSystemKeyValue(SYSTEM_KEY_TENANT_URL, SYSTEM_KEY_TYPE_TENANT);
		final String refreshUrl = "/modeletPooling/allocateModelets";
		requestContext.setAdminAware(false);
		runtimeIntegrationClient.allocateModelets(tenantBaseUrl, refreshUrl, authKey, poolAllocationInfo);
	}

	private void updatePool(final CompletePoolDetails poolDetails) throws SystemException, BusinessException {
		parseEnvironment(poolDetails);
		parseModel(poolDetails);
		RequestContext requestContext = RequestContext.getRequestContext();
		requestContext.setAdminAware(true);

		if(isSystemTempPool(poolDetails.getPool().getPoolName())) {
			return;
		}

		final PoolEntity existingPoolEntity = poolEntityDAO.findByPoolName(poolDetails.getPool().getPoolName());
		// existingPoolEntity.setPoolDesc(poolDetails.getPool().getPoolDesc());
		existingPoolEntity.setExecutionLanguage(poolDetails.getPool().getExecutionLanguage());
		Integer inactiveModelete = 0;
		if(!(poolDetails.getPool().getInactiveModeletCount() == null)) {
			if(poolDetails.getPool().getModeletAdded() > poolDetails.getPool().getModeletRemoved()) {
				inactiveModelete = poolDetails.getPool().getModeletCount() - poolDetails.getModeletClientInfoList().size();
				if(inactiveModelete < 0) {
					inactiveModelete = 0;
				}
			} else if(poolDetails.getPool().getModeletAdded() < poolDetails.getPool().getModeletRemoved()) {
				inactiveModelete = poolDetails.getPool().getInactiveModeletCount() - poolDetails.getPool().getModeletRemoved();
				if(inactiveModelete < 0) {
					inactiveModelete = 0;
				}
			} else if(poolDetails.getPool().getModeletAdded() == poolDetails.getPool().getModeletRemoved()) {
				inactiveModelete = poolDetails.getPool().getInactiveModeletCount();
			}
		}
		existingPoolEntity.setModeletCount(poolDetails.getModeletClientInfoList().size() + inactiveModelete);

		// existingPoolEntity.setModeletCapacity(getModeleCapacity(poolDetails.getPool().getEnvironment()));
		existingPoolEntity.setPriority(poolDetails.getPool().getPriority());
		existingPoolEntity.setWaitTimeout(poolDetails.getPool().getWaitTimeout());

		LOGGER.info("Pool Entity is getting updated. Pool Entity is : {}", existingPoolEntity.toString());
		final PoolEntity updatedPoolEntity = poolEntityDAO.saveAndFlush(existingPoolEntity);
		LOGGER.info("Pool Entity is updated Successfully. Pool Entity is : {}", updatedPoolEntity.toString());

		updateSystemModelets(poolDetails);

		updatePoolCriteriaDefMappingEntity(updatedPoolEntity.getId(), poolDetails);
		requestContext.setAdminAware(false);
	}

	private void updateSystemModelets(CompletePoolDetails poolDetails) {
		final IMap<String, ModeletClientInfo> systemModeletsMap = cacheRegistry.getMap(RA_SYSTEM_MODELETS);
		List<ModeletClientInfo> modeletClientInfos = poolDetails.getModeletClientInfoList();
		for (ModeletClientInfo modeletClientInfo : modeletClientInfos) {
			systemModeletConfig.updateModeletConfig(modeletClientInfo, poolDetails.getPool().getPoolName());
			modeletClientInfo.setPoolName(poolDetails.getPool().getPoolName());
			systemModeletsMap.put(modeletClientInfo.getHostKey(), modeletClientInfo);
		}

	}

	private void updatePoolForDelete(final CompletePoolDetails poolDetails) throws SystemException, BusinessException {
		parseEnvironment(poolDetails);
		parseModel(poolDetails);
		RequestContext requestContext = RequestContext.getRequestContext();
		requestContext.setAdminAware(true);

		if(isSystemTempPool(poolDetails.getPool().getPoolName())) {
			return;
		}

		final PoolEntity existingPoolEntity = poolEntityDAO.findByPoolName(poolDetails.getPool().getPoolName());
		// existingPoolEntity.setPoolDesc(poolDetails.getPool().getPoolDesc());
		existingPoolEntity.setExecutionLanguage(poolDetails.getPool().getExecutionLanguage());
		// existingPoolEntity.setModeletCapacity(getModeleCapacity(poolDetails.getPool().getEnvironment()));
		existingPoolEntity.setPriority(poolDetails.getPool().getPriority());
		LOGGER.info("Pool Entity is getting updated. Pool Entity is : {}", existingPoolEntity.toString());
		final PoolEntity updatedPoolEntity = poolEntityDAO.saveAndFlush(existingPoolEntity);
		LOGGER.info("Pool Entity is updated Successfully. Pool Entity is : {}", updatedPoolEntity.toString());

		updatePoolCriteriaDefMappingEntity(updatedPoolEntity.getId(), poolDetails);
		requestContext.setAdminAware(false);
	}

	private void updatePoolCriteriaDefMappingEntity(final String poolId, final CompletePoolDetails poolDetails) {
		final PoolCriteriaDefMappingEntity existingEntity = poolCriteriaDefDAO.findByPoolId(poolId);
		existingEntity.setPoolId(poolId);
		existingEntity.setPoolCriteriaValue(createPoolCriteriaValue(poolDetails));

		LOGGER.info("Pool Criteria Defination Mapping Entity is getting updated. Entity is : {}", existingEntity.toString());
		final PoolCriteriaDefMappingEntity updatedEntity = poolCriteriaDefDAO.saveAndFlush(existingEntity);
		LOGGER.info("Pool Criteria Defination Mapping Entity is updated Successfully. Entity is : {}", updatedEntity.toString());
	}

	@Override
	public void deletePool(final String poolId) throws SystemException, BusinessException {
		boolean updatePoolGoingOn = false;
		try {
			if(!isModeletPoolingInProgress()) {
				updatePoolGoingOn = true;
				RequestContext requestContext = RequestContext.getRequestContext();
				requestContext.setAdminAware(true);
				final PoolEntity existingPoolEntity = poolEntityDAO.findOne(poolId);
				final List<String> errorList = ModeletPoolingValidator
						.validateDeleteUpdatePools(existingPoolEntity, systemParameterProvider, this, poolObjectsLoader, cacheRegistry);
				if(!errorList.isEmpty()) {
					newBusinessException(BusinessExceptionCodes.BSE000305, new String[] {"Validation Issue" + errorList});
				}

				deletePoolCriteriaDefMappingEntity(existingPoolEntity.getId());
				deletePoolUsageOrder(existingPoolEntity.getId());

				final List<CompletePoolDetails> poolDetailList = getAllPoolDetails();
				java.util.Collections.sort(poolDetailList, new CompletePoolDetailsComparator());
				final CompletePoolDetails defaultPool = getDefaultPool(existingPoolEntity.getExecutionLanguage(),
						existingPoolEntity.getExecutionEnvironment(), poolDetailList);

				// This is not rqreuied
				// defaultPool.getPool().setModeletCount(defaultPool.getPool().getModeletCount().intValue() +
				// existingPoolEntity.getModeletCount().intValue());

				LOGGER.info("Pool Entity is getting deleted. Pool Entity is : {}", existingPoolEntity.toString());
				poolEntityDAO.delete(existingPoolEntity);

				// Setting priority for all existing pools which are hightest of this deleted pool
				/*
				 * if (PoolCriteriaUtil.isMatlab(existingPoolEntity.getExecutionLanguage())) { for (CompletePoolDetails cDetails :
				 * poolDetailList) { if (PoolCriteriaUtil.isMatlab(cDetails.getPool().getExecutionLanguage()) &&
				 * !(cDetails.getPool().getPoolName().equals(PoolConstants.DEFAULT_POOL))) { if (existingPoolEntity.getPriority()
				 * < cDetails.getPool().getPriority()) { cDetails.getPool().setPriority(cDetails.getPool().getPriority() - 1);
				 * updatePoolForDelete(cDetails); } } } } else if
				 * (PoolCriteriaUtil.isExcel(existingPoolEntity.getExecutionLanguage())) { for (CompletePoolDetails cDetails :
				 * poolDetailList) { if (PoolCriteriaUtil.isExcel(cDetails.getPool().getExecutionLanguage()) &&
				 * !(cDetails.getPool().getPoolName().equals(PoolConstants.DEFAULT_POOL))) { if (existingPoolEntity.getPriority()
				 * < cDetails.getPool().getPriority()) { cDetails.getPool().setPriority(cDetails.getPool().getPriority() - 1);
				 * updatePoolForDelete(cDetails); } } } } else { for (CompletePoolDetails cDetails : poolDetailList) { if
				 * (PoolCriteriaUtil.isR(cDetails.getPool().getExecutionLanguage()) &&
				 * cDetails.getPoolCriteriaDetails().getExecutionEnvironment()
				 * .equalsIgnoreCase(existingPoolEntity.getExecutionEnvironment()) &&
				 * !(cDetails.getPool().getPoolName().equals(PoolConstants.DEFAULT_POOL))) { if (existingPoolEntity.getPriority()
				 * < cDetails.getPool().getPriority()) { cDetails.getPool().setPriority(cDetails.getPool().getPriority() - 1);
				 * updatePoolForDelete(cDetails); } } } }
				 */

				LOGGER.info("Pool Entity is deleted Successfully");

				requestContext.setAdminAware(false);

				updatePool(defaultPool);

				reloadPoolDetailsIntoCache();

			} else {
				newBusinessException(BusinessExceptionCodes.BSE000302,
						new String[] {"Modelet Pooling updates are in progress,  Please try again after some time."});
			}
		} catch (SystemException | BusinessException e) {
			LOGGER.error("Exception occured while deleting pool.", e);
			newBusinessException(BusinessExceptionCodes.BSE000305, new String[] {e.getLocalizedMessage()});
		} finally {
			if(updatePoolGoingOn) {
				setModeletPoolingDone();
			}
		}
	}

	private void deletePoolCriteriaDefMappingEntity(final String poolId) {
		final PoolCriteriaDefMappingEntity existingEntity = poolCriteriaDefDAO.findByPoolId(poolId);
		LOGGER.info("Pool Criteria Defination Mapping Entity is getting deleted. Entity is : {}", existingEntity.toString());
		poolCriteriaDefDAO.delete(existingEntity);
		LOGGER.info("Pool Criteria Defination Mapping Entity is deleted Successfully.");
	}

	private void deletePoolUsageOrder(final String poolId) {
		final List<PoolUsageOrderMappingEntity> existingEntities = poolUsageOrderDAO.findByPoolId(poolId);
		LOGGER.info("Pool Usage Order Mapping Entity is getting created. Entities are : {}", existingEntities.toString());
		poolUsageOrderDAO.delete(existingEntities);
	}

	public static String createPoolCriteriaValue(final CompletePoolDetails poolDetails) {
		final String tenant = poolDetails.getPoolCriteriaDetails().getTenant();
		final String environment = poolDetails.getPoolCriteriaDetails().getExecutionLanguage();
		final String transactionType = poolDetails.getPoolCriteriaDetails().getTransactionType();
		final String model = poolDetails.getPoolCriteriaDetails().getModelName();
		final String modelVersion = poolDetails.getPoolCriteriaDetails().getModelVersion();
		final String transactionMode = poolDetails.getPoolCriteriaDetails().getTransactionMode();
		final String channel = poolDetails.getPoolCriteriaDetails().getChannel();
		final String executionEnvironment = poolDetails.getPoolCriteriaDetails().getExecutionEnvironment();

		final StringBuilder sb = new StringBuilder();
		sb.append(NUMBER_SIGN).append(TENANT).append(NUMBER_SIGN).append(" = ").append(tenant).append(" & ");
		sb.append(NUMBER_SIGN).append(EXECUTION_LANGUAGE).append(NUMBER_SIGN).append(" = ").append(environment).append(" & ");
		sb.append(NUMBER_SIGN).append(TRANSACTION_TYPE).append(NUMBER_SIGN).append(" = ").append(transactionType).append(" & ");
		sb.append(NUMBER_SIGN).append(MODEL).append(NUMBER_SIGN).append(" = ").append(model).append(" & ");
		sb.append(NUMBER_SIGN).append(MODEL_VERSION).append(NUMBER_SIGN).append(" = ").append(modelVersion).append(" & ");
		sb.append(NUMBER_SIGN).append(TRANSACTION_MODE).append(NUMBER_SIGN).append(" = ").append(transactionMode).append(" & ");
		sb.append(NUMBER_SIGN).append(CHANNEL).append(NUMBER_SIGN).append(" = ").append(channel).append(" & ");
		sb.append(NUMBER_SIGN).append(EXECUTION_ENVIRONMENT).append(NUMBER_SIGN).append(" = ").append(executionEnvironment);

		return sb.toString();
	}

	@Override
	public ModeletPoolingDetails getModeletPoolingDetails() throws SystemException {
		final ModeletPoolingDetails modeletPoolingDetails = new ModeletPoolingDetails();

		final List<String> transactionTypes = new ArrayList<>();
		transactionTypes.add(TransactionType.ANY.getType());

		transactionTypes.add(TransactionType.PROD.getType());
		transactionTypes.add(TransactionType.TEST.getType());

		final List<String> transactionModes = new ArrayList<>();
		transactionModes.add(TransactionMode.ANY.getMode());
		transactionModes.add(TransactionMode.ONLINE.getMode());
		transactionModes.add(TransactionMode.BATCH.getMode());
		transactionModes.add(TransactionMode.BULK.getMode());

		final List<String> channels = new ArrayList<>();
		channels.add(Channel.ANY.getChannel());
		channels.add(Channel.HTTP.getChannel());
		channels.add(Channel.FILE.getChannel());

		modeletPoolingDetails.setTransactionTypes(transactionTypes);
		modeletPoolingDetails.setTransactionModes(transactionModes);
		modeletPoolingDetails.setChannels(channels);

		Set<String> executionEnvironments = new TreeSet<>();
		executionEnvironments.add(SystemConstants.LINUX_OS);
		executionEnvironments.add(SystemConstants.WINDOWS_OS);

		Set<String> environments = new TreeSet<>();
		List<String> execEnvironments = modelExecEnvironmentProvider.getAllExecutionEnvironmentNames();
		for (String execEnvironment : execEnvironments) {
			environments.add(StringUtils.substringBefore(execEnvironment, "-"));
		}

		modeletPoolingDetails.setEnvironments(environments);
		modeletPoolingDetails.setExecutionEnvironments(executionEnvironments);
		modeletPoolingDetails.setModelNamesByTenant(versionDataContainer.getModelsWithVersions());
		modeletPoolingDetails.setModelNamesByTenantAndEnv(versionDataContainer.getModelNamesByTenantAndEnv());
		// final Set<String> allTenants = new TreeSet<String>();
		final Map<String, String> allTenantNameAndCode = new TreeMap<String, String>();
		allTenantNameAndCode.putAll(versionDataContainer.getTenantNameAndCode());
		allTenantNameAndCode.put(PoolConstants.ANY, PoolConstants.ANY);
		// allTenants.addAll(versionDataContainer.getTenantNames());
		// allTenants.add(PoolConstants.ANY);
		modeletPoolingDetails.setTenants(allTenantNameAndCode);
		// modeletPoolingDetails.setTenants(allTenants);
		setModeletServerDetails(modeletPoolingDetails);
		return modeletPoolingDetails;
	}

	@Override
	public boolean isModeletPoolingInProgress() {
		final IMap<String, String> map = poolObjectsLoader.getCacheRegistry().getMap(PoolConstants.MODELET_ALLOCATE_STATUS_MAP);
		return ModeletPoolingStatus.isInprogress(map.get(PoolConstants.MODELET_ALLOCATE_STATUS_KEY));
	}

	@Override
	public void setModeletPoolingInProgress() {
		final IMap<String, String> map = poolObjectsLoader.getCacheRegistry().getMap(PoolConstants.MODELET_ALLOCATE_STATUS_MAP);
		map.put(PoolConstants.MODELET_ALLOCATE_STATUS_KEY, ModeletPoolingStatus.IN_PROGRESS.getStatus());
	}

	@Override
	public void setModeletPoolingDone() {
		final IMap<String, String> map = poolObjectsLoader.getCacheRegistry().getMap(PoolConstants.MODELET_ALLOCATE_STATUS_MAP);
		map.put(PoolConstants.MODELET_ALLOCATE_STATUS_KEY, ModeletPoolingStatus.DONE.getStatus());
	}

	public static boolean isSystemTempPool(final String poolName) {
		return poolName.equalsIgnoreCase(PoolConstants.DEFAULT_POOL);
	}

	private TenantInfo getTenant(final String code) throws BusinessException, SystemException {
		Tenant tenant = tenantBO.getTenant(code);
		return convert(tenant, TenantInfo.class);
	}

	private String getSystemKeyValue(final String key, final String type) throws BusinessException, SystemException {
		String sysKeyValue = null;
		String tenantCode = RequestContext.getRequestContext().getTenantCode();
		LOGGER.info("Fetching value for system key {} and tenant {}.", key, tenantCode);
		TenantConfig tenantConfig = tenantBO.getTenantConfigDetails(tenantCode, key, type);
		if(tenantConfig != null) {
			sysKeyValue = tenantConfig.getValue();
		} else {
			newSystemException(BusinessExceptionCodes.BSE000109, new Object[] {key, tenantCode});
		}
		return sysKeyValue;
	}

	private void parseEnvironmentAndModel(final List<CompletePoolDetails> poolDetails) {
		for (final CompletePoolDetails poolDetail : poolDetails) {
			if(isSystemTempPool(poolDetail.getPool().getPoolName())) {
				continue;
			}
			parseEnvironment(poolDetail);
			parseModel(poolDetail);
		}
	}

	private void parseEnvironment(final CompletePoolDetails poolDetails) {
		final String environmentName = poolDetails.getPoolCriteriaDetails().getExecutionLanguage();
		poolDetails.getPool().setExecutionLanguage(environmentName);
		poolDetails.getPoolCriteriaDetails().setExecutionLanguage(environmentName);
	}

	private void parseModel(final CompletePoolDetails poolDetails) {
		final String model = poolDetails.getPoolCriteriaDetails().getModel();
		final String modelName = PoolCriteriaUtil.getModelName(model);
		final String modelVersion = PoolCriteriaUtil.getModelVersion(model);
		poolDetails.getPoolCriteriaDetails().setModelName(modelName);
		poolDetails.getPoolCriteriaDetails().setModelVersion(modelVersion);
	}

	private CompletePoolDetails getDefaultPool(final String environment, String executionEnvironment,
			final List<CompletePoolDetails> poolDetailList) {
		if(PoolCriteriaUtil.isMatlab(environment)) {
			return getMatlabDefaultPool(poolDetailList);
		} else if(PoolCriteriaUtil.isExcel(environment)) {
			return getExcelDefaultPool(poolDetailList);
		} else {
			return getRDefaultPool(poolDetailList, executionEnvironment);
		}
	}

	private CompletePoolDetails getMatlabDefaultPool(final List<CompletePoolDetails> poolDetailList) {
		CompletePoolDetails matlabDefaultpoolDetails = null;
		for (final CompletePoolDetails poolDetails : poolDetailList) {
			if(PoolCriteriaUtil.isMatlab(poolDetails.getPool().getExecutionLanguage())) {
				if(DefaultPool.isDefaultPool(poolDetails.getPool())) {
					matlabDefaultpoolDetails = poolDetails;
					break;
				}
			}
		}

		return matlabDefaultpoolDetails;
	}

	private CompletePoolDetails getExcelDefaultPool(final List<CompletePoolDetails> poolDetailList) {
		CompletePoolDetails excelDefaultpoolDetails = null;
		for (final CompletePoolDetails poolDetails : poolDetailList) {
			if(PoolCriteriaUtil.isExcel(poolDetails.getPool().getExecutionLanguage())) {
				if(DefaultPool.isDefaultPool(poolDetails.getPool())) {
					excelDefaultpoolDetails = poolDetails;
					break;
				}
			}
		}

		return excelDefaultpoolDetails;
	}

	private CompletePoolDetails getRDefaultPool(final List<CompletePoolDetails> poolDetailList, String executionEnvironment) {
		CompletePoolDetails rDefaultpoolDetails = null;
		for (final CompletePoolDetails poolDetails : poolDetailList) {
			if(PoolCriteriaUtil.isR(poolDetails.getPool().getExecutionLanguage()) && poolDetails.getPoolCriteriaDetails().getExecutionEnvironment()
					.equalsIgnoreCase(executionEnvironment)) {
				if(DefaultPool.isDefaultPool(poolDetails.getPool())) {
					rDefaultpoolDetails = poolDetails;
					break;
				}
			}
		}

		return rDefaultpoolDetails;
	}

	@Override
	public List<CompletePoolDetails> searchPool(final String searchString) throws SystemException {
		final List<CompletePoolDetails> matchedPoolDetails = new ArrayList<>();

		if(searchString != null && !searchString.trim().isEmpty()) {
			final List<CompletePoolDetails> allPoolDetails = getAllPoolDetails();
			for (final CompletePoolDetails poolDetails : allPoolDetails) {
				if(isPoolMatched(searchString, poolDetails) || isPoolCriteriaMatched(searchString, poolDetails) || isModeletMatched(searchString,
						poolDetails) || (poolDetails.getPoolCriteria() != null && poolDetails.getPoolCriteria().equals(searchString))) {
					matchedPoolDetails.add(poolDetails);
				}
			}
		}

		return matchedPoolDetails;
	}

	private boolean isPoolMatched(final String searchString, final CompletePoolDetails poolDetails) {
		final Pool pool = poolDetails.getPool();
		if(isStringEquals(pool.getExecutionLanguage(), searchString) || isStringEquals(pool.getId(), searchString) || isStringEquals(
				pool.getModeletCapacity(), searchString) || isStringEquals(pool.getPoolDesc(), searchString) || isStringEquals(pool.getPoolName(),
				searchString) || isStringEquals(pool.getPoolStatus(), searchString)) {
			return true;
		}

		try {
			final int intValue = Integer.parseInt(searchString);
			if(pool.getModeletCount() != null && pool.getModeletCount().intValue() == intValue
					|| pool.getPriority() != null && pool.getPriority().intValue() == intValue || pool.getDefaultPool() == intValue) {
				return true;
			}

		} catch (NumberFormatException nfe) {
			// ignore
			// TODO
		}

		return false;
	}

	private boolean isPoolCriteriaMatched(final String searchString, final CompletePoolDetails poolDetails) {
		final PoolCriteriaDetails poolCriteriaDetails = poolDetails.getPoolCriteriaDetails();

		if(poolCriteriaDetails == null) {
			return false;
		}

		if(isStringEquals(poolCriteriaDetails.getExecLangVersion(), searchString) || isStringEquals(poolCriteriaDetails.getExecutionLanguage(),
				searchString) || isStringEquals(poolCriteriaDetails.getModel(), searchString) || isStringEquals(poolCriteriaDetails.getModelName(),
				searchString) || isStringEquals(poolCriteriaDetails.getModelVersion(), searchString) || isStringEquals(
				poolCriteriaDetails.getTenant(), searchString) || isStringEquals(poolCriteriaDetails.getTransactionType(), searchString)
				|| isStringEquals(poolCriteriaDetails.getTransactionMode(), searchString)) {
			return true;
		}

		return false;
	}

	private boolean isModeletMatched(final String searchString, final CompletePoolDetails poolDetails) {
		final List<ModeletClientInfo> modeletList = poolDetails.getModeletClientInfoList();
		for (final ModeletClientInfo modelet : modeletList) {
			if(isModeletMatched(searchString, modelet)) {
				return true;
			}
		}
		return false;
	}

	private boolean isModeletMatched(final String searchString, final ModeletClientInfo modelet) {
		if(isStringEquals(modelet.getContextPath(), searchString) || isStringEquals(modelet.getExecutionLanguage(), searchString) || isStringEquals(
				modelet.getHost(), searchString) || isStringEquals(modelet.getHostKey(), searchString) || isStringEquals(modelet.getLoadedModel(),
				searchString) || isStringEquals(modelet.getLoadedModelVersion(), searchString) || isStringEquals(modelet.getMemberHost(),
				searchString) || isStringEquals(modelet.getMemberKey(), searchString) || isStringEquals(modelet.getModeletStatus(), searchString)
				|| isStringEquals(modelet.getPoolName(), searchString) || isStringEquals(modelet.getServerType(), searchString)) {
			return true;
		}
		try {
			final int intValue = Integer.parseInt(searchString);
			if(modelet.getMemberPort() == intValue || modelet.getPort() == intValue) {
				return true;
			}

		} catch (NumberFormatException nfe) {
			// ignore
		}

		return false;
	}

	private void setModeletServerDetails(final ModeletPoolingDetails modeletPoolingDetails) {
		final IMap<String, ModeletClientInfo> allModeletMap = poolObjectsLoader.getCacheRegistry().getMap(ALL_MODELET_MAP);
		final Set<String> keySet = allModeletMap.keySet();

		final Set<String> modeletServer = new HashSet<>();
		int matlabModeletCount = 0;
		int rModeletCount = 0;

		for (final String key : keySet) {
			final ModeletClientInfo clientInfo = allModeletMap.get(key);
			modeletServer.add(clientInfo.getHost());
			if(PoolCriteriaUtil.isMatlab(clientInfo.getExecutionLanguage())) {
				matlabModeletCount++;
			} else {
				rModeletCount++;
			}
		}

		modeletPoolingDetails.setModeletServerCount(modeletServer.size() + " Modelet Servers");
		modeletPoolingDetails.setMatlabModeletsCount(matlabModeletCount + " Matlab Modelets");
		modeletPoolingDetails.setrModeletsCount(rModeletCount + " R Modelets");
	}

	private boolean isStringEquals(final String firstString, final String secondString) {
		if(firstString != null && firstString.equalsIgnoreCase(secondString)) {
			return true;
		} else {
			return false;
		}
	}

	private void reloadPoolDetailsIntoCache() throws SystemException {
		poolObjectsLoader.loadPoolObjects();
	}

	private void setDefaultPrority(final CompletePoolDetails poolDetails, final List<CompletePoolDetails> poolDetailsList) {
		Integer defaultPoolPrority = null;

		CompletePoolDetails defaultRoolDetails;
		if(PoolCriteriaUtil.isMatlab(poolDetails.getPool().getExecutionLanguage())) {
			defaultRoolDetails = getMatlabDefaultPoolDetails(poolDetailsList);
		} else if(PoolCriteriaUtil.isExcel(poolDetails.getPool().getExecutionLanguage())) {
			defaultRoolDetails = getExcelDefaultPoolDetails(poolDetailsList);
		} else {
			defaultRoolDetails = getRDefaultPoolDetails(poolDetailsList, poolDetails.getPoolCriteriaDetails().getExecutionEnvironment());
		}

		defaultPoolPrority = defaultRoolDetails.getPool().getPriority();
		defaultRoolDetails.getPool().setPriority(defaultPoolPrority + 1);

		poolDetails.getPool().setPriority(defaultPoolPrority);
	}

	private CompletePoolDetails getRDefaultPoolDetails(final List<CompletePoolDetails> poolDetailsList, String executionEnvironment) {
		CompletePoolDetails poolDetails = null;
		for (CompletePoolDetails completePoolDetails : poolDetailsList) {
			if(PoolCriteriaUtil.isR(completePoolDetails.getPool().getExecutionLanguage()) && completePoolDetails.getPool().getDefaultPool() == 1
					&& completePoolDetails.getPool().getExecutionEnvironment().equalsIgnoreCase(executionEnvironment)) {
				poolDetails = completePoolDetails;
				break;
			}
		}

		return poolDetails;
	}

	// this is not requried, reuse getMatlabDefaultPool
	private CompletePoolDetails getMatlabDefaultPoolDetails(final List<CompletePoolDetails> poolDetailsList) {
		CompletePoolDetails poolDetails = null;
		for (CompletePoolDetails completePoolDetails : poolDetailsList) {
			if(PoolCriteriaUtil.isMatlab(completePoolDetails.getPool().getExecutionLanguage())
					&& completePoolDetails.getPool().getDefaultPool() == 1) {
				poolDetails = completePoolDetails;
				break;
			}
		}

		return poolDetails;
	}

	private CompletePoolDetails getExcelDefaultPoolDetails(final List<CompletePoolDetails> poolDetailsList) {
		CompletePoolDetails poolDetails = null;
		for (CompletePoolDetails completePoolDetails : poolDetailsList) {
			if(PoolCriteriaUtil.isExcel(completePoolDetails.getPool().getExecutionLanguage())
					&& completePoolDetails.getPool().getDefaultPool() == 1) {
				poolDetails = completePoolDetails;
				break;
			}
		}

		return poolDetails;
	}

	// Duplicate method, reuse getModeleCapacity
	private void setModeletCapacity(final CompletePoolDetails poolDetails) {
		if(PoolCriteriaUtil.isMatlab(poolDetails.getPool().getExecutionLanguage())) {
			poolDetails.getPool().setModeletCapacity("1GB - Linux 64 bit");
		} else {
			poolDetails.getPool().setModeletCapacity("4GB - Linux 64 bit");
		}
	}

	private void setNotDefaultPoolFalg(final CompletePoolDetails poolDetails) {
		poolDetails.getPool().setDefaultPool(DefaultPool.NON_DEFAULT.getDefaultPool());
	}

	private void switchModelet(final ModeletClientInfo modeletClientInfo, final String switchModeletUrl) throws BusinessException, SystemException {
		RequestContext requestContext = RequestContext.getRequestContext();
		requestContext.setAdminAware(true);
		final String authKey = authTokendelegate.getActiveAuthCode(getTenant(RequestContext.getRequestContext().getTenantCode()).getId());

		final String tenantBaseUrl = getSystemKeyValue(SYSTEM_KEY_TENANT_URL, SYSTEM_KEY_TYPE_TENANT);
		requestContext.setAdminAware(false);
		ModeletPoolingResponse modeletPoolingResponse = runtimeIntegrationClient
				.switchModelet(tenantBaseUrl, switchModeletUrl, authKey, modeletClientInfo);
		LOGGER.info("switchModelet status is :" + modeletPoolingResponse.getStatus());

	}

	private String fetchModeletResult(final ModeletClientInfo modeletClientInfo, final String switchModeletUrl)
			throws BusinessException, SystemException {
		RequestContext requestContext = RequestContext.getRequestContext();
		requestContext.setAdminAware(true);
		final String authKey = authTokendelegate.getActiveAuthCode(getTenant(RequestContext.getRequestContext().getTenantCode()).getId());

		final String tenantBaseUrl = getSystemKeyValue(SYSTEM_KEY_TENANT_URL, SYSTEM_KEY_TYPE_TENANT);
		requestContext.setAdminAware(false);
		ModeletPoolingResponse modeletPoolingResponse = runtimeIntegrationClient
				.fetchModeletResult(tenantBaseUrl, switchModeletUrl, authKey, modeletClientInfo);
		LOGGER.info("switchModelet status is :" + modeletPoolingResponse.getStatus());

		return modeletPoolingResponse.getStatus();

	}

	@Override
	public List<String> switchModelet(final ModeletClientInfo modeletClientInfo, final Object status) {
		List<String> errorList = new ArrayList<>();

		LOGGER.error("Switch Modelet is called");
		try {
			String switchModeletUrl;
			if(status != null && !StringUtils.equalsIgnoreCase(ModeletStatus.UNREGISTERED.getStatus(), (String) status)) {
				switchModeletUrl = "/modeletPooling/stopModelet";
			} else {
				switchModeletUrl = "/modeletPooling/startModelet";
			}

			switchModelet(modeletClientInfo, switchModeletUrl);

			LOGGER.error("Switch Modelet is completed");
		} catch (SystemException | BusinessException ex) {
			LOGGER.error("An error occurred while starting/stopring modelet {}.", modeletClientInfo, ex);
			errorList.add(ex.getLocalizedMessage());
		}

		return errorList;
	}

	@Override
	public List<String> fetchModeletCommandResult(ModeletClientInfo modeletClientInfo, Object status) {
		List<String> commandResultList = new ArrayList<>();

		LOGGER.error("fetch Modelet command result is called");
		try {
			String modeletFetchUrl = "/modeletPooling/getModeletResponse";

			String data = fetchModeletResult(modeletClientInfo, modeletFetchUrl);
			if(StringUtils.isNotEmpty(data)) {
				commandResultList.addAll(Arrays.asList(StringUtils.split(data, BusinessConstants.CHAR_NEWLINE)));
			}

			LOGGER.error("fetch Modelet command result is completed");
		} catch (SystemException | BusinessException ex) {
			LOGGER.error("An error occurred while fetching Modelet command result {}.", modeletClientInfo, ex);
		}

		return commandResultList;
	}

	@Override
	public ModeletRestartDetails getModeletRestartDetails() throws SystemException {
		LOGGER.info("Getting Modelet Restart Details");
		ModeletRestartDetails modeletRestartDetails = new ModeletRestartDetails();
		try {
			Boolean adminAware = AdminUtil.getActualAdminAware();
			AdminUtil.setAdminAwareTrue();
			List<ModeletRestartConfig> list = modeletRestartDAO.findAllByOrderByTenantIdAscModelNameAndVersionAsc();
			AdminUtil.setActualAdminAware(adminAware);

			List<ModeletRestartInfo> modeletRestartList = new ArrayList<ModeletRestartInfo>();
			if(list != null) {
				for (ModeletRestartConfig modeletRestartConfig : list) {
					ModeletRestartInfo info = convert(modeletRestartConfig, ModeletRestartInfo.class);
					modeletRestartList.add(info);

				}

			}

			modeletRestartDetails.setModeletRestartInfoList(modeletRestartList);
			final Map<String, String> allTenantNameAndCode = new TreeMap<String, String>();
			allTenantNameAndCode.putAll(versionDataContainer.getTenantNameAndCode());
			modeletRestartDetails.setTenants(allTenantNameAndCode);

			Map<String, Set<String>> tenantModelAndVersions = versionDataContainer.getModelsWithVersions();
			if(tenantModelAndVersions != null) {
				Map<String, Set<String>> tenantModelNames = new TreeMap<String, Set<String>>();
				Set<String> modelAndVersionsSet = tenantModelAndVersions.keySet();
				for (String tenant : modelAndVersionsSet) {
					Set<String> modelAndersionSet = tenantModelAndVersions.get(tenant);
					TreeSet<String> modelAndersionWithOutAny = new TreeSet<String>();
					for (String modelAndVersion : modelAndersionSet) {
						StringBuilder modelNamewithAnyVer = new StringBuilder(
								modelAndVersion.substring(0, modelAndVersion.lastIndexOf(BusinessConstants.UNDERSCORE)));
						modelNamewithAnyVer.append(BusinessConstants.UNDERSCORE).append("Any");
						if(!modelAndersionWithOutAny.contains(modelNamewithAnyVer.toString())) {
							modelAndersionWithOutAny.add(modelNamewithAnyVer.toString());
						}
						modelAndersionWithOutAny.add(modelAndVersion);

					}
					tenantModelNames.put(tenant, modelAndersionWithOutAny);
				}
				modeletRestartDetails.setModelNamesByTenant(tenantModelNames);
			}
		} catch (Exception ex) { // NOPMD
			LOGGER.error("exception while getting details ", ex);
			throw new SystemException(BusinessExceptionCodes.BSE000407, new Object[] {"Getting modelet Restart config", ex.getMessage()});
		}

		return modeletRestartDetails;
	}

	@Override
	public void deleteModeletSetting(ModeletRestartInfo modeletRestartInfo) throws SystemException {
		try {
			Boolean adminAware = AdminUtil.getActualAdminAware();
			AdminUtil.setAdminAwareTrue();
			modeletRestartDAO.delete(convert(modeletRestartInfo, ModeletRestartConfig.class));
			AdminUtil.setActualAdminAware(adminAware);
			final IMap<String, List<ModeletRestartInfo>> restartModeletsMap = cacheRegistry.getMap(FrameworkConstant.RESTART_MODELET_COUNT_MAP);
			restartModeletsMap.remove(modeletRestartInfo.getTenantId() + BusinessConstants.UNDERSCORE + modeletRestartInfo.getModelNameAndVersion());
		} catch (Exception ex) {
			LOGGER.error("exception while deleting modeleet restart config details ", ex);
			throw new SystemException(BusinessExceptionCodes.BSE000407, new Object[] {"deleting modelet Restart config", ex.getMessage()});
		}
	}

	@Override
	public void addModeletSetting(List<ModeletRestartInfo> modeletRestartInfoList) throws SystemException {
		try {
			List<ModeletRestartConfig> modeletRetsartList = new ArrayList<ModeletRestartConfig>();
			for (ModeletRestartInfo modeletRestartInfo : modeletRestartInfoList) {
				modeletRetsartList.add(convert(modeletRestartInfo, ModeletRestartConfig.class));
			}

			Boolean adminAware = AdminUtil.getActualAdminAware();
			AdminUtil.setAdminAwareTrue();

			List<ModeletRestartConfig> list = modeletRestartDAO.save(modeletRetsartList);

			AdminUtil.setActualAdminAware(adminAware);
			final IMap<String, List<ModeletRestartInfo>> restartModeletsMap = cacheRegistry.getMap(FrameworkConstant.RESTART_MODELET_COUNT_MAP);
			for (ModeletRestartConfig modeletRestart : list) {
				List<ModeletRestartInfo> existRestartInfos = restartModeletsMap
						.get(modeletRestart.getTenantId() + BusinessConstants.UNDERSCORE + modeletRestart.getModelNameAndVersion());
				ModeletRestartInfo restartInfo = convert(modeletRestart, ModeletRestartInfo.class);
				List<ModeletRestartInfo> newResatrtInfos = new ArrayList<ModeletRestartInfo>();
				if(existRestartInfos != null) {
					for (ModeletRestartInfo info : existRestartInfos) {
						if(info.getId() != null) {
							if(!restartInfo.getModelNameAndVersion().contains("_Any")) {
								restartInfo.setExecCount(info.getExecCount());
							}
							restartInfo.setModeletHostKey(info.getModeletHostKey());
							newResatrtInfos.add(restartInfo);
						} else {
							info.setRestartCount(restartInfo.getRestartCount());
							newResatrtInfos.add(info);
						}
					}
				} else {
					newResatrtInfos.add(restartInfo);
				}

				restartModeletsMap
						.put(modeletRestart.getTenantId() + BusinessConstants.UNDERSCORE + modeletRestart.getModelNameAndVersion(), newResatrtInfos);
			}

		} catch (Exception ex) {
			LOGGER.error("exception while adding modelet restart config details ", ex);
			throw new SystemException(BusinessExceptionCodes.BSE000407, new Object[] {"add/updating modelet Restart config", ex.getMessage()});

		}

	}

}