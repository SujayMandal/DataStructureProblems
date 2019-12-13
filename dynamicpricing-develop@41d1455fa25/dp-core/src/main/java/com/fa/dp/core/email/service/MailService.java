package com.fa.dp.core.email.service;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.fa.dp.core.email.service.constants.DPOutputEmailStatus;
import com.fa.dp.core.exception.SystemException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.fa.dp.core.email.service.constants.DPOutputEmailExceptionCodes.MAIL_SENDING_FAILED;
import static com.fa.dp.core.email.service.constants.DPOutputEmailStatus.FAILED;

public class MailService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

	private static final String MAIL_TYPE = "text/html";

	public static DPOutputEmailStatus sendEMail(final String smtpHostName, final String smtpServer,
			final MailDetails mailDetails) throws SystemException {
		LOGGER.info("Getting properties");
		final Properties properties = getProperties(smtpHostName, smtpServer);
		final Session session = Session.getDefaultInstance(properties);

		DPOutputEmailStatus status = FAILED;

		try {
			final MimeMessage message = new MimeMessage(session);

			addRecipients(message, mailDetails);

			message.setSubject(mailDetails.getSubject());
			LOGGER.info("Subject :" + mailDetails.getSubject());

			message.setContent(mailDetails.getBodyText(), MAIL_TYPE);
			LOGGER.info("Body :" + mailDetails.getBodyText());

			Transport.send(message);

			LOGGER.info("Mail sent successfully");

			status = DPOutputEmailStatus.SUCCESS;

		} catch (final MessagingException mex) {
			LOGGER.error("Mail sent Failed");
			LOGGER.error(mex.getLocalizedMessage(), mex);
			SystemException.newSystemException(MAIL_SENDING_FAILED.getCode(),
					new Object[] { mex.getLocalizedMessage() });
		}

		return status;
	}

	public static DPOutputEmailStatus sendEMailWithAttachments(final String smtpHostName, final String smtpServer,
			final MailDetails mailDetails, final List<EmailAttachment> attachments)
			throws SystemException {
		LOGGER.info("Getting properties");
		final Properties properties = getProperties(smtpHostName, smtpServer);
		LOGGER.info("properties : " + properties.toString());
		final Session session = Session.getDefaultInstance(properties);

		DPOutputEmailStatus status = FAILED;

		try {
			final MimeMessage message = new MimeMessage(session);

			addRecipients(message, mailDetails);

			message.setSubject(mailDetails.getSubject());
			LOGGER.info("Subject :" + mailDetails.getSubject());

			final Multipart multipart = new MimeMultipart();
			addBodyPart(multipart, mailDetails.getBodyText());
			addAttachments(multipart, attachments);
			message.setContent(multipart);
			
			LOGGER.info("getAllRecipients : "+message.getAllRecipients() != null ? message.getAllRecipients().toString() : "NA");
			try {
				LOGGER.info("getContent : "+message.getContent() != null ? message.getContent().toString() : "NA");
			} catch (IOException e) {
				LOGGER.info(e.getMessage());
			}
			LOGGER.info("getContentType : "+message.getContentType());
			LOGGER.info("getDisposition : "+message.getDisposition());
			LOGGER.info("getEncoding : "+message.getEncoding());
			LOGGER.info("getFrom : "+((message.getFrom() != null) ? message.getFrom().toString() : "na"));
			LOGGER.info("getSender : "+((message.getSender() != null) ? message.getSender().toString() : "na"));
			LOGGER.info("getSubject : "+message.getSubject());
			
			Transport.send(message);

			LOGGER.info("Mail sent successfully");

			status = DPOutputEmailStatus.SUCCESS;

		} catch (final MessagingException mex) {
			LOGGER.error("Mail sent Failed");
			LOGGER.error(mex.toString());
			LOGGER.error(mex.getLocalizedMessage(), mex);
			SystemException.newSystemException(MAIL_SENDING_FAILED.getCode(),
					new Object[] { mex.getLocalizedMessage() });
		}

		return status;
	}

	private static void addRecipients(final MimeMessage message, final MailDetails mailDetails)
			throws MessagingException, SystemException {
		if (mailDetails.getFromAddress() != null && mailDetails.getFromAddress().length() > 0) {
			message.setFrom(new InternetAddress(mailDetails.getFromAddress()));
			LOGGER.info("From Address : {}", mailDetails.getFromAddress());
		} else {
			LOGGER.error("FROM Address is empty");
			SystemException.newSystemException(MAIL_SENDING_FAILED.getCode(),
					new Object[] { "From Address is empty" });
		}

		if (mailDetails.getToAddress() != null && mailDetails.getToAddress().length() > 0) {
			message.addRecipients(Message.RecipientType.TO, getAddresses(mailDetails.getToAddress()));
			LOGGER.info("TO Address : {}", mailDetails.getToAddress());
		} else {
			LOGGER.error("TO Address is empty");
			SystemException.newSystemException(MAIL_SENDING_FAILED.getCode(),
					new Object[] { "To Address is empty" });
		}

		if (mailDetails.getCcAddress() != null && mailDetails.getCcAddress().length() > 0) {
			message.addRecipients(Message.RecipientType.CC, getAddresses((mailDetails.getCcAddress())));
			LOGGER.info("BC Address : {}", mailDetails.getCcAddress());
		} else {
			LOGGER.info("BC Address : empty");
		}

		if (mailDetails.getBccAddress() != null && mailDetails.getBccAddress().length() > 0) {
			message.addRecipients(Message.RecipientType.BCC, getAddresses((mailDetails.getBccAddress())));
			LOGGER.info("BCC Address : {}", mailDetails.getBccAddress());
		} else {
			LOGGER.info("BCC Address : empty");
		}
	}

	private static void addBodyPart(final Multipart multipart, final String bodyText) throws MessagingException {
		final BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(bodyText, MAIL_TYPE);
		LOGGER.info("Body :" + bodyText);
		multipart.addBodyPart(messageBodyPart);
	}

	private static void addAttachments(final Multipart multipart, final List<EmailAttachment> attachments)
			throws MessagingException {
		if (attachments != null && !attachments.isEmpty()) {
			
			for (final EmailAttachment attachment : attachments) {
				MimeBodyPart attachmentBodyPart = new MimeBodyPart();
				if(StringUtils.isBlank(attachment.getFileName())) {
					continue;
				}
				final DataSource source = new FileDataSource(attachment.getFile());
				attachmentBodyPart.setDataHandler(new DataHandler(source));
				attachmentBodyPart.setFileName(attachment.getFileName());
				multipart.addBodyPart(attachmentBodyPart);
				LOGGER.info("Attachment name is : " + attachment.getFileName());
			}
		} else {
			LOGGER.info("No attachments");
		}
	}

	private static Properties getProperties(final String smtpHostName, final String smtpServer) {
		final Properties properties = System.getProperties();
		properties.setProperty(smtpServer, smtpHostName);
		return properties;
	}

	private static Address[] getAddresses(final String address) throws AddressException {
		final String[] splits = address.split("[;:,]");
		final InternetAddress[] addresses = new InternetAddress[splits.length];
		int i = 0;
		for (String split : splits) {
			addresses[i++] = new InternetAddress(split);
		}

		return addresses;
	}
}
