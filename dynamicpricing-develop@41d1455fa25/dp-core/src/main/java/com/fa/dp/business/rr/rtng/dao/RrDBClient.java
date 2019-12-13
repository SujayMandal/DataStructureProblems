package com.fa.dp.business.rr.rtng.dao;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.info.RrInfo;
import com.fa.dp.business.info.RrResponse;
import com.fa.dp.business.rr.rtng.constant.RrDBConstant;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.util.TransactionStatus;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

@Named
public class RrDBClient extends AbstractDBClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(RrDBClient.class);

	@Inject
	private CacheManager cacheManager;

	@Inject
	@Named(value = "rrDataSource")
	private DataSource dataSource;

	@Inject
	private DPFileProcessBO dpFileProcessBO;

	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public RrResponse execute(DPProcessParamInfo dpInfo) {
		RrResponse rrResponse = null;
		//RrResponse rrReoDateResponse = new RrResponse();
		LOGGER.info("Enter RrDBClient :: method execute");
		try {
			String selectQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_RR_QUERY);
			LOGGER.info("Query to fetch RR Response :" + selectQuery);
			dpInfo.setStartTime(
					BigInteger.valueOf(DateConversionUtil.getMillisFromUtcToEst(System.currentTimeMillis())));

			rrResponse = jdbcTemplate.execute(selectQuery, new PreparedStatementCallback<RrResponse>() {
				@Override
				public RrResponse doInPreparedStatement(final PreparedStatement ps) throws SQLException {
						LOGGER.info("Inside doInPreparedStatement.");
						ps.setString(1, dpInfo.getAssetNumber());
						return createRRInfo(ps.executeQuery(), dpInfo);
				}
			});

			 /*Calling reo date Query  for Rr Response
			 *Case 1, if First query ran correctly but Reo date is null
			 */
			//rrReoDateResponse = callReoDateRRResponseQuery(rrResponse, dpInfo);

			//removing RR Response from Dp Info which was set temporarily for REO date Call Query
			dpInfo.setRrResponse(null);

			/*if (!TransactionStatus.FAIL.getTranStatus().equals(rrReoDateResponse.getTransactionStatus())) {
				rrReoDateResponse.setTransactionStatus(TransactionStatus.SUCCESS.getTranStatus());
			}*/

		} catch (DataAccessException dae) {			
			LOGGER.info(dae.getLocalizedMessage(), dae);
			/*String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(), IntegrationType.RR_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), dae);
			dpInfo.setErrorDetail(errorDetail);
			dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());
			rrReoDateResponse = new RrResponse();
			rrReoDateResponse.setErrorMsg("Error while Executing RR/REO Date query");
			rrReoDateResponse.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());*/
		} catch (Exception e) {
			LOGGER.info(e.getLocalizedMessage(), e);
			/*String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(), IntegrationType.RR_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), e);
			dpInfo.setErrorDetail(errorDetail);
			dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());

			rrReoDateResponse = new RrResponse();
			rrReoDateResponse.setErrorMsg("Error while Executing RR/REO Date query");
			rrReoDateResponse.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());*/
		} finally {
			dpInfo.setEndTime(BigInteger.valueOf(DateConversionUtil.getMillisFromUtcToEst(System.currentTimeMillis())));
			insertRrRtngResponse(rrResponse, dpInfo);
		}
		LOGGER.info("Exit RrDBClient :: method execute");
		return rrResponse;
	}

	private RrResponse callReoDateRRResponseQuery(RrResponse rrResponse, DPProcessParamInfo dpInfo) {
		String selectQuery = (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_RR_REODATE_QUERY);
		LOGGER.info("Next Query to fetch RR Response for  empty ReoDate or first Query Failure:" + selectQuery);
		//Temporarily Setting RR Response into Dp Info
		dpInfo.setRrResponse(rrResponse);
		RrResponse rrReoDateResponse = jdbcTemplate.execute(selectQuery, new PreparedStatementCallback<RrResponse>() {
			@Override
			public RrResponse doInPreparedStatement(final PreparedStatement ps) throws SQLException {
					ps.setString(1, dpInfo.getAssetNumber());
					return createRRInfoForReoDate(ps.executeQuery(), dpInfo);
			}
		});
		return rrReoDateResponse;
	}

	private RrResponse createRRInfoForReoDate(ResultSet rs, DPProcessParamInfo dpInfo) {
		LOGGER.info("Enter RrDBClient :: method createRRInfoForReoDate");
		RrInfo rrInfo = new RrInfo();
		RrResponse response = dpInfo.getRrResponse();
		try {
			if (rs != null) {
					while (rs.next()) {
						if (rs.getRow() > 0) {
							if(Objects.isNull(dpInfo.getRrResponse().getRrInfo())) {
								rrInfo.setLoanNumber(rs.getString(RrDBConstant.LOAN_NUM));
								rrInfo.setReoDate(rs.getString(RrDBConstant.REO_DT));
								rrInfo.setUbsRes(rs.getString(RrDBConstant.UPB));
								response.setRrInfo(rrInfo);
							}else if(StringUtils.isEmpty(dpInfo.getRrResponse().getRrInfo().getReoDate())){
								response.getRrInfo().setLoanNumber(rs.getString(RrDBConstant.LOAN_NUM));
								response.getRrInfo().setReoDate(rs.getString(RrDBConstant.REO_DT));
								response.getRrInfo().setUbsRes(rs.getString(RrDBConstant.UPB));
							}
						}
					}
			}
		} catch (SQLException sqle) {
			LOGGER.debug("Something went wrong with REO Date Query ",sqle);
		}finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					LOGGER.debug("Not able to close the Result set", e);
				}
			}
		}
		LOGGER.info("Exit RrDBClient :: method createRRInfoForReoDate");
		return response;
	}

	private RrResponse createRRInfo(final ResultSet rs, DPProcessParamInfo dpInfo) {
		LOGGER.info("Enter RrDBClient :: method createRRInfo");
		RrResponse rrResponse = new RrResponse();
		RrInfo rrInfo = new RrInfo();
		Boolean recordFound = false;
		try {
			if (rs != null) {
				try {
					while (rs.next()) {
						recordFound = true;
						if (rs.getRow() > 0) {
							rrInfo.setLoanNumber(rs.getString(RrDBConstant.LOANNUMBER));
							rrInfo.setPropStrNbr(rs.getString(RrDBConstant.PROPSTRNBR));
							rrInfo.setPropStreet(rs.getString(RrDBConstant.PROPSTREET));
							rrInfo.setPropertyCity(rs.getString(RrDBConstant.PROPCITY));
							rrInfo.setPropertyState(rs.getString(RrDBConstant.STATE));
							rrInfo.setPropertyZip(rs.getString(RrDBConstant.PROPZIP));
							rrInfo.setPropertyType(rs.getString(RrDBConstant.PROPTYPE));
							rrInfo.setOrgprinbal(rs.getString(RrDBConstant.ORGPRINBAL));
							rrInfo.setCurprinbal(rs.getString(RrDBConstant.CURPRINBAL));
							rrInfo.setPipmtamt(rs.getString(RrDBConstant.PIPMTAMT));
							rrInfo.setEscrowpmt(rs.getString(RrDBConstant.ESCROWPMT));
							rrInfo.setOccpType(rs.getString(RrDBConstant.OCCPTYPE));
							rrInfo.setFairMktVal(rs.getString(RrDBConstant.FAIRMKTVAL));
							rrInfo.setCreditScore(rs.getString(RrDBConstant.CREDITSCORE));
							rrInfo.setPurchasePrice(rs.getString(RrDBConstant.PURCHASEPRICE));
							rrInfo.setCreditScoreDt(rs.getString(RrDBConstant.CREDITSCOREDT));
							rrInfo.setOrgApprval(rs.getString(RrDBConstant.ORGAPPRVAL));
							rrInfo.setReoDate(rs.getString(RrDBConstant.REO_DATE));
							rrInfo.setUbsRes(rs.getString(RrDBConstant.UPB_RES));
						}
					}
					if (recordFound) {
						rrResponse.setRrInfo(rrInfo);
					} /*else {
						rrResponse = new RrResponse();
						rrResponse.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
						rrResponse.setErrorMsg("No Record Found In RR DB for Loan Number :" + dpInfo.getAssetNumber());
						String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(), IntegrationType.RR_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), null);
						dpInfo.setErrorDetail(errorDetail);
						dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());
					}*/

				} catch (SQLException sqle) {
					LOGGER.error(sqle.getMessage());
					/*rrResponse = new RrResponse();
					rrResponse.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
					rrResponse.setErrorMsg("Error while setting Properties in RrResponse");
					String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(), IntegrationType.RR_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), sqle);
					dpInfo.setErrorDetail(errorDetail);
					dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());*/
				} finally {
					if (rs != null) {
						rs.close();
					}
				}
			}

		} catch (SQLException sqle) {
			LOGGER.debug("Something went wrong with RR Query");
			/*rrResponse = new RrResponse();
			rrResponse.setTransactionStatus(TransactionStatus.FAIL.getTranStatus());
			rrResponse.setErrorMsg("Not able to close the Result set");
			String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(dpInfo.getId(), IntegrationType.RR_INTEGRATION.getIntegrationType(), dpInfo.getErrorDetail(), sqle);
			dpInfo.setErrorDetail(errorDetail);
			dpInfo.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());*/
		}
		LOGGER.info("Exit RrDBClient :: method createRRInfo");
		return rrResponse;
	}
}
