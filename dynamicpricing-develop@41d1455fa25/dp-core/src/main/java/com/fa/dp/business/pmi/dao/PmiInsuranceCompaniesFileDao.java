package com.fa.dp.business.pmi.dao;

import com.fa.dp.business.pmi.entity.PmiInsuranceCompaniesFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PmiInsuranceCompaniesFileDao extends JpaRepository<PmiInsuranceCompaniesFile, String> {
	
	@Modifying
	@Query("UPDATE PmiInsuranceCompaniesFile SET active = false WHERE active = true")
	void updateActiveToInactive();

}
