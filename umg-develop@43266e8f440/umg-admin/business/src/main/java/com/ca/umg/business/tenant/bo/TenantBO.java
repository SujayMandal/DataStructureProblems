/**
 * 
 */
package com.ca.umg.business.tenant.bo;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.tenant.entity.SystemKey;
import com.ca.umg.business.tenant.entity.Tenant;
import com.ca.umg.business.tenant.entity.TenantConfig;

/**
 * @author kamathan
 *
 */
public interface TenantBO {

    /**
     * Returns list of tenants
     * 
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    List<Tenant> listAll() throws BusinessException, SystemException;

    /**
     * Saves tenant information
     * 
     * @return
     * @throws SystemException
     */
    Tenant save(Tenant tenant) throws SystemException, BusinessException;

    TenantConfig getTenantConfigDetails(String code, String key, String type) throws BusinessException, SystemException;

    /**
     * Get Tenant Based on name tenant information
     * 
     * @return Tenant
     * @throws SystemException
     */
    Tenant getTenant(String code) throws SystemException;

    /**
     * update tenant information
     * 
     * @return
     * @throws SystemException
     */
    Tenant update(Tenant newTenant, Tenant existingTenant) throws SystemException, BusinessException;

    /**
     * update tenant information
     * 
     * @return
     * @throws SystemException
     */
    List<SystemKey> getSystemKeys() throws SystemException;

    /**
     * gets list of all tenant codes
     * 
     * @return list of all tenant codes
     */
    List<String> getListOfTenantCodes();

    /**
     * gets name of new tenant
     * 
     * @return count of existing tenant with same name or code
     */
    long getTenantCountByNameOrCode(String name, String code) throws SystemException;

    /**
     * Deletes tenant information
     * 
     * @return
     * @throws SystemException
     */
    void delete(Tenant tenant) throws SystemException;

    /**
     * create tenant folder
     * 
     * @return
     * @throws SystemException
     */
    void tenantFoldersCreation(Tenant tenant) throws BusinessException, SystemException;

    /**
     * delete tenant folder
     * 
     * @return
     * @throws SystemException
     */
    void newTenantFoldersDeletion(Tenant tenant) throws SystemException;
}
