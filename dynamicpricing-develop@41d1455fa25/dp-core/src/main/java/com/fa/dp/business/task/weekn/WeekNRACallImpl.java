package com.fa.dp.business.task.weekn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.info.StageFiveInfo;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class WeekNRACallImpl implements WeekNRACall {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WeekNRACallImpl.class);
	
	private static final String REGULAR = "Regular";
	
	public static final String DATE_TIME_FORMAT = "YYYY-MM-dd HH:mm:ss";

	public static final String STAGE5_DATE_TIME_FORMAT = "dd-MMM-YY";

	public static final String DATE_FORMAT = "MM/dd/YY";
	
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_TIME_FORMAT).withLocale(Locale.US).withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("EST")));

	public static final DateTimeFormatter STAGE5_DATE_TIME_FORMATTER = DateTimeFormat.forPattern(STAGE5_DATE_TIME_FORMAT).withLocale(Locale.US).withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("EST")));

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT).withLocale(Locale.US).withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("EST")));
	
	@Inject
	private DPFileProcessBO dpFileProcessBO;
	
	@Override
	public Map<String, Object> prepareRAMapping(DPProcessWeekNParamInfo entry, String modelName,
			String modelMajorVersion, String modelMinorVersion) {
		Map<String, Object> resultObj = new HashMap<>();

		if (null != entry.getHubzuDBResponse() && null != entry.getHubzuDBResponse().getHubzuInfos()
				&& null != entry.getStageFiveDBResponse() && null != entry.getStageFiveDBResponse().getStageFiveInfos()
				&& NumberUtils.isDigits(modelMajorVersion)) {
			LOGGER.info("Creating RA Json started..");
			Map<String, Object> header;
			Map<String, Object> data = new HashMap<>();
			Map<String, List<? extends Object>> hubzuInput;

			header = new HashMap<>();
			header.put("modelName", modelName);
			header.put("majorVersion", NumberUtils.createInteger(modelMajorVersion));
			header.put("minorVersion",
					(StringUtils.isNotBlank(modelMinorVersion)) ? NumberUtils.createInteger(modelMinorVersion) : null);
			header.put("transactionId", entry.getAssetNumber() + "_WeekN");
			/*header.put("transactionMode", "Online");
			header.put("executionGroup", entry.getAssignment());
			header.put("user", SecurityContextHolder.getContext().getAuthentication().getName());*/
			resultObj.put("header", header);

			LOGGER.info("header : " + header);
			
			int size = entry.getHubzuDBResponse().getHubzuInfos().size();
			
			List<String> selrPropIdVcNn = new ArrayList<>(size);
			List<String> selrAcntIdVcFk = new ArrayList<>(size);
			List<String> rbidPropIdVcPk = new ArrayList<>(size);
			List<String> address = new ArrayList<>(size);
			List<String> propCityVcFk = new ArrayList<>(size);
			List<String> propStatIdVcFk = new ArrayList<>(size);
			List<Double> propZipVcFk = new ArrayList<>(size);
			List<String> propCntyVc = new ArrayList<>(size);
			List<String> propSubTypeIdVcFk = new ArrayList<>(size);
			List<Double> areaSqurFeetNm = new ArrayList<>(size);
			List<Double> lotSizeVc = new ArrayList<>(size);
			List<Double> bdrmCntNt = new ArrayList<>(size);
			List<Double> btrmCntNm = new ArrayList<>(size);
			List<Double> totlRoomCntNm = new ArrayList<>(size);
			List<String> buldDateDt = new ArrayList<>(size);
			List<Double> reprValuNt = new ArrayList<>(size);
			List<String> reoPropSttsVc = new ArrayList<>(size);
			List<String> reoDateDt = new ArrayList<>(size);
			List<String> propSoldDateDt = new ArrayList<>(size);
			List<String> propSttsIdVcFk = new ArrayList<>(size);
			List<String> rbidPropListIdVcPk = new ArrayList<>(size);
			List<String> listTypeIdVcFk = new ArrayList<>(size);
			List<String> rbidPropIdVcFk = new ArrayList<>(size);
			List<Double> listAtmpNumbNtNn = new ArrayList<>(size);
			List<String> currentListStrtDate = new ArrayList<>(size);
			List<String> currentListEndDate = new ArrayList<>(size);
			List<String> listSttsDtlsVc = new ArrayList<>(size);
			List<String> propertySold = new ArrayList<>(size);
			List<Double> actvAutoBid = new ArrayList<>(size);
			List<Double> currentRsrvPrceNt = new ArrayList<>(size);
			List<Double> currentListPrceNt = new ArrayList<>(size);
			List<Double> hgstBidAmntNt = new ArrayList<>(size);
			List<Double> minmBidAmntNt = new ArrayList<>(size);
			List<String> occpncySttsAtLstCreatn = new ArrayList<>(size);
			List<String> sopProgramStatus = new ArrayList<>(size);
			List<String> isStatHotVc = new ArrayList<>(size);
			List<Double> buyItNowPrceNt = new ArrayList<>(size);
			List<String> rsrvPrceMetVc = new ArrayList<>(size);
			List<String> fallOutResnVc = new ArrayList<>(size);
			List<String> fallOutDateDt = new ArrayList<>(size);
			List<String> financialConsideredIndicator = new ArrayList<>(size);
			List<String> cashOnlyIndicator = new ArrayList<>(size);
			List<Double> propBiddingNumbids = new ArrayList<>(size);
			List<Double> propBiddingDistinctBidders = new ArrayList<>(size);
			List<Double> propBiddingMaxBid = new ArrayList<>(size);
			List<Double> propBiddingMinBid = new ArrayList<>(size);
			List<Double> totalNoViews = new ArrayList<>(size);
			List<Double> propBiddingDstnctWtchlst = new ArrayList<>(size);
			List<Double> actualListCycle = new ArrayList<>(size);
			List<Double> mostRecentListCycle = new ArrayList<>(size);
			List<String> apprtypCurrentlist = new ArrayList<>(size);
			List<Double> asismidmktvalCurrentlist = new ArrayList<>(size);
			List<String> conditioncdeCurrentlist= new ArrayList<>(size);
			List<Double> livingareaCurrentlist = new ArrayList<>(size);
			List<Double> totrepairamtCurrentlist = new ArrayList<>(size);
			List<String> apprtypFirstlist = new ArrayList<>(size);
			List<Double> asismidmktvalFirstlist = new ArrayList<>(size);
			List<String> conditioncdeFirstlist = new ArrayList<>(size);
			List<Double> livingareaFirstlist = new ArrayList<>(size);
			List<Double> totrepairamtFirstlist = new ArrayList<>(size);
			List<String> valuationDate = new ArrayList<>(size);
			List<Double> valuationForInitialListing = new ArrayList<>(size);
			List<String> assignment = new ArrayList<>(size);
			
			//get the Most recent Eligible loan from the week0 DB
			
			List<DPProcessParam> dpProcessParams = dpFileProcessBO.findByAssetNumberAndEligibleOrderByCreatedDateDesc(
					entry.getAssetNumber(), DPProcessParamAttributes.ELIGIBLE.getValue());
			DPProcessParam dpProcessParam;
			if(dpProcessParams.isEmpty()) {
				dpProcessParam = null;
			} else {
				dpProcessParam = dpProcessParams.get(0);
			}

			hubzuInput = new HashMap<>();
			StageFiveInfo firstList = null;
			StageFiveInfo currentList = null;
			for (StageFiveInfo stage5 : entry.getStageFiveDBResponse().getStageFiveInfos()) {
				if (stage5.getReViewDt() != null && entry.getHubzuDBResponse().getHubzuInfos().get(0).getCurrentListStrtDate() != null) {
					if (firstList == null && STAGE5_DATE_TIME_FORMATTER.parseDateTime(stage5.getReViewDt())
							.isBefore(DATE_TIME_FORMATTER.parseDateTime(entry.getHubzuDBResponse().getHubzuInfos().get(0).getCurrentListStrtDate()))) {
						firstList = stage5;
					}
					if (currentList == null && STAGE5_DATE_TIME_FORMATTER.parseDateTime(stage5.getReViewDt())
							.isBefore(DATE_TIME_FORMATTER.parseDateTime(entry.getHubzuDBResponse().getHubzuInfos()
									.get(entry.getHubzuDBResponse().getHubzuInfos().size() - 1).getCurrentListStrtDate()))) {
						currentList = stage5;
					}
				}
				if (firstList != null && currentList != null) {
					break;
				}
			}
			
			Double counter = 1.0;
			for (HubzuInfo ele : entry.getHubzuDBResponse().getHubzuInfos()) {
				selrPropIdVcNn.add(StringUtils.isBlank(ele.getSelrPropIdVcNn()) ? null : ele.getSelrPropIdVcNn());
				selrAcntIdVcFk.add(StringUtils.isBlank(ele.getSelrAcntIdVcFk()) ? null : ele.getSelrAcntIdVcFk());
				rbidPropIdVcPk.add(StringUtils.isBlank(ele.getRbidPropIdVcPk()) ? null : ele.getRbidPropIdVcPk());
				address.add(StringUtils.isBlank(ele.getAddress()) ? null : ele.getAddress());
				propCityVcFk.add(StringUtils.isBlank(ele.getPropCityVcFk()) ? null : ele.getPropCityVcFk());
				propStatIdVcFk.add(StringUtils.isBlank(ele.getPropStatIdVcFk()) ? null : ele.getPropStatIdVcFk());
				propZipVcFk.add(StringUtils.isBlank(ele.getPropZipVcFk()) ? null : Double.valueOf(ele.getPropZipVcFk()));
				propCntyVc.add(StringUtils.isBlank(ele.getPropCntyVc()) ? null : ele.getPropCntyVc());
				propSubTypeIdVcFk.add(StringUtils.isBlank(ele.getPropSubTypeIdVcFk()) ? null : ele.getPropSubTypeIdVcFk());
				areaSqurFeetNm.add(StringUtils.isBlank(ele.getAreaSqurFeetNm()) ? null : Double.valueOf(ele.getAreaSqurFeetNm()));
				lotSizeVc.add(StringUtils.isBlank(ele.getLotSizeVc()) ? null : Double.valueOf(ele.getLotSizeVc()));
				bdrmCntNt.add(StringUtils.isBlank(ele.getBdrmCntNt()) ? null : Double.valueOf(ele.getBdrmCntNt()));
				btrmCntNm.add(StringUtils.isBlank(ele.getBtrmCntNm()) ? null : Double.valueOf(ele.getBtrmCntNm()));
				totlRoomCntNm.add(StringUtils.isBlank(ele.getTotlRoomCntNm()) ? null : Double.valueOf(ele.getTotlRoomCntNm()));
				buldDateDt.add(StringUtils.isBlank(ele.getBuldDateDt()) ? null : ele.getBuldDateDt().trim().split(" ")[0]);
				reprValuNt.add(StringUtils.isBlank(ele.getReprValuNt()) ? null : Double.valueOf(ele.getReprValuNt()));
				reoPropSttsVc.add(StringUtils.isBlank(ele.getReoPropSttsVc()) ? null : ele.getReoPropSttsVc());
				reoDateDt.add(StringUtils.isBlank(ele.getReoDateDt()) ? null : ele.getReoDateDt().trim().split(" ")[0]);
				propSoldDateDt.add(StringUtils.isBlank(ele.getPropSoldDateDt()) ? null : ele.getPropSoldDateDt());
				propSttsIdVcFk.add(StringUtils.isBlank(ele.getPropSttsIdVcFk()) ? null : ele.getPropSttsIdVcFk());
				rbidPropListIdVcPk.add(StringUtils.isBlank(ele.getRbidPropListIdVcPk()) ? null : ele.getRbidPropListIdVcPk());
				listTypeIdVcFk.add(StringUtils.isBlank(ele.getListTypeIdVcFk()) ? null : ele.getListTypeIdVcFk());
				rbidPropIdVcFk.add(StringUtils.isBlank(ele.getRbidPropIdVcFk()) ? null : ele.getRbidPropIdVcFk());
				listAtmpNumbNtNn.add(StringUtils.isBlank(ele.getListAtmpNumbNtNn()) ? null : Double.valueOf(ele.getListAtmpNumbNtNn()));
				currentListStrtDate.add(StringUtils.isBlank(ele.getCurrentListStrtDate()) ? null : ele.getCurrentListStrtDate().trim().split(" ")[0]);
				currentListEndDate.add(StringUtils.isBlank(ele.getCurrentListEndDate()) ? null : ele.getCurrentListEndDate().trim().split(" ")[0]);
				listSttsDtlsVc.add(StringUtils.isBlank(ele.getListSttsDtlsVc()) ? null : ele.getListSttsDtlsVc());
				propertySold.add(StringUtils.isBlank(ele.getPropertySold()) ? null : ele.getPropertySold());
				actvAutoBid.add(StringUtils.isBlank(ele.getActvAutoBid()) ? null : Double.valueOf(ele.getActvAutoBid()));
				currentRsrvPrceNt.add(StringUtils.isBlank(ele.getCurrentRsrvPrceNt()) ? null : Double.valueOf(ele.getCurrentRsrvPrceNt()));
				currentListPrceNt.add(StringUtils.isBlank(ele.getCurrentListPrceNt()) ? null : Double.valueOf(ele.getCurrentListPrceNt()));
				hgstBidAmntNt.add(StringUtils.isBlank(ele.getHgstBidAmntNt()) ? null : Double.valueOf(ele.getHgstBidAmntNt()));
				minmBidAmntNt.add(StringUtils.isBlank(ele.getMinmBidAmntNt()) ? null : Double.valueOf(ele.getMinmBidAmntNt()));
				occpncySttsAtLstCreatn.add(StringUtils.isBlank(ele.getOccpncySttsAtLstCreatn()) ? null : ele.getOccpncySttsAtLstCreatn());
				sopProgramStatus.add(StringUtils.isBlank(ele.getSopProgramStatus()) ? null : ele.getSopProgramStatus());
				isStatHotVc.add(StringUtils.isBlank(ele.getIsStatHotVc()) ? null : ele.getIsStatHotVc());
				buyItNowPrceNt.add(StringUtils.isBlank(ele.getBuyItNowPrceNt()) ? null : Double.valueOf(ele.getBuyItNowPrceNt()));
				rsrvPrceMetVc.add(StringUtils.isBlank(ele.getRsrvPrceMetVc()) ? null : ele.getRsrvPrceMetVc());
				fallOutResnVc.add(StringUtils.isBlank(ele.getFallOutResnVc()) ? null : ele.getFallOutResnVc());
				fallOutDateDt.add(StringUtils.isBlank(ele.getFallOutDateDt()) ? null : ele.getFallOutDateDt().trim().split(" ")[0]);
				financialConsideredIndicator.add(StringUtils.isBlank(ele.getFinancialConsideredIndicator()) ? null : ele.getFinancialConsideredIndicator());
				cashOnlyIndicator.add(StringUtils.isBlank(ele.getCashOnlyIndicator()) ? null : ele.getCashOnlyIndicator());
				propBiddingNumbids.add(StringUtils.isBlank(ele.getPropBiddingNumbids()) ? null : Double.valueOf(ele.getPropBiddingNumbids()));
				propBiddingDistinctBidders.add(StringUtils.isBlank(ele.getPropBiddingDistinctBidders()) ? null : Double.valueOf(ele.getPropBiddingDistinctBidders()));
				propBiddingMaxBid.add(StringUtils.isBlank(ele.getPropBiddingMaxBid()) ? null : Double.valueOf(ele.getPropBiddingMaxBid()));
				propBiddingMinBid.add(StringUtils.isBlank(ele.getPropBiddingMinBid()) ? null : Double.valueOf(ele.getPropBiddingMinBid()));
				totalNoViews.add(StringUtils.isBlank(ele.getTotalNoViews()) ? null : Double.valueOf(ele.getTotalNoViews()));
				propBiddingDstnctWtchlst.add(StringUtils.isBlank(ele.getPropBiddingDstnctWtchlst()) ? null : Double.valueOf(ele.getPropBiddingDstnctWtchlst()));
				
				//STAGE FIVE RESPONSE PARAMETER
				actualListCycle.add(counter++);
				mostRecentListCycle.add((double) entry.getHubzuDBResponse().getHubzuInfos().size());
				
				apprtypCurrentlist.add(currentList == null ? "M" : StringUtils.isBlank(currentList.getApprTyp()) ? "M" : currentList.getApprTyp());
				asismidmktvalCurrentlist.add((currentList == null || StringUtils.isBlank(currentList.getAsIsMidMktVal())) ? null : Double.valueOf(currentList.getAsIsMidMktVal()));
				if(currentList == null) 
					conditioncdeCurrentlist.add("AVERAGE");
				else {
					if(StringUtils.isBlank(currentList.getConditionCde()) || currentList.getConditionCde().matches("Average|Average\r|AVERAGE"))
						conditioncdeCurrentlist.add("AVERAGE");
					else if(currentList.getConditionCde().matches("Good|Good\r|GOOD|Excellent|Excellent\r|EXCELLENT"))
						conditioncdeCurrentlist.add("GOOD");
					else if(currentList.getConditionCde().matches("Fair|Fair\r|FAIR"))
						conditioncdeCurrentlist.add("FAIR");
					else
						conditioncdeCurrentlist.add("POOR");
				}
				livingareaCurrentlist.add(currentList == null ? 0 : StringUtils.isBlank(currentList.getLivingArea()) ? 0 : Double.valueOf(currentList.getLivingArea()));
				totrepairamtCurrentlist.add(currentList == null ? 0 : StringUtils.isBlank(currentList.getTotRepairAmt()) ? 0 : Double.valueOf(currentList.getTotRepairAmt()));
				
				apprtypFirstlist.add(firstList == null ? "M" : StringUtils.isBlank(firstList.getApprTyp()) ? "M" : firstList.getApprTyp());
				asismidmktvalFirstlist.add((firstList == null || StringUtils.isBlank(firstList.getAsIsMidMktVal())) ? null : Double.valueOf(firstList.getAsIsMidMktVal()));
				if(firstList == null)
					conditioncdeFirstlist.add("AVERAGE");
				else {
					if(StringUtils.isBlank(firstList.getConditionCde()) || firstList.getConditionCde().matches("Average|Average\r|AVERAGE"))
						conditioncdeFirstlist.add("AVERAGE");
					else if(firstList.getConditionCde().matches("Good|Good\r|GOOD|Excellent|Excellent\r|EXCELLENT"))
						conditioncdeFirstlist.add("GOOD");
					else if(firstList.getConditionCde().matches("Fair|Fair\r|FAIR"))
						conditioncdeFirstlist.add("FAIR");
					else
						conditioncdeFirstlist.add("POOR");
				}
				livingareaFirstlist.add(firstList == null ? 0 : StringUtils.isBlank(firstList.getLivingArea()) ? 0 : Double.valueOf(firstList.getLivingArea()));
				totrepairamtFirstlist.add(firstList == null ? 0 : StringUtils.isBlank(firstList.getTotRepairAmt()) ? 0 : Double.valueOf(firstList.getTotRepairAmt()));
				
				
				//DATA From Week 0 DB
				if (firstList != null && dpProcessParam != null) {
					if(dpProcessParam.getAvSetDate() != null) {
						if (STAGE5_DATE_TIME_FORMATTER.parseDateTime(firstList.getReViewDt())
							.isBefore(DATE_FORMATTER.parseDateTime(dpProcessParam.getAvSetDate()))) {
							valuationDate.add(DATE_FORMATTER.parseDateTime(dpProcessParam.getAvSetDate()).toString(DateTimeFormat.forPattern("YYYY-MM-dd")));
							valuationForInitialListing.add(dpProcessParam.getAssetValue().doubleValue());
						} else {
							valuationDate.add(STAGE5_DATE_TIME_FORMATTER.parseDateTime(firstList.getReViewDt().trim().split(" ")[0]).toString(DateTimeFormat.forPattern("YYYY-MM-dd")));
							valuationForInitialListing.add(Double.valueOf(firstList.getAsIsMidMktVal()));
						}
					}
				} else if (firstList != null) {
					valuationDate.add(STAGE5_DATE_TIME_FORMATTER.parseDateTime(firstList.getReViewDt().trim().split(" ")[0]).toString(DateTimeFormat.forPattern("YYYY-MM-dd")));
					valuationForInitialListing.add(Double.valueOf(firstList.getAsIsMidMktVal()));
				} else if (dpProcessParam != null) {
					valuationDate.add(DATE_FORMATTER.parseDateTime(dpProcessParam.getAvSetDate()).toString(DateTimeFormat.forPattern("YYYY-MM-dd")));
					valuationForInitialListing.add(dpProcessParam.getAssetValue().doubleValue());
				} else {
					valuationDate.add(null);
					valuationForInitialListing.add(null);
				}
				
				//DP-386
				Double initialValuation = valuationForInitialListing.get(valuationForInitialListing.size()-1);
				entry.setInitialValuation(initialValuation != null ? BigDecimal.valueOf(initialValuation) : null);
				
				assignment.add(REGULAR);
			
			}
			
			hubzuInput.put("SELR_PROP_ID_VC_NN",selrPropIdVcNn);
			hubzuInput.put("SELR_ACNT_ID_VC_FK",selrAcntIdVcFk);
			hubzuInput.put("RBID_PROP_ID_VC_PK",rbidPropIdVcPk);
			hubzuInput.put("ADDRESS",address);
			hubzuInput.put("PROP_CITY_VC_FK",propCityVcFk);
			hubzuInput.put("PROP_STAT_ID_VC_FK",propStatIdVcFk);
			hubzuInput.put("PROP_ZIP_VC_FK",propZipVcFk);
			hubzuInput.put("PROP_CNTY_VC",propCntyVc);
			hubzuInput.put("PROP_SUB_TYPE_ID_VC_FK",propSubTypeIdVcFk);
			hubzuInput.put("AREA_SQUR_FEET_NM",areaSqurFeetNm);
			hubzuInput.put("LOT_SIZE_VC",lotSizeVc);
			hubzuInput.put("BDRM_CNT_NT",bdrmCntNt);
			hubzuInput.put("BTRM_CNT_NM",btrmCntNm);
			hubzuInput.put("TOTL_ROOM_CNT_NM",totlRoomCntNm);
			hubzuInput.put("BULD_DATE_DT",buldDateDt);
			hubzuInput.put("REPR_VALU_NT",reprValuNt);
			hubzuInput.put("REO_PROP_STTS_VC",reoPropSttsVc);
			hubzuInput.put("REO_DATE_DT",reoDateDt);
			hubzuInput.put("PROP_SOLD_DATE_DT",propSoldDateDt);
			hubzuInput.put("PROP_STTS_ID_VC_FK",propSttsIdVcFk);
			hubzuInput.put("RBID_PROP_LIST_ID_VC_PK",rbidPropListIdVcPk);
			hubzuInput.put("LIST_TYPE_ID_VC_FK",listTypeIdVcFk);
			hubzuInput.put("RBID_PROP_ID_VC_FK",rbidPropIdVcFk);
			hubzuInput.put("LIST_ATMP_NUMB_NT_NN",listAtmpNumbNtNn);
			hubzuInput.put("CURRENT_LIST_STRT_DATE",currentListStrtDate);
			hubzuInput.put("CURRENT_LIST_END_DATE",currentListEndDate);
			hubzuInput.put("LIST_STTS_DTLS_VC",listSttsDtlsVc);
			hubzuInput.put("PROPERTY_SOLD",propertySold);
			hubzuInput.put("ACTV_AUTO_BID",actvAutoBid);
			hubzuInput.put("CURRENT_RSRV_PRCE_NT",currentRsrvPrceNt);
			hubzuInput.put("CURRENT_LIST_PRCE_NT",currentListPrceNt);
			hubzuInput.put("HGST_BID_AMNT_NT",hgstBidAmntNt);
			hubzuInput.put("MINM_BID_AMNT_NT",minmBidAmntNt);
			hubzuInput.put("OCCPNCY_STTS_AT_LST_CREATN",occpncySttsAtLstCreatn);
			hubzuInput.put("SOP_PROGRAM_STATUS",sopProgramStatus);
			hubzuInput.put("IS_STAT_HOT_VC",isStatHotVc);
			hubzuInput.put("BUY_IT_NOW_PRCE_NT",buyItNowPrceNt);
			hubzuInput.put("RSRV_PRCE_MET_VC",rsrvPrceMetVc);
			hubzuInput.put("FALL_OUT_RESN_VC",fallOutResnVc);
			hubzuInput.put("FALL_OUT_DATE_DT",fallOutDateDt);
			hubzuInput.put("FINANCIAL_CONSIDERED_INDICATOR",financialConsideredIndicator);
			hubzuInput.put("CASH_ONLY_INDICATOR",cashOnlyIndicator);
			hubzuInput.put("PROP_BIDDING_NUMBIDS",propBiddingNumbids);
			hubzuInput.put("PROP_BIDDING_DISTINCT_BIDDERS",propBiddingDistinctBidders);
			hubzuInput.put("PROP_BIDDING_MAX_BID",propBiddingMaxBid);
			hubzuInput.put("PROP_BIDDING_MIN_BID",propBiddingMinBid);
			hubzuInput.put("TOTAL_NO_VIEWS",totalNoViews);
			hubzuInput.put("PROP_BIDDING_DSTNCT_WTCHLST",propBiddingDstnctWtchlst);
					
					
			hubzuInput.put("Actual_List_Cycle", actualListCycle);
			hubzuInput.put("Most_Recent_List_Cycle", mostRecentListCycle);
			hubzuInput.put("APPRTYP_CurrentList",apprtypCurrentlist);
			hubzuInput.put("ASISMIDMKTVAL_CurrentList",asismidmktvalCurrentlist);
			hubzuInput.put("CONDITIONCDE_CurrentList",conditioncdeCurrentlist);
			hubzuInput.put("LIVINGAREA_CurrentList",livingareaCurrentlist);
			hubzuInput.put("TOTREPAIRAMT_CurrentList",totrepairamtCurrentlist);
			hubzuInput.put("APPRTYP_FirstList",apprtypFirstlist);
			hubzuInput.put("ASISMIDMKTVAL_FirstList",asismidmktvalFirstlist);
			hubzuInput.put("CONDITIONCDE_FirstList",conditioncdeCurrentlist);
			hubzuInput.put("LIVINGAREA_FirstList",livingareaFirstlist);
			hubzuInput.put("TOTREPAIRAMT_FirstList",totrepairamtFirstlist);
			hubzuInput.put("VALUATION_DATE",valuationDate);
			hubzuInput.put("Valuation_for_Initial_Listing",valuationForInitialListing);
			hubzuInput.put("Assignment",assignment);

			
			data.put("modelInput", hubzuInput);

			LOGGER.info("modelInput" + hubzuInput);


			resultObj.put("data", data);
			LOGGER.info("RA JSON created :- " + resultObj.toString());
			LOGGER.info("RA Json creation ended");
		}
		return resultObj;
	}
}
