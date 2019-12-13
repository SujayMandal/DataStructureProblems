package com.ca.umg.sdc.rest.controller;

import static com.ca.umg.business.dbauth.UMGUserStatus.FIRST_TIME_LOGIN;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.util.Collection;

import org.slf4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ca.umg.business.dbauth.UMGUserDetails;

@Controller
@SuppressWarnings("PMD")
public class HomeController {

	private static final Logger LOGGER = getLogger(HomeController.class);

	private static final String ADMIN_ROLE = "ROLE_ADMIN";
	private static final String ADMIN_ROLE_RF = "ROLE_UMG_ADMIN";

	private static final String TENANT_ROLE = "ROLE_TENANT";
	private static final String TENANT_ROLE_RF = "ROLE_UMG_TENANT";

	@RequestMapping("/")
	public String renderHome() {
		if (isFirstTimeLogin()) {
			return "change-password";
		} else {
		    return "home/home";
		}
		/*else if (isAdminRole()) {
			return "home/home";
		} else if (isTenantRole()) {
			return "tenant-portal/tenant-portal-home";
		}*/ 

		//return null;
	}

	@RequestMapping("/admin")
	public String renderAdmin() {
		return "home/home";
	}

	@RequestMapping("/login")
	public String renderLogin() {
		return "login";
	}

	@RequestMapping("/changePassword")
	public String cahngePassword() {
		return "change-password";
	}

	@RequestMapping("/tenant")
	public String renderTenannt() {
		if (isTenantRole()) {
			return "tenant-portal/tenant-portal-home";
		}

		return null;
	}
	
	@RequestMapping("/approval-message")
	public String RedirectApprovalMessage() {
		return "approval-message";
	}

	public static boolean isAdminRole() {
		return hasRole(ADMIN_ROLE) || hasRole(ADMIN_ROLE_RF);
	}

	public static boolean isTenantRole() {
		return isAdminRole() || hasRole(TENANT_ROLE) || hasRole(TENANT_ROLE_RF);
	}

	private static boolean hasRole(final String role) {
		boolean roleFound = false;

		final SecurityContext context = getContext();
		if (context != null) {
			final Authentication authentication = context.getAuthentication();
			if (authentication != null) {
				printRoles(authentication.getAuthorities(), authentication.getName());
				for (GrantedAuthority authority : authentication.getAuthorities()) {
					if (role.equals(authority.getAuthority())) {
						roleFound = true;
						break;
					}
				}
			}
		}

		return roleFound;
	}

	private static void printRoles(final Collection authorities, final String username) {
		if (authorities != null) {
			LOGGER.info("Roles avaiable for User:" + username);
			for (Object authority : authorities) {
				LOGGER.info(((GrantedAuthority) authority).getAuthority());
			}
		} else {
			LOGGER.info("No Roles avaiable for User:" + username);
		}
	}

	private boolean isFirstTimeLogin() {
		boolean firstTimeLogin = false;

		final SecurityContext context = getContext();
		if (context != null) {
			final Authentication authentication = context.getAuthentication();
			if (authentication instanceof UsernamePasswordAuthenticationToken) {
				final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
				if (token.getPrincipal() instanceof UMGUserDetails) {
					final UMGUserDetails userDetails = (UMGUserDetails) token.getPrincipal();
					if (userDetails.getEnabledCode() == FIRST_TIME_LOGIN.getCode()) {
						firstTimeLogin = true;
					}
				}
			}
		}

		return firstTimeLogin;
	}
}