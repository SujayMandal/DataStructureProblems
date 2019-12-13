/**
 * 
 */
package com.fa.dp.core.tenant.delegate;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.tenant.bo.TenantBO;
import com.fa.dp.core.tenant.domain.Tenant;
import com.fa.dp.core.tenant.info.TenantInfo;

/**
 *
 *
 */
@Named
public class TenantDelegateImpl extends AbstractDelegate implements TenantDelegate {

    @Inject
    private TenantBO tenantBO;

    /*
     * (non-Javadoc)
     * 
     * @see com.fa.ra.client.core.tenant.delegate.TenantDelegate#getAllTenants()
     */
    @Override
    public List<TenantInfo> getAllTenants() throws SystemException {
        List<Tenant> tenants = tenantBO.getAllTenants();
        return convertToList(tenants, TenantInfo.class);
    }

}
