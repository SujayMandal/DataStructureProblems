package com.ca.umg.business.batching.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ca.umg.business.batching.entity.BatchTransaction;

public interface BatchTransactionDAO extends
		JpaRepository<BatchTransaction, String>,
		JpaSpecificationExecutor<BatchTransaction> {

	public List<BatchTransaction> findByBatchInputFile(String batchFileName);

}
