/**
 * 
 */
package com.fa.dp.core.adgroup.delegate;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.core.adgroup.bo.ADGroupBO;
import com.fa.dp.core.adgroup.domain.ADGroup;
import com.fa.dp.core.adgroup.info.ADGroupInfo;
import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.exception.SystemException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
@Named
public class ADGroupDelegateImpl extends AbstractDelegate implements ADGroupDelegate {

	private static final Logger LOGGER = LoggerFactory.getLogger(ADGroupDelegateImpl.class);

	@Inject
	private ADGroupBO adGroupBO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fa.ra.client.core.adgroup.delegate.ADGroupDelegate#getAllADGroups()
	 */
	@Override
	public List<ADGroupInfo> getAllADGroups() throws SystemException {
		List<ADGroup> adGroups = adGroupBO.getAllADGroups();
		return convertToList(adGroups, ADGroupInfo.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fa.ra.client.core.adgroup.delegate.ADGroupDelegate#getADGroupTypes()
	 */
	@Override
	public List<ADGroup> getADGroupTypes() throws SystemException {
		List<ADGroup> adGroups = adGroupBO.getAdGroupTypes();
		return convertToList(adGroups, ADGroup.class);
	}

}
