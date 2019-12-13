package com.ca.umg.business.mappingnotification.dao;

import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;
import static com.ca.umg.notification.NotificationConstants.FROM_ADDRESS;
import static com.ca.umg.notification.NotificationConstants.MAIL_SEPERATOR;
import static com.ca.umg.notification.NotificationConstants.OFFICIAL_EMAIL;
import static com.ca.umg.notification.NotificationExceptionCodes.FIND_ID_FAILED;
import static com.ca.umg.notification.NotificationExceptionCodes.FIND_TYPE_FAILED;
import static com.ca.umg.notification.NotificationExceptionCodes.NO_MAPPING_DATA;
import static com.ca.umg.notification.NotificationExceptionCodes.NO_SUPER_ADMIN;
import static com.ca.umg.notification.model.NotificationTypes.MAIL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.business.mappingnotification.entity.NotificationData;
import com.ca.umg.notification.model.MailDetails;
import com.ca.umg.notification.model.NotificationDetails;
import com.ca.umg.notification.util.NotificationUtil;

@SuppressWarnings("PMD")
@Named
public class NotificationDataDaoImpl implements NotificationDataDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationDataDaoImpl.class);

	private static final boolean ADMIN_AWARE = true;

	private static final String NOTIFICATION_RELATED_ID= "Select ID from $TABLENAME$ where NAME='$NAME$' ";

	private static final String NOTIFICATION_TYPE_ID= "Select ID from $TABLENAME$ where TYPE='$TYPE$' ";

	private static final String FETCH_SUPER_ADMIN_TO_ADDRESS = "select OFFICIAL_EMAIL from USERS where sys_admin = true";

	private static final String FETCH_EMAIL_DETAILS = "select netp.ID, TO_ADDRESS, FROM_ADDRESS, CC_ADDRESS, BCC_ADDRESS, SUBJECT_DEFINITION, BODY_DEFINITION, "
			+ "MAIL_CONTENT_TYPE, MOBILE, TYPE, CLASSIFICATION , TENANT_ID "
			+ "from NOTIFICATION_EVENT_TEMPLATE_MAPPING netp "
			+ "join NOTIFICATION_EMAIL_TEMPLATE net on netp.NOTIFICATION_TEMPLATE_ID = net.ID "
			+ "join NOTIFICATION_EVENT ne on netp.NOTIFICATION_EVENT_ID = ne.ID "
			+ "join NOTIFICATION_TYPE nt on netp.NOTIFICATION_TYPE_ID = nt.ID "
			+ "where netp.ID = '$ID$'and TENANT_ID ='$TID$' and IS_ACTIVE = 1";

	private static final String FETCH_NOTIFICATION_DETAILS = "select net.TENANT_ID, net.ID, net.LAST_UPDATED_BY, net.LAST_UPDATED_ON, "
			+ "NOTIFICATION_EVENT.NAME , NOTIFICATION_EVENT.DESCRIPTION  from NOTIFICATION_EVENT_TEMPLATE_MAPPING net, NOTIFICATION_EVENT "
			+ "where net.NOTIFICATION_EVENT_ID = NOTIFICATION_EVENT.ID and net.TENANT_ID ='$TID$' and NOTIFICATION_EVENT.CLASSIFICATION = 'Feature'";

	private static final String IS_DUPLICATE_MAPPING = "select COUNT(ID) from NOTIFICATION_EVENT_TEMPLATE_MAPPING where TENANT_ID = '$TID$' and NOTIFICATION_EVENT_ID = '$EID$'";

	private static final String GET_ALL_EVENT = "select ID, NAME, DESCRIPTION from NOTIFICATION_EVENT where CLASSIFICATION = 'Feature' ORDER BY NAME";
	
	private static final String GET_NOTIFICATION_TEMPLATE_BY_EVENT_ID = " select net.SUBJECT_DEFINITION , net.BODY_DEFINITION from NOTIFICATION_EMAIL_TEMPLATE net, NOTIFICATION_EVENT ne "
			+ "where ne.ID = net.NOTIFICATION_EVENT_ID and ne.ID = '$NEID$'";

	private static final String GET_TEMPLATE_ID_BY_EVENT_ID = "select ID from NOTIFICATION_EMAIL_TEMPLATE where NOTIFICATION_EVENT_ID='$EID$'";
	@Inject
	@Named(value = "dataSource")
	private DataSource dataSource;

	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	 @Inject
	 private SystemParameterProvider systemParameterProvider;
	 
	@Override
	public String findId(String tableName, String name) throws SystemException {
		String Id = null;
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		try{
			String sql = NOTIFICATION_RELATED_ID.replace("$TABLENAME$", tableName);
			sql = sql.replace("$NAME$",name);
			LOGGER.info("FETCH ID: " + sql);
			Id = jdbcTemplate.queryForObject(sql, String.class);
		}
		catch (Exception e) {
			final Object[] arguments = new Object[1];
			arguments[0] = FIND_ID_FAILED.getDescription();
			LOGGER.error(FIND_ID_FAILED.getDescription());
			LOGGER.error(e.getMessage());
		} finally {
			setAdminAware(actualAdminAware);			
		}

		return Id;
	}

	@Override
	public String findTypeId(String tableName, String name) throws SystemException{
		String Id = null;
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		try{
			String sql = NOTIFICATION_TYPE_ID.replace("$TABLENAME$", tableName);
			sql = sql.replace("$TYPE$",name);
			LOGGER.info("FETCH TYPE ID: " + sql);
			Id = jdbcTemplate.queryForObject(sql, String.class);
		}
		catch (Exception e) {
			final Object[] arguments = new Object[1];
			arguments[0] = FIND_TYPE_FAILED.getDescription();
			LOGGER.error(FIND_TYPE_FAILED.getDescription());
			LOGGER.error(e.getMessage());
			SystemException.newSystemException(FIND_TYPE_FAILED.getCode(), arguments);
		} finally {
			setAdminAware(actualAdminAware);			
		}
		return Id;
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
					if (address != null && address.containsKey(OFFICIAL_EMAIL) && address.get(OFFICIAL_EMAIL) != null) {
						if (sb.length() > 0) {
							sb.append(MAIL_SEPERATOR);
						}

						sb.append(address.get(OFFICIAL_EMAIL).toString());
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
			final Object[] arguments = new Object[1];
			arguments[0] = NO_SUPER_ADMIN.getDescription();
			LOGGER.error(NO_SUPER_ADMIN.getDescription());
			LOGGER.error(e.getMessage());
			SystemException.newSystemException(NO_SUPER_ADMIN.getCode(), arguments); 
		} finally {
			setAdminAware(actualAdminAware);			
		}

		return toAddress;
	}


	@Override
	public MailDetails getMappingDataByMappingId(String mappingID) throws SystemException {
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		MailDetails mailDetails = null;
		String sql = FETCH_EMAIL_DETAILS.replace("$ID$", mappingID);
		sql = sql.replace("$TID$", RequestContext.getRequestContext().getTenantCode());
		LOGGER.info("FETCH EMAIL DETAILS : " + sql);

		List<NotificationDetails> mailDetailsList = null;
		try {
			mailDetailsList = jdbcTemplate.query(sql, new RowMapper<NotificationDetails>() {
				@Override
				public NotificationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
					return NotificationDetails.of(rs);
				}
			});

			if (mailDetailsList.isEmpty()) {
				LOGGER.error(NO_MAPPING_DATA.getDescription());
			} else {
				for (final NotificationDetails nd : mailDetailsList) {
					if (nd.getNotificationType().equalsIgnoreCase(
							MAIL.getType())) {
						mailDetails = (MailDetails) nd;
						final String subject = new String(mailDetails.getSubjectDefinition());
					    final String body = new String(mailDetails.getBodyDefinition());
					    mailDetails.setSubject(subject);
					    mailDetails.setBodyText(body);
					}
				}
			}
		} catch (Exception e) {
			final Object[] arguments = new Object[1];
			arguments[0] = NO_MAPPING_DATA.getDescription();
			LOGGER.error(NO_MAPPING_DATA.getDescription());
			LOGGER.error(e.getMessage());
			SystemException.newSystemException(NO_MAPPING_DATA.getCode(), arguments); 
		}
		finally {
			setAdminAware(actualAdminAware);			
		}
		return mailDetails;
	}

	@Override
	public List<NotificationData> getAllFeatureNotification()throws BusinessException, SystemException {
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		final List<NotificationData> notificationList=new ArrayList<NotificationData>(); 
		String sql = FETCH_NOTIFICATION_DETAILS.replace("$TID$", RequestContext.getRequestContext().getTenantCode());
		LOGGER.info("FETCH NOTIFICATION DETAILS: " + sql);
		try{
			jdbcTemplate.query(sql,new ResultSetExtractor<List<NotificationData>>(){  
				@Override  
				public List<NotificationData> extractData(ResultSet rs) throws SQLException  
				{  
					while(rs.next()){  
						NotificationData notificationData=new NotificationData();  
						notificationData.setTenantCode(rs.getString("TENANT_ID")); 
						notificationData.setId(rs.getString("ID"));  
						notificationData.setLastUpdatedBy(rs.getString("LAST_UPDATED_BY")); 
						notificationData.setLastUpdatedOn(rs.getLong("LAST_UPDATED_ON")); 
						notificationData.setEventName(rs.getString("NAME"));
						notificationData.setDescription(rs.getString("DESCRIPTION"));
						notificationData.setLastupdatedOnDate(NotificationUtil.getDateTimeFormatted(notificationData.getLastUpdatedOn()));
						notificationList.add(notificationData); 
						
					}  
					return notificationList;  
				}  
			});  
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		finally {
			setAdminAware(actualAdminAware);			
		}
		return notificationList;
	}

	@Override
	public boolean isDuplicateMapping(String tenantId, String eventId)throws BusinessException, SystemException {
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		boolean isDuplicate = false;
		String sql = IS_DUPLICATE_MAPPING.replace("$TID$", tenantId);
		sql = sql.replace("$EID$", eventId);
		LOGGER.info("CHECK DEPLICATE MAPPING: " + sql);
		try{
			int count = jdbcTemplate.queryForObject(sql, Integer.class);
			if (count > 0) {
				isDuplicate = true;
			}
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			throw e;
		} finally {
			setAdminAware(actualAdminAware);			
		}
		return isDuplicate;
	}

	@Override
	public List<NotificationData> getAllEventDetails() throws BusinessException, SystemException {//rename
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		final List<NotificationData> notificationList=new ArrayList<NotificationData>(); 
		LOGGER.info("FETCH NOTIFICATION DETAILS: " + GET_ALL_EVENT);
		try{
			jdbcTemplate.query(GET_ALL_EVENT,new ResultSetExtractor<List<NotificationData>>(){  
				@Override  
				public List<NotificationData> extractData(ResultSet rs) throws SQLException  
				{  
					while(rs.next()){  
						NotificationData notificationData=new NotificationData();  
						notificationData.setId(rs.getString("ID"));  
						notificationData.setEventName(rs.getString("NAME"));
						notificationData.setDescription(rs.getString("DESCRIPTION"));
						notificationList.add(notificationData);  
					}  
					return notificationList;  
				}  
			});  
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		finally {
			setAdminAware(actualAdminAware);			
		}
		
		return notificationList;
	}

	
	@Override
	public MailDetails getTemplateDataByEventId(String eventID)throws BusinessException, SystemException {
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		final MailDetails mailDetails = new MailDetails();
		String sql = GET_NOTIFICATION_TEMPLATE_BY_EVENT_ID.replace("$NEID$", eventID);
		LOGGER.info("FETCH TEMPLATE DETAILS : " + sql);
		try {
			jdbcTemplate.query(sql,new ResultSetExtractor<MailDetails>(){  
				@Override  
				public MailDetails extractData(ResultSet rs) throws SQLException  
				{  
					while(rs.next()){  
						 mailDetails.setSubjectDefinition(rs.getBytes("SUBJECT_DEFINITION"));
					     mailDetails.setBodyDefinition(rs.getBytes("BODY_DEFINITION"));
					     mailDetails.setFromAddress(systemParameterProvider.getParameter(FROM_ADDRESS));
					     mailDetails.setTenantCode(RequestContext.getRequestContext().getTenantCode());
					     final String subject = new String(mailDetails.getSubjectDefinition());
					     final String body = new String(mailDetails.getBodyDefinition());
					     mailDetails.setSubject(subject);
					     mailDetails.setBodyText(body);
					}  
					return mailDetails;  
				}  
			});  
		} catch (Exception e) {
			final Object[] arguments = new Object[1];
			arguments[0] = NO_MAPPING_DATA.getDescription();
			LOGGER.error(NO_MAPPING_DATA.getDescription());
			LOGGER.error(e.getMessage());
			SystemException.newSystemException(NO_MAPPING_DATA.getCode(), arguments); 
		}
		finally {
			setAdminAware(actualAdminAware);			
		}
		return mailDetails;
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

	@Override
	public String getTemplateIdByEventId(String eventID) throws BusinessException, SystemException {
		String Id = null;
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		try{
			String sql = GET_TEMPLATE_ID_BY_EVENT_ID.replace("$EID$", eventID);
			LOGGER.info("FETCH TEMPLATE ID BY EVENT ID: " + sql);
			Id = jdbcTemplate.queryForObject(sql, String.class);
		}
		catch (Exception e) {
			final Object[] arguments = new Object[1];
			arguments[0] = FIND_ID_FAILED.getDescription();
			LOGGER.error(FIND_ID_FAILED.getDescription());
			LOGGER.error(e.getMessage());
		} finally {
			setAdminAware(actualAdminAware);			
		}

		return Id;
	}

	


}
