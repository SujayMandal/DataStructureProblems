/**
 * 
 */
package com.fa.dp.core.transaction.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
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
@Table(name = "RA_TNT_TRANSACTION_IO")
public class TransactionIO extends AbstractAuditable {

	private static final long serialVersionUID = -3248916384229757384L;
	
	@OneToOne
	@JoinColumn(name = "TRANSACTION_ID")
	private Transaction transaction;

	@Column(name = "TRANSACTION_INPUT")
	@Lob
	private byte[] input;

	@Column(name = "TRANSACTION_OUTPUT")
	@Lob
	private byte[] output;

}
