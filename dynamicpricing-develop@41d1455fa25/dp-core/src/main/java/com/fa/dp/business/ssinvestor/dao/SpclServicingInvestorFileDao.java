package com.fa.dp.business.ssinvestor.dao;

import com.fa.dp.business.ssinvestor.entity.SpclServicingInvestorFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SpclServicingInvestorFileDao extends JpaRepository<SpclServicingInvestorFile, String> {
	
	@Modifying
	@Query("UPDATE SpclServicingInvestorFile SET active = false WHERE active = true")
	void updateActiveToInactive();

}
