package com.fa.dp.business.weekn.entity;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author yogeshku
 */

@Entity
@Setter
@Getter
@Table(name = "DP_WEEKN_PARAMS")
public class DPProcessWeekNParam extends AbstractAuditable {

	private static final long serialVersionUID = 7938545633750642470L;
	@Column(name = "ASSET_NUMBER")
	private String assetNumber;
	
	@Column(name = "PROP_TEMP")
	private String propTemp;

	@Column(name = "OLD_ASSET_NUMBER")
	private String oldAssetNumber;

	@Column(name = "CLASSIFICATION")
	private String classification;

	@Column(name = "ELIGIBLE")
	private String eligible;

	@Column(name = "ASSIGNMENT")
	private String assignment;

	@Column(name = "EXCLUSION_REASON")
	private String exclusionReason;

	@Column(name = "DELIVERY_DATE")
	private Long deliveryDate;

	@Column(name = "LP_DOLLAR_ADJUSTMENT_REC")
	private BigDecimal lpDollarAdjustmentRec;

	@Column(name = "LP_PERCENT_ADJUSTMENT_REC")
	private BigDecimal lpPercentAdjustmentRec;

	@Column(name = "MODEL_VERSION")
	private String modelVersion;

	@Column(name = "MOST_RECENT_LIST_END_DATE")
	private String mostRecentListEndDate;

	@Column(name = "MOST_RECENT_LIST_PRICE")
	private BigDecimal mostRecentListPrice;

	@Column(name = "INITIAL_VALUATION")
	private BigDecimal initialValuation;

	@Column(name = "MOST_RECENT_LIST_STATUS")
	private String mostRecentListStatus;

	@Column(name = "MOST_RECENT_PROPERTY_STATUS")
	private String mostRecentPropertyStatus;

	@Column(name = "RBID_PROP_ID_VC_PK")
	private String rbidPropIdVcPk;

	@Column(name = "UPDATE_TIMESTAMP")
	private Long updateTimestamp;

	@Column(name = "STATE")
	private String state;

	@Column(name = "ZIPCODE")
	private String zipCode;

	@Column(name = "PRIVATE_MORTGAGE_INSURANCE")
	private String privateMortgageInsurance;

	@Column(name = "SELLER_OCCUPIED_PROPERTY")
	private String sellerOccupiedProperty;

	@Column(name = "LIST_PRCE_NT")
	private BigDecimal listPrceNt;

	@Column(name = "LIST_STRT_DATE_DT_NN")
	private String listStrtDateDtNn;

	@Column(name = "LIST_END_DATE_DT_NN")
	private String listEndDateDtNn;

	@Column(name = "LIST_STTS_DTLS_VC")
	private String listSttsDtlsVc;

	@Column(name = "DATE_OF_LAST_REDUCTION")
	private String dateOfLastReduction;

	@Column(name = "CLIENT_CODE")
	private String clientCode;

	@Column(name = "PROP_SOLD_DATE_DT")
	private String propSoldDateDt;

	// bi-directional many-to-one association to dpWeekNProcessStatus
	@ManyToOne
	@JoinColumn(name = "DP_WEEKN_PRCS_STATUS_ID")
	private DPWeekNProcessStatus dpWeekNProcessStatus;

	// bi-directional many-to-one association to command
	@ManyToOne
	@JoinColumn(name = "FAILED_STEP_COMMAND_ID", nullable = true)
	private Command command;

	@Deprecated
	//TODO to be deleted after release 3.3
	@Column(name = "WEEKN_ID")
	private String dpProcessWeekNParamOriginal;

	// Story 432
	@Column(name = "PRIOR_RECOMMENDED")
	private String isPriorRecommended;

}
