package com.ca.umg.notification;

import java.io.File;
import java.util.*;

import javax.activation.*;
import javax.mail.*;
import javax.mail.Flags.*;
import javax.mail.internet.*;

import com.sun.mail.smtp.*;

public class SendMail2 {

	public static final String TO = "nageswara.reddy@altisource.com";
	public static final String CC = "nageswara.reddy@altisource.com";
	public static final String BCC = "nageswara.reddy@altisource.com";
	public static final String FROM = "umg@altisource.com";
	
	public static final String BOUNCE = "nageswara.reddy@altisource.com"; // should be different than FROM

	
	public static void main(String[] args) {
		final Properties properties = System.getProperties();				
		addStmpServerDetails(properties);
		addBounceAddress(properties);
		
		final Session session = Session.getDefaultInstance(properties);
		
		session.getProperties().setProperty("mail.smtp.from", BOUNCE);
		
		Transport transport = null;
		StringBuffer reason;
		boolean mailSend;
		
		try {
			final MimeMessage message = new MimeMessage(session);
			
//			final InternetAddress[] arrayReplyTo  = new InternetAddress[1];
//            arrayReplyTo[0] = new InternetAddress(BOUNCE);
           				
//			message.setReplyTo(arrayReplyTo);
//            message.setHeader("Return-Path:","<nageswara.reddy@altisource.com>");
            
			
			addReceipients(message);			
			message.setSubject("Test Mail");

			final Multipart multipart = new MimeMultipart();
			addBody(multipart);
			addAttachment(multipart);

			message.setContent(multipart);

			final SMTPMessage smtpMessage = new SMTPMessage(message);
			
			smtpMessage.setReplyTo(getAddresses(BOUNCE));
			
			smtpMessage.setEnvelopeFrom(BOUNCE);
			
			// Send message
		
			transport = session.getTransport("smtp");			
//			transport.connect();			
//			transport.addTransportListener(new RAMailTransportListener());
//			transport.sendMessage(smtpMessage, smtpMessage.getAllRecipients());
			
			System.out.println("URL Name : " + transport.getURLName());
			SMTPTransport smptTransport = new SMTPTransport(session, transport.getURLName());			
			smptTransport.addTransportListener(new RAMailTransportListener());
			smptTransport.connect();
			smptTransport.setReportSuccess(true);
			
			smptTransport.sendMessage(smtpMessage, smtpMessage.getAllRecipients());
			
			URLName urlname;
			
			System.out.println("LastServerResponse :" + smptTransport.getLastServerResponse());
			
			System.out.println("LastReturnCode : " + smptTransport.getLastReturnCode());
			
			
//			Transport.send(smtpMessage);	
			
			showOtherDetails(smtpMessage);
									
			showFromAddressDetails(smtpMessage);
			
			showReplyToAddresses(smtpMessage);
			
			showHeadersDetails(smtpMessage);
			
			showFlagDetails(smtpMessage);
						
			showFolderDetails(smtpMessage);			
			
			System.out.println("message sent successfully....");

		} catch(MessagingException ex) {
			
			if (ex instanceof SMTPSendFailedException) {
				final SMTPSendFailedException exx = (SMTPSendFailedException) ex;
				
				System.out.println("Command : " + exx.getCommand());
				System.out.println("ReturnCode : " + exx.getReturnCode());
			}
			
			ex.printStackTrace(System.err);
			StringBuffer exception = new StringBuffer(ex.getMessage().toString());
			  
	        if (exception.indexOf("ConnectException") >= 0)      // connection problem.
	        {
	            reason = new StringBuffer(" Unable to Connect Mail server");
	        }
	        else if (exception.indexOf("SendFailedException") >= 0)      // Wrong To Address 
	        {
	            reason = new StringBuffer("Wrong To Mail address");
	        }
	        else if (exception.indexOf("FileNotFoundException") >= 0)    //File Not Found at Specified Location
	        {
	            reason = new StringBuffer("File Not Found at Specific location");                   
	        }
	        else        // Email has not been sent.
	        {
	            reason = new StringBuffer("Email has not been sent.");
	        }
	        System.err.println("Exception occured while sending the mail : " + ex);
	        System.err.println("Email has not been sent due to one of these reasons :" +
	                " 1)Invalid email address 2)Attachments in mail may not be found and 3)Connection problem.");
	        mailSend =false;
		} finally {
			if (transport != null) {
				try {
					transport.close();
				} catch (MessagingException me) {
			        System.err.println("Exception while closing transport : " + me);					
				}
			}
		}
	}



	private static void showOtherDetails(final SMTPMessage smtpMessage) throws MessagingException {
		System.out.println("Current Date : " + new Date());

		System.out.println("Sent Date : " + smtpMessage.getSentDate());

		System.out.println("Received Date : " + smtpMessage.getReceivedDate());
		
		System.out.println("Message ID : " + smtpMessage.getMessageID());
		
		System.out.println("Message Number : " + smtpMessage.getMessageNumber());
		
		System.out.println("Notify Options : "+ smtpMessage.getNotifyOptions());
		
		System.out.println("Return Options : "+ smtpMessage.getReturnOption());

		System.out.println("Get Envelope From : " + smtpMessage.getEnvelopeFrom());
	}



	private static void showFromAddressDetails(final SMTPMessage smtpMessage) throws MessagingException {
		System.out.println("********* FROM ADDRESSES *********");

		final Address[] fromAddresses = smtpMessage.getFrom();
		
		if (fromAddresses != null) {
			for (Address fromAddress : fromAddresses) {
				System.out.println("From Address : type " + fromAddress.getType() + ", toString : " + fromAddress.toString());
			}
		} else {
			System.out.println("From Address is EMPTY");
		}
	}



	private static void showReplyToAddresses(final SMTPMessage smtpMessage) throws MessagingException {
		System.out.println("********* REPLY TO ADDDRESSES*********");

		final Address[] replyToAddresses = smtpMessage.getReplyTo();
		
		if (replyToAddresses != null) {
			for (Address replyToAddress : replyToAddresses) {
				System.out.println("Reply To Address : type " + replyToAddress.getType() + ", toString : " + replyToAddress.toString());
			}
		} else {
			System.out.println("Reply To Address is EMPTY");
		}
	}



	private static void showHeadersDetails(final SMTPMessage smtpMessage) throws MessagingException {
		System.out.println("********* ALL HEADERS *********");
		for (Enumeration<Header> e = smtpMessage.getAllHeaders(); e.hasMoreElements();) {
			final Header header = e.nextElement();
			System.out.println("Header Name : " + header.getName() + ", value : " + header.getValue());
		    System.out.println(header + "      :" + smtpMessage.getHeader(header.getName(), ";"));				
		}
		
		System.out.println("********* ALL HEADERS LINES *********");
		for (Enumeration e = smtpMessage.getAllHeaderLines(); e.hasMoreElements();) {
			System.out.println(e.nextElement());				
		}
	}



	private static void showFlagDetails(final SMTPMessage smtpMessage) throws MessagingException {
		final Flags flags = smtpMessage.getFlags();
		
		System.out.println("********* FLAGS *********");
		if (flags != null) {
			System.out.println("********* ALL SYSTEM FLAGS *********");
			for (Flag f : flags.getSystemFlags()) {
				System.out.println(f.toString());				
			}
			
			System.out.println("********* ALL USER FLAGS *********");
			for (String f : flags.getUserFlags()) {
				System.out.println(f);				
			}				
		} else {
			System.out.println("Flags are EMPTY");
		}
	}



	private static void showFolderDetails(final SMTPMessage smtpMessage) throws MessagingException {
		final Folder folder = smtpMessage.getFolder();
		
		System.out.println("********* FOLDER DETAILS *********");

		if (folder != null) {
			System.out.println("Name : " + folder.getName());
			System.out.println("FullName : " + folder.getFullName());
			System.out.println("Mode : " + folder.getMode());
			System.out.println("MessageCount : " + folder.getMessageCount());
			System.out.println("NewMessageCount : " + folder.getNewMessageCount());
			System.out.println("DeletedMessageCount : " + folder.getDeletedMessageCount());
			System.out.println("UnreadMessageCount : " + folder.getUnreadMessageCount());
			System.out.println("Type : " + folder.getType());
			System.out.println("Parent : " + folder.getParent());
		}
	}
	
	
	
	private static void addStmpServerDetails(final Properties properties) {
		final String host = "NAV8EHCNMP01.ASCORP.COM";// or IP address
		properties.setProperty("mail02.svc.den.vz.altidev.net", host);
		// properties.setProperty("mail.smtp.port", "25");		
	}
	
	
	
	private static void addBounceAddress(final Properties properties) {
		properties.put("mail.smtp.from", BOUNCE);
	}

	
	
	private static void addReceipients(final MimeMessage message) throws MessagingException{
		message.setFrom(new InternetAddress(FROM));
		message.addRecipients(Message.RecipientType.TO, getAddresses(TO));
//		message.addRecipients(Message.RecipientType.CC, getAddresses(cc));
//		message.addRecipients(Message.RecipientType.BCC, getAddresses(bcc));		
	}
	
	
	
	

	private static void addBody(final Multipart multipart) throws MessagingException {
		final BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent("Test mail", "text/html");
		multipart.addBodyPart(messageBodyPart);		
	}

	
	
	
	private static void addAttachment(final Multipart multipart) throws MessagingException {
		final MimeBodyPart attachmentBodyPart = new MimeBodyPart();
		final StringBuilder dataFileLocation = new StringBuilder("/sanpath/ocwen/model/REPORT_MODEL/REPORT_MODEL-MID-2016-Apr-20-07-31/");
		final File file = new File(dataFileLocation.append("R_Bulk_IO_Template_Ver02.xlsx").toString());
		final DataSource source = new FileDataSource(file);
		attachmentBodyPart.setDataHandler(new DataHandler(source));
		attachmentBodyPart.setFileName("R_Bulk_IO_Template_Ver02.xlsx");	
		multipart.addBodyPart(attachmentBodyPart);
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
