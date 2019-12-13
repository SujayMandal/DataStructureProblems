package com.ca.umg.business.pooling.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ca.umg.business.pooling.entity.PoolCriteriaDefMappingEntity;

public interface PoolCriteriaDefMappingEntityDAO extends JpaRepository<PoolCriteriaDefMappingEntity, String>, JpaSpecificationExecutor<PoolCriteriaDefMappingEntity> {

	public PoolCriteriaDefMappingEntity findByPoolId(String poolId);
	
}