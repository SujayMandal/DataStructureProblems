package com.ca.umg.notification;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.notification.model.NotificationAttachment;
import com.ca.umg.notification.model.NotificationEvent;
import com.ca.umg.notification.model.NotificationEventData;

@Ignore
public class NotificationServiceImplTest {
	 private MockMvc mockMvc;
	 @Autowired
	    private WebApplicationContext ctx;
	 @Before
	    public void setUp() {
	        mockMvc = webAppContextSetup(ctx).build();
	        //mockMvc = standaloneSetup(controller).build();
	       // buildDummyLists();
	    }
	@Test(expected = NullPointerException.class)
	public void sendNotificationTest() throws SystemException, BusinessException {
		final NotificationEvent event = null;
		final NotificationEventData eventData = null;
		final boolean async = false;
		NotificationService service = new NotificationServiceImpl();
		//service.sendNotification(event, eventData, async);
		
	}
	
	@Test(expected = NullPointerException.class)
	public void sendMailWithAttachmentsTest() throws SystemException, BusinessException {
		final NotificationEvent event = null;
		final NotificationEventData eventData = null;
		final List<NotificationAttachment> eventDataList = new ArrayList<NotificationAttachment>();
		final boolean async = false;
		NotificationService service = new NotificationServiceImpl();
		//service.sendMailWithAttachments(event, eventData, eventDataList, async);
		assertNotNull(eventDataList);
	}
}
