package com.fa.dp.business.test.bo;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class RtngTestRowMapper implements RowMapper<RtngInfo>{

    @Override
        public RtngInfo mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            RtngInfo rtngInfo = new RtngInfo();
            rtngInfo.setLoanNumber(rs.getString(RtngDBConstant.LOAN_NUMBER));
            rtngInfo.setPropertyId(rs.getString(RtngDBConstant.PROPERTY_ID));
            rtngInfo.setVendorOrderNbr(rs.getString(RtngDBConstant.VENDOR_ORDER_NBR));
            rtngInfo.setOrderIngestionDate(rs.getString(RtngDBConstant.ORDER_INGESTION_DATE));
            rtngInfo.setOrderCreatedDate(rs.getString(RtngDBConstant.ORDER_CREATED_DATE));
            rtngInfo.setProductType(rs.getString(RtngDBConstant.PRODUCT_TYPE));
            rtngInfo.setVendorOrderStatus(rs.getString(RtngDBConstant.VENDOR_ORDER_STATUS));
            rtngInfo.setInvestorCode(rs.getString(RtngDBConstant.INVESTOR_CODE));
            rtngInfo.setInvestorName(rs.getString(RtngDBConstant.INVESTOR_NAME));
            rtngInfo.setVendorFulfilledDate(rs.getString(RtngDBConstant.VENDOR_FULFILLED_DATE));
            rtngInfo.setVendorAddress1(rs.getString(RtngDBConstant.VENDOR_ADDRESS1));
            rtngInfo.setVendorAddress2(rs.getString(RtngDBConstant.VENDOR_ADDRESS2));
            rtngInfo.setVendorCity(rs.getString(RtngDBConstant.VENDOR_CITY));
            rtngInfo.setVendorState(rs.getString(RtngDBConstant.VENDOR_STATE));
            rtngInfo.setVendorZip(rs.getString(RtngDBConstant.VENDOR_ZIP));
            rtngInfo.setPropertyAddress1(rs.getString(RtngDBConstant.PROPERTY_ADDRESS1));
            rtngInfo.setPropertyAddress2(rs.getString(RtngDBConstant.PROPERTY_ADDRESS2));
            rtngInfo.setPropertyCity(rs.getString(RtngDBConstant.PROPERTY_CITY));
            rtngInfo.setPropertyState(rs.getString(RtngDBConstant.PROPERTY_STATE));
            rtngInfo.setPropertyZip(rs.getString(RtngDBConstant.PROPERTY_ZIP));
            rtngInfo.setCurrentReviewHigh(rs.getString(RtngDBConstant.CURRENT_REVIEW_HIGH));
            rtngInfo.setCurrentReviewLow(rs.getString(RtngDBConstant.CURRENT_REVIEW_LOW));
            rtngInfo.setReviewMidValue(rs.getString(RtngDBConstant.REVIEW_MID_VALUE));
            rtngInfo.setPropertyType(rs.getString(RtngDBConstant.PROPERTY_TYPE));
            rtngInfo.setAsIsLow(rs.getString(RtngDBConstant.AS_IS_LOW));
            rtngInfo.setAsIsHigh(rs.getString(RtngDBConstant.AS_IS_HIGH));
            rtngInfo.setPropertyCondition(rs.getString(RtngDBConstant.PROPERTY_CONDITION));
            rtngInfo.setSiteSize(rs.getString(RtngDBConstant.SITE_SIZE));
            rtngInfo.setGla(rs.getString(RtngDBConstant.GLA));
            rtngInfo.setRoomCount(rs.getString(RtngDBConstant.ROOM_COUNT));
            rtngInfo.setBathRoomCount(rs.getString(RtngDBConstant.BATHROOM_COUNT));
            rtngInfo.setBedRoomCount(rs.getString(RtngDBConstant.BEDROOM_COUNT));
            rtngInfo.setTotalRoomCount(rs.getString(RtngDBConstant.TOTAL_ROOM_COUNT));
            rtngInfo.setAge(rs.getString(RtngDBConstant.AGE));
            rtngInfo.setDesign(rs.getString(RtngDBConstant.DESIGN));
            rtngInfo.setRepairLow(rs.getString(RtngDBConstant.REPAIR_LOW));
            rtngInfo.setRepairHigh(rs.getString(RtngDBConstant.REPAIR_HIGH));
            rtngInfo.setSuggestLow(rs.getString(RtngDBConstant.SUGGEST_LOW));
            rtngInfo.setSuggestHigh(rs.getString(RtngDBConstant.SUGGEST_HIGH));
            rtngInfo.setSuggestRepairLow(rs.getString(RtngDBConstant.SUGGEST_REPAIR_LOW));
            rtngInfo.setSuggestRepairHigh(rs.getString(RtngDBConstant.SUGGEST_REPAIR_HIGH));
            rtngInfo.setRepairAmount1(rs.getString(RtngDBConstant.REPAIR_AMOUNT1));
            rtngInfo.setRepairAmount2(rs.getString(RtngDBConstant.REPAIR_AMOUNT2));
            rtngInfo.setRepairAmount3(rs.getString(RtngDBConstant.REPAIR_AMOUNT3));
            rtngInfo.setRepairAmount4(rs.getString(RtngDBConstant.REPAIR_AMOUNT4));
            rtngInfo.setRepairAmount5(rs.getString(RtngDBConstant.REPAIR_AMOUNT5));
            rtngInfo.setRepairAmount6(rs.getString(RtngDBConstant.REPAIR_AMOUNT6));
            rtngInfo.setRepairAmount7(rs.getString(RtngDBConstant.REPAIR_AMOUNT7));
            rtngInfo.setRepairAmount8(rs.getString(RtngDBConstant.REPAIR_AMOUNT8));
            rtngInfo.setRepairAmountTotal(rs.getString(RtngDBConstant.REPAIR_AMOUNT_TOTAL));
            rtngInfo.setIswstrat(rs.getString(RtngDBConstant.IS_WSTRAT));
            rtngInfo.setRepairedCosts(rs.getString(RtngDBConstant.REPAIRED_COSTS));
            rtngInfo.setOrderApprovedDate(rs.getString(RtngDBConstant.ORDER_APPROVED_DATE));
            rtngInfo.setReviewRepairLow(rs.getString(RtngDBConstant.REVIEW_REPAIR_LOW));
            rtngInfo.setReviewRepairHigh(rs.getString(RtngDBConstant.REVIEW_REPAIR_HIGH));
            rtngInfo.setReviewRepairMid(rs.getString(RtngDBConstant.REVIEW_REPAIR_MID));
            rtngInfo.setActionComments(rs.getString(RtngDBConstant.ACTION_COMMENTS));
            rtngInfo.setFairMarketValue(rs.getString(RtngDBConstant.FAIR_MARKET_VALUE));

            return rtngInfo;
        }
}
