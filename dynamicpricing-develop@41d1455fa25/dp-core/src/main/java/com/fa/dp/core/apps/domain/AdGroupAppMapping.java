package com.fa.dp.core.apps.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fa.dp.core.adgroup.domain.ADGroup;
import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the AD_GROUP_APP_MAPPING database table.
 */
@Entity
@Setter
@Getter
@Table(name = "AD_GROUP_APP_MAPPING")
public class AdGroupAppMapping extends AbstractAuditable {

	private static final long serialVersionUID = -3091788330428491073L;

	// bi-directional many-to-one association to RaTntAdGroup
	@ManyToOne
	@JoinColumn(name = "AD_GROUP_ID")
	private ADGroup raTntAdGroup;

	// bi-directional many-to-one association to RaTntApp
	@ManyToOne
	@JoinColumn(name = "APP_ID")
	private TenantApp raTntApp;

}