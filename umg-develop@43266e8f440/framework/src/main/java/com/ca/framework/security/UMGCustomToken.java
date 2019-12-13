package com.ca.framework.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class UMGCustomToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;

	private final Object principal;
	
	private Collection  authorities;

	public UMGCustomToken(String umgToken) {
		super(null);
		super.setAuthenticated(true); // must use super, as we override
		this.principal = umgToken;
		this.setDetailsAuthorities();
	}

	@Override
	public Object getCredentials() {
		return "";
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	private void setDetailsAuthorities() {
		String username = principal.toString();
		SpringUserDetailsAdapter adapter = new SpringUserDetailsAdapter(username);
		authorities = adapter.getAuthorities();
	}
	
    @Override
    public Collection getAuthorities() {
        return authorities;
    }

}
