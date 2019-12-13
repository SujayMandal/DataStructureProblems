/**
 * 
 */
package com.fa.dp.core.adgroup.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;
import com.fa.dp.core.tenant.domain.Tenant;

import lombok.Getter;
import lombok.Setter;

/**
 *
 *
 */
@Entity
@Setter
@Getter
@Table(name = "RA_TNT_AD_GROUP")
public class ADGroup extends AbstractAuditable {

	private static final long serialVersionUID = 8502947132235573112L;
	@Column(name = "GROUP_NAME")
	private String name;

	@ManyToOne
	@JoinColumn(name = "TENANT_ID")
	private Tenant tenant;

}
