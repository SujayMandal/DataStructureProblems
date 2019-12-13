package com.fa.dp.business.week0.entity;

import java.math.BigDecimal;

import javax.persistence.*;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * The persistent class for the DYNAMIC_PRICING_INPUT_PARAMS database table.
 * 
 */

@Entity
@Setter
@Getter
@Table(name = "DP_WEEK0_PARAMS")
public class DPProcessParam extends AbstractAuditable {

	private static final long serialVersionUID = 3713756864557570511L;
	@Column(name = "ASSET_NUMBER")
	private String assetNumber;

	@Column(name = "PROP_TEMP")
	private String propTemp;

	@Column(name = "OLD_ASSET_NUMBER")
	private String oldAssetNumber;

	@Column(name = "ASSET_VALUE")
	private BigDecimal assetValue;

	@Column(name = "UPLOAD_FLAG")
	private String uploadFlag;

	@Column(name = "AV_SET_DATE")
	private String avSetDate;

	@Column(name = "CLASSIFICATION")
	private String classification;

	@Column(name = "CLIENT_CODE")
	private String clientCode;

	@Column(name = "LIST_PRICE")
	private BigDecimal listPrice;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "ASSIGNMENT")
	private String assignment;

	@Column(name = "ASSIGNMENT_DATE")
	private Long assignmentDate;

	@Column(name = "ELIGIBLE")
	private String eligible;

	@Column(name = "NOTES")
	private String notes;

	@Column(name = "ERROR_DETAIL")
	private String errorDetail;

	@Column(name = "PROPERTY_TYPE")
	private String propertyType;

	@Column(name = "RT_SOURCE")
	private String rtSource;

	@Column(name = "STATE")
	private String state;

	@Column(name = "WEEK_0_PRICE")
	private BigDecimal week0Price;

	@Column(name = "PCT_AV")
	private String pctAV;

	@Column(name = "WITHIN_BUSINESS_RULES")
	private String withinBusinessRules;

	// bi-directional many-to-one association to DynamicPricingFilePrcsStatus
	@ManyToOne
	@JoinColumn(name = "DYNAMIC_PRICING_FILE_ID")
	private DynamicPricingFilePrcsStatus dynamicPricingFilePrcsStatus;

	@Column(name = "ENSEMBLE")
	private String ensemble;

	@Column(name = "NOTES_RA")
	private String notesRa;

	@Column(name = "PR_MODE")
	private String prMode;

	@Column(name = "UPDATE_TIMESTAMP")
	private Long updateTimestamp;

	@ManyToOne
	@JoinColumn(name = "FAILED_STEP_COMMAND_ID", nullable = true)
	private Command command;


	@Deprecated
	//TODO to be deleted after release 3.3
	@Column(name = "WEEK0_ID")
	private String dpProcessParamOriginal;


}