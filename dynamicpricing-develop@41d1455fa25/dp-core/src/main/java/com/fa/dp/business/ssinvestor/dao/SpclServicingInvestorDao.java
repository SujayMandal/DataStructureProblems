package com.fa.dp.business.ssinvestor.dao;

import java.util.List;

import com.fa.dp.business.ssinvestor.entity.SpclServicingInvestor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpclServicingInvestorDao extends JpaRepository<SpclServicingInvestor, String> {

	@Query("SELECT DISTINCT i.investorCode FROM SpclServicingInvestor i, SpclServicingInvestorFile f WHERE i.ssInvestorFileId = f.id AND f.active = true")
	List<SpclServicingInvestor> findByActiveTrue();
	
}
