package com.fa.dp.business.rr.rtng.dao;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.info.RTNGResponse;
import com.fa.dp.business.info.RtngInfo;
import com.fa.dp.business.rr.rtng.constant.RtngDBConstant;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.util.TransactionStatus;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

@Named
public class RtngDBClient extends AbstractDBClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(RtngDBClient.class);
	
	@Inject
	private CacheManager cacheManager;

	@Inject
	@Named(value = "rtngDataSource")
	private DataSource dataSource;
	
	@Inject
	private DPFileProcessBO dpFileProcessBO;

	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public RTNGResponse execute(DPProcessParamInfo dpInfo) {
		LOGGER.info("Enter RtngDBClient :: method execute");
		RTNGResponse rtngInfo = null;
		try {
			String selectQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_RTNG_QUERY);
			LOGGER.info("Query to fetch RTNG info :" + selectQuery);
			dpInfo.setStartTime(BigInteger.valueOf(DateConversionUtil.getMillisFromUtcToEst(System.currentTimeMillis())));
			rtngInfo = jdbcTemplate.execute(selectQuery, (PreparedStatementCallback<RTNGResponse>) ps -> {
				try {
					ps.setString(1, dpInfo.getAssetNumber());
					return createRTNGInfo(ps.executeQuery(), dpInfo);
				} catch (SQLException sqle) {
					LOGGER.error("SQL Exception occured", sqle);
					throw sqle;
				}
			});
			if (!TransactionStatus.FAIL.getTranStatus().equals(rtngInfo.getTransactionStatus())) {
				rtngInfo.setTransactionStatus(TransactionStatus.SUCCESS.getTranStatus());
			}
		} catch (DataAccessException dae) {
			String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(), IntegrationType.RTNG_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), dae);
			dpInfo.setErrorDetail(errorDetail);
			dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());
			LOGGER.debug(dae.getLocalizedMessage(), dae);
			rtngInfo = new RTNGResponse();
			rtngInfo.setErrorMsg("Error while Executing RTNG query");
			rtngInfo.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
		}  catch (Exception e) {
			String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(), IntegrationType.RTNG_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), e);
			dpInfo.setErrorDetail(errorDetail);
			dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());
			LOGGER.debug(e.getLocalizedMessage(), e);
			rtngInfo = new RTNGResponse();
			rtngInfo.setErrorMsg("Error while Executing RTNG query");
			rtngInfo.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
		} finally {
			dpInfo.setEndTime(BigInteger.valueOf(DateConversionUtil.getMillisFromUtcToEst(System.currentTimeMillis())));
			insertRrRtngResponse(rtngInfo, dpInfo);
		}
		LOGGER.info("Exit RtngDBClient :: method execute");
		return rtngInfo;
	}

	private RTNGResponse createRTNGInfo(final ResultSet rs, DPProcessParamInfo dpInfo) throws SQLException {
		LOGGER.info("Enter RtngDBClient :: method createRTNGInfo");
		List<RtngInfo> rtngInfos = new ArrayList<>();
		RTNGResponse rtngResponse = new RTNGResponse();
		Boolean recordFound = false;
		LOGGER.info("Resultset : " + rs);
		try {
			if (rs != null) {
				try {
					while (rs.next()) {
						LOGGER.info("Resultset 2 : " + rs);
						recordFound = true;
						if (rs.getRow() > 0) {
							LOGGER.info("Resultset 3 : " + rs);
							RtngInfo rtngInfo = convertToRtngInfo(rs);
							rtngInfos.add(rtngInfo);
						}
					}
					if (recordFound) {
						rtngResponse.setRtngInfos(rtngInfos);
					} else {
						rtngResponse = new RTNGResponse();
						rtngResponse.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
						rtngResponse
								.setErrorMsg("No Record Found In RTNG DB for Loan Number :" + dpInfo.getAssetNumber());
						String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(), IntegrationType.RTNG_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), null);
						dpInfo.setErrorDetail(errorDetail);
						dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());
					}

				} catch (SQLException sqle) {
					LOGGER.error(sqle.getLocalizedMessage(), sqle);
					rtngResponse = new RTNGResponse();
					rtngResponse.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
					rtngResponse.setErrorMsg("Error while setting Properties in RTNGResponse");
					String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(), IntegrationType.RTNG_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), sqle);
					dpInfo.setErrorDetail(errorDetail);
					dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());
				} finally {
					if (rs != null) {
						rs.close();
					}
				}
			}
		} catch (SQLException sqle) {
			LOGGER.info("Not able to close the Result set");
			rtngResponse = new RTNGResponse();
			rtngResponse.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
			rtngResponse.setErrorMsg("Not able to close the Result set");
			String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(), IntegrationType.RTNG_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), sqle);
			dpInfo.setErrorDetail(errorDetail);
			dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());
		}
		LOGGER.info("rtngInfos : " + rtngInfos);
		LOGGER.info("Exit RtngDBClient :: method createRTNGInfo");
		return rtngResponse;
	}

	private RtngInfo convertToRtngInfo(final ResultSet rs) throws SQLException {
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
