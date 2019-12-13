package com.ca.umg.business.tenant.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ca.umg.business.tenant.entity.Tenant;

/**
 * Spring Data JPA repository for {@link Tenant} entity
 * 
 * @author devasiaa
 * 
 */
public interface TenantDAO extends JpaRepository<Tenant, String> {

    Tenant findByCode(String code);

    Tenant findByName(String name);
    
    long countByNameIgnoreCaseOrCodeIgnoreCase(String name, String code);

}
