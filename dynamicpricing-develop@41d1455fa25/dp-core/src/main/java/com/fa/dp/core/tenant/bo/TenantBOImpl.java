/**
 * 
 */
package com.fa.dp.core.tenant.bo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.tenant.dao.TenantDao;
import com.fa.dp.core.tenant.domain.Tenant;

/**
 *
 *
 */
@Named
public class TenantBOImpl implements TenantBO {

    @Inject
    private TenantDao tenantDao;

    /*
     * (non-Javadoc)
     * 
     * @see com.fa.ra.client.core.tenant.bo.TenantBO#getAllTenants()
     */
    @Override
    public List<Tenant> getAllTenants() throws SystemException {
        return tenantDao.findAll();
    }

}
