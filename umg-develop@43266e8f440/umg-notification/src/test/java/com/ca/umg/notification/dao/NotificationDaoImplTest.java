package com.ca.umg.notification.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.notification.model.NotificationDetails;
import com.ca.umg.notification.model.NotificationEvent;
import com.ca.umg.notification.model.NotificationEventData;

@Ignore
public class NotificationDaoImplTest {
	private JdbcTemplate jdbcTemplate;
	@Inject
    @Named(value = "dataSource")
    private DataSource dataSource;
    
    
    @PostConstruct
    public void initializeTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Test
	public void getToAddressesTest() throws BusinessException, SystemException {
		NotificationDao dao = new NotificationDaoImpl();
		String emailAddress = dao.getToAddresses("MODEL APPROVAL EVENT","localhost");
		assertEquals(1, emailAddress.split(",").length);
	}

	@Test
	public void getNotificationDetailsTest() throws BusinessException,SystemException {
		NotificationEvent event = null;
		NotificationEventData eventData = null;
		NotificationDao dao = new NotificationDaoImpl();
		List<NotificationDetails> nList = new ArrayList<NotificationDetails>();
		nList = dao.getNotificationDetails(event, eventData);
		assertNotNull(nList);
	}
}
