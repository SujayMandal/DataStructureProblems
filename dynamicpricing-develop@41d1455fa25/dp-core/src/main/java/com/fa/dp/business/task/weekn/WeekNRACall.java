package com.fa.dp.business.task.weekn;

import java.util.Map;

import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;

public interface WeekNRACall {
	/**
	 * WeekN RA call
	 * @param info
	 * @Param modelName
	 * @Param modelMajorVersion
	 * @throws SystemException
	 */	
	Map<String, Object> prepareRAMapping(
            DPProcessWeekNParamInfo info, String modelName, String modelMajorVersion,
            String modelMinorVersion)throws SystemException;
}
