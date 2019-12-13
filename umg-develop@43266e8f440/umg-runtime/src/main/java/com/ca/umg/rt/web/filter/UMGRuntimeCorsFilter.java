package com.ca.umg.rt.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ca.framework.core.systemparameter.SystemParameterConstants;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class UMGRuntimeCorsFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(UMGRuntimeCorsFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String tenantURL = httpServletRequest.getRequestURL().toString();
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(request.getServletContext());
		SystemParameterProvider systemParameterProvider = ctx.getBean(SystemParameterProvider.class);
		String allowedHosts = systemParameterProvider.getParameter(SystemParameterConstants.ALLOWED_HOSTS);

		if (tenantURL.contains("/runtime")) {
        	if("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
        		response.setHeader("Access-Control-Allow-Methods", "POST, GET");
                response.setHeader("Access-Control-Allow-Headers",
                        "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent, AuthToken");
                response.setHeader("Access-Control-Allow-Origin", allowedHosts);
                response.getWriter().write(0);
                response.flushBuffer();
        	} else {
	            LOGGER.info("Entered CORSFilter : doFilterInternal ");            
	            String authTokenWithTenantCode = httpServletRequest.getHeader("AuthToken");         
				if (authTokenWithTenantCode != null) {					
					response.setHeader("Access-Control-Allow-Methods", "POST, GET");
	                response.setHeader("Access-Control-Allow-Headers",
	                        "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent, AuthToken");
                    response.setHeader("Access-Control-Allow-Origin", allowedHosts);
	                filterChain.doFilter(request, response);
	            } else {
	            	response.setHeader("Access-Control-Allow-Methods", "POST, GET");
	                response.setHeader("Access-Control-Allow-Headers",
	                        "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent, AuthToken");
                    response.setHeader("Access-Control-Allow-Origin", allowedHosts);
	            }
        	}
        }
    }
}
