package com.fa.dp.business.sop.weekN.entity;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Setter
@Getter
@Table(name = "DP_SOP_WEEKN_PARAMS")
public class DPSopWeekNParam extends AbstractAuditable {

	private static final long serialVersionUID = 6669083187739530160L;

	@Column(name = "ASSET_NUMBER")
	private String assetNumber;

	@Column(name = "OLD_ASSET_NUMBER")
	private String oldAssetNumber;

	@Column(name = "PROP_TEMP")
	private String propTemp;

	@Column(name = "CLASSIFICATION")
	private String classification;

	@Column(name = "ELIGIBLE")
	private String eligible;

	@Column(name = "ASSIGNMENT")
	private String assignment;

	@Column(name = "EXCLUSION_REASON")
	private String exclusionReason;

	@Column(name = "RBID_PROP_ID_VC_PK")
	private String rbidPropIdVcPk;

	@Column(name = "STATE")
	private String state;

	@Column(name = "ZIPCODE")
	private String zipCode;

	@Column(name = "PRIVATE_MORTGAGE_INSURANCE")
	private String privateMortgageInsurance;

	@Column(name = "SELLER_OCCUPIED_PROPERTY")
	private String sellerOccupiedProperty;

	@Column(name = "MOST_RECENT_LIST_END_DATE")
	private String mostRecentListEndDate;

	@Column(name = "FAILED_STEP_COMMAND_NAME")
	private String failedStepCommandName;

	// Part of story #629
	@Column(name = "LIST_STRT_DATE_DT_NN")
	private String listStrtDateDtNn;

	@Column(name = "LIST_END_DATE_DT_NN")
	private String listEndDateDtNn;

	@Column(name = "MOST_RECENT_LIST_STATUS")
	private String mostRecentListStatus;

	@Column(name = "PROP_SOLD_DATE_DT")
	private String propSoldDateDt;

	@Column(name = "PRIOR_RECOMMENDED")
	private String isPriorRecommended;

	@Column(name = "DATE_OF_LAST_REDUCTION")
	private String dateOfLastReduction;

	@Column(name = "MOST_RECENT_PROPERTY_STATUS")
	private String mostRecentPropertyStatus;

	@Column(name = "MOST_RECENT_LIST_PRICE")
	private BigDecimal mostRecentListPrice;

	@Column(name = "LP_DOLLAR_ADJUSTMENT_REC")
	private BigDecimal lpDollarAdjustmentRec;

	@Column(name = "LP_PERCENT_ADJUSTMENT_REC")
	private BigDecimal lpPercentAdjustmentRec;

	@Column(name = "CLIENT_CODE")
	private String clientCode;

	@Column(name = "MODEL_VERSION")
	private String modelVersion;

	@Column(name = "DELIVERY_DATE")
	private Long deliveryDate;

	@Column(name = "INITIAL_VALUATION")
	private BigDecimal initialValuation;

	// bi-directional many-to-one association to dpWeekNProcessStatus
	@ManyToOne
	@JoinColumn(name = "DP_SOP_WEEKN_FILE_ID")
	private DPSopWeekNProcessStatus sopWeekNProcessStatus;


}
