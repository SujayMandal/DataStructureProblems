package com.ca.umg.business.tenant.delegate;

import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.AuthTokenInfo;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.tenant.bo.AuthTokenBO;
import com.ca.umg.business.tenant.entity.AuthToken;
import com.ca.umg.business.tenant.entity.Tenant;
import com.ca.umg.business.tenant.util.AuthTokenUtil;
import com.ca.umg.business.util.AdminUtil;

/**
 * @author basanaga
 *
 */
@Named
public class AuthTokenDelegateImpl extends AbstractDelegate implements AuthTokenDelegate {

    // private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenDelegateImpl.class);

	@Inject
	private AuthTokenBO authTokenBO;

	@Override
    public List<AuthTokenInfo> listAll(String tenantId) throws SystemException {
		AdminUtil.setAdminAwareTrue();
		List<AuthTokenInfo> list;
		try {
            list = authTokenBO.listAll(tenantId);
		} finally {
			getRequestContext().setAdminAware(AdminUtil.getActualAdminAware());
		}
		return list;

	}

	@Override
	public AuthToken create(Tenant tenant) {
        AuthToken authToken = AuthTokenUtil.getBasicAuthToken(tenant);
        authToken.setActiveFrom(new DateTime(DateTimeZone.UTC).getMillis());
        authToken.setActiveUntil(authToken.getActiveFrom() + DateUtils.MILLIS_PER_DAY * 364);
		authToken.setComment("Tenant Onboarded");
        authToken.setStatus(BusinessConstants.STATUS_ACTIVE);
		return authToken;

	}


    @Override
    public List<AuthTokenInfo> createNewAuthToken(String tenantId) throws SystemException {
        authTokenBO.createNewAuthToken(tenantId);
        return listAll(tenantId);
    }


	@Override
    public String getActiveAuthCode(String tenantId) throws SystemException {      
        AuthTokenInfo authToken = authTokenBO.getActiveAuthToken(tenantId);
        String authCode = null;
        if (authToken != null) {
            authCode = authToken.getAuthCode();

        }

        return authCode;
	}

	@Override
    public AuthTokenInfo getActiveAuthToken(String tenantId) throws SystemException {		
           return authTokenBO.getActiveAuthToken(tenantId);
	}

    @Override
    public List<AuthTokenInfo> activateAuthToken(String tenantId, String authTokenId, String comment)
 throws SystemException {
            authTokenBO.activateAuthtoken(tenantId, authTokenId, comment);


        return listAll(tenantId);

	}

}
