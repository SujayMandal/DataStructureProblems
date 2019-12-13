package com.fa.dp.business.task;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.business.week0.entity.DynamicPricingFilePrcsStatus;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.email.service.EmailAttachment;
import com.fa.dp.core.email.service.MailDetails;
import com.fa.dp.core.email.service.MailService;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.systemparam.provider.SystemParameterProvider;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.systemparam.util.SystemParameterConstant;
import com.fa.dp.core.util.RAClientConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "week0EmailIntegration")
public class Week0EmailIntegration extends AbstractCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(Week0EmailIntegration.class);

	@Inject
	private DPFileProcessBO dpFileProcessBO;

	@Inject
	private CacheManager cacheManager;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	@Override
	public void execute(Object data) throws SystemException {

		LOGGER.info("Week0EmailIntegration -> processTask() started.");
		Long startTime = DateTime.now().getMillis();
		DPProcessParamEntryInfo dpProcessParamEntryInfo = null;
		if (checkData(data, DPProcessParamEntryInfo.class)) {
			dpProcessParamEntryInfo = ((DPProcessParamEntryInfo) data);

			String calculatedDate = null;
			String[] names = dpProcessParamEntryInfo.getDPFileProcessStatusInfo().getSysGnrtdInputFileName()
					.split(RAClientConstants.CHAR_UNDER_SCORE);
			if (names.length > 2) {
				calculatedDate = names[names.length - 2] + RAClientConstants.CHAR_HYPHEN + names[names.length - 1].replace(".xls", RAClientConstants.CHAR_EMPTY);
			}

			String smtpHostName = String
					.valueOf(systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_EMAIL_OUTPUT_SMTP_SERVER_NAME));
			String smtpServer = String
					.valueOf(systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_EMAIL_OUTPUT_SMTP_HOST_NAME));

			Map<String, String> dpProcessToList = new LinkedHashMap<String, String>();

			Map<String, String> dpProcessCCList = new LinkedHashMap<String, String>();

			dpProcessToList.put(DPProcessParamAttributes.OCN.getValue(), null);
			dpProcessToList.put(DPProcessParamAttributes.PHH.getValue(), null);
			dpProcessToList.put(DPProcessParamAttributes.NRZ.getValue(), null);

			dpProcessCCList.put(DPProcessParamAttributes.OCN.getValue(), null);
			dpProcessCCList.put(DPProcessParamAttributes.PHH.getValue(), null);
			dpProcessCCList.put(DPProcessParamAttributes.NRZ.getValue(), null);

			sendEmail(DPProcessParamAttributes.OCN.getValue(), smtpHostName, smtpServer, calculatedDate, dpProcessToList, dpProcessCCList,
					dpProcessParamEntryInfo);
			sendEmail(DPProcessParamAttributes.PHH.getValue(), smtpHostName, smtpServer, calculatedDate, dpProcessToList, dpProcessCCList,
					dpProcessParamEntryInfo);
			sendEmail(DPProcessParamAttributes.NRZ.getValue(), smtpHostName, smtpServer, calculatedDate, dpProcessToList, dpProcessCCList,
					dpProcessParamEntryInfo);

			if (null != dpProcessToList && (StringUtils.isNotBlank(dpProcessToList.get(DPProcessParamAttributes.OCN.getValue())) || StringUtils
					.isNotBlank(dpProcessToList.get(DPProcessParamAttributes.PHH.getValue())) || StringUtils
					.isNotBlank(dpProcessToList.get(DPProcessParamAttributes.NRZ.getValue())))) {
				DynamicPricingFilePrcsStatus processStatus = dpFileProcessBO
						.findDPProcessStatusById(dpProcessParamEntryInfo.getDPFileProcessStatusInfo().getId());

				if (null != processStatus) {
					DateTime date = new DateTime();
					processStatus.setEmailTimestamp(date.toString());
					processStatus.setToList(JSONObject.toJSONString(dpProcessToList));
					processStatus.setCcList(JSONObject.toJSONString(dpProcessCCList));

					dpFileProcessBO.saveDPProcessStatus(processStatus);
				}
			}
		}
		log.info("Time taken for Week0EmailIntegration : " + (DateTime.now().getMillis() - startTime) + "ms");
		LOGGER.info("Week0EmailIntegration -> processTask() ended.");

	}

	private void sendEmail(String classification, String smtpHostName, String smtpServer, String calculatedDate, Map<String, String> dpProcessToList,
			Map<String, String> dpProcessCCList, DPProcessParamEntryInfo dpProcessParamEntryInfo) {
		String toList;
		String ccList;
		String emailSubject;
		String from;
		String emailBody;

		List<EmailAttachment> attachments = new ArrayList<EmailAttachment>();
		EmailAttachment attachment;

		if (DPProcessParamAttributes.OCN.getValue().equals(classification)) {
			toList = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_EMAIL_OUTPUT_TO_LIST));
			ccList = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_EMAIL_OUTPUT_CC_LIST));
			emailSubject = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_EMAIL_OUTPUT_SUBJECT));
			emailSubject = MessageFormat.format(emailSubject, calculatedDate);
			from = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_EMAIL_OUTPUT_FROM));

			if (StringUtils.isNotBlank(dpProcessParamEntryInfo.getDPFileProcessStatusInfo().getOcnOutputFileName())) {
				emailBody = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_EMAIL_OUTPUT_BODY));
				attachment = new EmailAttachment();
				attachment.setFileName(dpProcessParamEntryInfo.getDPFileProcessStatusInfo().getOcnOutputFileName());
				attachment.setFile(new File(systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_SAN_PATH) + File.separator
						+ dpProcessParamEntryInfo.getDPFileProcessStatusInfo().getOcnOutputFileName()));
				attachment.setDeleteFileAfterSend(false);
				attachments.add(attachment);
			} else {
				emailBody = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NOPROPERTY_EMAIL_OUTPUT_BODY));
			}
		} else if (DPProcessParamAttributes.PHH.getValue().equals(classification)) {
			toList = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_EMAIL_OUTPUT_TO_LIST));
			ccList = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_EMAIL_OUTPUT_CC_LIST));
			emailSubject = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_EMAIL_OUTPUT_SUBJECT));
			emailSubject = MessageFormat.format(emailSubject, calculatedDate);
			from = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_EMAIL_OUTPUT_FROM));

			if (StringUtils.isNotBlank(dpProcessParamEntryInfo.getDPFileProcessStatusInfo().getOcnOutputFileName())) {
				emailBody = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_EMAIL_OUTPUT_BODY));
				attachment = new EmailAttachment();
				attachment.setFileName(dpProcessParamEntryInfo.getDPFileProcessStatusInfo().getOcnOutputFileName());
				attachment.setFile(new File(systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_SAN_PATH) + File.separator
						+ dpProcessParamEntryInfo.getDPFileProcessStatusInfo().getPhhOutputFileName()));
				attachment.setDeleteFileAfterSend(false);
				attachments.add(attachment);
			} else {
				emailBody = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NOPROPERTY_EMAIL_OUTPUT_BODY));
			}
		} else {
			toList = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_EMAIL_OUTPUT_TO_LIST));
			ccList = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_EMAIL_OUTPUT_CC_LIST));
			emailSubject = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_EMAIL_OUTPUT_SUBJECT));
			emailSubject = MessageFormat.format(emailSubject, calculatedDate);
			from = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_EMAIL_OUTPUT_FROM));

			if (StringUtils.isNotBlank(dpProcessParamEntryInfo.getDPFileProcessStatusInfo().getNrzOutputFileName())) {
				emailBody = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_EMAIL_OUTPUT_BODY));
				attachment = new EmailAttachment();
				attachment.setFileName(dpProcessParamEntryInfo.getDPFileProcessStatusInfo().getNrzOutputFileName());
				attachment.setFile(new File(systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_SAN_PATH) + File.separator
						+ dpProcessParamEntryInfo.getDPFileProcessStatusInfo().getNrzOutputFileName()));
				attachment.setDeleteFileAfterSend(false);
				attachments.add(attachment);
			} else {
				emailBody = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NOPROPERTY_EMAIL_OUTPUT_BODY));
			}
		}

		MailDetails mailDetails = new MailDetails();
		mailDetails.setToAddress(toList);
		mailDetails.setCcAddress(ccList);
		mailDetails.setFromAddress(from);
		mailDetails.setSubject(emailSubject);
		mailDetails.setBodyText(emailBody);

		try {
			if (CollectionUtils.isNotEmpty(attachments))
				MailService.sendEMailWithAttachments(smtpHostName, smtpServer, mailDetails, attachments);
			else
				MailService.sendEMail(smtpHostName, smtpServer, mailDetails);

			dpProcessToList.put(classification, toList);
			dpProcessCCList.put(classification, ccList);
		} catch (SystemException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
	}

}
