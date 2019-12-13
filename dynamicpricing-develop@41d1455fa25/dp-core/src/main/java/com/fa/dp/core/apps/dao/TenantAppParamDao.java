package com.fa.dp.core.apps.dao;

import java.util.List;

import com.fa.dp.core.apps.domain.TenantApp;
import com.fa.dp.core.apps.domain.TenantAppParam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TenantAppParamDao extends JpaRepository<TenantAppParam, String> {
	
	@Modifying
	@Query("update TenantAppParam app set app.attrValue = ?2, app.lastModifiedBy = ?3, app.lastModifiedDate = ?4 where app.attrKey = ?1")
	void setAppParameterValueByKey(String key, String value, String updatedBy, long updateOn);
	
	List<TenantAppParam> findByTenantAppAndClassificationOrderByLastModifiedDateDesc(TenantApp tntApp, String type);
}