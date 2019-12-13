package com.ca.umg.business.transaction.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.ca.umg.business.transaction.entity.Transaction;

public interface TransactionDAO extends JpaRepository<Transaction, String>, JpaSpecificationExecutor<Transaction> {
	
	/**
	 * @return Return the maximum available date.
	 */
	@Query("select max(u.runAsOfDate) from #{#entityName} u")
	Long findMaxRunAsOfDate();
    /**
     * Gets all the distinct Library Names.
     * 
     * @return
     */
    @Query("select distinct(u.libraryName) from #{#entityName} u")
    List<String> findAllLibraries();

    /**
     * Gets all the distinct tenant model Names.
     * 
     * @return
     */
    @Query("select distinct(u.tenantModelName) from #{#entityName} u")
    List<String> findAllTenantModelNames();

}
