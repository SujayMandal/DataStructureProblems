package com.ca.umg.business.pooling.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ca.umg.business.pooling.entity.PoolUsageOrderMappingEntity;

public interface PoolUsageOrderMappingEntityDAO extends JpaRepository<PoolUsageOrderMappingEntity, String>, JpaSpecificationExecutor<PoolUsageOrderMappingEntity> {

	public List<PoolUsageOrderMappingEntity> findByPoolId(String poolId);
}