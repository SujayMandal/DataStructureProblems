package com.ca.umg.business.dbauth;

import static com.ca.umg.business.dbauth.UMGUserStatus.ACTIVE;
import static com.ca.umg.business.dbauth.UMGUserStatus.FIRST_TIME_LOGIN;
import static com.google.common.base.Objects.toStringHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ca.umg.business.accessprivilege.Privileges;
import com.ca.umg.business.constants.BusinessConstants;

@SuppressWarnings("PMD")
public class UMGUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;
	private final String username;
	private final String password;
	private final boolean enabled;
	private String tenantCode;
	private final Collection<UMGUserRoleDetails> authorities;
	private final int enabledCode;
	private final Boolean isSysAdmin;
	private List<String> tenantList;

	public UMGUserDetails(final String username, final String password, final int enabled, final String tenantCode, 
	        final String role_list, final String perm_list, final Boolean sys_admin, 
	        final List<Privileges> staticPagePrivilegesList, final List<Privileges> staticActionPrivilegesList) {
		this.username = username;
		this.password = password;
		this.enabled = enabled == ACTIVE.getCode() || enabled == FIRST_TIME_LOGIN.getCode() ? true : false;
		this.tenantCode = tenantCode;
		enabledCode = enabled;
		authorities = new ArrayList<UMGUserRoleDetails>();
		UMGUserRoleDetails roleDetails = null;
		Set<String> tmpSet = new HashSet<>();
		if (role_list != null) {
		    for (String role : StringUtils.split(role_list, BusinessConstants.CHAR_COMMA)) {
	            if (tmpSet.add(role)) {
	                roleDetails = new UMGUserRoleDetails(role, tenantCode);
	                authorities.add(roleDetails);
	            }
	        }
		}
		tmpSet.clear();
		if (perm_list != null) {
		    for (String privilege : StringUtils.split(perm_list, BusinessConstants.CHAR_COMMA)) {
		        if (tmpSet.add(privilege)) {
		            roleDetails = new UMGUserRoleDetails(privilege, tenantCode);
		            authorities.add(roleDetails);
		        }
		    }
		}
		
		if (sys_admin) {
		    setAuthoritiesForSysAdmin(staticPagePrivilegesList, staticActionPrivilegesList);
		}
		
        this.isSysAdmin = sys_admin;
	}
	
	private void setAuthoritiesForSysAdmin(final List<Privileges> staticPagePrivilegesList, 
	        final List<Privileges> staticActionPrivilegesList) {
	    Set<String> tmpSet = new HashSet<>();
	    UMGUserRoleDetails roleDetails = null;
        if (CollectionUtils.isNotEmpty(staticPagePrivilegesList)) {
            for (Privileges privilege : staticPagePrivilegesList) {
                if (tmpSet.add(privilege.getPermission())) {
                    roleDetails = new UMGUserRoleDetails(privilege.getPermission(), tenantCode);
                    authorities.add(roleDetails);
                }
            }
        }
        tmpSet.clear();
        if (CollectionUtils.isNotEmpty(staticActionPrivilegesList)) {
            for (Privileges privilege : staticActionPrivilegesList) {
                if (tmpSet.add(privilege.getPermission())) {
                    roleDetails = new UMGUserRoleDetails(privilege.getPermission(), tenantCode);
                    authorities.add(roleDetails);
                }
            }
        }
        
        //adding this explicitly for super admin
        roleDetails = new UMGUserRoleDetails(BusinessConstants.ROLE_SUPER_ADMIN, tenantCode);
        authorities.add(roleDetails);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean equals(final Object obj) {
		boolean eq = false;
		if (obj instanceof UMGUserDetails) {
			eq = ((UMGUserDetails) obj).getUsername().equals(username);
		}
		return eq;
	}

	@Override
	public int hashCode() {
		return username.hashCode();
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public int getEnabledCode() {
		return enabledCode;
	}

	public Boolean getIsSysAdmin() {
        return isSysAdmin;
    }

    public List<String> getTenantList() {
        return tenantList;
    }

    public void setTenantList(List<String> tenantList) {
        this.tenantList = tenantList;
    }

    @Override
	public String toString() {
		return toStringHelper(this).add("Username", username).add("Authorities", authorities.toString()).toString();
	}
}