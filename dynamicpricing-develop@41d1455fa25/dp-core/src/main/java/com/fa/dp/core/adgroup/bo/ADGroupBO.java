/**
 * 
 */
package com.fa.dp.core.adgroup.bo;

import java.util.List;

import com.fa.dp.core.adgroup.domain.ADGroup;
import com.fa.dp.core.exception.SystemException;

/**
 *
 *
 */
public interface ADGroupBO {

	/**
	 * 
	 * @return
	 * @throws SystemException
	 */
	public List<ADGroup> getAllADGroups() throws SystemException;

	/**
	 * 
	 * @return
	 * @throws SystemException
	 */
	public List<ADGroup> getAdGroupTypes() throws SystemException;

}
