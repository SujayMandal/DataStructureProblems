package com.ca.umg.business.mappingnotification.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractAuditable;
@Entity
@Table(name = "NOTIFICATION_EVENT_TEMPLATE_MAPPING")
public class MappingNotificationEntity extends AbstractAuditable {
	private static final long serialVersionUID = 1L;
	
	    @Column(name = "NOTIFICATION_EVENT_ID")
	    @Property
	    private String notificationEventId;

	    @Property    
	    @Column(name = "NOTIFICATION_TEMPLATE_ID")
	    private String notificationTemplateId;

	    @Column(name = "NOTIFICATION_TYPE_ID")
	    @Property        
	    private String notifiacytionTypeId;
	    
	    @Column(name = "TENANT_ID")
	    @Property
	    private String tenantId;
	    
	    @Column(name = "TO_ADDRESS")
	    @Property    
	    private String toAddress;
	    
	    @Column(name = "FROM_ADDRESS")
	    @Property    
	    private String fromAddress;
	    
	    @Column(name = "CC_ADDRESS")
	    @Property    
	    private String ccAddress;
	    
	    @Column(name = "BCC_ADDRESS")
	    @Property    
	    private String bccAddress;
	    
	    @Column(name = "MOBILE")
	    @Property    
	    private String mobile;
	    
	    
	    @Column(name = "NAME")
	    @Property    
	    private String name;
	    

		public String getNotificationEventId() {
			return notificationEventId;
		}

		public void setNotificationEventId(String notificationEventId) {
			this.notificationEventId = notificationEventId;
		}

		public String getNotificationTemplateId() {
			return notificationTemplateId;
		}

		public void setNotificationTemplateId(String notificationTemplateId) {
			this.notificationTemplateId = notificationTemplateId;
		}

		public String getNotifiacytionTypeId() {
			return notifiacytionTypeId;
		}

		public void setNotifiacytionTypeId(String notifiacytionTypeId) {
			this.notifiacytionTypeId = notifiacytionTypeId;
		}

		public String getTenantId() {
			return tenantId;
		}

		public void setTenantId(String tenantId) {
			this.tenantId = tenantId;
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

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}


		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

}
