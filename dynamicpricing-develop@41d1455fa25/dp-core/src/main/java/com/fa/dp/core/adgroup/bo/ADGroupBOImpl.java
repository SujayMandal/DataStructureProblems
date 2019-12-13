/**
 * 
 */
package com.fa.dp.core.adgroup.bo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.core.adgroup.dao.ADGroupDao;
import com.fa.dp.core.adgroup.domain.ADGroup;
import com.fa.dp.core.exception.SystemException;

/**
 *
 *
 */
@Named
public class ADGroupBOImpl implements ADGroupBO {

	@Inject
	private ADGroupDao adGroupDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fa.ra.client.core.adgroup.bo.ADGroupBO#getAllADGroups()
	 */
	@Override
	public List<ADGroup> getAllADGroups() throws SystemException {
		return adGroupDao.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fa.ra.client.core.adgroup.bo.ADGroupBO#getAdGroupTypes()
	 */
	@Override
	public List<ADGroup> getAdGroupTypes() throws SystemException {
		return adGroupDao.findAll();
	}


}
