/**
 *
 */
package com.fa.dp.core.model.domain;

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
@Table(name = "RA_TNT_MODEL_DETAIL")
public class ModelDetail extends AbstractAuditable {

	private static final long serialVersionUID = -7769345663676970834L;

	@Column(name = "NAME")
	private String name;

	@Column(name = "MAJOR_VERSION")
	private Integer majorVersion;

	@Column(name = "MINOR_VERSION")
	private String minorVersion;

	@Column(name = "DESCRIPTION")
	private String description;

	@ManyToOne
	@JoinColumn(name = "TENANT_ID")
	private Tenant tenant;

}
