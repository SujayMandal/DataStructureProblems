/**
 * 
 */
package com.fa.dp.core.tenant.delegate;

import java.util.List;

import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.tenant.info.TenantInfo;

/**
 *
 *
 */
public interface TenantDelegate {

    /**
     * Returns all tenants defined in the system.
     * 
     * @return
     * @throws SystemException
     */
    public List<TenantInfo> getAllTenants() throws SystemException;
}
