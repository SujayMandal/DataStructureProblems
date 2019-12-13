package com.ca.umg.business.tenant.bo;

import java.util.List;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.AuthTokenInfo;
import com.ca.umg.business.tenant.entity.AuthToken;

/**
 * 
 * @author basanaga
 *
 */
public interface AuthTokenBO {
   
    /**
     * Returns list of AuthTokens
     * @param tenantId
     * @return List of all authTokens of tenant id
     * @throws SystemException
     */
    List<AuthTokenInfo> listAll(String tenantId) throws SystemException;

    /**
     * Saves AuthToken information
     * 
     * @return
     * @throws SystemException
     */
    AuthToken save(AuthToken authToken) throws SystemException;
   
    /**
     * Get active auth token info
     * @param tenantId
     * @return
     * @throws SystemException
     */
    AuthTokenInfo getActiveAuthToken(String tenantId) throws SystemException;    

    /**
     * This method used to activate auth token
     * 
     * @param tenantId
     * @param authTokenId
     * @param comment
     * @throws SystemException
     */
    void activateAuthtoken(String tenantId, String authTokenId, String comment) throws SystemException;

    /**
     * This method used to deactivate the pending auth token
     * @param authToken
     */
    void deactivateFromPending(AuthToken authToken);

    /**
     * This method used to get the pedning auth token
     * @param tenantId
     * @return
     */
    AuthToken getPendingAuthToken(String tenantId);

    /**
     * This methid used to create new auth token
     * @param tenantId
     * @throws SystemException
     */
    void createNewAuthToken(String tenantId) throws SystemException;



}
