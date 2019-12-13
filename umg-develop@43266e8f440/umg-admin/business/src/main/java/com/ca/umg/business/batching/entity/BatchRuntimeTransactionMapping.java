package com.ca.umg.business.batching.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.MultiTenantEntity;


@Entity
@Table(name = "BATCH_TXN_RUNTIME_TXN_MAPPING")
public class BatchRuntimeTransactionMapping extends MultiTenantEntity {

	private static final long serialVersionUID = 7062634040318553489L;
	
	
	@Property
    @NotNull(message = "Batch ID cannot be null.")
    @Column(name = "BATCH_ID")
	private String batchTransaction;
	
	@Property
    @NotNull(message = "Transaction ID cannot be null.")
    @Column(name = "TRANSACTION_ID")
	private String transaction;
	
	@Property
	@Column(name = "STATUS")
	private String status;
	
	@Property
	@Column(name = "ERROR")
	private String error;

	public String getBatchTransaction() {
		return batchTransaction;
	}

	public void setBatchTransaction(String batchTransaction) {
		this.batchTransaction = batchTransaction;
	}

	public String getTransaction() {
		return transaction;
	}

	public void setTransaction(String transaction) {
		this.transaction = transaction;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	
	
}
