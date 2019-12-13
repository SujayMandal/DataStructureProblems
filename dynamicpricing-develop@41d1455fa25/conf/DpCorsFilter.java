package com.fa.dp.security.filter;

import com.fa.dp.core.util.RAClientConstants;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class DpCorsFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String origin = request.getHeader("Origin");
//		response.setHeader("Access-Control-Allow-Origin", "https://davxapscst01.ascorp.com/");
		response.setHeader("Access-Control-Allow-Origin", origin);
		response.setHeader("Allow", origin);
		response.setHeader("Access-Control-Allow-Methods", "POST, GET");
		response.setHeader("Access-Control-Allow-Headers",
				"Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent, AuthToken");

		String username = null;
		if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (principal instanceof UserDetails) {
				username = ((UserDetails)principal).getUsername();
			} else {
				username = principal.toString();
			}
		}
		MDC.put(RAClientConstants.USER_ID, username);
		filterChain.doFilter(request, response);
		MDC.remove(RAClientConstants.USER_ID);
	}
}
