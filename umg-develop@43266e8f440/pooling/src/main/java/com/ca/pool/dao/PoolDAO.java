package com.ca.pool.dao;

import java.util.List;

import com.ca.pool.model.Pool;
import com.ca.pool.model.PoolCriteria;
import com.ca.pool.model.PoolCriteriaDefMapping;
import com.ca.pool.model.PoolUsageOrderMapping;

public interface PoolDAO {

	public abstract List<Pool> loadAllPool();

	public abstract List<PoolCriteria> loadAllPoolCriteria();

	public abstract List<PoolCriteriaDefMapping> loadAllPoolCriteriaDefMapping();

	public abstract List<PoolUsageOrderMapping> loadAllPoolUsageOrderMapping();

}