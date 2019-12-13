/**
 * 
 */
package com.ca.umg.business.tenant.configurator;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;

import com.ca.framework.core.db.persistance.TenantRoutingDataSource;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.tenant.entity.Tenant;

/**
 * @author kamathan
 *
 */
@Named
public class BasicTenantConfiguration implements TenantConfigurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicTenantConfiguration.class.getName());

    @Inject
    private TenantRoutingDataSource tenantRoutingDatasource;

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.tenant.configurator.TenantConfigurator#provisionSchema(com.ca.umg.business.tenant.entity.Tenant)
     */
    @Override
    public void provisionSchema(Tenant tenant) throws SystemException {
        LOGGER.info("Provisioning schema to tenant {}.", tenant.getName());
        try {
            tenantRoutingDatasource.provisionNewSchemaToTenant(tenant.getCode());
        } catch (BeansException exc) {
            throw SystemException.newSystemException(BusinessExceptionCodes.BSE000003, new Object[] { exc.getMessage() }, exc);
        }

    }

    @Override
    public void initializeTenantDatasource(Tenant tenant) throws SystemException {
        try {
            tenantRoutingDatasource.initializeDatasourceForTenant(tenant.getCode());
        } catch (BeansException exc) {
            throw SystemException.newSystemException(BusinessExceptionCodes.BSE000004, new Object[] { exc.getMessage() }, exc);
        }

    }

}
