package com.fa.dp.business.pmi.dao;

import java.util.List;

import com.fa.dp.business.pmi.entity.PmiInsuranceCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PmiInsuranceCompanyDao extends JpaRepository<PmiInsuranceCompany, String> {

	@Query("SELECT DISTINCT p.companyCode FROM PmiInsuranceCompany p, PmiInsuranceCompaniesFile f WHERE p.pmiCompaniesFileId = f.id AND f.active = true")
	List<PmiInsuranceCompany> findByActiveTrue();

	@Query("SELECT DISTINCT p FROM PmiInsuranceCompany p, PmiInsuranceCompaniesFile f WHERE p.pmiCompaniesFileId = f.id AND f.active = true")
	List<PmiInsuranceCompany> findInsuranceCompanyByActiveTrue();
	

}
