package com.fa.dp.business.task.sop.weekn.ra.input;

import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.info.StageFiveInfo;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.weekN.delegate.DPSopWeekNParamDelegate;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.util.StringUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

@Slf4j
@Named
public class DpSopWeekNRAModelPreparatorImpl implements DpSopWeekNRAModelPreparator {

	public static final String DATE_TIME_FORMAT = "YYYY-MM-dd HH:mm:ss";
	public static final String STAGE5_DATE_TIME_FORMAT = "dd-MMM-YY";
	public static final String DATE_FORMAT = "MM/dd/YY";
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_TIME_FORMAT).withLocale(Locale.US)
			.withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("EST")));
	public static final DateTimeFormatter STAGE5_DATE_TIME_FORMATTER = DateTimeFormat.forPattern(STAGE5_DATE_TIME_FORMAT).withLocale(Locale.US)
			.withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("EST")));
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT).withLocale(Locale.US)
			.withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("EST")));

	private static final String REGULAR = "Regular";
	@Inject
	private DPSopWeekNParamDelegate sopWeekNParamDelegate;

	@Override
	public Map<String, Object> prepareSopWeekNRAMapping(DPSopWeekNParamInfo sopWeekNParam, String modelName, String modelMajorVersion,
			String modelMinorVersion) throws SystemException {
		Map<String, Object> resultObj = new HashMap<>();

		if(null != sopWeekNParam.getHubzuDBResponse() && null != sopWeekNParam.getHubzuDBResponse().getHubzuInfos() && null != sopWeekNParam
				.getStageFiveDBResponse() && null != sopWeekNParam.getStageFiveDBResponse().getStageFiveInfos() && NumberUtils
				.isDigits(modelMajorVersion)) {
			log.info("Creating RA Json started..");
			Map<String, Object> header;
			Map<String, Object> data = new HashMap<>();
			Map<String, List<? extends Object>> hubzuInput;

			header = new HashMap<>();
			header.put(RAClientConstants.MODEL_NAME, modelName);
			header.put(RAClientConstants.MAJOR_VERSION, NumberUtils.createInteger(modelMajorVersion));
			header.put(RAClientConstants.MINOR_VERSION,
					(StringUtils.isNotBlank(modelMinorVersion)) ? NumberUtils.createInteger(modelMinorVersion) : null);
			header.put(RAClientConstants.TRANSACTION_ID, sopWeekNParam.getAssetNumber() + RAClientConstants.SOP_WEEKN_SUFFIX);
			/*header.put("transactionMode", "Online");
			header.put("executionGroup", sopWeekNParam.getAssignment());
			header.put("user", SecurityContextHolder.getContext().getAuthentication().getName());*/
			resultObj.put(RAClientConstants.HEADER, header);

			log.info("header : " + header);

			int size = sopWeekNParam.getHubzuDBResponse().getHubzuInfos().size();

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
			List<String> conditioncdeCurrentlist = new ArrayList<>(size);
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

			List<DPSopWeek0ParamInfo> dpProcessParams = sopWeekNParamDelegate
					.fetchSopWeek0ParamsRA(sopWeekNParam.getAssetNumber(), DPProcessParamAttributes.ELIGIBLE.getValue());
			//DPSopWeekNParamInfo sopWeek0ParamInfo;
			DPSopWeek0ParamInfo sopWeek0ParamInfo;
			if(dpProcessParams.isEmpty()) {
				sopWeek0ParamInfo = null;
			} else {
				sopWeek0ParamInfo = dpProcessParams.get(0);
			}

			hubzuInput = new HashMap<>();
			StageFiveInfo firstList = null;
			StageFiveInfo currentList = null;
			for (StageFiveInfo stage5 : sopWeekNParam.getStageFiveDBResponse().getStageFiveInfos()) {
				if(stage5.getReViewDt() != null && sopWeekNParam.getHubzuDBResponse().getHubzuInfos().get(0).getCurrentListStrtDate() != null) {
					if(firstList == null && STAGE5_DATE_TIME_FORMATTER.parseDateTime(stage5.getReViewDt()).isBefore(
							DATE_TIME_FORMATTER.parseDateTime(sopWeekNParam.getHubzuDBResponse().getHubzuInfos().get(0).getCurrentListStrtDate()))) {
						firstList = stage5;
					}
					if(currentList == null && STAGE5_DATE_TIME_FORMATTER.parseDateTime(stage5.getReViewDt()).isBefore(DATE_TIME_FORMATTER
							.parseDateTime(sopWeekNParam.getHubzuDBResponse().getHubzuInfos()
									.get(sopWeekNParam.getHubzuDBResponse().getHubzuInfos().size() - 1).getCurrentListStrtDate()))) {
						currentList = stage5;
					}
				}
				if(firstList != null && currentList != null) {
					break;
				}
			}

			Double counter = 1.0;
			for (HubzuInfo ele : sopWeekNParam.getHubzuDBResponse().getHubzuInfos()) {
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
				buldDateDt.add(StringUtils.isBlank(ele.getBuldDateDt()) ? null : ele.getBuldDateDt().trim().split(RAClientConstants.CHAR_SPACE)[0]);
				reprValuNt.add(StringUtils.isBlank(ele.getReprValuNt()) ? null : Double.valueOf(ele.getReprValuNt()));
				reoPropSttsVc.add(StringUtils.isBlank(ele.getReoPropSttsVc()) ? null : ele.getReoPropSttsVc());
				reoDateDt.add(StringUtils.isBlank(ele.getReoDateDt()) ? null : ele.getReoDateDt().trim().split(RAClientConstants.CHAR_SPACE)[0]);
				propSoldDateDt.add(StringUtils.isBlank(ele.getPropSoldDateDt()) ? null : ele.getPropSoldDateDt());
				propSttsIdVcFk.add(StringUtils.isBlank(ele.getPropSttsIdVcFk()) ? null : ele.getPropSttsIdVcFk());
				rbidPropListIdVcPk.add(StringUtils.isBlank(ele.getRbidPropListIdVcPk()) ? null : ele.getRbidPropListIdVcPk());
				listTypeIdVcFk.add(StringUtils.isBlank(ele.getListTypeIdVcFk()) ? null : ele.getListTypeIdVcFk());
				rbidPropIdVcFk.add(StringUtils.isBlank(ele.getRbidPropIdVcFk()) ? null : ele.getRbidPropIdVcFk());
				listAtmpNumbNtNn.add(StringUtils.isBlank(ele.getListAtmpNumbNtNn()) ? null : Double.valueOf(ele.getListAtmpNumbNtNn()));
				currentListStrtDate.add(StringUtils.isBlank(ele.getCurrentListStrtDate()) ?
						null :
						ele.getCurrentListStrtDate().trim().split(RAClientConstants.CHAR_SPACE)[0]);
				currentListEndDate.add(StringUtils.isBlank(ele.getCurrentListEndDate()) ?
						null :
						ele.getCurrentListEndDate().trim().split(RAClientConstants.CHAR_SPACE)[0]);
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
				fallOutDateDt.add(StringUtils.isBlank(ele.getFallOutDateDt()) ?
						null :
						ele.getFallOutDateDt().trim().split(RAClientConstants.CHAR_SPACE)[0]);
				financialConsideredIndicator
						.add(StringUtils.isBlank(ele.getFinancialConsideredIndicator()) ? null : ele.getFinancialConsideredIndicator());
				cashOnlyIndicator.add(StringUtils.isBlank(ele.getCashOnlyIndicator()) ? null : ele.getCashOnlyIndicator());
				propBiddingNumbids.add(StringUtils.isBlank(ele.getPropBiddingNumbids()) ? null : Double.valueOf(ele.getPropBiddingNumbids()));
				propBiddingDistinctBidders
						.add(StringUtils.isBlank(ele.getPropBiddingDistinctBidders()) ? null : Double.valueOf(ele.getPropBiddingDistinctBidders()));
				propBiddingMaxBid.add(StringUtils.isBlank(ele.getPropBiddingMaxBid()) ? null : Double.valueOf(ele.getPropBiddingMaxBid()));
				propBiddingMinBid.add(StringUtils.isBlank(ele.getPropBiddingMinBid()) ? null : Double.valueOf(ele.getPropBiddingMinBid()));
				totalNoViews.add(StringUtils.isBlank(ele.getTotalNoViews()) ? null : Double.valueOf(ele.getTotalNoViews()));
				propBiddingDstnctWtchlst
						.add(StringUtils.isBlank(ele.getPropBiddingDstnctWtchlst()) ? null : Double.valueOf(ele.getPropBiddingDstnctWtchlst()));

				//STAGE FIVE RESPONSE PARAMETER
				actualListCycle.add(counter++);
				mostRecentListCycle.add((double) sopWeekNParam.getHubzuDBResponse().getHubzuInfos().size());

				apprtypCurrentlist.add(currentList == null ?
						RAClientConstants.M :
						StringUtils.isBlank(currentList.getApprTyp()) ? RAClientConstants.M : currentList.getApprTyp());
				asismidmktvalCurrentlist.add((currentList == null || StringUtils.isBlank(currentList.getAsIsMidMktVal())) ?
						null :
						Double.valueOf(currentList.getAsIsMidMktVal()));
				if(currentList == null)
					conditioncdeCurrentlist.add(RAClientConstants.AVERAGE);
				else {
					if(StringUtils.isBlank(currentList.getConditionCde()) || currentList.getConditionCde()
							.matches(RAClientConstants.CLIENT_CODE_AVERAGE_MATCHER))
						conditioncdeCurrentlist.add(RAClientConstants.AVERAGE);
					else if(currentList.getConditionCde().matches(RAClientConstants.CLIENT_CODE_GOOD_EXCELLENT_MATCHER))
						conditioncdeCurrentlist.add(RAClientConstants.GOOD);
					else if(currentList.getConditionCde().matches(RAClientConstants.CLIENT_CODE_FAIR_MATCHER))
						conditioncdeCurrentlist.add(RAClientConstants.FAIR);
					else
						conditioncdeCurrentlist.add(RAClientConstants.POOR);
				}
				livingareaCurrentlist.add(currentList == null ?
						0 :
						StringUtils.isBlank(currentList.getLivingArea()) ? 0 : Double.valueOf(currentList.getLivingArea()));
				totrepairamtCurrentlist.add(currentList == null ?
						0 :
						StringUtils.isBlank(currentList.getTotRepairAmt()) ? 0 : Double.valueOf(currentList.getTotRepairAmt()));

				apprtypFirstlist.add(firstList == null ?
						RAClientConstants.M :
						StringUtils.isBlank(firstList.getApprTyp()) ? RAClientConstants.M : firstList.getApprTyp());
				asismidmktvalFirstlist.add((firstList == null || StringUtils.isBlank(firstList.getAsIsMidMktVal())) ?
						null :
						Double.valueOf(firstList.getAsIsMidMktVal()));
				if(firstList == null)
					conditioncdeFirstlist.add(RAClientConstants.AVERAGE);
				else {
					if(StringUtils.isBlank(firstList.getConditionCde()) || firstList.getConditionCde()
							.matches(RAClientConstants.CLIENT_CODE_AVERAGE_MATCHER))
						conditioncdeFirstlist.add(RAClientConstants.AVERAGE);
					else if(firstList.getConditionCde().matches(RAClientConstants.CLIENT_CODE_GOOD_EXCELLENT_MATCHER))
						conditioncdeFirstlist.add(RAClientConstants.GOOD);
					else if(firstList.getConditionCde().matches(RAClientConstants.CLIENT_CODE_FAIR_MATCHER))
						conditioncdeFirstlist.add(RAClientConstants.FAIR);
					else
						conditioncdeFirstlist.add(RAClientConstants.POOR);
				}
				livingareaFirstlist
						.add(firstList == null ? 0 : StringUtils.isBlank(firstList.getLivingArea()) ? 0 : Double.valueOf(firstList.getLivingArea()));
				totrepairamtFirstlist.add(firstList == null ?
						0 :
						StringUtils.isBlank(firstList.getTotRepairAmt()) ? 0 : Double.valueOf(firstList.getTotRepairAmt()));

				//DATA From Week 0 DB
				if(firstList != null && sopWeek0ParamInfo != null) {
					if(sopWeek0ParamInfo.getAvSetDate() != null && !StringUtils.equals(sopWeek0ParamInfo.getAvSetDate() ,"")) {
						if(STAGE5_DATE_TIME_FORMATTER.parseDateTime(firstList.getReViewDt())
								.isBefore(DATE_FORMATTER.parseDateTime(sopWeek0ParamInfo.getAvSetDate()))) {
							valuationDate.add(DATE_FORMATTER.parseDateTime(sopWeek0ParamInfo.getAvSetDate())
									.toString(DateConversionUtil.US_DATE_TIME_FORMATTER));
							valuationForInitialListing.add(sopWeek0ParamInfo.getAssetValue().doubleValue());
						} else {
							valuationDate.add(STAGE5_DATE_TIME_FORMATTER
									.parseDateTime(firstList.getReViewDt().trim().split(RAClientConstants.CHAR_SPACE)[0])
									.toString(DateConversionUtil.US_DATE_TIME_FORMATTER));
							valuationForInitialListing.add(Double.valueOf(firstList.getAsIsMidMktVal()));
						}
					} else{
						Date assignDateMillis = new Date(sopWeek0ParamInfo.getAssignmentDate());
						SimpleDateFormat formatter= new SimpleDateFormat(DATE_FORMAT);
						String assignDateStr = formatter.format(assignDateMillis);
						if(STAGE5_DATE_TIME_FORMATTER.parseDateTime(firstList.getReViewDt())
								.isBefore(DATE_FORMATTER.parseDateTime(assignDateStr))) {
							valuationDate.add(DATE_FORMATTER.parseDateTime(assignDateStr)
									.toString(DateConversionUtil.US_DATE_TIME_FORMATTER));
							valuationForInitialListing.add(sopWeek0ParamInfo.getAssetValue().doubleValue());
						} else {
							valuationDate.add(STAGE5_DATE_TIME_FORMATTER
									.parseDateTime(firstList.getReViewDt().trim().split(RAClientConstants.CHAR_SPACE)[0])
									.toString(DateConversionUtil.US_DATE_TIME_FORMATTER));
							valuationForInitialListing.add(Double.valueOf(firstList.getAsIsMidMktVal()));
						}
					}
				} else if(firstList != null) {
					valuationDate.add(STAGE5_DATE_TIME_FORMATTER.parseDateTime(firstList.getReViewDt().trim().split(RAClientConstants.CHAR_SPACE)[0])
							.toString(DateConversionUtil.US_DATE_TIME_FORMATTER));
					valuationForInitialListing.add(Double.valueOf(firstList.getAsIsMidMktVal()));
				} else if(sopWeek0ParamInfo != null && StringUtils.isNotEmpty(sopWeek0ParamInfo.getAvSetDate())) {
					valuationDate
							.add(DATE_FORMATTER.parseDateTime(sopWeek0ParamInfo.getAvSetDate()).toString(DateConversionUtil.US_DATE_TIME_FORMATTER));
					valuationForInitialListing.add(sopWeek0ParamInfo.getAssetValue().doubleValue());
				} else if(sopWeek0ParamInfo != null && StringUtils.isEmpty(sopWeek0ParamInfo.getAvSetDate())) {

					Date assignDateMillis = new Date(sopWeek0ParamInfo.getAssignmentDate());
					SimpleDateFormat formatter= new SimpleDateFormat(DATE_FORMAT);
					String assignDateStr = formatter.format(assignDateMillis);
					valuationDate.add(DATE_FORMATTER.parseDateTime(assignDateStr)
							.toString(DateConversionUtil.US_DATE_TIME_FORMATTER));
					valuationForInitialListing.add(sopWeek0ParamInfo.getAssetValue().doubleValue());
				} else {
					valuationDate.add(null);
					valuationForInitialListing.add(null);
				}

				//DP-386
				Double initialValuation = valuationForInitialListing.get(valuationForInitialListing.size() - 1);
				sopWeekNParam.setInitialValuation(initialValuation != null ? BigDecimal.valueOf(initialValuation) : null);

				assignment.add(REGULAR);

			}

			hubzuInput.put(RAClientConstants.SELR_PROP_ID_VC_NN, selrPropIdVcNn);
			hubzuInput.put(RAClientConstants.SELR_ACNT_ID_VC_FK, selrAcntIdVcFk);
			hubzuInput.put(RAClientConstants.RBID_PROP_ID_VC_PK, rbidPropIdVcPk);
			hubzuInput.put(RAClientConstants.ADDRESS, address);
			hubzuInput.put(RAClientConstants.PROP_CITY_VC_FK, propCityVcFk);
			hubzuInput.put(RAClientConstants.PROP_STAT_ID_VC_FK, propStatIdVcFk);
			hubzuInput.put(RAClientConstants.PROP_ZIP_VC_FK, propZipVcFk);
			hubzuInput.put(RAClientConstants.PROP_CNTY_VC, propCntyVc);
			hubzuInput.put(RAClientConstants.PROP_SUB_TYPE_ID_VC_FK, propSubTypeIdVcFk);
			hubzuInput.put(RAClientConstants.AREA_SQUR_FEET_NM, areaSqurFeetNm);
			hubzuInput.put(RAClientConstants.LOT_SIZE_VC, lotSizeVc);
			hubzuInput.put(RAClientConstants.BDRM_CNT_NT, bdrmCntNt);
			hubzuInput.put(RAClientConstants.BTRM_CNT_NM, btrmCntNm);
			hubzuInput.put(RAClientConstants.TOTL_ROOM_CNT_NM, totlRoomCntNm);
			hubzuInput.put(RAClientConstants.BULD_DATE_DT, buldDateDt);
			hubzuInput.put(RAClientConstants.REPR_VALU_NT, reprValuNt);
			hubzuInput.put(RAClientConstants.REO_PROP_STTS_VC, reoPropSttsVc);
			hubzuInput.put(RAClientConstants.REO_DATE_DT, reoDateDt);
			hubzuInput.put(RAClientConstants.PROP_SOLD_DATE_DT, propSoldDateDt);
			hubzuInput.put(RAClientConstants.PROP_STTS_ID_VC_FK, propSttsIdVcFk);
			hubzuInput.put(RAClientConstants.RBID_PROP_LIST_ID_VC_PK, rbidPropListIdVcPk);
			hubzuInput.put(RAClientConstants.LIST_TYPE_ID_VC_FK, listTypeIdVcFk);
			hubzuInput.put(RAClientConstants.RBID_PROP_ID_VC_FK, rbidPropIdVcFk);
			hubzuInput.put(RAClientConstants.LIST_ATMP_NUMB_NT_NN, listAtmpNumbNtNn);
			hubzuInput.put(RAClientConstants.CURRENT_LIST_STRT_DATE, currentListStrtDate);
			hubzuInput.put(RAClientConstants.CURRENT_LIST_END_DATE, currentListEndDate);
			hubzuInput.put(RAClientConstants.LIST_STTS_DTLS_VC, listSttsDtlsVc);
			hubzuInput.put(RAClientConstants.PROPERTY_SOLD, propertySold);
			hubzuInput.put(RAClientConstants.ACTV_AUTO_BID, actvAutoBid);
			hubzuInput.put(RAClientConstants.CURRENT_RSRV_PRCE_NT, currentRsrvPrceNt);
			hubzuInput.put(RAClientConstants.CURRENT_LIST_PRCE_NT, currentListPrceNt);
			hubzuInput.put(RAClientConstants.HGST_BID_AMNT_NT, hgstBidAmntNt);
			hubzuInput.put(RAClientConstants.MINM_BID_AMNT_NT, minmBidAmntNt);
			hubzuInput.put(RAClientConstants.OCCPNCY_STTS_AT_LST_CREATN, occpncySttsAtLstCreatn);
			hubzuInput.put(RAClientConstants.SOP_PROGRAM_STATUS, sopProgramStatus);
			hubzuInput.put(RAClientConstants.IS_STAT_HOT_VC, isStatHotVc);
			hubzuInput.put(RAClientConstants.BUY_IT_NOW_PRCE_NT, buyItNowPrceNt);
			hubzuInput.put(RAClientConstants.RSRV_PRCE_MET_VC, rsrvPrceMetVc);
			hubzuInput.put(RAClientConstants.FALL_OUT_RESN_VC, fallOutResnVc);
			hubzuInput.put(RAClientConstants.FALL_OUT_DATE_DT, fallOutDateDt);
			hubzuInput.put(RAClientConstants.FINANCIAL_CONSIDERED_INDICATOR, financialConsideredIndicator);
			hubzuInput.put(RAClientConstants.CASH_ONLY_INDICATOR, cashOnlyIndicator);
			hubzuInput.put(RAClientConstants.PROP_BIDDING_NUMBIDS, propBiddingNumbids);
			hubzuInput.put(RAClientConstants.PROP_BIDDING_DISTINCT_BIDDERS, propBiddingDistinctBidders);
			hubzuInput.put(RAClientConstants.PROP_BIDDING_MAX_BID, propBiddingMaxBid);
			hubzuInput.put(RAClientConstants.PROP_BIDDING_MIN_BID, propBiddingMinBid);
			hubzuInput.put(RAClientConstants.TOTAL_NO_VIEWS, totalNoViews);
			hubzuInput.put(RAClientConstants.PROP_BIDDING_DSTNCT_WTCHLST, propBiddingDstnctWtchlst);

			hubzuInput.put(RAClientConstants.ACTUAL_LIST_CYCLE, actualListCycle);
			hubzuInput.put(RAClientConstants.MOST_RECENT_LIST_CYCLE, mostRecentListCycle);
			hubzuInput.put(RAClientConstants.APPRTYP_CURRENT_LIST, apprtypCurrentlist);
			hubzuInput.put(RAClientConstants.ASISMIDMKTVAL_CURRENT_LIST, asismidmktvalCurrentlist);
			hubzuInput.put(RAClientConstants.CONDITIONCDE_CURRENT_LIST, conditioncdeCurrentlist);
			hubzuInput.put(RAClientConstants.LIVINGAREA_CURRENT_LIST, livingareaCurrentlist);
			hubzuInput.put(RAClientConstants.TOTREPAIRAMT_CURRENT_LIST, totrepairamtCurrentlist);
			hubzuInput.put(RAClientConstants.APPRTYP_FIRST_LIST, apprtypFirstlist);
			hubzuInput.put(RAClientConstants.ASISMIDMKTVAL_FIRST_LIST, asismidmktvalFirstlist);
			hubzuInput.put(RAClientConstants.CONDITIONCDE_FIRST_LIST, conditioncdeCurrentlist);
			hubzuInput.put(RAClientConstants.LIVINGAREA_FIRST_LIST, livingareaFirstlist);
			hubzuInput.put(RAClientConstants.TOTREPAIRAMT_FIRST_LIST, totrepairamtFirstlist);
			hubzuInput.put(RAClientConstants.VALUATION_DATE, valuationDate);
			hubzuInput.put(RAClientConstants.VALUATION_FOR_INITIAL_LISTING, valuationForInitialListing);
			hubzuInput.put(RAClientConstants.ASSIGNMENT, assignment);

			data.put(RAClientConstants.MODEL_INPUT, hubzuInput);

			log.info(RAClientConstants.MODEL_INPUT + hubzuInput);

			resultObj.put(RAClientConstants.DATA, data);
			log.info("RA JSON created :- " + resultObj.toString());
			log.info("RA Json creation ended");
		}
		return resultObj;
	}
}
