package com.fa.dp.business.ssinvestor.bo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.business.pmi.entity.PmiInsuranceCompaniesFile;
import com.fa.dp.business.pmi.dao.PmiInsuranceCompaniesFileDao;
import com.fa.dp.business.pmi.entity.PmiInsuranceCompany;
import com.fa.dp.business.pmi.dao.PmiInsuranceCompanyDao;
import com.fa.dp.business.ssinvestor.entity.SpclServicingInvestor;
import com.fa.dp.business.ssinvestor.dao.SpclServicingInvestorDao;
import com.fa.dp.business.ssinvestor.entity.SpclServicingInvestorFile;
import com.fa.dp.business.ssinvestor.dao.SpclServicingInvestorFileDao;

@Named
public class SpclServicingInvestorBOImpl implements SpclServicingInvestorBO {
	
	@Inject
	private SpclServicingInvestorDao spclServicingInvestorDao;
	
	@Inject
	private SpclServicingInvestorFileDao spclServicingInvestorFileDao;
	
	@Inject 
	private PmiInsuranceCompanyDao pmiCompaniesDao;
	
	@Inject 
	private PmiInsuranceCompaniesFileDao pmiCompaniesFileDao;

	@Override
	public List<SpclServicingInvestor> findByActiveTrue() {
		return spclServicingInvestorDao.findByActiveTrue();
	}
	
	@Override
	public void updateActiveToInactive() {
		spclServicingInvestorFileDao.updateActiveToInactive();
	}

	@Override
	public SpclServicingInvestorFile saveSSInvFile(SpclServicingInvestorFile ssInvFile) {
		return spclServicingInvestorFileDao.save(ssInvFile);
	}

	@Override
	public SpclServicingInvestor saveSSInvestor(SpclServicingInvestor ssInvestor) {
		return spclServicingInvestorDao.save(ssInvestor);
	}

	@Override
	public List<SpclServicingInvestorFile> getAllSSInvestorFiles() {
		return spclServicingInvestorFileDao.findAll();
	}

	@Override
	public void updatePmiActiveToInactive() {
		pmiCompaniesFileDao.updateActiveToInactive();
	}

	@Override
	public PmiInsuranceCompaniesFile savePmiCompFile(PmiInsuranceCompaniesFile pmiCompaniesFile) {
		return pmiCompaniesFileDao.save(pmiCompaniesFile);
	}

	@Override
	public PmiInsuranceCompany savePmiInsuranceCompany(PmiInsuranceCompany pmiCompanies) {
		return pmiCompaniesDao.save(pmiCompanies);
	}

	@Override
	public List<PmiInsuranceCompany> findPmiCompsByActiveTrue() {
		return pmiCompaniesDao.findByActiveTrue();
	}

	@Override
	public List<PmiInsuranceCompany> findPmiInsCompsByActiveTrue() {
		return pmiCompaniesDao.findInsuranceCompanyByActiveTrue();
	}

}
