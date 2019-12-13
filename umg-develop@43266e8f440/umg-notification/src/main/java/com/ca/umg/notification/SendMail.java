package com.ca.umg.notification;

import static com.ca.umg.notification.NotificationExceptionCodes.MAIL_SENDING_FAILED;
import static com.ca.umg.notification.model.NotificationStatus.FAILED;

import java.util.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;  
import javax.mail.internet.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.notification.model.MailDetails;
import com.ca.umg.notification.model.NotificationAttachment;
import com.ca.umg.notification.model.NotificationStatus;  
  
public class SendMail {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SendMail.class);
	
	private static final String MAIL_TYPE = "text/html";
	
	public static NotificationStatus sendEMail(final SystemParameterProvider systemParameterProvider, final MailDetails mailDetails) throws BusinessException, SystemException {
		LOGGER.info("Getting properties");
		final Properties properties = getProperties(systemParameterProvider);
		final Session session = Session.getDefaultInstance(properties);
		
		NotificationStatus status = FAILED;
		
		try {
			final MimeMessage message = new MimeMessage(session);
			
			addRecipients(message, mailDetails);
			
			message.setSubject(mailDetails.getSubject());
			LOGGER.info("Subject :" + mailDetails.getSubject());
			
			message.setContent(mailDetails.getBodyText(), MAIL_TYPE);
			LOGGER.info("Body :" + mailDetails.getBodyText());

			Transport.send(message);
			
			LOGGER.info("Mail sent successfully");
			
			status = NotificationStatus.SUCCESS;

		} catch (final MessagingException mex) {
			LOGGER.error("Mail sent Failed");
			LOGGER.error(mex.getLocalizedMessage(), mex);			
//			mex.printStackTrace();
			SystemException.newSystemException(MAIL_SENDING_FAILED.getCode(), new String[] {mex.getLocalizedMessage()});
		}
		
		return status;		
	}
	
	public static NotificationStatus sendEMailWithAttachments(final SystemParameterProvider systemParameterProvider, final MailDetails mailDetails, 
			final List<NotificationAttachment> attachments) throws BusinessException, SystemException {
		LOGGER.info("Getting properties");
		final Properties properties = getProperties(systemParameterProvider);
		final Session session = Session.getDefaultInstance(properties);
		
		NotificationStatus status = FAILED;
		
		try {
			final MimeMessage message = new MimeMessage(session);
			
			addRecipients(message, mailDetails);
			
			message.setSubject(mailDetails.getSubject());
			LOGGER.info("Subject :" + mailDetails.getSubject());
			
			final Multipart multipart = new MimeMultipart();			
			addBodyPart(multipart, mailDetails.getBodyText());
			addAttachments(multipart, attachments);			
			message.setContent(multipart);
			Transport.send(message);

			LOGGER.info("Mail sent successfully");
			
			status = NotificationStatus.SUCCESS;

		} catch (final MessagingException mex) {
			LOGGER.error("Mail sent Failed");
			LOGGER.error(mex.getLocalizedMessage(), mex);			
//			mex.printStackTrace();
			SystemException.newSystemException(MAIL_SENDING_FAILED.getCode(), new String[] {mex.getLocalizedMessage()});
		}
		
		return status;		
	}
	
	private static void addRecipients(final MimeMessage message, final MailDetails mailDetails) throws MessagingException, BusinessException {
		if (mailDetails.getFromAddress() != null && mailDetails.getFromAddress().length() > 0) {
			message.setFrom(new InternetAddress(mailDetails.getFromAddress()));
			LOGGER.info("From Address : {}", mailDetails.getFromAddress());				
		} else {
			LOGGER.error("FROM Address is empty");
			BusinessException.newBusinessException(MAIL_SENDING_FAILED.getCode(), new String[] {"From Address is empty"});				
		}
		
		if (mailDetails.getToAddress() != null && mailDetails.getToAddress().length() > 0) {
			message.addRecipients(Message.RecipientType.TO, getAddresses(mailDetails.getToAddress()));
			LOGGER.info("TO Address : {}", mailDetails.getToAddress());
		} else {
			LOGGER.error("TO Address is empty");
			BusinessException.newBusinessException(MAIL_SENDING_FAILED.getCode(), new String[] {"To Address is empty"});
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
	
	private static void addAttachments(final Multipart multipart, final List<NotificationAttachment> attachments) throws MessagingException {
		MimeBodyPart attachmentBodyPart = null;
		if (attachments != null && !attachments.isEmpty()) {
			attachmentBodyPart = new MimeBodyPart();
			
			for (final NotificationAttachment attachment : attachments) {
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
	
	private static Properties getProperties(final SystemParameterProvider systemParameterProvider) {		
		final String host = systemParameterProvider.getParameter("SMTP_HOST_NAME");
		LOGGER.info("Host Name : {}", host);
		
		final String smtpServer = systemParameterProvider.getParameter("SMTP_SERVER");
		LOGGER.info("SMTP Server : {}", smtpServer);
		
		final Properties properties = System.getProperties();
		properties.setProperty(smtpServer, host);
		return properties;
	}
	
	private static Address[] getAddresses(final String address) throws AddressException {
		final String[] splits = address.split("[;:,]");
		final InternetAddress[] addresses = new InternetAddress[splits.length];
		int i = 0;
		for (String split : splits) {
			addresses[i++] =  new InternetAddress(split);
		}
		
		return addresses;
	}
	
	public static void main(String[] args) {
		final String to = "nageswara.reddy@altisource.com";
		final String from = "nageswara.reddy@altisource.com";
		final String host = "NAV8EHCNMP01.ASCORP.COM";// or IP address

		// Get the session object
		final Properties properties = System.getProperties();
		properties.setProperty("mail02.svc.mia.vz.altidev.net", host);
		final Session session = Session.getDefaultInstance(properties);

		// compose the message
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject("Test Mail");
			message.setText("Hello, this is a test mail");

			// Send message
			Transport.send(message);
//			System.out.println("message sent successfully....");
			LOGGER.error("message sent successfully....");
		} catch (MessagingException mex) {
//			System.err.println();
			LOGGER.error("Sending mail failed....", mex);
//			mex.printStackTrace();
		}
	}
}