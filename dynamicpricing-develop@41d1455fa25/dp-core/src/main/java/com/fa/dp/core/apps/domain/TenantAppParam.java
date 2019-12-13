package com.fa.dp.core.apps.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the RA_TNT_APP_PARAMS database table.
 */
@Entity
@Setter
@Getter
@Table(name = "RA_TNT_APP_PARAMS")
public class TenantAppParam extends AbstractAuditable {

	private static final long serialVersionUID = 5248281671461373671L;

	@Column(name = "ATTR_KEY")
	private String attrKey;

	@Column(name = "ATTR_VALUE")
	private String attrValue;

	@Column(name = "CLASSIFICATION")
	private String classification;

	// bi-directional many-to-one association to RaTntApp
	@ManyToOne
	@JoinColumn(name = "RA_TNT_APP_ID")
	private TenantApp tenantApp;

}