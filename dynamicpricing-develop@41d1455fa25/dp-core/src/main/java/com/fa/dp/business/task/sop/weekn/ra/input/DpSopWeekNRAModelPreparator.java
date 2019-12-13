package com.fa.dp.business.task.sop.weekn.ra.input;

import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;

import java.util.Map;

public interface DpSopWeekNRAModelPreparator {
	Map<String, Object> prepareSopWeekNRAMapping(DPSopWeekNParamInfo sopWeekNParam, String modelName, String modelMajorVersion,
			String modelMinorVersion) throws SystemException;
}
