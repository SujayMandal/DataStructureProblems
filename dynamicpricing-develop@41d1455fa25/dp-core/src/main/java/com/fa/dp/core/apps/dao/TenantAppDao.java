package com.fa.dp.core.apps.dao;

import com.fa.dp.core.apps.domain.TenantApp;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantAppDao extends JpaRepository<TenantApp, String> {

/*	@Query("SELECT DISTINCT apps FROM AdGroupAppMapping mapping JOIN mapping.raTntApp apps "
			+ " JOIN mapping.raTntAdGroup groups WHERE groups.name IN :adGroupNames order by apps.appPriority ASC")
	public List<TenantApp> getTenantAppsByADGroupsSortedByPriorty(@Param("adGroupNames") List<String> adGroups);*/
	
	TenantApp findByCode(String code);

}
