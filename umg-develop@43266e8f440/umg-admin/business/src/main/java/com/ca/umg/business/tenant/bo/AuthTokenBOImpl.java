package com.ca.umg.business.tenant.bo;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.encryption.EncryptionUtil;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.TenantData;
import com.ca.framework.core.info.tenant.AuthTokenInfo;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.tenant.dao.AuthTokenContainerDAO;
import com.ca.umg.business.tenant.dao.AuthTokenDAO;
import com.ca.umg.business.tenant.entity.AuthToken;
import com.ca.umg.business.tenant.entity.Tenant;
import com.ca.umg.business.tenant.util.AuthTokenUtil;

/**
 * @author basanaga
 * 
 */
@Named
public class AuthTokenBOImpl implements AuthTokenBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenBOImpl.class);

    @Inject
    private AuthTokenDAO authTokenDAO;

    @Inject
    private AuthTokenContainerDAO authTokenContainerDAO;

    @Inject
    private CacheRegistry cacheRegistry;

    /* (non-Javadoc)
     * @see com.ca.umg.business.tenant.bo.AuthTokenBO#listAll(java.lang.String)
     */
    @Override
    public List<AuthTokenInfo> listAll(String tenantId) throws SystemException {
        return authTokenContainerDAO.getAllAuthTokensForTenantId(tenantId);
    }

    /* (non-Javadoc)
     * @see com.ca.umg.business.tenant.bo.AuthTokenBO#save(com.ca.umg.business.tenant.entity.AuthToken)
     */
    @Override
    public AuthToken save(AuthToken authToken) throws SystemException {
        return authTokenDAO.save(authToken);
    }

    /* (non-Javadoc)
     * @see com.ca.umg.business.tenant.bo.AuthTokenBO#getActiveAuthToken(java.lang.String)
     */
    @Override
    public AuthTokenInfo getActiveAuthToken(String tenantId) throws SystemException {
        List<AuthTokenInfo> authTokenInfos = authTokenContainerDAO.getActiveAuthToken(tenantId);
        if (CollectionUtils.sizeIsEmpty(authTokenInfos)) {
            throw new SystemException(BusinessExceptionCodes.BSE000905, new Object[] {});
        }
        return authTokenInfos.get(0);

    }

    /* (non-Javadoc)
     * @see com.ca.umg.business.tenant.bo.AuthTokenBO#activateAuthtoken(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    @SuppressWarnings("PMD.PreserveStackTrace")
    public void activateAuthtoken(String tenantId, String authTokenId, String comment) throws SystemException {
        List<AuthToken> authTokenList = null;
        authTokenList = authTokenDAO.getAuthTokenByStatus(tenantId, BusinessConstants.STATUS_ACTIVE);
        if (CollectionUtils.isNotEmpty(authTokenList)) {
            LOGGER.info("Started...Setting active authtoken to Deactivate status.");
            AuthToken authToken = authTokenList.get(0);
            authToken.setActiveUntil(System.currentTimeMillis());
            authToken.setStatus(BusinessConstants.STATUS_DEACTIVATED);
            authToken.setComment(AuthTokenUtil.DEACTIAVTED_FROM_ACTIVE);
            authTokenDAO.save(authToken);
            LOGGER.info("Done...Setting active authtoken to Deactivate status.");
        }
        boolean isActivateTokenFound = Boolean.FALSE;
        AuthToken pendingAuthToken = getPendingAuthToken(tenantId);
        if (pendingAuthToken != null) {
            if (StringUtils.equals(authTokenId, pendingAuthToken.getId())) {
                LOGGER.info("Started...Setting Pending authtoken to active status.");
                pendingAuthToken.setStatus(BusinessConstants.STATUS_ACTIVE);
                pendingAuthToken.setComment(comment);
                pendingAuthToken.setActiveFrom(System.currentTimeMillis());
                pendingAuthToken.setActiveUntil(pendingAuthToken.getActiveFrom() + DateUtils.MILLIS_PER_DAY * 364);
                authTokenDAO.save(pendingAuthToken);
                LOGGER.info("Done...Setting Pending authtoken to active status.");
                setActiveAuthToken(pendingAuthToken,tenantId);
                isActivateTokenFound = Boolean.TRUE;
            } else {
                LOGGER.info("Started...Setting Pending authtoken to Deactivative status ");
                deactivateFromPending(pendingAuthToken);
                LOGGER.info("Done...Setting Pending authtoken to Deactivative status ");
            }
        }


        if (!isActivateTokenFound) {
            AuthToken deactivateAt = authTokenDAO.findOne(authTokenId);
            LOGGER.info("Started...Setting Deactivate authtoken to Active status ");
            AuthToken newAuthToken = new AuthToken();
            newAuthToken.setStatus(BusinessConstants.STATUS_ACTIVE);
            newAuthToken.setComment(comment);
            newAuthToken.setAuthCode(deactivateAt.getAuthCode());
            newAuthToken.setActiveFrom(System.currentTimeMillis());
            newAuthToken.setTenant(deactivateAt.getTenant());
            if (deactivateAt.getActiveFrom() != null) {
                newAuthToken.setActiveUntil(deactivateAt.getActiveFrom() + DateUtils.MILLIS_PER_DAY * 364);
            }else{
                newAuthToken.setActiveUntil(newAuthToken.getActiveFrom() + DateUtils.MILLIS_PER_DAY * 364);                
            }
            authTokenDAO.save(newAuthToken);
            setActiveAuthToken(newAuthToken,tenantId);
            LOGGER.info("Done...Setting Deactivate authtoken to Active status ");
        }

    }

    /**
     * THis method used to set the active authtoken tino hazel cast
     * @param pendingAuthToken
     */
    private void setActiveAuthToken(AuthToken pendingAuthToken,String tenantId) {
        Map<String, TenantData> tenantUrlMap = cacheRegistry.getMap(FrameworkConstant.TENANT_URL_MAP);
        String tenantCode = authTokenContainerDAO.getTenantCode(tenantId);
        if(MapUtils.isNotEmpty(tenantUrlMap)){
            TenantData tenantData = (TenantData) tenantUrlMap.get(tenantCode);
            tenantData.setAuthToken(EncryptionUtil.decryptToken(pendingAuthToken.getAuthCode()));
            tenantUrlMap.put(tenantCode, tenantData);
        }
        Map<String, TenantInfo> tenantMap = cacheRegistry.getMap(FrameworkConstant.TENANT_MAP);
        TenantInfo tenantInfo = tenantMap.get(tenantCode);
        tenantInfo.setActiveAuthToken(EncryptionUtil.decryptToken(pendingAuthToken.getAuthCode()));
        tenantMap.put(tenantCode, tenantInfo);
    }

    /* (non-Javadoc)
     * @see com.ca.umg.business.tenant.bo.AuthTokenBO#deactivateFromPending(com.ca.umg.business.tenant.entity.AuthToken)
     */
    @Override
    public void deactivateFromPending(AuthToken authToken) {
        authToken.setStatus(BusinessConstants.STATUS_DEACTIVATED);
        authToken.setComment(AuthTokenUtil.DEACTIAVTED_FROM_PENDING);
        authTokenDAO.save(authToken);

    }

    /* (non-Javadoc)
     * @see com.ca.umg.business.tenant.bo.AuthTokenBO#getPendingAuthToken(java.lang.String)
     */
    @Override
    public AuthToken getPendingAuthToken(String tenantId) {
        AuthToken authToken = null;
        List<AuthToken> pendingAuthtokens = authTokenDAO.getAuthTokenByStatus(tenantId, BusinessConstants.STATUS_PENDING);
        if (CollectionUtils.isNotEmpty(pendingAuthtokens)) {
            authToken = pendingAuthtokens.get(0);
        }
        return authToken;
    }

    /* (non-Javadoc)
     * @see com.ca.umg.business.tenant.bo.AuthTokenBO#createNewAuthToken(java.lang.String)
     */
    @Override
    public void createNewAuthToken(String tenantId) throws SystemException {
        Tenant tenant = new Tenant();
        tenant.setId(tenantId);
        AuthToken authToken = AuthTokenUtil.getBasicAuthToken(tenant);        
            AuthToken pedningAuthToken = getPendingAuthToken(tenantId);
            if (pedningAuthToken != null) {
                deactivateFromPending(pedningAuthToken);
            }
            authToken.setTenant(tenant);
            authToken = save(authToken);      
    }

}
