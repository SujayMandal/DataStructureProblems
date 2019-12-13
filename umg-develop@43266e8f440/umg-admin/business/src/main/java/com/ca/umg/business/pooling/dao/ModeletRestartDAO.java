package com.ca.umg.business.pooling.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ca.umg.business.pooling.entity.ModeletRestartConfig;


public interface ModeletRestartDAO extends JpaRepository<ModeletRestartConfig, String>{
	
    List<ModeletRestartConfig> findAllByOrderByTenantIdAscModelNameAndVersionAsc();

}
