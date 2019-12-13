/**
 * 
 */
package com.fa.dp.core.model.delegate;

import java.util.List;

import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.model.info.ModelDetailInfo;

/**
 *
 *
 */
public interface ModelDetailDelegate {

	/**
	 * 
	 * @return
	 * @throws SystemException
	 */
	public List<ModelDetailInfo> getAllModelDetails() throws SystemException;

	/**
	 * 
	 * @param modelName
	 * @return
	 * @throws SystemException
	 */
	public List<ModelDetailInfo> getMajorVersionDetails(String modelName) throws SystemException;

	/**
	 * 
	 * @param modelName
	 * @return
	 * @throws SystemException
	 */
	public List<ModelDetailInfo> getMinorVersionDetails(String modelName) throws SystemException;
}
