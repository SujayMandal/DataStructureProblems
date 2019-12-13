/**
 * 
 */
package com.fa.dp.core.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fa.dp.core.adgroup.info.ADGroupInfo;
import com.fa.dp.core.apps.info.TenantAppInfo;
import com.fa.dp.core.model.info.ModelDetailInfo;

/**
 *
 *
 */
public interface CacheManager {

	/**
	 * 
	 * Returns list of unique AD Group names defined in the system
	 * 
	 * @return
	 */
	public Set<String> getAllADGroups();

	/**
	 * Returns AD Group details for the given AD Group name
	 * 
	 * @param adGroup
	 * @return
	 */
	public ADGroupInfo getADGroupDetails(String adGroup);

	/**
	 * Returns model details for the given model name
	 * 
	 * @param modelName
	 * @return
	 */
	public ModelDetailInfo getModelDetailVersions(String modelName);

	/**
	 * Returns ad group type - ad groups mapping
	 * 
	 * @return
	 */
	public Map<String, List<String>> getADGroupTypeMappings();

	/**
	 * Returns tenant auth code for communicating with RA for given tenant code
	 * 
	 * @param tenantCode
	 * @return
	 */
	public String getTenantAuthCode(String tenantCode);

	/**
	 * 
	 * Returns app param value for given app param key
	 * 
	 * @param appParamKey
	 * @return
	 */
	public Object getAppParamValue(String appParamKey);

	/**
	 * Returns ad group associated with the given application
	 * 
	 * @return
	 */
	public String getAdGroupByApp(String appName);

	/**
	 * Returns tenant app detail for given url
	 * 
	 * @param url
	 * @return
	 */
	public TenantAppInfo getAppDetailsForUrl(String url);
	
	/**
	 * 
	 */
	public void reBuildAppParams();

}