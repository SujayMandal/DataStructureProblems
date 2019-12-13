package com.ca.umg.business.tenant.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ca.umg.business.tenant.entity.AuthToken;

/**
 * @author basanaga
 *
 */
public interface AuthTokenDAO extends JpaRepository<AuthToken, String> {
    

    /**
     * This method used to get the authtoken by status
     * @param tenantId
     * @param status
     * @return
     */
    @Query(value = "SELECT at FROM AuthToken at WHERE at.status = :status and at.tenant.id = :tenantId")
    List<AuthToken> getAuthTokenByStatus(@Param("tenantId") String tenantId, @Param("status") String status);
    
}


