package com.ca.umg.business.mappingnotification.delegate;

import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mappingnotification.bo.MappingNotificationBo;
import com.ca.umg.business.mappingnotification.entity.MappingNotification;
import com.ca.umg.business.mappingnotification.entity.NotificationData;
import com.ca.umg.notification.model.MailDetails;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
@Named
public class MappingNotificationDelegateImpl implements MappingNotificationDelegate {

	@Inject
	private MappingNotificationBo bo;
	
	@Override
	public void createMapping(final MappingNotification nd)throws BusinessException,SystemException  {
		bo.createMapping(nd);
	}

	@Override
	public void updateMapping(final MappingNotification nd) throws BusinessException,SystemException {
		bo.updateMapping(nd);
	}

	@Override
	public void deleteMapping(final String mappingId) throws BusinessException,SystemException {
		bo.deleteMapping(mappingId);
	}

	@Override
	public MailDetails getMappingDataByMappingId(String mappingID)throws BusinessException, SystemException {
		return bo.getMappingDataByMappingId(mappingID);
	}

	@Override
	public List<NotificationData> getAllFeatureNotification()throws BusinessException, SystemException {
		return bo.getAllFeatureNotification();
	}

	@Override
	public boolean isDuplicateMapping(String tenantId, String eventId)throws BusinessException, SystemException {
		return bo.isDuplicateMapping(tenantId, eventId);
	}
	
	@Override
	public String getNotificationEventId(String tableName, String notificationName) throws BusinessException,SystemException {
	    return bo.getNotificationEventId(tableName, notificationName);
	}

	@Override
	public List<NotificationData> getAllEventDetails()throws BusinessException, SystemException {
		return bo.getAllEventDetails();
	}

	@Override
	public Set<String> getAllModels() throws BusinessException, SystemException {
		Set<String> modelWithVersion = bo.getAllModels();
		Set<String> modelList = new TreeSet<String>();
		Iterator<String> iterator = modelWithVersion.iterator();
		 while (iterator.hasNext()){
			   String model = iterator.next();
			   String modelsave = model.substring(0,model.lastIndexOf(PoolConstants.MODEL_SEPERATOR));
			   modelList.add(modelsave);
		 }
		return modelList;
	}

	@Override
	public MailDetails getTemplateDataByEventId(String eventID)throws BusinessException, SystemException {
		return bo.getTemplateDataByEventId(eventID);
	}

}
