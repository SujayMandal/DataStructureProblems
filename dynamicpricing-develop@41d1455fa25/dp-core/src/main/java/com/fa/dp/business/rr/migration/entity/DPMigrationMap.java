package com.fa.dp.business.rr.migration.entity;

/**
 * 
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import com.fa.dp.core.entityaudit.domain.AbstractPersistable;

/**
 * @author mandasuj
 *
 */

@Entity
@Setter
@Getter
@Table(name = "DP_MIGRATION_MAP")
public class DPMigrationMap extends AbstractPersistable {

	private static final long serialVersionUID = 3169433096353620746L;

	@Column(name = "ASSET_NUMBER")
	private String assetNumber;

	@Column(name = "OLD_ASSET_NUMBER")
	private String oldAssetNumber;

	@Column(name = "PROP_TEMP")
	private String propTemp;
}