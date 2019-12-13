package com.fa.dp.core.transaction.dao;

import java.util.List;

import com.fa.dp.core.transaction.domain.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionDao extends JpaRepository<Transaction, String>, JpaSpecificationExecutor<Transaction> {

    public List<Transaction> findByStatus(String status);
    
    public List<Transaction>findBymodelNameIn(String modelName);

}
