package com.ca.umg.business.tenant.util;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.encryption.EncryptionUtil;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.tenant.entity.AuthToken;
import com.ca.umg.business.tenant.entity.Tenant;

/**
 * This utility class used for authtoken functionality
 * @author basanaga
 *
 */
public final class AuthTokenUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenUtil.class);
    public static final String DEACTIAVTED_FROM_PENDING = "System Deactivated From Pending ";
    public static final String DEACTIAVTED_FROM_ACTIVE = "System Deactivated From Active ";

    private AuthTokenUtil() {

    }

    /**
     * This method used to get new authtoken
     * @param tenant
     * @return AuthToken
     */
    public static AuthToken getBasicAuthToken(Tenant tenant) {
        AuthToken authToken = new AuthToken();
        authToken.setAuthCode(generateRandomAuthToken());
        authToken.setTenant(tenant);
        authToken.setStatus(BusinessConstants.STATUS_PENDING);
        return authToken;
    }


    /**
     * This method generates random nnew authtoken
     * @return
     */
    private static String generateRandomAuthToken() {
        UUID authToken = UUID.randomUUID();
        LOGGER.debug("The Generated Auth Token : " + authToken.toString());
        return EncryptionUtil.encryptToken(authToken.toString());
    }

}
