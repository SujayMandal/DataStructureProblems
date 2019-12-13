package com.fa.dp.core.apps.delegate;

import java.io.IOException;
import java.util.List;

import com.fa.dp.core.apps.info.AdGroupAppMappingInfo;
import com.fa.dp.core.apps.info.RATenantAppParamsRequest;
import com.fa.dp.core.apps.info.TenantAppInfo;
import com.fa.dp.core.apps.info.TenantAppParamInfo;
import com.fa.dp.core.exception.SystemException;

public interface TenantAppDelegate {

	/**
	 * Returns list of apps associated with ad-group
	 * 
	 * @return
	 * @throws SystemException
	 */
	public List<TenantAppInfo> getTenantAppsByADGroupsSortedByPriorty(List<String> adGroups) throws SystemException;

	/**
	 * Returns all tenant application params defined in the system
	 */
	public List<TenantAppParamInfo> getAllAppParams();

	/**
	 * Lists all client application defined in the system
	 * 
	 * @return
	 * @throws SystemException
	 */
	public List<TenantAppInfo> getAllTenantApps() throws SystemException;

	/**
	 * Returns all the ad group to application mappings
	 * 
	 * @return
	 * @throws SystemException
	 */
	public List<AdGroupAppMappingInfo> getAllAdGroupAppMappings() throws SystemException;

	/**
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public RATenantAppParamsRequest updateTenantAppParams(RATenantAppParamsRequest request, String updatedBy)throws
            SystemException, IOException;
	/**
	 * @param raTenantId
	 * @return
	 * @throws SystemException
	 * @throws IOException
	 */
	public RATenantAppParamsRequest getTenantAppParam(String type)throws SystemException, IOException;


}
