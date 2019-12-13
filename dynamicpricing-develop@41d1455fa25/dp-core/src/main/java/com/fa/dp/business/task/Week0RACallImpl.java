package com.fa.dp.business.task;

import com.fa.dp.business.info.RrInfo;
import com.fa.dp.business.info.RtngInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Named
public class Week0RACallImpl implements Week0RACall {

	private static final Logger LOGGER = LoggerFactory.getLogger(Week0RACallImpl.class);

	private static final String EXEC_GROUP = "Modeled";
	private static SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

	@Override
	public Map<String, Object> prepareRAMapping(DPProcessParamInfo entry, String modelName, String modelMajorVersion,
	                                            String modelMinorVersion, String priceModeInput) {
		Map<String, Object> resultObj = new HashMap<>();

		if (null != entry.getRrResponse() || null != entry.getRtngResponse() && NumberUtils.isDigits(modelMajorVersion)) {
			LOGGER.info("Creating RA Json started..");
			Long startTime = DateTime.now().getMillis();
			Map<String, Object> header;
			Map<String, Object> data = new HashMap<>();
			Map<String, List<Object>> rrInput;
			Map<String, List<String>> rtngInput;
			Map<String, List<Object>> caInput;
			Map<String, Object> assignment;

			// Following are the fileds used in RTNG Data preparation
			List<String> loanNum = new ArrayList<>();
			List<String> propId = new ArrayList<>();
			List<String> vendorOrderNbr = new ArrayList<>();
			List<String> orderIngestnDate = new ArrayList<>();
			List<String> orderCreatedDate = new ArrayList<>();
			List<String> prodType = new ArrayList<>();
			List<String> vendorOrderStatus = new ArrayList<>();
			List<String> investorCode = new ArrayList<>();
			List<String> investorName = new ArrayList<>();
			List<String> vendorFulfilledDate = new ArrayList<>();
			List<String> vendorAddr1 = new ArrayList<>();
			List<String> vendorAddr2 = new ArrayList<>();
			List<String> vendorCity = new ArrayList<>();
			List<String> vendorState = new ArrayList<>();
			List<String> vendorZip = new ArrayList<>();
			List<String> propAddr1 = new ArrayList<>();
			List<String> propAddr2 = new ArrayList<>();
			List<String> propCity = new ArrayList<>();
			List<String> propState = new ArrayList<>();
			List<String> propZip = new ArrayList<>();
			List<String> currReviewHigh = new ArrayList<>();
			List<String> currReviewLow = new ArrayList<>();
			List<String> reviewMidValue = new ArrayList<>();
			List<String> propType = new ArrayList<>();
			List<String> asIsLow = new ArrayList<>();
			List<String> asIsHigh = new ArrayList<>();
			List<String> propCondition = new ArrayList<>();
			List<String> siteSize = new ArrayList<>();
			List<String> gla = new ArrayList<>();
			List<String> roomCount = new ArrayList<>();
			List<String> bathroomCount = new ArrayList<>();
			List<String> bedroomCount = new ArrayList<>();
			List<String> totalRoomCount = new ArrayList<>();
			List<String> age = new ArrayList<>();
			List<String> design = new ArrayList<>();
			List<String> repairLow = new ArrayList<>();
			List<String> repairHigh = new ArrayList<>();
			List<String> suggestLow = new ArrayList<>();
			List<String> suggestHigh = new ArrayList<>();
			List<String> suggestRepairLow = new ArrayList<>();
			List<String> suggestRepairHigh = new ArrayList<>();
			List<String> repairAmount1 = new ArrayList<>();
			List<String> repairAmount2 = new ArrayList<>();
			List<String> repairAmount3 = new ArrayList<>();
			List<String> repairAmount4 = new ArrayList<>();
			List<String> repairAmount5 = new ArrayList<>();
			List<String> repairAmount6 = new ArrayList<>();
			List<String> repairAmount7 = new ArrayList<>();
			List<String> repairAmount8 = new ArrayList<>();
			List<String> repairAmountTotal = new ArrayList<>();
			List<String> isWstrat = new ArrayList<>();
			List<String> repairedCosts = new ArrayList<>();
			List<String> orderApprovedDate = new ArrayList<>();
			List<String> reviewRepairLow = new ArrayList<>();
			List<String> reviewRepairHigh = new ArrayList<>();
			List<String> reviewReapairMid = new ArrayList<>();
			List<String> actionComments = new ArrayList<>();
			List<String> fairMarketValue = new ArrayList<>();

			// Preparing Header for RA Call
			header = new HashMap<>();
			header.put("modelName", modelName);
			header.put("majorVersion", NumberUtils.createInteger(modelMajorVersion));
			header.put("minorVersion",
					  (StringUtils.isNotBlank(modelMinorVersion)) ? NumberUtils.createInteger(modelMinorVersion) : null);
			data.put("priceModeInput", priceModeInput);
			header.put("transactionId", entry.getAssetNumber() + "_Week0");
			header.put("transactionMode", "Online");
			header.put("executionGroup", "Modeled");
			header.put("user", SecurityContextHolder.getContext().getAuthentication().getName());
			resultObj.put("header", header);

			LOGGER.info("header : " + header);

			// Preparing RR Data for RA Call
			rrInput = new HashMap<>();
			if (null != entry.getRrResponse().getRrInfo()) {
				RrInfo rrInfo = entry.getRrResponse().getRrInfo();
				rrInput.put("LOANNUMBER", Collections.singletonList(rrInfo.getLoanNumber()));
				rrInput.put("PROPSTRNBR", Collections.singletonList(rrInfo.getPropStrNbr()));
				rrInput.put("PROPSTREET", Collections.singletonList(rrInfo.getPropStreet()));
				rrInput.put("PROPCITY", Collections.singletonList(rrInfo.getPropertyCity()));
				rrInput.put("STATE", Collections.singletonList(entry.getState()));
				rrInput.put("PROPZIP", Collections.singletonList(rrInfo.getPropertyZip()));
				rrInput.put("PROPTYPE", Collections.singletonList(rrInfo.getPropertyType()));
				rrInput.put("ORGPRINBAL", Collections.singletonList(
						  StringUtils.isBlank(rrInfo.getOrgprinbal()) ? null : Double.parseDouble(rrInfo.getOrgprinbal())));
				rrInput.put("CURPRINBAL", Collections.singletonList(
						  StringUtils.isBlank(rrInfo.getCurprinbal()) ? null : Double.parseDouble(rrInfo.getCurprinbal())));

				rrInput.put("PIPMTAMT", Collections.singletonList(
						  StringUtils.isBlank(rrInfo.getPipmtamt()) ? null : Double.parseDouble(rrInfo.getPipmtamt())));
				rrInput.put("ESCROWPMT", Collections.singletonList(
						  StringUtils.isBlank(rrInfo.getEscrowpmt()) ? null : Double.parseDouble(rrInfo.getEscrowpmt())));
				rrInput.put("OCCPTYPE", Collections.singletonList(rrInfo.getOccpType()));
				rrInput.put("FAIRMKTVAL", Collections.singletonList(
						  StringUtils.isBlank(rrInfo.getFairMktVal()) ? null : Double.parseDouble(rrInfo.getFairMktVal())));
				rrInput.put("CREDITSCORE", Collections.singletonList(
						  StringUtils.isBlank(rrInfo.getCreditScore()) ? null : Double.parseDouble(rrInfo.getCreditScore())));
				rrInput.put("PURCHASEPRICE", Collections.singletonList(StringUtils.isBlank(rrInfo.getPurchasePrice()) ? null
						  : Double.parseDouble(rrInfo.getPurchasePrice())));
				rrInput.put("CREDITSCOREDT", Collections.singletonList(rrInfo.getCreditScoreDt()));
				rrInput.put("ORGAPPRVAL", Collections.singletonList(
						  StringUtils.isBlank(rrInfo.getOrgApprval()) ? null : Double.parseDouble(rrInfo.getOrgApprval())));
				rrInput.put("REO_DATE", Collections.singletonList(rrInfo.getReoDate()));
				rrInput.put("UPB_RES", Collections.singletonList(
						  StringUtils.isBlank(rrInfo.getUbsRes()) ? null : Double.parseDouble(rrInfo.getUbsRes())));

				data.put("rrInput", rrInput);
				LOGGER.info("rrInput : " + rrInput);
			} else {
				rrInput.put("LOANNUMBER", null);
				rrInput.put("PROPSTRNBR", null);
				rrInput.put("PROPSTREET", null);
				rrInput.put("PROPCITY", null);
				rrInput.put("STATE", null);
				rrInput.put("PROPZIP", null);
				rrInput.put("PROPTYPE", null);
				rrInput.put("ORGPRINBAL", null);
				rrInput.put("CURPRINBAL", null);

				rrInput.put("PIPMTAMT", null);
				rrInput.put("ESCROWPMT", null);
				rrInput.put("OCCPTYPE", null);
				rrInput.put("FAIRMKTVAL", null);
				rrInput.put("CREDITSCORE", null);
				rrInput.put("PURCHASEPRICE", null);
				rrInput.put("CREDITSCOREDT", null);
				rrInput.put("ORGAPPRVAL", null);
				rrInput.put("REO_DATE", null);
				rrInput.put("UPB_RES", null);

				data.put("rrInput", rrInput);
				LOGGER.info("rrInput : " + rrInput);
			}

			// Preparing RTNG input for RA
			rtngInput = new HashMap<>();
			if (null != entry.getRtngResponse().getRtngInfos()) {
				for (RtngInfo ele : entry.getRtngResponse().getRtngInfos()) {
					loanNum.add(ele.getLoanNumber());
					propId.add(StringUtils.isBlank(ele.getPropertyId()) ? null : ele.getPropertyId());
					vendorOrderNbr.add(StringUtils.isBlank(ele.getVendorOrderNbr()) ? null : ele.getVendorOrderNbr());
					orderIngestnDate
							  .add(StringUtils.isBlank(ele.getOrderIngestionDate()) ? null : ele.getOrderIngestionDate());
					orderCreatedDate.add(StringUtils.isBlank(ele.getOrderCreatedDate()) ? null : ele.getOrderCreatedDate());
					prodType.add(StringUtils.isBlank(ele.getProductType()) ? null : ele.getProductType());
					vendorOrderStatus
							  .add(StringUtils.isBlank(ele.getVendorOrderStatus()) ? null : ele.getVendorOrderStatus());
					investorCode.add(StringUtils.isBlank(ele.getInvestorCode()) ? null : ele.getInvestorCode());
					investorName.add(StringUtils.isBlank(ele.getInvestorName()) ? null : ele.getInvestorName());
					vendorFulfilledDate
							  .add(StringUtils.isBlank(ele.getVendorFulfilledDate()) ? null : ele.getVendorFulfilledDate());
					vendorAddr1.add(StringUtils.isBlank(ele.getVendorAddress1()) ? null : ele.getVendorAddress1());
					vendorAddr2.add(StringUtils.isBlank(ele.getVendorAddress2()) ? null : ele.getVendorAddress2());
					vendorCity.add(StringUtils.isBlank(ele.getVendorCity()) ? null : ele.getVendorCity());
					vendorState.add(StringUtils.isBlank(ele.getVendorState()) ? null : ele.getVendorState());
					vendorZip.add(StringUtils.isBlank(ele.getVendorZip()) ? null : ele.getVendorZip());
					propAddr1.add(StringUtils.isBlank(ele.getPropertyAddress1()) ? null : ele.getPropertyAddress1());
					propAddr2.add(StringUtils.isBlank(ele.getPropertyAddress2()) ? null : ele.getPropertyAddress2());
					propCity.add(StringUtils.isBlank(ele.getPropertyCity()) ? null : ele.getPropertyCity());
					propState.add(StringUtils.isBlank(ele.getPropertyState()) ? null : ele.getPropertyState());
					propZip.add(StringUtils.isBlank(ele.getPropertyZip()) ? null : ele.getPropertyZip());
					currReviewHigh.add(StringUtils.isBlank(ele.getCurrentReviewHigh()) ? null : ele.getCurrentReviewHigh());
					currReviewLow.add(StringUtils.isBlank(ele.getCurrentReviewLow()) ? null : ele.getCurrentReviewLow());
					reviewMidValue.add(StringUtils.isBlank(ele.getReviewMidValue()) ? null : ele.getReviewMidValue());
					propType.add(StringUtils.isBlank(ele.getPropertyType()) ? null : ele.getPropertyType());
					asIsHigh.add(StringUtils.isBlank(ele.getAsIsHigh()) ? null : ele.getAsIsHigh());
					asIsLow.add(StringUtils.isBlank(ele.getAsIsLow()) ? null : ele.getAsIsLow());
					propCondition.add(StringUtils.isBlank(ele.getPropertyCondition()) ? null : ele.getPropertyCondition());
					siteSize.add(StringUtils.isBlank(ele.getSiteSize()) ? null : ele.getSiteSize());
					gla.add(StringUtils.isBlank(ele.getGla()) ? null : ele.getGla());
					roomCount.add(StringUtils.isBlank(ele.getRoomCount()) ? null : ele.getRoomCount());
					bathroomCount.add(StringUtils.isBlank(ele.getBathRoomCount()) ? null : ele.getBathRoomCount());
					bedroomCount.add(StringUtils.isBlank(ele.getBedRoomCount()) ? null : ele.getBedRoomCount());
					totalRoomCount.add(StringUtils.isBlank(ele.getTotalRoomCount()) ? null : ele.getTotalRoomCount());
					age.add(StringUtils.isBlank(ele.getAge()) ? null : ele.getAge());
					design.add(StringUtils.isBlank(ele.getDesign()) ? null : ele.getDesign());
					repairLow.add(StringUtils.isBlank(ele.getRepairLow()) ? null : ele.getRepairLow());
					repairHigh.add(StringUtils.isBlank(ele.getRepairHigh()) ? null : ele.getRepairHigh());
					suggestLow.add(StringUtils.isBlank(ele.getSuggestLow()) ? null : ele.getSuggestLow());
					suggestHigh.add(StringUtils.isBlank(ele.getSuggestHigh()) ? null : ele.getSuggestHigh());
					suggestRepairLow.add(StringUtils.isBlank(ele.getSuggestRepairLow()) ? null : ele.getSuggestRepairLow());
					suggestRepairHigh
							  .add(StringUtils.isBlank(ele.getSuggestRepairHigh()) ? null : ele.getSuggestRepairHigh());
					repairAmount1.add(StringUtils.isBlank(ele.getRepairAmount1()) ? null : ele.getRepairAmount1());
					repairAmount2.add(StringUtils.isBlank(ele.getRepairAmount2()) ? null : ele.getRepairAmount2());
					repairAmount3.add(StringUtils.isBlank(ele.getRepairAmount3()) ? null : ele.getRepairAmount3());
					repairAmount4.add(StringUtils.isBlank(ele.getRepairAmount4()) ? null : ele.getRepairAmount4());
					repairAmount5.add(StringUtils.isBlank(ele.getRepairAmount5()) ? null : ele.getRepairAmount5());
					repairAmount6.add(StringUtils.isBlank(ele.getRepairAmount6()) ? null : ele.getRepairAmount6());
					repairAmount7.add(StringUtils.isBlank(ele.getRepairAmount7()) ? null : ele.getRepairAmount7());
					repairAmount8.add(StringUtils.isBlank(ele.getRepairAmount8()) ? null : ele.getRepairAmount8());
					repairAmountTotal
							  .add(StringUtils.isBlank(ele.getRepairAmountTotal()) ? null : ele.getRepairAmountTotal());
					isWstrat.add(StringUtils.isBlank(ele.getIswstrat()) ? null : ele.getIswstrat());
					repairedCosts.add(StringUtils.isBlank(ele.getRepairedCosts()) ? null : ele.getRepairedCosts());
					orderApprovedDate
							  .add(StringUtils.isBlank(ele.getOrderApprovedDate()) ? null : ele.getOrderApprovedDate());
					reviewRepairLow.add(StringUtils.isBlank(ele.getReviewRepairLow()) ? null : ele.getReviewRepairLow());
					reviewRepairHigh.add(StringUtils.isBlank(ele.getReviewRepairHigh()) ? null : ele.getReviewRepairHigh());
					reviewReapairMid.add(StringUtils.isBlank(ele.getReviewRepairMid()) ? null : ele.getReviewRepairMid());
					//DP-319
					actionComments.add(null);
					fairMarketValue.add(StringUtils.isBlank(ele.getFairMarketValue()) ? null : ele.getFairMarketValue());
				}
				rtngInput.put("Loan_Number", loanNum);
				rtngInput.put("Property_Id", propId);
				rtngInput.put("Vendor_Order_Nbr", vendorOrderNbr);
				rtngInput.put("Order_Ingestion_Date", orderIngestnDate);
				rtngInput.put("Order_Created_Date", orderCreatedDate);
				rtngInput.put("Product_Type", prodType);
				rtngInput.put("Vendor_Order_Status", vendorOrderStatus);
				rtngInput.put("Investor_Code", investorCode);
				rtngInput.put("Investor_Name", investorName);
				rtngInput.put("Vendor_Fulfilled_Date", vendorFulfilledDate);
				rtngInput.put("Vendor_Address1", vendorAddr1);
				rtngInput.put("Vendor_Address2", vendorAddr2);
				rtngInput.put("Vendor_City", vendorCity);
				rtngInput.put("Vendor_State", vendorState);
				rtngInput.put("Vendor_Zip", vendorZip);
				rtngInput.put("Property_Address1", propAddr1);
				rtngInput.put("Property_Address2", propAddr2);
				rtngInput.put("Property_City", propCity);
				rtngInput.put("Property_State", propState);
				rtngInput.put("Property_Zip", propZip);
				rtngInput.put("Current_Review_High", currReviewHigh);
				rtngInput.put("Current_Review_Low", currReviewLow);
				rtngInput.put("Review_Mid_Value", reviewMidValue);
				rtngInput.put("Property_Type", propType);
				rtngInput.put("As_Is_Low", asIsLow);
				rtngInput.put("As_Is_High", asIsHigh);
				rtngInput.put("Property_Condition", propCondition);
				rtngInput.put("GLA", gla);
				rtngInput.put("Site_Size", siteSize);
				rtngInput.put("Room_Count", roomCount);
				rtngInput.put("Bathroom_Count", bathroomCount);
				rtngInput.put("Bedroom_Count", bedroomCount);
				rtngInput.put("Total_Room_Count", totalRoomCount);
				rtngInput.put("Age", age);
				rtngInput.put("Design", design);
				rtngInput.put("Repair_High", repairHigh);
				rtngInput.put("Repair_Low", repairLow);
				rtngInput.put("Suggest_High", suggestHigh);
				rtngInput.put("Suggest_Low", suggestLow);
				rtngInput.put("Suggest_Repair_High", suggestRepairHigh);
				rtngInput.put("Suggest_Repair_Low", suggestRepairLow);
				rtngInput.put("Repair_Amount1", repairAmount1);
				rtngInput.put("Repair_Amount2", repairAmount2);
				rtngInput.put("Repair_Amount3", repairAmount3);
				rtngInput.put("Repair_Amount4", repairAmount4);
				rtngInput.put("Repair_Amount5", repairAmount5);
				rtngInput.put("Repair_Amount6", repairAmount6);
				rtngInput.put("Repair_Amount7", repairAmount7);
				rtngInput.put("Repair_Amount8", repairAmount8);
				rtngInput.put("Repair_Amount_Total", repairAmountTotal);
				rtngInput.put("Is_WSTRAT", isWstrat);
				rtngInput.put("Repaired_Costs", repairedCosts);
				rtngInput.put("Order_Approved_Date", orderApprovedDate);
				rtngInput.put("Review_Repair_Low", reviewRepairLow);
				rtngInput.put("Review_Repair_High", reviewRepairHigh);
				rtngInput.put("Review_Repair_Mid", reviewReapairMid);
				rtngInput.put("Action_Comments", actionComments);
				rtngInput.put("FAIR_MARKET_VALUE", fairMarketValue);

				data.put("rtngInput", rtngInput);

				LOGGER.info("rtngInput" + rtngInput);
			}

			// Preparing CA input for RA
			caInput = new HashMap<>();
			caInput.put("LoanNum", Collections.singletonList(entry.getAssetNumber()));
			// DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-mm-dd");
			// DateTime date = dateFormat.parseDateTime(entry.getTimestamp());
			caInput.put("Retro__Date", Collections.singletonList(
					  StringUtils.isBlank(entry.getTimestamp()) ? null : entry.getTimestamp().substring(0, 10)));
			caInput.put("RegOnly_AVM_Value", Collections.singletonList(
					  StringUtils.isBlank(entry.getEstimated()) ? null : Double.parseDouble(entry.getEstimated())));
			caInput.put("RegOnly_FSD_Score", Collections
					  .singletonList(StringUtils.isBlank(entry.getFsd()) ? null : Double.parseDouble(entry.getFsd())));
			caInput.put("ReoOnly_AVM_Value", Collections.singletonList(
					  StringUtils.isBlank(entry.getEstimatedREO()) ? null : Double.parseDouble(entry.getEstimatedREO())));
			caInput.put("ReoOnly_FSD_Score", Collections.singletonList(
					  StringUtils.isBlank(entry.getFsdREO()) ? null : Double.parseDouble(entry.getFsdREO())));

			data.put("caInput", caInput);

			LOGGER.info("caInput" + caInput);

			assignment = new HashMap<>();
			RtngInfo rtngInfo = entry.getRtngResponse().getRtngInfos().get(0);
			assignment.put("Loan_Number", entry.getAssetNumber());
			assignment.put("Exec_Group", EXEC_GROUP);
			assignment.put("Asset_Value", entry.getAssetValue() != null ? Double.valueOf(entry.getAssetValue()) : 0.00D);
			assignment.put("Group_Reason", entry.getEligible());
			assignment.put("Assignment_Date",
					  Objects.isNull(entry.getAssignmentDate()) ? null : entry.getAssignmentDate().equals(0) ? null : formatter.format(new Date(entry.getAssignmentDate())));
			assignment.put("Property_State",
					  StringUtils.isBlank(rtngInfo.getPropertyState()) ? null : rtngInfo.getPropertyState());
			assignment.put("Modeled_counter", Double.valueOf(entry.getModeledCount()));
			assignment.put("Benchmark_counter", Double.valueOf(entry.getBenchmarkCount()));
			assignment.put("AV_Lower_slab", Double.valueOf(entry.getLowerSlab()));
			assignment.put("AV_Higher_slab", Double.valueOf(entry.getHigherSlab()));
			assignment.put("Investor_Code",
					  StringUtils.isBlank(rtngInfo.getInvestorCode()) ? null : rtngInfo.getInvestorCode());
			assignment.put("Property_Type",
					  StringUtils.isBlank(rtngInfo.getPropertyType()) ? null : rtngInfo.getPropertyType());
			assignment.put("Review_Mid_Value", StringUtils.isBlank(rtngInfo.getReviewMidValue()) ? null
					  : Double.parseDouble(rtngInfo.getReviewMidValue()));
			assignment.put("Investor_Name",
					  StringUtils.isBlank(rtngInfo.getInvestorName()) ? null : rtngInfo.getInvestorName());
			assignment.put("RTNG_Vendor_Order_Nbr",
					  StringUtils.isBlank(rtngInfo.getVendorOrderNbr()) ? null : rtngInfo.getVendorOrderNbr());
			assignment.put("RTNG_Order_Ingestion_Date",
					  StringUtils.isBlank(rtngInfo.getOrderIngestionDate()) ? null : rtngInfo.getOrderIngestionDate());
			assignment.put("Property_City",
					  StringUtils.isBlank(rtngInfo.getPropertyCity()) ? null : rtngInfo.getPropertyCity());
			assignment.put("As_Is_High",
					  StringUtils.isBlank(rtngInfo.getAsIsHigh()) ? null : Double.valueOf(rtngInfo.getAsIsHigh()));
			assignment.put("As_Is_Low",
					  StringUtils.isBlank(rtngInfo.getAsIsLow()) ? null : Double.valueOf(rtngInfo.getAsIsLow()));

			data.put("assignment", assignment);

			LOGGER.info("assignment" + assignment);

			data.put("listingDate", null);

			resultObj.put("data", data);
			LOGGER.info("RA JSON created :- " + resultObj.toString());
			log.info("Time taken for RA Json creation ended : " + (DateTime.now().getMillis() - startTime) + "ms");
			LOGGER.info("RA Json creation ended");
		}
		return resultObj;
	}

}