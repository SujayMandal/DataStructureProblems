/**
 * 
 */
package com.fa.dp.core.transaction.dao;

import com.fa.dp.core.transaction.domain.Transaction;
import com.fa.dp.core.transaction.domain.TransactionIO;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 *
 */
public interface TransactionIODao extends JpaRepository<TransactionIO, String> {

    public TransactionIO findByTransaction(Transaction transaction);
}
