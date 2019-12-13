package com.ca.sdc.webui.core.filter;

import static com.ca.umg.report.service.ReportService.RA_REPORT_REQUEST_MAPPING;
import static com.ca.umg.report.service.ReportService.RA_REPORT_REQUEST_MAPPING_API;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ca.framework.core.systemparameter.SystemParameterConstants;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.logging.appender.AppenderConstants;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.tenant.delegate.TenantDelegate;

public class UMGSecurityFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(UMGSecurityFilter.class);

    private static final String TOKEN_DELIMITER = ".";

    private TenantDelegate tenantDelegate;

    private CacheRegistry cacheRegistry;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String tenantURL = httpServletRequest.getRequestURL().toString();
        ApplicationContext ctx = WebApplicationContextUtils
                .getRequiredWebApplicationContext(request.getServletContext());
        SystemParameterProvider systemParameterProvider = ctx.getBean(SystemParameterProvider.class);
        String allowedHosts = systemParameterProvider.getParameter(SystemParameterConstants.ALLOWED_HOSTS);

        if ("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
            response.setHeader("Access-Control-Allow-Methods", "POST, GET");
            response.setHeader("Access-Control-Allow-Headers",
                    "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent, AuthToken");
            response.setHeader("Access-Control-Allow-Origin", allowedHosts);
            response.getWriter().write(0);
            response.flushBuffer();
        } else if (StringUtils.equalsIgnoreCase(httpServletRequest.getMethod(), "PUT")
                || StringUtils.equalsIgnoreCase(httpServletRequest.getMethod(), "HEAD")
                || StringUtils.equalsIgnoreCase(httpServletRequest.getMethod(), "DELETE")
                || StringUtils.equalsIgnoreCase(httpServletRequest.getMethod(), "PATCH")
        ) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        } else {
            processRequest(request, response, chain, httpServletRequest, tenantURL);
        }
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain, HttpServletRequest httpServletRequest, String tenantURL) throws IOException, ServletException {
        if (tenantURL.contains("/version/getVersionDetails/") || tenantURL.contains("/api/v1.0/search")
                || tenantURL.contains(RA_REPORT_REQUEST_MAPPING + RA_REPORT_REQUEST_MAPPING_API)
                || tenantURL.contains("/version/getTenantIODefinition/")) {
            String authTokenWithTenantCode = null;
            String tenantCode = null;
            boolean isValidToken = false;
            authTokenWithTenantCode = httpServletRequest.getHeader("AuthToken");
            LOGGER.info("Token Received : " + authTokenWithTenantCode);
            try {
                tenantCode = extractTenantCode(authTokenWithTenantCode);
                String authToken = extractAuthToken(authTokenWithTenantCode);
                isValidToken = authUserByToken(authToken, tenantCode);
            } catch (SystemException | BusinessException e) {
                LOGGER.error("Authorization Exception : " + e.getLocalizedMessage());
            }
            if (!isValidToken) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } else {
                setRequestContext(tenantURL, tenantCode);
            }
        }
        chain.doFilter(request, response);
        if (RequestContext.getRequestContext() != null) {
            RequestContext.getRequestContext().destroy();
        }
        MDC.clear();
    }


    private String extractAuthToken(String authTokenWithTenantCode) {
        return authTokenWithTenantCode.substring(authTokenWithTenantCode.indexOf(TOKEN_DELIMITER) + 1);
    }

    private void throwException(String exceptionCode, String message) throws SystemException {
        throw new SystemException(exceptionCode, new Object[]{message});
    }

    private boolean validateToken(String authToken, String tenantCode) throws BusinessException, SystemException {

        if (tenantDelegate == null) {
            ApplicationContext ctx = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(getFilterConfig().getServletContext());
            this.tenantDelegate = ctx.getBean(TenantDelegate.class);
        }

        if (cacheRegistry == null) {
            ApplicationContext ctx = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(getFilterConfig().getServletContext());
            this.cacheRegistry = ctx.getBean(CacheRegistry.class);
        }

        Map<String, TenantInfo> tenantMap = cacheRegistry.getMap(FrameworkConstant.TENANT_MAP);
        TenantInfo tenantInfo = tenantMap.get(tenantCode);
        if (tenantInfo == null) {
            throwException(BusinessExceptionCodes.BSE000812, "Tenant Code does not exist : " + tenantCode);
        }
        boolean isValid = false;
        if (StringUtils.equals(authToken, tenantInfo.getActiveAuthToken())) {
            isValid = true;
        }
        return isValid;
    }

    private String extractTenantCode(String authTokenWithTenantCode) throws SystemException {
        String tenantCode = null;
        if (!StringUtils.isEmpty(authTokenWithTenantCode) && authTokenWithTenantCode.contains(TOKEN_DELIMITER)) {
            tenantCode = authTokenWithTenantCode.substring(0, authTokenWithTenantCode.indexOf(TOKEN_DELIMITER));
        } else {
            throwException(BusinessExceptionCodes.BSE000812, "Tenant Code Not Found.");
        }
        return tenantCode;
    }

    /**
     * authenticate the user based on token
     *
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    private boolean authUserByToken(String authToken, String tenantCode) throws SystemException, BusinessException {
        if (StringUtils.isEmpty(authToken)) {
            throwException(BusinessExceptionCodes.BSE000812, "Auth token not found");
        }
        return validateToken(authToken, tenantCode);
    }

    public void setRequestContext(String tenantURL, String tenantCode) {
        Properties properties = new Properties();
        properties.put(RequestContext.TENANT_URL, tenantURL);
        properties.put(RequestContext.TENANT_CODE, tenantCode);
        RequestContext requestContext = new RequestContext(properties);
        MDC.put(AppenderConstants.MDC_TENANT_CODE, requestContext.getTenantCode());
    }

    public TenantDelegate getTenantDelegate() {
        return tenantDelegate;
    }

    public void setTenantDelegate(TenantDelegate tenantDelegate) {
        this.tenantDelegate = tenantDelegate;
    }

    public CacheRegistry getCacheRegistry() {
        return cacheRegistry;
    }

    public void setCacheRegistry(CacheRegistry cacheRegistry) {
        this.cacheRegistry = cacheRegistry;
    }

    /*
     * private String getTenantCodeFromURL(final String tenantURL) { String allUrlInfos =
     * EncryptionUtil.decryptToken(getEncryptedIdFromURL(tenantURL)); String[] allUrlInfo = allUrlInfos.split(ID_FIELD_SEPERATOR);
     * return allUrlInfo[2]; }
     */

    /*
     * private String getAuthtokenFromURL(final String tenantURL, final ServletContext servletContext) { Tenant t = null; String[]
     * allUrlInfo = null; try { String allUrlInfos = EncryptionUtil.decryptToken(tenantURL.substring(tenantURL.lastIndexOf("/")+1,
     * tenantURL.length())); allUrlInfo = allUrlInfos.split(",");
     *
     * if (tenantBO == null) { ApplicationContext ctx =
     * WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext); this.tenantBO = ctx.getBean(TenantBO.class); }
     *
     * t = tenantBO.getTenant(allUrlInfo[2]); } catch (SystemException e) { // TODO Auto-generated catch block
     * e.printStackTrace(); } return allUrlInfo[2] +"." + EncryptionUtil.decryptToken(t.getAuthToken()); }
     */

}