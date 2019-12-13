package com.ca.umg.business.accessprivilege.delegate;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.userdetails.UserDetails;

import com.altisource.iam.dto.RFUserDetails;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.dbauth.UMGUserDetails;

public interface SwitchTenantDelegate {
	/**
     * sets the authentication object with {@link UMGUserDetails} for the passed tenant
     * @param tenantCode
     * @param request
     * @throws SystemException
     */
    public void switchAndSetTenant (String tenantCode, HttpServletRequest request) throws SystemException;
    
    /**
     * gets the {@link UMGUserDetails} object for the passed rfUserdetails and tenant code
     * @param rfUserDetails
     * @param tenantCode
     * @return
     */
    public UserDetails getUmgUserDetails (RFUserDetails rfUserDetails, String tenantCode) throws SystemException;  
}
