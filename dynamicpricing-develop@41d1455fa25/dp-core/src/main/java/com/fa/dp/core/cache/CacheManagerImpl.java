/**
 * 
 */
package com.fa.dp.core.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.core.adgroup.delegate.ADGroupDelegate;
import com.fa.dp.core.adgroup.info.ADGroupInfo;
import com.fa.dp.core.apps.delegate.TenantAppDelegate;
import com.fa.dp.core.apps.info.AdGroupAppMappingInfo;
import com.fa.dp.core.apps.info.TenantAppInfo;
import com.fa.dp.core.apps.info.TenantAppParamInfo;
import com.fa.dp.core.encryption.EncryptionUtil;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.model.delegate.ModelDetailDelegate;
import com.fa.dp.core.model.info.ModelDetailInfo;
import com.fa.dp.core.tenant.delegate.TenantDelegate;
import com.fa.dp.core.tenant.info.TenantInfo;
import com.fa.dp.core.util.RAClientConstants;
import com.hazelcast.core.HazelcastInstance;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * 
 * Builds application cache for faster access for information such as tenant
 * information,model information and model association with tenants.
 * 
 *
 *
 */
@Named
public class CacheManagerImpl implements CacheManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheManagerImpl.class);

	@Inject
	private HazelcastInstance hazelcastInstance;

	@Inject
	private TenantDelegate tenantDelegate;

	@Inject
	private ADGroupDelegate adGroupDelegate;

	@Inject
	private ModelDetailDelegate modelDetailDelegate;

	@Inject
	private TenantAppDelegate tenantAppDelegate;

	private Map<String, ADGroupInfo> allADGroups = new HashMap<String, ADGroupInfo>();

	private Map<String, ModelDetailInfo> modelDetailInfo = new HashMap<String, ModelDetailInfo>();

	private Map<String, String> allTenantsMap = new HashMap<String, String>();

	private Map<String, List<String>> adGroupTypes = new HashMap<String, List<String>>();

	private Map<String, Map<String, Object>> appParamsMap = new HashMap<>();

	private Map<String, TenantAppInfo> tenantAppByLaunchUrl = new HashMap<>();

	/**
	 * Holds ad group to client app mapping
	 */
	private Map<String, String> adGroupAppMap = new HashMap<String, String>();

	/**
	 * Holds ad group name - model information
	 */
	private Map<String, List<ModelDetailInfo>> allGroupModels = new HashMap<String, List<ModelDetailInfo>>();

	@PostConstruct
	public void init() {
		try {
			populateAllTenants();
			buildAdGroupTenantAssociation();
			buildModelInformation();
			buildApps();
			buildAppParams();
			buildAppAdGroupMapping();
		} catch (SystemException e) {
			LOGGER.error("An error occurred while building cache, aborting server startup.", e);
			System.exit(1);
		}
	}

	private void buildApps() throws SystemException {
		List<TenantAppInfo> tenantApps = tenantAppDelegate.getAllTenantApps();
		if (CollectionUtils.isNotEmpty(tenantApps)) {
			this.tenantAppByLaunchUrl = tenantApps.stream()
					.collect(Collectors.toMap(TenantAppInfo::getAppLaunchUrl, TenantAppInfo -> TenantAppInfo));
		}
	}

	private void buildAppAdGroupMapping() throws SystemException {
		List<AdGroupAppMappingInfo> adGroupAppMappings = tenantAppDelegate.getAllAdGroupAppMappings();
		if (CollectionUtils.isNotEmpty(adGroupAppMappings)) {
			this.adGroupAppMap = adGroupAppMappings.stream().collect(HashMap<String, String>::new,
					(m, c) -> m.put(c.getRaTntApp().getCode(), c.getRaTntAdGroup().getName()), (m, u) -> {
					});
		}
	}

	/*
	 * Builds tenant cache
	 */
	private void populateAllTenants() throws SystemException {
		List<TenantInfo> tenants = tenantDelegate.getAllTenants();
		if (CollectionUtils.isNotEmpty(tenants)) {
			for (TenantInfo tenant : tenants) {
				allTenantsMap.put(StringUtils.lowerCase(tenant.getCode()),
						EncryptionUtil.decryptToken(tenant.getAuthCode()));
			}
		}
		LOGGER.info("allTenantsMap cache populated : " + allTenantsMap);
	}

	/*
	 * Builds ad group and tenant association cache.
	 */
	private void buildAdGroupTenantAssociation() throws SystemException {
		List<ADGroupInfo> adGroups = adGroupDelegate.getAllADGroups();
		if (CollectionUtils.isNotEmpty(adGroups)) {
			for (ADGroupInfo adGroup : adGroups) {
				allADGroups.put(adGroup.getName(), adGroup);
				if (!adGroupTypes.containsKey(adGroup.getType())) {
					adGroupTypes.put(adGroup.getType(), new ArrayList<String>());
				}
				adGroupTypes.get(adGroup.getType()).add(adGroup.getName());
			}
		}
		LOGGER.info("adGroupTypes cache populated : " + adGroupTypes);
	}

	/**
	 * Build Model information
	 * 
	 * @return
	 * @throws SystemException
	 */
	private void buildModelInformation() throws SystemException {
		List<ModelDetailInfo> modelDetails = modelDetailDelegate.getAllModelDetails();
		if (CollectionUtils.isNotEmpty(modelDetails)) {
			for (ModelDetailInfo modelDetail : modelDetails) {
				String modelKey = new StringBuffer(StringUtils.lowerCase(modelDetail.getName()))
						.append(RAClientConstants.CHAR_HYPHEN).append(modelDetail.getMajorVersion())
						.append(RAClientConstants.CHAR_HYPHEN).append(modelDetail.getMinorVersion()).toString();
				modelDetailInfo.put(modelKey, modelDetail);
			}
		}
		LOGGER.info("modelDetailInfo cache populated : " + modelDetailInfo);
	}


	private void buildAppParams() {
		List<TenantAppParamInfo> appParams = tenantAppDelegate.getAllAppParams();
		if (CollectionUtils.isNotEmpty(appParams)) {
			for (TenantAppParamInfo appParamInfo : appParams) {
				if (!appParamsMap.containsKey(appParamInfo.getTenantApp().getCode())) {
					appParamsMap.put(appParamInfo.getTenantApp().getCode(), new HashMap<>());
				}
				appParamsMap.get(appParamInfo.getTenantApp().getCode()).put(appParamInfo.getAttrKey(),
						appParamInfo.getAttrValue());
			}
		}
		LOGGER.info("appParamsMap cache populated : " + appParamsMap);
	}

	public HazelcastInstance getCacheInstance() {
		return hazelcastInstance;
	}

	@Override
	public ADGroupInfo getADGroupDetails(String adGroup) {
		return allADGroups.get(adGroup);
	}

	@Override
	public ModelDetailInfo getModelDetailVersions(String modelName) {
		return modelDetailInfo.get(modelName);
	}

	@Override
	public Set<String> getAllADGroups() {
		return allADGroups.keySet();
	}

	@Override
	public Map<String, List<String>> getADGroupTypeMappings() {
		return adGroupTypes;
	}

	@Override
	public String getTenantAuthCode(String tenantCode) {
		return allTenantsMap.get(StringUtils.lowerCase(tenantCode));
	}

	@Override
	public Object getAppParamValue(String appParamKey) {
		LOGGER.debug("CacheManagerImpl -> getAppParamValue() ");
		Object value = null;
		Map<String, Object> paramsByApp = appParamsMap.get(MDC.get(RAClientConstants.APP_CODE));
		LOGGER.debug("APP Code : " + MDC.get(RAClientConstants.APP_CODE));
		LOGGER.debug("paramsByApp size : " + (paramsByApp != null ? paramsByApp.size() : 0));
		// if(paramsByApp == null) paramsByApp = appParamsMap.get("ECMA");
		if (MapUtils.isNotEmpty(paramsByApp)) {
			value = paramsByApp.get(appParamKey);
		}
		LOGGER.info("value for key : " + appParamKey + " : " + value);
		return value;
	}

	@Override
	public String getAdGroupByApp(String appName) {
		return adGroupAppMap.get(appName);
	}

	@Override
	public TenantAppInfo getAppDetailsForUrl(String url) {
		return tenantAppByLaunchUrl.get(url);
	}

	@Override
	public void reBuildAppParams() {
         buildAppParams();
	}

}