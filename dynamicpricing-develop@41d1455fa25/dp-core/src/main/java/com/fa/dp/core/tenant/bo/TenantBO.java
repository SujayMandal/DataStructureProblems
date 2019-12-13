/**
 * 
 */
package com.fa.dp.core.tenant.bo;

import java.util.List;

import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.tenant.domain.Tenant;

/**
 *
 *
 */
public interface TenantBO {
    /**
     * Returns list of tenants defined in the system.
     * 
     * @return
     * @throws SystemException
     */
    public List<Tenant> getAllTenants() throws SystemException;

}
