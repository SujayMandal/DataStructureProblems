package com.fa.dp.business.sop.weekN.input.info;

import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.info.StageFiveDBResponse;
import com.fa.dp.core.base.info.BaseInfo;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Set;

@Data
public class DPSopWeekNParamInfo extends BaseInfo {

	private static final long serialVersionUID = 2941336272913536960L;

	private String assetNumber;

	private String oldAssetNumber;

	private String propTemp;

	private String classification;

	private String eligible;

	private String assignment;

	private String exclusionReason;

	private String rbidPropIdVcPk;

	private String state;

	private String zipCode;

	private String privateMortgageInsurance;

	private String sellerOccupiedProperty;

	private String mostRecentListEndDate;

	private String failedStepCommandName;

	// Part of story #629
	private String listStrtDateDtNn;

	private String listEndDateDtNn;

	private String mostRecentListStatus;

	private String propSoldDateDt;

	private String isPriorRecommended;

	private String dateOfLastReduction;

	private String mostRecentPropertyStatus;

	private BigDecimal mostRecentListPrice;

	private BigDecimal lpDollarAdjustmentRec;

	private BigDecimal lpPercentAdjustmentRec;

	private String clientCode;

	private String modelVersion;

	private HubzuDBResponse hubzuDBResponse;

	private StageFiveDBResponse stageFiveDBResponse;

	private String pmiCompanyInsuranceId;

	private Long deliveryDate;

	private String specialServicingFlag;

	private BigDecimal initialValuation;

	private DPSopWeekNProcessStatusInfo sopWeekNProcessStatus;

	private int lastListCycle;

}
