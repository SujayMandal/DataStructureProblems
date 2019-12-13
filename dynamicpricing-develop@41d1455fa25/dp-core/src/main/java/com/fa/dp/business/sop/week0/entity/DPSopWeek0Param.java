package com.fa.dp.business.sop.week0.entity;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "DP_SOP_WEEK0_PARAMS")
public class DPSopWeek0Param extends AbstractAuditable {

	private static final long serialVersionUID = 2071382073870306249L;

	@Column(name = "ASSET_NUMBER")
	private String assetNumber;

	@Column(name = "OLD_ASSET_NUMBER")
	private String oldAssetNumber;

	@Column(name = "PROP_TEMP")
	private String propTemp;

	@Column(name = "STATE")
	private String state;

	@Column(name = "PROPERTY_TYPE")
	private String propertyType;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "ASSET_VALUE")
	private BigDecimal assetValue;

	@Column(name = "AV_SET_DATE")
	private String avSetDate;

	@Column(name = "REO_DATE")
	private String reoDate;

	@Column(name = "LIST_PRICE")
	private String listPrice;

	@Column(name = "CLASSIFICATION")
	private String classification;

	@Column(name = "ELIGIBLE")
	private String eligible;

	@Column(name = "ASSIGNMENT")
	private String assignment;

	@Column(name = "ASSIGNMENT_DATE")
	private Long assignmentDate;

	@Column(name = "NOTES")
	private String notes;

	@Column(name = "ERROR_DETAIL")
	private String errorDetail;

	@Column(name = "FAILED_STEP_COMMAND_NAME")
	private String failedStepCommandName;
	
	@ManyToOne
	@JoinColumn(name = "DP_SOP_WEEK0_FILE_ID")
	private DPSopWeek0ProcessStatus sopWeek0ProcessStatus;

	@Column(name = "UPLOAD_FLAG")
	private String uploadFlag;

}
