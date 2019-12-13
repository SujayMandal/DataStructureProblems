package com.fa.dp.business.info;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class HubzuInfo implements Serializable {

	private static final long serialVersionUID = -7515173310265310793L;
	private String selrPropIdVcNn;

	private String oldPropId;

	private String oldLoanNumber;

	private String selrAcntIdVcFk;

	private String rbidPropIdVcPk;

	private String address;

	private String propCityVcFk;

	private String propStatIdVcFk;

	private String propZipVcFk;

	private String propCntyVc;

	private String propSubTypeIdVcFk;

	private String areaSqurFeetNm;

	private String lotSizeVc;

	private String bdrmCntNt;

	private String btrmCntNm;

	private String totlRoomCntNm;

	private String buldDateDt;

	private String reprValuNt;

	private String reoPropSttsVc;

	private String reoDateDt;

	private String propSoldDateDt;

	private String propSttsIdVcFk;

	private String rbidPropListIdVcPk;

	private String listTypeIdVcFk;

	private String rbidPropIdVcFk;

	private String listAtmpNumbNtNn;

	private String currentListStrtDate;

	private String currentListEndDate;

	private String listSttsDtlsVc;

	private String propertySold;

	private String actvAutoBid;

	private String currentRsrvPrceNt;

	private String currentListPrceNt;

	private String hgstBidAmntNt;

	private String minmBidAmntNt;

	private String occpncySttsAtLstCreatn;

	private String sopProgramStatus;

	private String isStatHotVc;

	private String buyItNowPrceNt;

	private String rsrvPrceMetVc;

	private String fallOutResnVc;

	private String fallOutDateDt;

	private String financialConsideredIndicator;

	private String cashOnlyIndicator;

	private String propBiddingNumbids;

	private String propBiddingDistinctBidders;

	private String propBiddingMaxBid;

	private String propBiddingMinBid;

	private String totalNoViews;

	private String propBiddingDstnctWtchlst;

	private String clntCodeVc;

	private String isSpclHndlPropVc;

	// Story 78
	private String ss;

	private String pmi;

	private String sop;

	private Long listPrceNt;

	private Date listStrtDateDtNn;

	private Date listEndDateDtNn;

	private Date assignmentDate;

	private String dateOfLastReduction;

	// Story 125
	private int lastListCycle;

	// Story 176
	private String autoRLSTVc;

}
