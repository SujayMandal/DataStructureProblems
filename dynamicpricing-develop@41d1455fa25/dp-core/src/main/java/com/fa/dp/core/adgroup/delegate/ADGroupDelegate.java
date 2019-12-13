/**
 * 
 */
package com.fa.dp.core.adgroup.delegate;

import java.util.List;

import com.fa.dp.core.adgroup.domain.ADGroup;
import com.fa.dp.core.adgroup.info.ADGroupInfo;
import com.fa.dp.core.exception.SystemException;

/**
 *
 *
 */
public interface ADGroupDelegate {

	/**
	 * Returns all AD Groups defined in the system.
	 * 
	 * @return
	 * @throws SystemException
	 */
	public List<ADGroupInfo> getAllADGroups() throws SystemException;

	/**
	 * 
	 * @return
	 * @throws SystemException
	 */
	public List<ADGroup> getADGroupTypes() throws SystemException;

}
