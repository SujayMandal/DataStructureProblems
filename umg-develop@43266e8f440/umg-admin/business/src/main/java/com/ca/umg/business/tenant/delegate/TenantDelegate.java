/**
 * 
 */
package com.ca.umg.business.tenant.delegate;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.umg.business.batching.info.BatchFileInfo;
import com.ca.umg.business.tenant.entity.SystemKey;
import com.ca.umg.business.tenant.entity.TenantConfig;

/**
 * @author kamathan
 * 
 */
public interface TenantDelegate {

	/**
	 * Returns the list of tenants
	 * 
	 * @return
	 * @throws BusinessException
	 * @throws SystemException
	 */
	List<TenantInfo> listAll() throws BusinessException, SystemException;

	/**
	 * saves tenant information
	 * 
	 * @param tenantInfo
	 * @return
	 */
	TenantInfo create(TenantInfo tenantInfo, boolean isNewSchema)
			throws SystemException;

	/**
	 * Returns the tenant config for the given tenant code, system key and
	 * tenant type.
	 * 
	 * @param code
	 * @param key
	 * @param type
	 * @return
	 * @throws BusinessException
	 * @throws SystemException
	 */
	TenantConfig getTenantConfig(String code, String key, String type)
			throws BusinessException, SystemException;
	
	
	/**
	 * Returns the value of given system key and its type.
	 * 
	 * @param key
	 * @param type
	 * @return
	 * @throws BusinessException
	 * @throws SystemException
	 */
	String getSystemKeyValue(String key, String type)
			throws BusinessException, SystemException;

    /**
     * Returns the value of given system key and its type for given tenant.
     * 
     * @param key
     * @param type
     * @param code
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    String getSystemKeyValueForTenant(String key, String type, String code) throws BusinessException, SystemException;

	/**
	 * Returns the tenant for the tenant code
	 * 
	 * @param name
	 * @return
	 * @throws BusinessException
	 * @throws SystemException
	 */
	TenantInfo getTenant(String code) throws BusinessException, SystemException;

	/**
	 * Returns the Tenant details - If the SystemKeys are not set to
	 * tenant-tenantConfig, It adds those system keys to tenant- tenant configs
	 * 
	 * @param name
	 * @return
	 * @throws BusinessException
	 * @throws SystemException
	 */
	TenantInfo getTenantWithAllSystemKeys() throws BusinessException,
	SystemException;
	
	/**
	 * Returns the required Tenant details - If the SystemKeys are not set to
	 * tenant-tenantConfig, It adds those system keys to tenant- tenant configs for the specified tenant
	 * 
	 * @param name
	 * @return
	 * @throws BusinessException
	 * @throws SystemException
	 */
	TenantInfo getTenantWithAllSystemKeys(String code) throws BusinessException,
			SystemException;
	
	/**
	 * Returns the SystemKeys 
	 * 
	 * @param name
	 * @return
	 * @throws BusinessException
	 * @throws SystemException
	 */
	List<SystemKey> getAllSystemKeys() throws BusinessException,
	SystemException;
	
	/**
	 * update tenant information
	 * 
	 * @param tenantInfo
	 * @return
	 */
	TenantInfo update(TenantInfo tenantInfo) throws SystemException,
			BusinessException;

	/**
	 * Upload text file containing request JSON(s) for asynchronous execution
	 * 
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void fileUpload(BatchFileInfo batchFileInfo) throws BusinessException,
			SystemException;

	/**
	 * This method will write a file into the SAN location location is already
	 * specified in the File object
	 */
	void writeFileToDirectory(File file, InputStream is) throws SystemException;
	
	/**
	 * This method is for Batch Deployment and all its Wrapper
	 */
    void deployBatch(String code) throws BusinessException, SystemException;
	
	/**
	 * This method is for Batch Undeployment and all its Wrapper
	 */
	void undeployBatch(String code) throws BusinessException, SystemException;
	
	/**
	 * This method checks if tenant with same name or code already exists
	 */
	public long getTenantCountByNameOrCode(String name, String code) throws SystemException;

}
