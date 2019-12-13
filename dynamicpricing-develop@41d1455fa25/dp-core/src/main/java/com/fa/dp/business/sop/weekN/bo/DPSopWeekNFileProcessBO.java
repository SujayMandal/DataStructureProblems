package com.fa.dp.business.sop.weekN.bo;

import java.util.List;
import java.util.Map;

import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.sop.weekN.entity.DPSopWeekNProcessStatus;
import com.fa.dp.core.exception.SystemException;

public interface DPSopWeekNFileProcessBO {

	/**
	 *
	 * @param id
	 * @return
	 */
	DPSopWeekNProcessStatus findSopWeekNProcessById(String id) throws SystemException;
	
	Map<String,DPSopWeek0Param> findLatestNonDuplicateInSopWeek0ForAsset(List<String> assetNumbers);

}
