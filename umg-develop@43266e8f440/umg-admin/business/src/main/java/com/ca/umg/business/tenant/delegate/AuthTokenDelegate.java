package com.ca.umg.business.tenant.delegate;

import java.util.List;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.AuthTokenInfo;
import com.ca.umg.business.tenant.entity.AuthToken;
import com.ca.umg.business.tenant.entity.Tenant;

/**
 * 
 * @author basanaga
 *
 */
public interface AuthTokenDelegate {

	/**
	 * Returns the list of authTokens
	 * 
	 * @return
	 * @throws BusinessException
	 * @throws SystemException
	 */
    List<AuthTokenInfo> listAll(String tenantId) throws SystemException;

	/**
	 * Create Authtoken for new tenant
	 * 
	 * @return
	 */
	AuthToken create(Tenant tenant);


    /**
     * This method used to get active auth code
     * @param tenantId
     * @return
     * @throws SystemException
     */
    String getActiveAuthCode(String tenantId) throws SystemException;

    /**
     * This method used to get activate auth Token Info Object
     * @param tenantId
     * @return
     * @throws SystemException
     */
    AuthTokenInfo getActiveAuthToken(String tenantId) throws SystemException;

    /**
     * This method used to create new Authtoken 
     * @param tenantId
     * @return
     * @throws SystemException
     */
    List<AuthTokenInfo> createNewAuthToken(String tenantId) throws SystemException;

    /**
     * 
     * This method used to activate authtoken
     * @param tenantId
     * @param authTokenId
     * @param comment
     * @return
     * @throws SystemException
     */
    List<AuthTokenInfo> activateAuthToken(String tenantId, String authTokenId, String comment) throws SystemException;

}
