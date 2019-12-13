package com.fa.dp.business.weekn.report.info;

import com.fa.dp.business.weekn.run.status.info.WeekNDailyRunStatusInfo;
import com.fa.dp.core.base.info.BaseInfo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class WeekNDailyQAReportInfo extends BaseInfo {

	private static final long serialVersionUID = -337916360872018231L;
	private String selrPropIdVcNn;
	private String rbidPropIdVcPk;
	private String oldPropId;
	private String oldLoanNumber;
	private String reoPropSttsVc;
	private String propSoldDateDt;
	private String propSttsIdVcFk;
	private String rbidPropListIdVcPk;
	private String listTypeIdVcFk;
	private LocalDate previousListStartDate;
	private LocalDate previousListEndDate;
	private Long previousListPrice;
	private LocalDate currentListStartDate;
	private LocalDate currentListEndDate;
	private Long listPriceNt;
	private String listSttsDtlsVc;
	private String occpncySttsAtLstCreatn;
	private String actualListCycle;
	private String weeknRecommendedListPriceReduction;
	private String weeknRecommendedDate;
	private String weeknExclusionReason;
	private String pctPriceChangeFrmLastList;
	private String ruleViolation;
	private String weeknMissingreport;
	private String classification;

	private Boolean status;

	private WeekNDailyRunStatusInfo weekNDailyRunStatusInfo;

}
