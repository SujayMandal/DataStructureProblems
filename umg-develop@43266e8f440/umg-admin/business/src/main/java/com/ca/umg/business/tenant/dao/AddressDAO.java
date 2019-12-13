/**
 * 
 */
package com.ca.umg.business.tenant.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ca.umg.business.tenant.entity.Address;
import com.ca.umg.business.tenant.entity.Tenant;

/**
 * Repository for {@link Address} entity.
 * 
 * @author kamathan
 * @version 1.0
 */
public interface AddressDAO extends JpaRepository<Address, String> {

    /**
     * Returns the addresses of a given tenant.
     * 
     * @param tenant
     * @return
     */
    List<Address> findByTenant(Tenant tenant);
}
