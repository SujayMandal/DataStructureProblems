package com.fa.dp.business.weekn.run.status.bo;

import com.fa.dp.business.weekn.report.dao.SOPWeekNDailyQAReportDao;
import com.fa.dp.business.weekn.report.entity.SOPWeekNDailyQAReport;
import com.fa.dp.business.weekn.report.info.WeekNDailyQAReportInfo;
import com.fa.dp.business.weekn.run.status.dao.SOPWeekNDailyRunStatusDao;
import com.fa.dp.business.weekn.run.status.entity.SOPWeekNDailyRunStatus;
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
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

@Named
@Slf4j
public class SOPWeekNDailyRunStatusBOImpl extends AbstractDelegate implements SOPWeekNDailyRunStatusBO {

    @Inject
    private SOPWeekNDailyRunStatusDao sopWeekNDailyRunStatusDao;

    @Inject
    private CacheManager cacheManager;

    @Inject
    private SystemParameterProvider systemParameterProvider;

    @Inject
    private SOPWeekNDailyQAReportDao sopWeekNDailyQAReportDao;

    @Override
    public LocalDate getLastRunDateForSOP() throws SystemException {
        SOPWeekNDailyRunStatus sopWeeknRunStatus = sopWeekNDailyRunStatusDao.findTopByTotalRecordGreaterThanOrderByLastRunDateDesc(0);

        //DateTimeFormat.forPattern(DateConversionUtil.DATE_DD_MMM_YY).print()

        return sopWeeknRunStatus != null && sopWeeknRunStatus.getFetchEndDate() != null ? sopWeeknRunStatus.getFetchEndDate() : null;
    }
    @Override
    public void notifyDailyRunStatus(WeekNDailyRunStatusInfo weekNDailyRunStatus, String exceptionTrace, List<String> failedLoanNumbers)
            throws SystemException {
       String smtpHostName = String
                .valueOf(systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_EMAIL_OUTPUT_SMTP_SERVER_NAME));
        String smtpServer = String
                .valueOf(systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_EMAIL_OUTPUT_SMTP_HOST_NAME));

        String toList = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_TO_QA_REPORT_LIST));
        String ccList = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_CC_LIST));
        String emailSubject = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_EMAIL_OUTPUT_SUBJECT));
        emailSubject = MessageFormat
                .format(emailSubject, StringUtils.isEmpty(exceptionTrace) ? RAClientConstants.WEEKN_QA_SUCCESS : RAClientConstants.WEEKN_QA_FAILURE);
        String from = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_FROM));
        String emailBody;

        if (StringUtils.isEmpty(exceptionTrace)) {
            emailBody = MessageFormat.format(String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_WEEKN_QA_REPORT_EMAIL_BODY)),
                    DateConversionUtil.getCurrentEstDate().toString(DateConversionUtil.US_DATE_TIME_FORMATTER), weekNDailyRunStatus.getTotalRecord(),
                    weekNDailyRunStatus.getSuccessCount(), weekNDailyRunStatus.getFailCount(),
                    DateConversionUtil.getCurrentEstDate().toString(DateConversionUtil.US_DATE_TIME_FORMATTER),
                    StringUtils.join(failedLoanNumbers, RAClientConstants.HTML_NEXT_LINE));
        } else {
            emailBody = MessageFormat.format(String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_WEEKN_QA_REPORT_ERROR_EMAIL_BODY)),
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
            log.error("Problem in sending notification for daily qa report SOP", e);
            SystemException.newSystemException(CoreExceptionCodes.DPRPRT001, e);
        }
    }

    @Override
    public WeekNDailyRunStatusInfo saveWeekNSOPRunStatus(WeekNDailyRunStatusInfo weekNDailyRunStatusInfo) throws SystemException {
        WeekNDailyRunStatusInfo statusInfo = null;
        try {
            SOPWeekNDailyRunStatus runStatus = sopWeekNDailyRunStatusDao.save(convert(weekNDailyRunStatusInfo, SOPWeekNDailyRunStatus.class));
            statusInfo = convert(runStatus, WeekNDailyRunStatusInfo.class);
        } catch (Exception e) {
            log.error("problem in saving weekn daily run status {}", e);
            SystemException.newSystemException(CoreExceptionCodes.DPRPRT005, e);
        }
        return statusInfo;
    }

    @Override
    public void saveWeekNSOPQaReport(List<WeekNDailyQAReportInfo> weeknQAReportList, WeekNDailyRunStatusInfo weeknRunStatusInfo) throws SystemException {
        try {
            List<SOPWeekNDailyQAReport> qaReportList = convertToList(weeknQAReportList, SOPWeekNDailyQAReport.class);
            qaReportList.forEach(c -> c.setSopWeekNDailyRunStatus(convert(weeknRunStatusInfo, SOPWeekNDailyRunStatus.class)));
            sopWeekNDailyQAReportDao.saveAll(qaReportList);
        } catch (Exception e) {
            log.error("problem in saving SOP weekn daily qa report {}", e);
            SystemException.newSystemException(CoreExceptionCodes.DPRPRT004, e);
        }
    }
}
