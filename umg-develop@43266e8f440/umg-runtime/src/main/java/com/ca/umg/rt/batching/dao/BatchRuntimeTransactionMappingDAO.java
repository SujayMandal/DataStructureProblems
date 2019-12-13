package com.ca.umg.rt.batching.dao;

import java.util.List;

import com.ca.umg.rt.batching.entity.BatchRuntimeTransactionMapping;

public interface BatchRuntimeTransactionMappingDAO {

    void save(BatchRuntimeTransactionMapping runtimeTransactionMapping);

    List<BatchRuntimeTransactionMapping> findAllByBatchId(String batchId);

	void update(BatchRuntimeTransactionMapping runtimeTransactionMapping);
}
