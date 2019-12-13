/**
 * 
 */
package com.ca.umg.business.tenant.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ca.umg.business.tenant.entity.TenantConfig;

/**
 * Repository for {@link TenantConfig} entity.
 * 
 * @author kamathan
 * @version 1.0
 */
public interface TenantConfigDAO extends JpaRepository<TenantConfig, String> {

	TenantConfig findByTenantCodeAndSystemKeyKeyAndSystemKeyType(
			String tenantCode, String key, String type);

	/**
	 * returns the list of system key mapped to tenant
	 * 
	 * @param tenantCode
	 * @param type
	 * @return
	 */
	@Query("select u.systemKey.key from #{#entityName} u where u.tenant.code = ?1 and u.systemKey.type = ?2 and u.value = ?3")
	List<String> findPluginsForTenantCodeAndSystemKeyType(String tenantCode,
			String type, String configValue);
}
