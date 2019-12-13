package com.ca.umg.notification.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class SMSDetails extends NotificationDetails {

	private byte[] smsDefinition;
	private String mobile;
	private String smsText;
			
	public byte[] getSmsDefinition() {
		return smsDefinition;
	}

	public void setSmsDefinition(byte[] smsDefinition) {
		if (smsDefinition != null) {
	        this.smsDefinition = Arrays.copyOf(smsDefinition, smsDefinition.length);
		}
	}

	public String getSmsText() {
		return smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}	
	
	public static SMSDetails of (final ResultSet rs) throws SQLException {
        final SMSDetails smsDetails = new SMSDetails();
        smsDetails.setNotificationType(rs.getString("TYPE"));
        smsDetails.setId(rs.getString("ID"));
        smsDetails.setMobile(rs.getString("MOBILE"));        
        return smsDetails;
	}
}
