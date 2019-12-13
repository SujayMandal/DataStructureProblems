package com.ca.framework.core.web.filter;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.ca.framework.core.logging.appender.AppenderConstants;
import com.ca.framework.core.requestcontext.RequestContext;

public class TenantResolutionFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantResolutionFilter.class);

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String tenantURL = httpServletRequest.getRequestURL().toString();

        String tenantCode = extractTenantCode(tenantURL);

        Properties properties = new Properties();
        properties.put(RequestContext.TENANT_URL, tenantURL);
        properties.put(RequestContext.TENANT_CODE, tenantCode);
        RequestContext requestContext = new RequestContext(properties);
        MDC.put(AppenderConstants.MDC_TENANT_CODE, requestContext.getTenantCode());
        LOGGER.info("Before entering the filter process");
        filterChain.doFilter(request, response);
        LOGGER.info("After entering the filter process");
        RequestContext.getRequestContext().destroy();
        MDC.clear();
    }

    private String extractTenantCode(String tenantURL) {

        String frontStripped = StringUtils.substringAfter(tenantURL, "http://");
        String tenantCode = null;
        LOGGER.info("front stripped --> " + frontStripped);
        if (frontStripped.indexOf(':') > -1) {
            tenantCode = StringUtils.substringBefore(frontStripped, ":");
        } else if (frontStripped.indexOf('/') > -1) {
            tenantCode = StringUtils.substringBefore(frontStripped, "/");
        } else {
            tenantCode = frontStripped;
        }
        
        if(tenantCode.indexOf('.') > -1){
            tenantCode = StringUtils.substringBefore(tenantCode, ".");
        }
        
        LOGGER.info("FinaL TENANT CODE ---> " + tenantCode);
        return tenantCode;
    }

    @Override
    public void init(FilterConfig filterChain) throws ServletException {
        LOGGER.info("init method is called.");
    }

}
