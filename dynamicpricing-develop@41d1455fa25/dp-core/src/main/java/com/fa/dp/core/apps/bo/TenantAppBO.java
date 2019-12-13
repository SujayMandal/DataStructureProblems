package com.fa.dp.core.apps.bo;

import java.util.List;

import com.fa.dp.core.apps.domain.AdGroupAppMapping;
import com.fa.dp.core.apps.domain.TenantApp;
import com.fa.dp.core.apps.domain.TenantAppParam;
import com.fa.dp.core.exception.SystemException;

public interface TenantAppBO {

	/**
	 * Returns list of apps associated with ad-group
	 * 
	 * @return
	 * @throws SystemException
	 */
	public List<TenantApp> getTenantAppsByADGroupsSortedByPriorty(List<String> adGroups) throws SystemException;

	/**
	 * Returns all the application parameters
	 * 
	 * @return
	 */
	List<TenantAppParam> getAllAppParams();

	/**
	 * Returns all client application
	 * 
	 * @return
	 * @throws SystemException
	 */
	List<TenantApp> getAllTenantApps() throws SystemException;

	/**
	 * Returns all AD group and ra client app mappings.
	 * 
	 * @return
	 * @throws SystemException
	 */
	public List<AdGroupAppMapping> getAllAdGroupAppMapping() throws SystemException;
	
	
	/**
	 * @param list
	 * @return
	 * @throws SystemException
	 */
	public void updateAppParams(List<TenantAppParam> list, String updatedBy) throws SystemException;

	/**
	 * @param raTenantId
	 * @return
	 */
	public List<TenantAppParam> getTenantAppParams(String code, String type)throws SystemException;
	
	/**
	 * @param code
	 * @return
	 */
	public TenantApp getTenantApp(String code);
	

}
