package com.ca.sdc.webui.core.filter;

import static com.ca.umg.business.dbauth.UserLoginActivity.LOGIN_FAILED;
import static com.ca.umg.business.dbauth.UserLoginActivity.LOGIN_SUCCESS;
import static com.ca.umg.business.dbauth.util.PasswordUtil.isPasswordMatchedWithEncoded;
import static com.ca.umg.business.exception.codes.BusinessExceptionCodes.BSE0000513;
import static com.ca.umg.business.exception.codes.BusinessExceptionCodes.BSE0000514;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.dbauth.UMGUserDetails;
import com.ca.umg.business.dbauth.UserLoginAudit;
import com.ca.umg.business.dbauth.bo.UMGUserLoginActivityBO;

@Component(value = "authenticationProvider")
@SuppressWarnings("PMD")
public class UMGAuthenticationProvider implements AuthenticationProvider {

	private final UserDetailsService userProfileService;

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

	@Inject
	private UMGUserLoginActivityBO userLoginActivityBO;

	@Autowired
	public UMGAuthenticationProvider(final UserDetailsService userProfileService) {
		this.userProfileService = userProfileService;
	}

	@Autowired
	public void setEncoder(final BCryptPasswordEncoder encoder) {
		this.encoder = encoder;
	}

	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
		UserDetails profile = userProfileService.loadUserByUsername(authentication.getPrincipal().toString());

		if (profile == null) {
			throw new UsernameNotFoundException(format("Invalid credentials", authentication.getPrincipal()));
		}

		final String ipAddress = ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
		final UserLoginAudit loginAudit = createUserLoginAudit((UMGUserDetails) profile, ipAddress);

		if (isActivevUser((UMGUserDetails) profile)) {
			if (!isPasswordMatchedWithEncoded(authentication.getCredentials().toString(), profile.getPassword(), encoder)) {
				logFailedActivity(loginAudit);
				if (lockUser((UMGUserDetails) profile)) {
					throw new BadCredentialsException("User account locked. Please contact system administrator.");
				}
				throw new BadCredentialsException("Invalid login credentials");
			}

			final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(profile, null, profile.getAuthorities());
			logSucessActivity(loginAudit);
			getContext().setAuthentication(token);
			return token;
		} else {
			logFailedActivity(loginAudit);
			throw new UsernameNotFoundException(format(getFailureMessage((UMGUserDetails) profile), authentication.getPrincipal()));
		}
	}

	private String getFailureMessage(final UMGUserDetails userDetails) {
		String message = "User is not active";
		switch (userDetails.getEnabledCode()) {
			case 0:
				message = "User should change password on first time.";
				break;
			case 2:
				message = "User account is deactivated. Please contact system administrator.";
				break;
			case 3:
				message = "User account locked. Please contact system administrator.";
				break;
			case 4:
				message = "User account is logically deleted. Please contact system administrator.";
				break;
			default:
				break;
		}

		return message;
	}

	private boolean isActivevUser(final UMGUserDetails userDetails) {
		return userDetails.isEnabled();

	}

	@Override
	public boolean supports(final Class<?> aClass) {
		return aClass.equals(UsernamePasswordAuthenticationToken.class);
	}

	private UserLoginAudit createUserLoginAudit(final UMGUserDetails userDetails, final String ipAddress) {
		final UserLoginAudit loginAudit = new UserLoginAudit();
		loginAudit.setUsername(userDetails.getUsername());
		loginAudit.setTenantCode(userDetails.getTenantCode());
		loginAudit.setAccessTime(currentTimeMillis());
		loginAudit.setIpAddress(ipAddress);
		return loginAudit;
	}

	private void logSucessActivity(final UserLoginAudit loginAudit) throws AuthenticationException {
		try {
			loginAudit.setActivity(LOGIN_SUCCESS);
			loginAudit.setReasonCode(BSE0000513);
			userLoginActivityBO.logActivity(loginAudit);
		} catch (BusinessException e) {
			throw new com.hazelcast.client.AuthenticationException(e.getMessage());
		}
	}

	private void logFailedActivity(final UserLoginAudit loginAudit) throws AuthenticationException {
		try {
			loginAudit.setActivity(LOGIN_FAILED);
			loginAudit.setReasonCode(BSE0000514);
			userLoginActivityBO.logActivity(loginAudit);
		} catch (BusinessException e) {
			throw new com.hazelcast.client.AuthenticationException(e.getMessage());
		}
	}

	private boolean lockUser(final UMGUserDetails userDetails) {
		boolean status = false;

		try {
			boolean value = userLoginActivityBO.isLoginConsecutivelyFailed(userDetails.getUsername(), userDetails.getTenantCode());
			if (value) {
				userLoginActivityBO.lockUser(userDetails.getUsername(), userDetails.getTenantCode());
				status = true;
			}
		} catch (BusinessException be) {
			throw new BadCredentialsException("Invalid login credentials");
		}

		return status;
	}
}