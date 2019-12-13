package com.fa.dp.core.apps.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the RA_TNT_APPS database table.
 */
@Entity
@Setter
@Getter
@Table(name = "RA_TNT_APPS")
public class TenantApp extends AbstractAuditable {

	private static final long serialVersionUID = -6626475667808712233L;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "NAME")
	private String name;

	@Column(name = "CODE")
	private String code;

	@Column(name = "APP_LAUNCH_URL")
	private String appLaunchUrl;

	@Column(name = "APP_LAUNCH_PAGE")
	private String appLaunchPage;

}