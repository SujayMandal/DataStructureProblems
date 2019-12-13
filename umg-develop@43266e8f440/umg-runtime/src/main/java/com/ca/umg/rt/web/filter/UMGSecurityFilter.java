package com.ca.umg.rt.web.filter;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.TenantData;
import com.ca.framework.core.logging.appender.AppenderConstants;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterConstants;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.security.UMGCustomToken;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.util.MessageVariables;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

@SuppressWarnings("PMD")
public class UMGSecurityFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(UMGSecurityFilter.class);

    private static final String TOKEN_DELIMITER = ".";

    @Inject
    private CacheRegistry cacheRegistry;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authTokenWithTenantCode = ((HttpServletRequest) request).getHeader(MessageVariables.AUTH_TOKEN);
        String tenantCode = null;
        String tenantURL = ((HttpServletRequest) request).getRequestURL().toString();

        ApplicationContext ctx = WebApplicationContextUtils
                .getRequiredWebApplicationContext(request.getServletContext());
        SystemParameterProvider systemParameterProvider = ctx.getBean(SystemParameterProvider.class);
        String allowedHosts = systemParameterProvider.getParameter(SystemParameterConstants.ALLOWED_HOSTS);

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Methods", "POST, GET");
            response.setHeader("Access-Control-Allow-Headers",
                    "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent, AuthToken");
            response.setHeader("Access-Control-Allow-Origin", allowedHosts);
            response.setHeader("X-Frame-Options", "SAMEORIGIN");
            response.getWriter().write(0);
            response.flushBuffer();
        } else if (StringUtils.equalsIgnoreCase(request.getMethod(), "PUT")
                || StringUtils.equalsIgnoreCase(request.getMethod(), "HEAD")
                || StringUtils.equalsIgnoreCase(request.getMethod(), "DELETE")
                || StringUtils.equalsIgnoreCase(request.getMethod(), "PATCH")
        ) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        } else {
            processRequest(request, response, chain, authTokenWithTenantCode, tenantURL);
        }

    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain, String authTokenWithTenantCode, String tenantURL) throws IOException, ServletException {
        String tenantCode;
        if (tenantURL.contains("modeletPooling/getAllModeletInfo") || tenantURL.contains("modeletPooling/refreshModeletAllocation")) {
            if (request.getParameterNames().hasMoreElements()) {
                String userName = request.getParameter("user");
                tenantCode = request.getParameter("tenantCode");
                setRequestContext(tenantURL, tenantCode);
                MDC.put("USER_NAME", userName);
            }
        } else if (!tenantURL.contains("/modelExecEngine/")) {
            try {
                tenantCode = extractTenantCode(authTokenWithTenantCode);
                String authToken = extractAuthToken(authTokenWithTenantCode);
                authUserByToken(authToken, tenantCode);
            } catch (SystemException e) {
                LOGGER.error("Authorization Exception : " + e.getLocalizedMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            setRequestContext(tenantURL, tenantCode);
        }
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        chain.doFilter(request, response);
        if (RequestContext.getRequestContext() != null) {
            RequestContext.getRequestContext().destroy();
        }
        MDC.clear();
    }

    public String extractAuthToken(String authTokenWithTenantCode) {
        return authTokenWithTenantCode.substring(authTokenWithTenantCode.indexOf(TOKEN_DELIMITER) + 1);
    }

    /**
     * authenticate the user based on token
     *
     * @return
     * @throws SystemException
     */
    public UMGCustomToken authUserByToken(String authToken, String tenantCode) throws SystemException {
        boolean isAuthenticated = validateToken(authToken, tenantCode);
        if (!isAuthenticated) {
            throwException(RuntimeExceptionCode.RSE000812, "Invalid Auth token");
        }
        return new UMGCustomToken(authToken);
    }

    private void throwException(String exceptionCode, String message) throws SystemException {
        throw new SystemException(exceptionCode, new Object[]{message});
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

    public String extractTenantCode(String authTokenWithTenantCode) throws SystemException {
        String tenantCode = null;
        if (!StringUtils.isEmpty(authTokenWithTenantCode) && authTokenWithTenantCode.contains(TOKEN_DELIMITER)) {
            tenantCode = authTokenWithTenantCode.substring(0, authTokenWithTenantCode.indexOf(TOKEN_DELIMITER));
        } else {
            throwException(RuntimeExceptionCode.RSE000812, "TenantCode Not Found.");
        }
        return tenantCode;
    }

    public void setRequestContext(String tenantURL, String tenantCode) {
        Properties properties = new Properties();
        properties.put(RequestContext.TENANT_URL, tenantURL);
        properties.put(RequestContext.TENANT_CODE, tenantCode);
        RequestContext requestContext = new RequestContext(properties);
        MDC.put(AppenderConstants.MDC_TENANT_CODE, requestContext.getTenantCode());
    }

}
