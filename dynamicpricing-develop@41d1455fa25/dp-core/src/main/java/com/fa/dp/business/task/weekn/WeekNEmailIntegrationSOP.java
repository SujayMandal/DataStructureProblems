package com.fa.dp.business.task.weekn;

import java.io.File;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.weekn.dao.DPWeekNProcessStatusRepo;
import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.email.service.EmailAttachment;
import com.fa.dp.core.email.service.MailDetails;
import com.fa.dp.core.email.service.MailService;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.systemparam.provider.SystemParameterProvider;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.systemparam.util.SystemParameterConstant;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

/**
 * @author yogeshku
 *
 */
@Named
@Scope("prototype")
@CommandDescription(name = "weekNEmailIntegrationSOP")
public class WeekNEmailIntegrationSOP extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeekNEmailIntegrationSOP.class);

    private static final String SUBJECT_DATE_FORMAT = "yyyy-MM-dd";

    @Inject
    private CacheManager cacheManager;

    @Inject
    private SystemParameterProvider systemParameterProvider;

    @Inject
    private DPWeekNProcessStatusRepo dpWeekNProcessStatusRepo;

    @Override
    public void execute(Object data) throws SystemException {
        LOGGER.info("weekNEmailIntegrationSOP -> processTask started.");
        DPProcessWeekNParamEntryInfo infoObject = null;
        if (checkData(data, DPProcessWeekNParamEntryInfo.class)){
            infoObject = ((DPProcessWeekNParamEntryInfo) data);

            if(null != infoObject && null != infoObject.getDpWeeknProcessStatus()) {
                String ocnFileName = infoObject.getDpWeeknProcessStatus().getOcnOutputFileName();
                String nrzFileName = infoObject.getDpWeeknProcessStatus().getNrzOutputFileName();

                LOGGER.debug("Generated Ocn File Name "+ocnFileName+" Generated Nrz File Name :"+nrzFileName);

                DateFormat format = new SimpleDateFormat(SUBJECT_DATE_FORMAT);

                String calculatedDate = format.format(new Date());

                String smtpHostName = String.valueOf(systemParameterProvider
                        .getSystemParamValue(SystemParameterConstant.SYS_PARAM_EMAIL_OUTPUT_SMTP_SERVER_NAME));
                String smtpServer = String.valueOf(systemParameterProvider
                        .getSystemParamValue(SystemParameterConstant.SYS_PARAM_EMAIL_OUTPUT_SMTP_HOST_NAME));

                String toList;
                String ccList;
                String emailSubject;
                String from;
                String emailBody;

                Map<String, String> dpProcessToList = new LinkedHashMap<String, String>();

                Map<String, String> dpProcessCCList = new LinkedHashMap<String, String>();

                dpProcessToList.put(DPProcessParamAttributes.OCN.getValue(), null);
                dpProcessToList.put(DPProcessParamAttributes.NRZ.getValue(), null);

                dpProcessCCList.put(DPProcessParamAttributes.OCN.getValue(), null);
                dpProcessCCList.put(DPProcessParamAttributes.NRZ.getValue(), null);

                if (StringUtils.isNotBlank(ocnFileName)) {

                   /* toList = String
                            .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_WEEKN_EMAIL_OUTPUT_TO_LIST));
                    ccList = String
                            .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_WEEKN_EMAIL_OUTPUT_CC_LIST));
                    emailSubject = String
                            .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_WEEKN_EMAIL_OUTPUT_SUBJECT));
                    emailSubject = MessageFormat.format(emailSubject, calculatedDate);
                    from = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_WEEKN_EMAIL_OUTPUT_FROM));

                    emailBody = String
                            .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_WEEKN_EMAIL_OUTPUT_BODY));*/

                    toList = String
                            .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_TO_QA_REPORT_LIST));
                    ccList = String
                            .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_CC_LIST ));
                    emailSubject = String
                            .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_EMAIL_OUTPUT_SUBJECT));
                    emailSubject = MessageFormat.format(emailSubject, calculatedDate);
                    from = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_FROM));

                    emailBody = String
                            .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_WEEKN_QA_REPORT_EMAIL_BODY));

                    MailDetails mailDetails = new MailDetails();
                    mailDetails.setToAddress(toList);
                    mailDetails.setCcAddress(ccList);
                    mailDetails.setFromAddress(from);
                    mailDetails.setSubject(emailSubject);
                    mailDetails.setBodyText(emailBody);

                    List<EmailAttachment> attachments = new ArrayList<EmailAttachment>();

                    EmailAttachment nrzAttachment = new EmailAttachment();
                    nrzAttachment.setFileName(ocnFileName);
                    nrzAttachment.setFile(
                            new File(systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_SAN_PATH)
                                    + File.separator + ocnFileName));
                    nrzAttachment.setDeleteFileAfterSend(false);
                    attachments.add(nrzAttachment);

                    try {
                        MailService.sendEMailWithAttachments(smtpHostName, smtpServer, mailDetails, attachments);

                        dpProcessToList.put(DPProcessParamAttributes.OCN.getValue(), toList);
                        dpProcessCCList.put(DPProcessParamAttributes.OCN.getValue(), ccList);
                    } catch (SystemException e) {
                        LOGGER.error(e.getLocalizedMessage(), e);
                    }
                }
                if (StringUtils.isNotBlank(nrzFileName)) {

                    /*toList = String
                            .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_WEEKN_EMAIL_OUTPUT_TO_LIST));
                    ccList = String
                            .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_WEEKN_EMAIL_OUTPUT_CC_LIST));
                    emailSubject = String
                            .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_WEEKN_EMAIL_OUTPUT_SUBJECT));
                    emailSubject = MessageFormat.format(emailSubject, calculatedDate);
                    from = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_WEEKN_EMAIL_OUTPUT_FROM));

                    emailBody = String
                            .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_WEEKN_EMAIL_OUTPUT_BODY));*/
                    toList = String
                            .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_TO_QA_REPORT_LIST));
                    ccList = String
                            .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_CC_LIST ));
                    emailSubject = String
                            .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_EMAIL_OUTPUT_SUBJECT));
                    emailSubject = MessageFormat.format(emailSubject, calculatedDate);
                    from = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_SOP_WEEKN_FROM));

                    emailBody = String
                            .valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_WEEKN_QA_REPORT_EMAIL_BODY));

                    MailDetails mailDetails = new MailDetails();
                    mailDetails.setToAddress(toList);
                    mailDetails.setCcAddress(ccList);
                    mailDetails.setFromAddress(from);
                    mailDetails.setSubject(emailSubject);
                    mailDetails.setBodyText(emailBody);

                    List<EmailAttachment> attachments = new ArrayList<EmailAttachment>();

                    EmailAttachment ocnAttachment = new EmailAttachment();
                    ocnAttachment.setFileName(nrzFileName);
                    ocnAttachment.setFile(
                            new File(systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_SAN_PATH)
                                    + File.separator + nrzFileName));
                    ocnAttachment.setDeleteFileAfterSend(false);
                    attachments.add(ocnAttachment);

                    try {
                        MailService.sendEMailWithAttachments(smtpHostName, smtpServer, mailDetails, attachments);

                        dpProcessToList.put(DPProcessParamAttributes.NRZ.getValue(), toList);
                        dpProcessCCList.put(DPProcessParamAttributes.NRZ.getValue(), ccList);
                    } catch (SystemException e) {
                        LOGGER.error(e.getLocalizedMessage(), e);
                    }
                }

                if (null != dpProcessToList
                        && (StringUtils.isNotBlank(dpProcessToList.get(DPProcessParamAttributes.OCN.getValue()))
                        || StringUtils.isNotBlank(dpProcessToList.get(DPProcessParamAttributes.NRZ.getValue())))) {
                    DPWeekNProcessStatus processStatus = dpWeekNProcessStatusRepo
                            .getOne(infoObject.getDpWeeknProcessStatus().getId());

                    if (null != processStatus) {
                        DateTime date = new DateTime();
                        processStatus.setEmailTimestamp(date.toString());
                        processStatus.setToList(JSONObject.toJSONString(dpProcessToList));
                        processStatus.setCcList(JSONObject.toJSONString(dpProcessCCList));

                        dpWeekNProcessStatusRepo.save(processStatus);
                    }
                }

            } else {
                LOGGER.info("EmailIntegrationService -> processTask() infoObject.getDPFileProcessStatusInfo() unavailable.");
            }
        }

        LOGGER.info("weekNEmailIntegrationSOP -> processTask ended.");

    }

}

