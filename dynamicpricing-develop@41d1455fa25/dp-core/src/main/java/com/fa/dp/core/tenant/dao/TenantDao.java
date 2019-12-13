/**
 * 
 */
package com.fa.dp.core.tenant.dao;

import com.fa.dp.core.tenant.domain.Tenant;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 *
 */
public interface TenantDao extends JpaRepository<Tenant, String> {

    public Tenant findByCode(String code);

}
