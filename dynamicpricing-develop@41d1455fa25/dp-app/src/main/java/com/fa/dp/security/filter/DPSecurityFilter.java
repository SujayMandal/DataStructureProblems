package com.fa.dp.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fa.dp.framework.encryption.EncryptionUtil;

@Slf4j
@Component
public class DPSecurityFilter extends OncePerRequestFilter {

	@Value("${UPLOAD_AUTH_TOKEN}")
	public String uploadAuthToken;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String userToken = ((HttpServletRequest) request).getHeader("token");

		if(!(request.getMethod().equals("POST") || request.getMethod().equals("GET"))) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			return;
		}

		if (((HttpServletRequest) request).getRequestURL().toString().contains("dp/uploadSSInvestorFile")
				|| ((HttpServletRequest) request).getRequestURL().toString().contains("dp/uploadPMICompanies")) {
			if (userToken.isEmpty()) {
				log.error("Auth token missing");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			} else {
				String decryptedUserToken = EncryptionUtil.decryptToken(userToken);
				String decryptedPropToken = EncryptionUtil.decryptToken(uploadAuthToken);
				if (!decryptedPropToken.equals(decryptedUserToken)) {
					log.error("Authorization Error");
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}
			}
		}

		filterChain.doFilter(request, response);
	}

}