package com.fa.dp.core.email.service;

import java.util.Arrays;

public class MailDetails {

	private byte[] subjectDefinition;
	private byte[] bodyDefinition;
	private String toAddress;
	private String fromAddress;
	private String ccAddress;
	private String bccAddress;
	private String mailContentType;
	private String subject;
	private String bodyText;

	public byte[] getSubjectDefinition() {
		return subjectDefinition;
	}
	
	public void setSubjectDefinition(byte[] subjectDefinition) {
		if (subjectDefinition != null) {
	        this.subjectDefinition = Arrays.copyOf(subjectDefinition, subjectDefinition.length);
			}
	}
	
	public byte[] getBodyDefinition() {
		return bodyDefinition;
	}
	
	public void setBodyDefinition(byte[] bodyDefinition) {
		if (bodyDefinition != null) {
	        this.bodyDefinition = Arrays.copyOf(bodyDefinition, bodyDefinition.length);
			}
	}
	
	public String getToAddress() {
		return toAddress;
	}
	
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	
	public String getFromAddress() {
		return fromAddress;
	}
	
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	
	public String getCcAddress() {
		return ccAddress;
	}
	
	public void setCcAddress(String ccAddress) {
		this.ccAddress = ccAddress;
	}
	
	public String getBccAddress() {
		return bccAddress;
	}
	
	public void setBccAddress(String bccAddress) {
		this.bccAddress = bccAddress;
	}
	
	public String getMailContentType() {
		return mailContentType;
	}

	public void setMailContentType(String mailContentType) {
		this.mailContentType = mailContentType;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBodyText() {
		return bodyText;
	}

	public void setBodyText(String bodyText) {
		this.bodyText = bodyText;
	}
}
