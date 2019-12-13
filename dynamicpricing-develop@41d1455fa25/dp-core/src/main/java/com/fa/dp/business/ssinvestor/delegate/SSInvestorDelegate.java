package com.fa.dp.business.ssinvestor.delegate;

import java.io.IOException;
import java.util.List;

import com.fa.dp.business.pmi.info.PmiInsuranceCompanyInfo;
import com.fa.dp.business.ssinvestor.info.SpclServicingInvestorFileInfo;
import com.fa.dp.business.ssinvestor.info.SpclServicingInvestorInfo;
import com.fa.dp.core.exception.SystemException;

import org.springframework.web.multipart.MultipartFile;

public interface SSInvestorDelegate {
	
	List<SpclServicingInvestorInfo> validateFile(MultipartFile file) throws SystemException, IOException;
	
	List<SpclServicingInvestorInfo> uploadSSInvestors(String fileName, List<SpclServicingInvestorInfo> ssInvestors);

	List<SpclServicingInvestorFileInfo> getSsInvestorFiles();

	List<PmiInsuranceCompanyInfo> validatePmiCompaniesFile(MultipartFile file) throws SystemException, IOException;

	List<PmiInsuranceCompanyInfo> uploadPmiCompanies(String fileName, List<PmiInsuranceCompanyInfo> pmiCompaniesList);

}
