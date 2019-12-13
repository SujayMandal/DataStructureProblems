package com.ca.umg.business.mappingnotification.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ca.umg.business.mappingnotification.entity.MappingNotificationEntity;

public interface MappingNotificationDao extends JpaRepository<MappingNotificationEntity, String>, JpaSpecificationExecutor<MappingNotificationEntity> {

	//public MappingNotificationEntity findByTemplateName(String poolName);
}
