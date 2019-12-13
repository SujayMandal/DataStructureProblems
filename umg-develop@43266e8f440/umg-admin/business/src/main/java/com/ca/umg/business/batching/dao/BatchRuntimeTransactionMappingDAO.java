package com.ca.umg.business.batching.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ca.umg.business.batching.entity.BatchRuntimeTransactionMapping;

public interface BatchRuntimeTransactionMappingDAO extends JpaRepository<BatchRuntimeTransactionMapping,String>{
}