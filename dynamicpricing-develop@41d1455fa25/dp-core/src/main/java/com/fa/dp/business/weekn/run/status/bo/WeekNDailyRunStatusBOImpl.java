package com.fa.dp.business.weekn.run.status.bo;

import com.fa.dp.business.db.client.HubzuDBClient;
import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.weekn.report.dao.SOPWeekNDailyQAReportDao;
import com.fa.dp.business.weekn.report.dao.WeekNDailyQAReportDao;
import com.fa.dp.business.weekn.report.entity.SOPWeekNDailyQAReport;
import com.fa.dp.business.weekn.report.entity.WeekNDailyQAReport;
import com.fa.dp.business.weekn.report.info.WeekNDailyQAReportInfo;
import com.fa.dp.business.weekn.run.status.dao.SOPWeekNDailyRunStatusDao;
import com.fa.dp.business.weekn.run.status.dao.WeekNDailyRunStatusDao;
import com.fa.dp.business.weekn.run.status.entity.SOPWeekNDailyRunStatus;
import com.fa.dp.business.weekn.run.status.entity.WeekNDailyRunStatus;
import com.fa.dp.business.weekn.run.status.info.WeekNDailyRunStatusInfo;
import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.email.service.MailDetails;
import com.fa.dp.core.email.service.MailService;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.systemparam.provider.SystemParameterProvider;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.systemparam.util.SystemParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@Slf4j
public class WeekNDailyRunStatusBOImpl extends AbstractDelegate implements WeekNDailyRunStatusBO {

	@Inject
	private WeekNDailyRunStatusDao weekNDailyRunStatusDao;

	@Inject
	private SOPWeekNDailyRunStatusDao sopWeekNDailyRunStatusDao;

	@Inject
	private WeekNDailyQAReportDao weekNDailyQAReportDao;

	@Inject
	private HubzuDBClient hubzuDBClient;

	@Inject
	private CacheManager cacheManager;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	@Value("${WEEKN_INITIAL_QUERY_IN_CLAUSE_COUNT}")
	private int initialQueryInClauseCount;

	@Override
	public LocalDate getLastRunDate() throws SystemException {
		WeekNDailyRunStatus weeknRunStatus = weekNDailyRunStatusDao.findTopByTotalRecordGreaterThanOrderByLastRunDateDesc(0);

		//DateTimeFormat.forPattern(DateConversionUtil.DATE_DD_MMM_YY).print()

		return weeknRunStatus != null && weeknRunStatus.getFetchEndDate() != null ? weeknRunStatus.getFetchEndDate() : null;
	}

	@Override
	public HubzuDBResponse fetchQaReportHubzuResponse(LocalDate startDate, LocalDate endDate, Boolean sopStatus) throws SystemException {
		log.debug("fetchQaReportHubzuResponse start date : ", startDate);
		HubzuDBResponse data = null;
		try {
			//DateTime startTime = DateTimeFormat.forPattern(DateConversionUtil.DATE_TIME_FORMAT).parseDateTime(startDate);
			//startDate = DateTimeFormat.forPattern(DateConversionUtil.DATE_DD_MMM_YY).print(startTime).toUpperCase();
			//DateTime endTime = DateTimeFormat.forPattern(DateConversionUtil.DATE_TIME_FORMAT).parseDateTime(endDate);
			//endDate = DateTimeFormat.forPattern(DateConversionUtil.DATE_DD_MMM_YY).print(endTime).toUpperCase();
			log.debug("fetchQaReportHubzuResponse new start date : ", startDate);
			log.debug("fetchQaReportHubzuResponse end date : ", endDate);
			data = hubzuDBClient.fetchQaReportHubzuResponse(startDate.format(DateConversionUtil.LOCAL_DATE_FORMATTER).toUpperCase(),
					endDate.format(DateConversionUtil.LOCAL_DATE_FORMATTER).toUpperCase(), sopStatus);
		} catch (Exception e) {
			log.error("qa report hubzu query failure : {}", e);
			SystemException.newSystemException(CoreExceptionCodes.DPRPRT002, e);
		}
		return data;
	}

	@Override
	public List<WeekNDailyQAReportInfo> fetchPreviousListingDataBySellerrPropertyId(List<String> sellerPropertyIds) throws SystemException {

		List<WeekNDailyQAReportInfo> weekNDailyQAREportList = new ArrayList<>();
		List<List<String>> splittedSellerPropertyIds = ListUtils.partition(sellerPropertyIds, initialQueryInClauseCount);

		for (List<String> subSellerPropId : splittedSellerPropertyIds) {

			log.debug("Fetching prior valid listing for seller property id list : {}", subSellerPropId.toArray());
			try {
				List<WeekNDailyQAReport> subReportList = weekNDailyQAReportDao
						.findByStatusAndSelrPropIdVcNnInOrderByCreatedDateDesc(true, subSellerPropId);
				weekNDailyQAREportList.addAll(convertToList(subReportList, WeekNDailyQAReportInfo.class));
				log.debug("subReportList size  : {}", subReportList.size());
			} catch (Exception e) {
				log.error("problem in getting previous listing data {}", e);
				SystemException.newSystemException(CoreExceptionCodes.DPRPRT003, e);
			}
		}
		return weekNDailyQAREportList;
	}

	@Override
	public void saveWeekNQaReport(List<WeekNDailyQAReportInfo> weeknQAReportList, WeekNDailyRunStatusInfo weeknRunStatusInfo) throws SystemException {
		try {
			List<WeekNDailyQAReport> qaReportList = convertToList(weeknQAReportList, WeekNDailyQAReport.class);
			qaReportList.forEach(c -> c.setWeekNDailyRunStatus(convert(weeknRunStatusInfo, WeekNDailyRunStatus.class)));
			weekNDailyQAReportDao.saveAll(qaReportList);
		} catch (Exception e) {
			log.error("problem in saving weekn daily qa report {}", e);
			SystemException.newSystemException(CoreExceptionCodes.DPRPRT004, e);
		}
	}


	@Override
	public WeekNDailyRunStatusInfo saveWeekNRunStatus(WeekNDailyRunStatusInfo weekNDailyRunStatusInfo) throws SystemException {
		WeekNDailyRunStatusInfo statusInfo = null;
		try {
			WeekNDailyRunStatus runStatus = weekNDailyRunStatusDao.save(convert(weekNDailyRunStatusInfo, WeekNDailyRunStatus.class));
			statusInfo = convert(runStatus, WeekNDailyRunStatusInfo.class);
		} catch (Exception e) {
			log.error("problem in saving weekn daily run status {}", e);
			SystemException.newSystemException(CoreExceptionCodes.DPRPRT005, e);
		}
		return statusInfo;
	}

	@Override
	public List<HubzuInfo> getMigratedHubzuResponse(List<String> assetNumberList, Map<String, String> migrationNewPropToPropMap, Boolean sopStatus)
			throws SystemException {
		List<HubzuInfo> migratedResponse = null;
		try {
			migratedResponse = hubzuDBClient.getMigratedHubzuResponse(assetNumberList, migrationNewPropToPropMap, sopStatus);
		} catch (Exception e) {
			log.error("Problem in getting migrated response from hubzu. {}", e);
			SystemException.newSystemException(CoreExceptionCodes.DPRPRT006, e);
		}
		return migratedResponse;
	}

	@Override
	public void notifyDailyRunStatus(WeekNDailyRunStatusInfo weekNDailyRunStatus, String exceptionTrace, List<String> failedLoanNumbers)
			throws SystemException {
		String smtpHostName = String
				.valueOf(systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_EMAIL_OUTPUT_SMTP_SERVER_NAME));
		String smtpServer = String
				.valueOf(systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_EMAIL_OUTPUT_SMTP_HOST_NAME));

		String toList = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_QA_REPORT_EMAIL_TO_LIST));
		String ccList = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_QA_REPORT_EMAIL_CC_LIST));
		String emailSubject = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_QA_REPORT_EMAIL_SUBJECT));
		emailSubject = MessageFormat
				.format(emailSubject, StringUtils.isEmpty(exceptionTrace) ? RAClientConstants.WEEKN_QA_SUCCESS : RAClientConstants.WEEKN_QA_FAILURE);
		String from = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_QA_REPORT_EMAIL_FROM));
		String emailBody;

		if (StringUtils.isEmpty(exceptionTrace)) {
			emailBody = MessageFormat.format(String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_QA_REPORT_EMAIL_BODY)),
					DateConversionUtil.getCurrentEstDate().toString(DateConversionUtil.US_DATE_TIME_FORMATTER), weekNDailyRunStatus.getTotalRecord(),
					weekNDailyRunStatus.getSuccessCount(), weekNDailyRunStatus.getFailCount(),
					DateConversionUtil.getCurrentEstDate().toString(DateConversionUtil.US_DATE_TIME_FORMATTER),
					StringUtils.join(failedLoanNumbers, RAClientConstants.HTML_NEXT_LINE));
		} else {
			emailBody = MessageFormat.format(String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_QA_REPORT_ERROR_EMAIL_BODY)),
					DateConversionUtil.getCurrentEstDate().toString(DateConversionUtil.US_DATE_TIME_FORMATTER), exceptionTrace)
					.replaceAll(RAClientConstants.NEXT_LINE_REGEX, RAClientConstants.HTML_NEXT_LINE);
		}

		MailDetails mailDetails = new MailDetails();
		mailDetails.setToAddress(toList);
		mailDetails.setCcAddress(ccList);
		mailDetails.setFromAddress(from);
		mailDetails.setSubject(emailSubject);
		mailDetails.setBodyText(emailBody);

		try {
			MailService.sendEMail(smtpHostName, smtpServer, mailDetails);
		} catch (SystemException e) {
			log.error("Problem in sending notification for daily qa report", e);
			SystemException.newSystemException(CoreExceptionCodes.DPRPRT001, e);
		}
	}
}
