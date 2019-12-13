package com.fa.dp.business.weekn.input.info;

import com.fa.dp.business.base.input.info.DPProcessParamBaseInfo;
import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.info.StageFiveDBResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class DPProcessWeekNParamInfo extends DPProcessParamBaseInfo {

	private static final long serialVersionUID = 150258652261617586L;

	private String exclusionReason;

	private Long deliveryDate;

	private BigDecimal lpDollarAdjustmentRec;

	private BigDecimal lpPercentAdjustmentRec;

	private String modelVersion;

	private String mostRecentListEndDate;

	private BigDecimal mostRecentListPrice;

	private BigDecimal initialValuation;

	private String mostRecentListStatus;

	private String mostRecentPropertyStatus;

	private String rbidPropIdVcPk;

	private String zipCode;

	private String listEndDateDtNnstart;

	private String listEndDateDtNnend;

	private Set<String> naLoanNumber;

	private DPWeekNProcessStatusInfo dpWeekNProcessStatus;

	private HubzuDBResponse hubzuDBResponse;

	private HubzuDBResponse ssPmiHubzuResponse;

	private StageFiveDBResponse stageFiveDBResponse;

	// Story 78
	private String privateMortgageInsurance;

	private String sellerOccupiedProperty;

	private String listPrceNt;

	private String listStrtDateDtNn;

	private String listEndDateDtNn;

	private String listSttsDtlsVc;

	private String dateOfLastReduction;

	// Story 125
	private int lastListCycle;

	// Story 172
	private String autoRLSTVc;

	// Story 328
	private String specialServicingFlag;

	private String pmiCompanyInsuranceId;

	// Story 432
	private String isPriorRecommended;

}
