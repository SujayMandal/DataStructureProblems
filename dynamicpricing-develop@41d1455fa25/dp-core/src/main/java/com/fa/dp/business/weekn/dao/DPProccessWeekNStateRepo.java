package com.fa.dp.business.weekn.dao;

import java.util.List;

import com.fa.dp.business.weekn.entity.DPProccessWeekNStates;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DPProccessWeekNStateRepo extends JpaRepository<DPProccessWeekNStates, String> {

	List<DPProccessWeekNStates> findByZipCode(String zipCode);
	
}
