package com.ca.umg.business.accessprivilege.dao;

import org.springframework.security.core.userdetails.UserDetails;

import com.ca.framework.core.exception.SystemException;

public interface SwitchTenantDAO {

    public UserDetails switchAndSetTenant (String tenantCode) throws SystemException;
}
