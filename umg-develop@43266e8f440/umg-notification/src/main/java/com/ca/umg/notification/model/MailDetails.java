package com.ca.umg.notification.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class MailDetails extends NotificationDetails {

	private byte[] subjectDefinition;
	private byte[] bodyDefinition;
	private String toAddress;
	private String fromAddress;
	private String ccAddress;
	private String bccAddress;
	private String mailContentType;
	private String mobile;
	private String subject;
	private String bodyText;
	private String tenantCode;
	private NotificationClassification classiffication;

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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
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
	
	public NotificationClassification getClassiffication() {
		return classiffication;
	}

	public void setClassiffication(NotificationClassification classiffication) {
		this.classiffication = classiffication;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public static MailDetails of(final ResultSet rs) throws SQLException {
        final MailDetails mailDetails = new MailDetails();
        mailDetails.setId(rs.getString("ID"));
        mailDetails.setToAddress(rs.getString("TO_ADDRESS"));
        mailDetails.setBccAddress(rs.getString("BCC_ADDRESS"));
        mailDetails.setCcAddress(rs.getString("CC_ADDRESS"));
        mailDetails.setFromAddress(rs.getString("FROM_ADDRESS"));
        mailDetails.setSubjectDefinition(rs.getBytes("SUBJECT_DEFINITION"));
        mailDetails.setBodyDefinition(rs.getBytes("BODY_DEFINITION"));
        mailDetails.setMailContentType(rs.getString("MAIL_CONTENT_TYPE"));
        mailDetails.setNotificationType(rs.getString("TYPE"));
        mailDetails.setMobile(rs.getString("MOBILE"));   
        mailDetails.setClassiffication(NotificationClassification.of(rs.getString("CLASSIFICATION")));
        mailDetails.setTenantCode("TENANT_ID");
        return mailDetails;
	}
}
