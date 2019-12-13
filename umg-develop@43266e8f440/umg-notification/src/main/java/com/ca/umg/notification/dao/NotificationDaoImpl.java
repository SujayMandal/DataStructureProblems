package com.ca.umg.notification.dao;

import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;
import static com.ca.umg.notification.NotificationExceptionCodes.EVENT_NOOT_FOUND;
import static com.ca.umg.notification.NotificationExceptionCodes.NO_SUPER_ADMIN;
import static com.ca.umg.notification.NotificationExceptionCodes.TEMPLATE_NOT_AVAILABLE;
import static com.ca.umg.notification.NotificationExceptionCodes.NO_MAPPING_DATA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.notification.NotificationConstants;
import com.ca.umg.notification.NotificationExceptionCodes;
import com.ca.umg.notification.model.NotificationDetails;
import com.ca.umg.notification.model.NotificationEvent;
import com.ca.umg.notification.model.NotificationEventData;

@Named
public class NotificationDaoImpl implements NotificationDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationDaoImpl.class);
	
	private static final boolean ADMIN_AWARE = true;
	
	private static final String FETCH_EMAIL_DETAILS = "select netp.ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, SUBJECT_DEFINITION, BODY_DEFINITION, "
			+ "MAIL_CONTENT_TYPE, MOBILE, TYPE, CLASSIFICATION, TENANT_ID "
			+ "from NOTIFICATION_EVENT_TEMPLATE_MAPPING netp "
			+ "join NOTIFICATION_EMAIL_TEMPLATE net on netp.NOTIFICATION_TEMPLATE_ID = net.ID "
			+ "join NOTIFICATION_EVENT ne on netp.NOTIFICATION_EVENT_ID = ne.ID "
			+ "join NOTIFICATION_TYPE nt on netp.NOTIFICATION_TYPE_ID = nt.ID "
			+ "where ne.NAME = '$NAME$' and TENANT_ID = '$TENANT_CODE$' and IS_ACTIVE = 1";
	
	private static final String FETCH_TO_ADDRESS = "select TO_ADDRESS "
			+ "from NOTIFICATION_EVENT_TEMPLATE_MAPPING netp join NOTIFICATION_EVENT nt "
		    + "on nt.ID = netp.NOTIFICATION_EVENT_ID "
		    + "where nt.NAME = '$NAME$' and TENANT_ID = '$TENANT_CODE$'";
	
	private static final String FETCH_SUPER_ADMIN_TO_ADDRESS = "select OFFICIAL_EMAIL from USERS where sys_admin = true";
	
	private static final String FETCH_NOTIFICATION_EVENT = "select * from NOTIFICATION_EVENT where name = '$EVENT_NAME$'";
	
	private static final String FETCH_NOTIFICATION_EVENT_TEMPLATE_MAPPING = "select * from NOTIFICATION_EVENT_TEMPLATE_MAPPING where name = '$EVENT_NAME$'";

    
    private JdbcTemplate jdbcTemplate;

    @Inject 
    private DataSource dataSource;
    
    
    @PostConstruct
    public void initializeTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Override
	public List<NotificationDetails> getNotificationDetails(final NotificationEvent event, final NotificationEventData eventData) throws BusinessException, SystemException {
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		
		String sql = FETCH_EMAIL_DETAILS.replace("$NAME$", event.getName());
		sql = sql.replace("$TENANT_CODE$", eventData.getNotificationHeaders().getTenantCode());
		
		LOGGER.info("FETCH EMAIL DETAILS : " + sql);
		
		List<NotificationDetails> mailDetailsList = null;
		
		try {
	    	mailDetailsList = jdbcTemplate.query(sql, new RowMapper<NotificationDetails> () {

				@Override
	            public NotificationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
					return NotificationDetails.of(rs);
	            }
	    	});	
	    	
	    	
	    	if (mailDetailsList.isEmpty()) {
	    		LOGGER.info(TEMPLATE_NOT_AVAILABLE.getDescription());
//	    		createSystemException();
	    	}
	    	
	    	LOGGER.info("No of notification templates for {} are {}", event.getName(), mailDetailsList.size());
	    		    	
		} catch (Exception e) {
			LOGGER.error(TEMPLATE_NOT_AVAILABLE.getDescription());
			LOGGER.error(e.getMessage());
			createSystemException(TEMPLATE_NOT_AVAILABLE);
		} finally {
	    	setAdminAware(actualAdminAware);			
		}
		
		return mailDetailsList;
	}
	
	@Override
	public String getToAddresses(final String eventName, final String tenantCode) throws BusinessException, SystemException {
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		
		String sql = FETCH_TO_ADDRESS.replace("$NAME$", eventName);
		sql = sql.replace("$TENANT_CODE$", tenantCode);
		
		LOGGER.info("FETCH TO ADDRESS DETAILS : " + sql);

		String toAddress = null;
		try {
			toAddress = jdbcTemplate.queryForObject(sql, String.class);
	    	
	    	LOGGER.info("No of addresses got for for {} are {}", eventName, toAddress);
	    		    	
		} catch (Exception e) {
			LOGGER.error(TEMPLATE_NOT_AVAILABLE.getDescription());
			LOGGER.error(e.getMessage());
		} finally {
	    	setAdminAware(actualAdminAware);			
		}
		
		return toAddress;
	}
	
	@Override
	public String getSuperAdminToAddresses() throws BusinessException, SystemException {
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		
		LOGGER.info("FETCH SUPER ADMIN TO ADDRESS DETAILS : " + FETCH_SUPER_ADMIN_TO_ADDRESS);

		String toAddress = null;
		
		try {
			final List<Map<String, Object>> addresses = jdbcTemplate.queryForList(FETCH_SUPER_ADMIN_TO_ADDRESS);
	    	
			if (addresses != null && !addresses.isEmpty()) {
				final StringBuilder sb = new StringBuilder();
				for (Map<String, Object> address : addresses) {
					if (address != null && address.containsKey(NotificationConstants.OFFICIAL_EMAIL) && address.get(NotificationConstants.OFFICIAL_EMAIL) != null) {
						if (sb.length() > 0) {
							sb.append(NotificationConstants.MAIL_SEPERATOR);
						}
						
						sb.append(address.get(NotificationConstants.OFFICIAL_EMAIL).toString());
					}
				}
				
				toAddress = sb.toString();
			}
			
	    	if (toAddress == null || toAddress.isEmpty()) {
	    		LOGGER.error(NO_SUPER_ADMIN.getDescription());
	    	} else {	    	
	    		LOGGER.info("Super admin(s) email id is : ", toAddress);
	    	}
	    		    	
		} catch (Exception e) {
			LOGGER.error(NO_SUPER_ADMIN.getDescription());
			LOGGER.error(e.getMessage());
			createSystemException(NO_SUPER_ADMIN);
		} finally {
	    	setAdminAware(actualAdminAware);			
		}
		
		return toAddress;
	}
	
	@Override
	public NotificationEvent getNotificationEvent(final String eventName) throws BusinessException, SystemException {
		final String sql = FETCH_NOTIFICATION_EVENT.replace("$EVENT_NAME$", eventName);
		LOGGER.info("FETCH SUPER ADMIN TO ADDRESS DETAILS : " + sql);

		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		
		NotificationEvent notificationEvent = null;
		
		try {
			final List<Map<String, Object>> events = jdbcTemplate.queryForList(sql);
	    	
			if (events != null && !events.isEmpty()) {
				for (Map<String, Object> event : events) {
					
					notificationEvent = new NotificationEvent();
					notificationEvent.setClassification(event.get("CLASSIFICATION").toString());
					notificationEvent.setId(event.get("ID").toString());
					notificationEvent.setDescription(event.get("DESCRIPTION").toString());
					notificationEvent.setName(event.get("NAME").toString());
					
					break;
				}
					
			}
			
	    	if (notificationEvent == null) {
	    		LOGGER.error(EVENT_NOOT_FOUND.getDescription());
	    		createSystemException(EVENT_NOOT_FOUND);
	    	} else {	    	
	    		LOGGER.info("Notification Event found, it is : {}", notificationEvent);
	    	}
	    		    	
		} catch (Exception e) {
			LOGGER.error(EVENT_NOOT_FOUND.getDescription());
			LOGGER.error(e.getMessage());
			createSystemException(EVENT_NOOT_FOUND);
		} finally {
	    	setAdminAware(actualAdminAware);			
		}
		
		return notificationEvent;
	}
	
	@Override
	public String getTenantCode(final String eventName) throws BusinessException, SystemException {
		final String sql = FETCH_NOTIFICATION_EVENT_TEMPLATE_MAPPING.replace("$EVENT_NAME$", eventName);
		LOGGER.info("FETCH TENANT Code FROM NOTIFICATION EVENT EMPLATE MAPPING: " + sql);

		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		
		String tenantCode = NotificationConstants.NA;
		
		try {
			final List<Map<String, Object>> events = jdbcTemplate.queryForList(sql);
	    	
			if (events != null && !events.isEmpty()) {
				for (Map<String, Object> event : events) {
					tenantCode = event.get(NotificationConstants.TENANT_ID).toString();
					break;
				}
					
			}	
			
			if (StringUtils.equalsIgnoreCase(tenantCode, NotificationConstants.NA)) {
	    		LOGGER.error(NO_MAPPING_DATA.getDescription());
	    		createSystemException(NO_MAPPING_DATA);
	    	} else {	    	
	    		LOGGER.info("TENANT Code found, it is : {}", tenantCode);
	    	}
	    		    	
		} catch (Exception e) {
			LOGGER.error(NO_MAPPING_DATA.getDescription());
			LOGGER.error(e.getMessage());
			createSystemException(NO_MAPPING_DATA);
		} finally {
	    	setAdminAware(actualAdminAware);			
		}
		LOGGER.info("TENANT CODE IS "+tenantCode+" For EVENT NAME "+eventName);
		return tenantCode;
	}
	
	private void setAdminAware(boolean adminAware) {
        if (getRequestContext() != null) {
            getRequestContext().setAdminAware(adminAware);
        }
    }
    
    private boolean getActualAdminAware() {
        boolean isAdminAware = false;
        if (getRequestContext() != null) {
            isAdminAware = getRequestContext().isAdminAware();
        }
        return isAdminAware;
    }
    
    private void createSystemException(final NotificationExceptionCodes exceptionCode) throws SystemException {
		final Object[] arguments = new Object[1];
		arguments[0] = exceptionCode.getDescription();
		SystemException.newSystemException(exceptionCode.getCode(), arguments);    	
    }
}
