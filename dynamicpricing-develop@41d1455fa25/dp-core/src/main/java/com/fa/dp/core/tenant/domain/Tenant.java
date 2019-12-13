/**
 * 
 */
package com.fa.dp.core.tenant.domain;

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
@Table(name = "RA_TNT_TENANT")
public class Tenant extends AbstractAuditable {

	private static final long serialVersionUID = 7421734043158926816L;

	@Column(name = "NAME")
	private String name;

	@Column(name = "CODE")
	private String code;

	@Column(name = "AUTH_CODE")
	private String authCode;

}
