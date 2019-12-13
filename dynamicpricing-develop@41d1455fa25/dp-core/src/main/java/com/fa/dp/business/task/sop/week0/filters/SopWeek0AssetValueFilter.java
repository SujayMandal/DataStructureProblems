package com.fa.dp.business.task.sop.week0.filters;

import com.fa.dp.core.exception.SystemException;

/**
 * @author misprakh
 */

public interface SopWeek0AssetValueFilter {

	/**
	 * Default declaration for asset value filter.
	 * This method filters the assets or loan number based on the asset value defined in the system.
	 *
	 * @param data
	 * @throws SystemException
	 *
	 */
	public void executeAssetValueFilter(Object data, String filterName) throws SystemException;
}
