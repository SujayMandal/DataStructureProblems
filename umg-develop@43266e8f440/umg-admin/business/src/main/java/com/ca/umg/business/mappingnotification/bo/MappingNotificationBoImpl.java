package com.ca.umg.business.mappingnotification.bo;

import static com.ca.framework.core.exception.BusinessException.newBusinessException;
import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;
import static com.ca.umg.notification.NotificationConstants.NOTIFICATION_MAIL_TYPE;
import static com.ca.umg.notification.NotificationConstants.NOTIFICATION_TYPE_TABLENAME;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mappingnotification.dao.MappingNotificationDao;
import com.ca.umg.business.mappingnotification.dao.NotificationDataDao;
import com.ca.umg.business.mappingnotification.entity.MappingNotification;
import com.ca.umg.business.mappingnotification.entity.MappingNotificationEntity;
import com.ca.umg.business.mappingnotification.entity.NotificationData;
import com.ca.umg.business.version.dao.VersionContainerDAO;
import com.ca.umg.notification.dao.NotificationDaoImpl;
import com.ca.umg.notification.model.MailDetails;
@SuppressWarnings("PMD")
@Named
public class MappingNotificationBoImpl implements MappingNotificationBo {
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationDaoImpl.class);
	private static final boolean ADMIN_AWARE = true;
	@Autowired
	private MappingNotificationDao dao;
	
	@Autowired
	private NotificationDataDao notificationDataDao;
	
	@Inject
	private VersionContainerDAO versionContainerDAO;
	
	@Override
	public void createMapping(final MappingNotification md) throws BusinessException,SystemException {
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		List<String> errorList =  MappingNotificationValidator.validateCreateMapping(md);
		try{
		if (!errorList.isEmpty()) {
			LOGGER.error("Validation Issue for create mapping : " + errorList );
				newBusinessException(BusinessExceptionCodes.BSE000401, new String[] { "Validation Issue : " + errorList });
		}
		if(!(notificationDataDao.isDuplicateMapping(md.getTenantCode(), md.getNotificationEventId()))){
		MappingNotificationEntity entity = new MappingNotificationEntity();
		if(md.getCcAddress()!=null){
		entity.setCcAddress(md.getCcAddress());
		}
		entity.setFromAddress(md.getFromAddress());
		entity.setName(md.getName());
		//entity.setMobile(md.getMobile());
		entity.setNotifiacytionTypeId(notificationDataDao.findTypeId(NOTIFICATION_TYPE_TABLENAME, NOTIFICATION_MAIL_TYPE));
		entity.setNotificationEventId(md.getNotificationEventId());
		entity.setNotificationTemplateId(notificationDataDao.getTemplateIdByEventId(md.getNotificationEventId()));
		entity.setTenantId(getRequestContext().getTenantCode());
		entity.setToAddress(md.getToAddress());
		dao.saveAndFlush(entity);
		}
		else{
			LOGGER.error("Duplicate Row Found" );
			newBusinessException(BusinessExceptionCodes.BSE000402, new String[] { "Event already configured " });
		}
		}
		catch (BusinessException | SystemException e) {
			 LOGGER.error(e.getLocalizedMessage());
			 newBusinessException(BusinessExceptionCodes.BSE000402, new String[] { e.getLocalizedMessage() });
            
            //send failure message
		} 
		catch(Exception ex){
			 newBusinessException(BusinessExceptionCodes.BSE000406, new String[] { "Notification add failed. Try again in some time or contact RA support at RealAnalyticsSupport@altisource.com for assistance" });
		}
		finally {
			setAdminAware(actualAdminAware);			
		}
	}

	@Override
	public void updateMapping(final MappingNotification md) throws BusinessException,SystemException {
		List<String> errorList =  MappingNotificationValidator.validateUpdateMapping(md);
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		try{
		if(errorList.isEmpty()){
		MappingNotificationEntity entity = dao.findOne(md.getId());
		entity.setCcAddress(md.getCcAddress());
		entity.setToAddress(md.getToAddress());
		dao.saveAndFlush(entity);
		}
		else{
			LOGGER.error("Validation Issue For Update Mapping : " + errorList );
			newBusinessException(BusinessExceptionCodes.BSE000403, new String[] { "Validation Issue" + errorList });
		}
	}
		catch (BusinessException  e) {
			 LOGGER.error(e.getLocalizedMessage());
			 newBusinessException(BusinessExceptionCodes.BSE000403, new String[] { e.getLocalizedMessage() });
           
           //send failure message
		} 
		catch(Exception ex){
			 newBusinessException(BusinessExceptionCodes.BSE000406, new String[] { "Notification edit failed. Try again in some time or contact RA support at RealAnalyticsSupport@altisource.com for assistance" });
		}
		finally {
			setAdminAware(actualAdminAware);			
		}
	}
	
	@Override
	public void deleteMapping(final String mappingId) throws BusinessException,SystemException {
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		try{
		dao.delete(mappingId);
		}
		catch(Exception ex){
			 newBusinessException(BusinessExceptionCodes.BSE000406, new String[] { "Notification delete failed. Try again in some time or contact RA support at RealAnalyticsSupport@altisource.com for assistance" });
		}
		finally {
			setAdminAware(actualAdminAware);			
		}
		}

	@Override
	public MailDetails getMappingDataByMappingId(String mappingID)throws BusinessException, SystemException {
		return notificationDataDao.getMappingDataByMappingId(mappingID);
	}

	@Override
	public List<NotificationData> getAllFeatureNotification()throws BusinessException, SystemException {
		return notificationDataDao.getAllFeatureNotification();
	}

	@Override
	public boolean isDuplicateMapping(String tenantId, String eventId)throws BusinessException, SystemException {
		return notificationDataDao.isDuplicateMapping(tenantId, eventId);
	}
	
	@Override
	public String getNotificationEventId(String tableName, String notificationName) throws BusinessException,SystemException {
	    return notificationDataDao.findId(tableName, notificationName);
	}

	@Override
	public List<NotificationData> getAllEventDetails()throws BusinessException, SystemException {
		return notificationDataDao.getAllEventDetails();
	}

	@Override
	public Set<String> getAllModels() throws BusinessException, SystemException {
		return versionContainerDAO.getAllModelVersions();
	}

	@Override
	public MailDetails getTemplateDataByEventId(String eventID) throws BusinessException, SystemException {
		
		return notificationDataDao.getTemplateDataByEventId(eventID);
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
}
