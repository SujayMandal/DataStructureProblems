/**
 * 
 */
package com.fa.dp.core.transaction.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;

/**
 *
 *
 */
@Entity
@Setter
@Getter
@Table(name = "RA_TNT_TRANSACTIONS")
public class Transaction extends AbstractAuditable {

	private static final long serialVersionUID = 2085993992450160155L;

	private String tenantCode;

	@Column(name = "CLIENT_TRANSACTION_ID")
	private String clientTransactionId;

	@Column(name = "RA_TRANSACTION_ID")
	private String raTransactionId;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "TRANSACTION_DATE")
	private long transactionDate;

	@Column(name = "USER")
	private String user;

	@Column(name = "MODEL_NAME")
	private String modelName;

	@Column(name = "MAJOR_VERSION")
	private Integer majorVersion;

	@Column(name = "MINOR_VERSION")
	private String minorVersion;

}
