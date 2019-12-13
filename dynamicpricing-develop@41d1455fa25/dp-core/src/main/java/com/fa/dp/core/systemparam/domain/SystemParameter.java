/**
 * 
 */
package com.fa.dp.core.systemparam.domain;

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
@Table(name = "RA_TNT_SYSTEM_PARAMETERS")
public class SystemParameter extends AbstractAuditable {

	private static final long serialVersionUID = 8726560594294384033L;

	@Column(name = "SYS_KEY")
	private String key;

	@Column(name = "SYS_VALUE")
	private String value;

	@Column(name = "DESCRIPTION")
	private String description;

}
