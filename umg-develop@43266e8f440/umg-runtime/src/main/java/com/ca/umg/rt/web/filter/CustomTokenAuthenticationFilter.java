package com.ca.umg.rt.web.filter;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.TenantData;
import com.ca.framework.core.logging.appender.AppenderConstants;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.security.CustomTokenAuthenticationSuccessHandler;
import com.ca.framework.security.NoOpAuthenticationManager;
import com.ca.framework.security.UMGCustomToken;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.util.MessageVariables;

public class CustomTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
     
    private static final String TOKEN_DELIMITER = ".";

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomTokenAuthenticationFilter.class);
    
    @Inject
    private CacheRegistry cacheRegistry;

    
    public CustomTokenAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
//        super.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(defaultFilterProcessesUrl));
        setAuthenticationManager(new NoOpAuthenticationManager());
        setAuthenticationSuccessHandler(new CustomTokenAuthenticationSuccessHandler());
    }
 
    /**
     * Attempt to authenticate request - basically just pass over to another method to authenticate request headers 
     */
    @Override 
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String authTokenWithTenantCode = request.getHeader(MessageVariables.AUTH_TOKEN);
        UMGCustomToken userAuthenticationToken = null;
        String tenantCode = null;
        try {
            tenantCode = extractTenantCode(authTokenWithTenantCode);
            String authToken = extractAuthToken(authTokenWithTenantCode);
            userAuthenticationToken = authUserByToken(authToken, tenantCode);
        } catch (SystemException e) { 
            LOGGER.error("Error Occurred During Authentication : " + e.getLocalizedMessage());
            throw new AuthenticationServiceException("Authentication Failed"); // NOPMD
        }
        String tenantURL = ((HttpServletRequest)request).getRequestURL().toString();
        setRequestContext(tenantURL, tenantCode);
        return userAuthenticationToken;
    }
 
 
    private String extractAuthToken(String authTokenWithTenantCode) {
        return authTokenWithTenantCode.substring(authTokenWithTenantCode.indexOf(TOKEN_DELIMITER) + 1);
    }

    /**
     * authenticate the user based on token
     * @return
     * @throws SystemException 
     */
    private UMGCustomToken authUserByToken(String authToken, String tenantCode) throws SystemException {
        boolean isAuthenticated = validateToken(authToken, tenantCode);
        if (!isAuthenticated) {
            throwException(RuntimeExceptionCode.RSE000812, "Invalid Auth token");
        }
        return new UMGCustomToken(authToken);
    }
    
    private void throwException(String exceptionCode, String message) throws SystemException {
        throw new SystemException(exceptionCode, new Object[] { message });
    }
    
    private boolean validateToken(String authToken, String tenantCode) throws SystemException {
        TenantData tenantInfo = (TenantData) cacheRegistry.getMap(FrameworkConstant.TENANT_URL_MAP).get(tenantCode);
        if (tenantInfo == null) {
            throwException(RuntimeExceptionCode.RSE000812, "Tenant Code does not exist : " + tenantCode);
        }
        String storedToken = tenantInfo.getAuthToken();

        if (StringUtils.isEmpty(storedToken)) {
            throwException(RuntimeExceptionCode.RSE000812, "Auth token not found for tenant");
		}
		if (StringUtils.equals(authToken, storedToken)) {
            return true;
        }
        return false;
    }

    private String extractTenantCode(String authTokenWithTenantCode) throws SystemException {
        String tenantCode = null;
        if (!StringUtils.isEmpty(authTokenWithTenantCode) && authTokenWithTenantCode.contains(TOKEN_DELIMITER)) {
            tenantCode = authTokenWithTenantCode.substring(0, authTokenWithTenantCode.indexOf(TOKEN_DELIMITER));
        } else {
            throwException(RuntimeExceptionCode.RSE000812, "TenantCode Not Found."); 
        }
        return tenantCode;
    }
 
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        super.doFilter(req, res, chain);
        // TODO: Need to manage execution of other filters in the chain
        if (RequestContext.getRequestContext() != null) {
            RequestContext.getRequestContext().destroy();
        }
    }

    public void setRequestContext(String tenantURL, String tenantCode) {
        Properties properties = new Properties();
        properties.put(RequestContext.TENANT_URL, tenantURL);
        properties.put(RequestContext.TENANT_CODE, tenantCode);
        RequestContext requestContext = new RequestContext(properties);
        MDC.put(AppenderConstants.MDC_TENANT_CODE, requestContext.getTenantCode());
    }
}