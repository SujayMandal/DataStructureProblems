package com.fa.dp.business.ssinvestor.bo;

import java.util.List;

import com.fa.dp.business.pmi.entity.PmiInsuranceCompany;
import com.fa.dp.business.pmi.entity.PmiInsuranceCompaniesFile;
import com.fa.dp.business.ssinvestor.entity.SpclServicingInvestor;
import com.fa.dp.business.ssinvestor.entity.SpclServicingInvestorFile;

public interface SpclServicingInvestorBO {
	
	List<SpclServicingInvestor> findByActiveTrue();

	void updateActiveToInactive();

	SpclServicingInvestorFile saveSSInvFile(SpclServicingInvestorFile ssInvFileInfo);

	SpclServicingInvestor saveSSInvestor(SpclServicingInvestor ssInvestor);

	List<SpclServicingInvestorFile> getAllSSInvestorFiles();
	
	void updatePmiActiveToInactive();

	PmiInsuranceCompaniesFile savePmiCompFile(PmiInsuranceCompaniesFile pmiCompaniesFile);

	PmiInsuranceCompany savePmiInsuranceCompany(PmiInsuranceCompany pmiCompanies);

	List<PmiInsuranceCompany> findPmiCompsByActiveTrue();

	List<PmiInsuranceCompany> findPmiInsCompsByActiveTrue();
}
