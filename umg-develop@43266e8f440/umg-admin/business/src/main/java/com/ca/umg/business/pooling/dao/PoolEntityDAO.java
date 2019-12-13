package com.ca.umg.business.pooling.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ca.umg.business.pooling.entity.PoolEntity;

public interface PoolEntityDAO extends JpaRepository<PoolEntity, String>, JpaSpecificationExecutor<PoolEntity> {

	public PoolEntity findByPoolName(String poolName);
	
}