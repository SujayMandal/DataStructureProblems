package com.fa.dp.business.task;

import java.util.Map;

import com.fa.dp.business.validation.input.info.DPProcessParamInfo;

public interface Week0RACall {
	
	Map<String, Object> prepareRAMapping(
            DPProcessParamInfo infoObject, String modelName, String modelMajorVersion,
            String modelMinorVersion, String priceModeInput);

}
