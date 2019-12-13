package com.ca.sdc.webui.core.filter;

import static com.ca.umg.report.service.ReportService.RA_REPORT_REQUEST_MAPPING;
import static com.ca.umg.report.service.ReportService.RA_REPORT_REQUEST_MAPPING_API;

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

public class UMGCorsFilter extends OncePerRequestFilter {
	private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	private static final Logger LOGGER = LoggerFactory.getLogger(UMGCorsFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String tenantURL = httpServletRequest.getRequestURL().toString();

		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(request.getServletContext());
		SystemParameterProvider systemParameterProvider = ctx.getBean(SystemParameterProvider.class);
		String allowedHosts = systemParameterProvider.getParameter(SystemParameterConstants.ALLOWED_HOSTS);
		if (tenantURL.contains("/api/v1.0/search")
				|| tenantURL.contains(RA_REPORT_REQUEST_MAPPING + RA_REPORT_REQUEST_MAPPING_API)) {

			if ("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
				response.setHeader("Access-Control-Allow-Methods", "POST, GET");
				response.setHeader("Access-Control-Allow-Headers",
						"Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent, AuthToken");
				response.setHeader("Access-Control-Allow-Origin", allowedHosts);
				response.setHeader("X-Frame-Options", "SAMEORIGIN");
				response.getWriter().write(0);
				response.flushBuffer();
			} else {
				LOGGER.info("Entered CORSFilter : doFilterInternal ");
				String authTokenWithTenantCode = httpServletRequest.getHeader("AuthToken");
				if (authTokenWithTenantCode != null) {
					String origin = request.getHeader("Origin");
					response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
					response.setHeader("Allow", origin);
					response.setHeader("Access-Control-Allow-Methods", "POST, GET");
					response.setHeader("Access-Control-Allow-Headers",
							"Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent, AuthToken,Content-Disposition");
					response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
					response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, allowedHosts);
					response.setHeader("X-Frame-Options", "SAMEORIGIN");
					filterChain.doFilter(request, response);
				} else {
					String origin = request.getHeader("Origin");
					response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
					response.setHeader("Allow", origin);
					response.setHeader("Access-Control-Allow-Methods", "POST, GET");
					response.setHeader("Access-Control-Allow-Headers",
							"Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent, AuthToken");
					response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, allowedHosts);
					response.setHeader("X-Frame-Options", "SAMEORIGIN");
					// response.sendError(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED.value());
				}
			}
		}
	}
}
