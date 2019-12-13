/**
 * 
 */
package com.ca.umg.business.tenant.configurator;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.tenant.entity.Tenant;

/**
 * @author kamathan
 * @version 1.0
 */
public interface TenantConfigurator {

    void provisionSchema(Tenant tenant) throws SystemException;

    void initializeTenantDatasource(Tenant tenant) throws SystemException;
}
