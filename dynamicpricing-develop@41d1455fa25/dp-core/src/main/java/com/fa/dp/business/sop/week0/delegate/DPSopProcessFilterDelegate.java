package com.fa.dp.business.sop.week0.delegate;

import java.util.List;

import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0ProcessStatus;
import com.fa.dp.business.sop.week0.input.info.DPSopParamEntryInfo;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.core.exception.SystemException;

/**
 * @author misprakh
 */

public interface DPSopProcessFilterDelegate {

	/**
	 * Filter asset number based on asset value
	 *
	 * @param inputParamEntry
	 * @param filterName
	 * @throws SystemException
	 */
	void filterOnAssetValue(DPSopParamEntryInfo inputParamEntry, String filterName) throws SystemException;

	/**
	 * Used to check the status of file If status is in-progress then process will not start
	 *
	 * @param fileStatus
	 * @return
	 * @throws SystemException
	 */
	DPSopWeek0ProcessStatus checkForFileStatus(String fileStatus) throws SystemException;;

	/**
	 *
	 * @param dpSopParamEntryInfo
	 * @param filterName
	 */
	void filterOnDuplicates(DPSopParamEntryInfo dpSopParamEntryInfo, String filterName) throws SystemException;

	/**
	 *
	 * @param state
	 * @param lowerAssetValue
	 * @param higherAssetValue
	 * @param classification
	 * @return
	 * @throws SystemException
	 */
	int countBenchmark(String state, int lowerAssetValue, int higherAssetValue, String classification) throws SystemException;

	/**
	 *
	 * @param state
	 * @param lowerAssetValue
	 * @param higherAssetValue
	 * @param classification
	 * @return
	 */
	int countModeled(String state, int lowerAssetValue, int higherAssetValue, String classification) throws SystemException;

	/**
	 *
	 * @param dpSopWeek0Param
	 * @return
	 */
	DPSopWeek0Param saveDPSopWeek0Param(DPSopWeek0Param dpSopWeek0Param) throws SystemException;
	
	/**
     * @param   fileId
     * @param   type
     * @return List<DPSopWeek0ParamInfo>
     * @throws SystemException
     */
	List<DPSopWeek0ParamInfo> getAssetDetails(String fileId, String type) throws SystemException;
}
