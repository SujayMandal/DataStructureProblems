package com.fa.dp.security.config.ldap;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * 
 *
 *
 */
public class CustomUser extends User {

	private static final long serialVersionUID = -2002989342497721840L;

	private List<String> adGroups;

	private String currentAppCode;

	public String getCurrentAppCode() {
		return currentAppCode;
	}

	public void setCurrentAppCode(String currentAppCode) {
		this.currentAppCode = currentAppCode;
	}

	public CustomUser(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,
			List<String> adGroups) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.adGroups = adGroups;
	}

	public List<String> getAdGroups() {
		return adGroups;
	}

}
