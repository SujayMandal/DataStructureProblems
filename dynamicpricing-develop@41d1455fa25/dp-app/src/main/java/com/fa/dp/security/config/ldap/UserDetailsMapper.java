package com.fa.dp.security.config.ldap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.core.cache.CacheManager;

import com.fa.dp.core.util.RAClientConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

/**
 * Assigns custom role by matching the allowed role against groups returned from
 * the active directory
 * 
 *
 *
 */
@Named
public class UserDetailsMapper implements UserDetailsContextMapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsMapper.class);

	@Inject
	private CacheManager cacheManager;

	@Override
	public UserDetails mapUserFromContext(DirContextOperations dirCtx, String username,
			Collection<? extends GrantedAuthority> grantedAuthorities) {
		Boolean userHasAccess = Boolean.FALSE;
		List<GrantedAuthority> mappedAuthorities = new ArrayList<GrantedAuthority>();
		List<String> groupsInfo = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(grantedAuthorities)) {
			Set<String> systemAdGroups = cacheManager.getAllADGroups();
			for (GrantedAuthority granted : grantedAuthorities) {
				if (systemAdGroups.contains(granted.getAuthority())) {
					LOGGER.error("Adding user AD group {} to granted authorities.", granted.getAuthority());
					groupsInfo.add(granted.getAuthority());
					userHasAccess = Boolean.TRUE;
				}
			}
		}

		if(!userHasAccess) {
			throw new UsernameNotFoundException("There was an error with your login. Please contact administrator");
		}

		return new CustomUser(username, RAClientConstants.CHAR_EMPTY, true, true, true, true, mappedAuthorities, groupsInfo);
	}

	@Override
	public void mapUserToContext(UserDetails arg0, DirContextAdapter arg1) {
		LOGGER.info("Nothing to implement here.");

	}

}
