package com.fa.dp.core.systemparam.adapter;

import java.util.Map;

import com.fa.dp.core.apps.info.RATenantAppParamsRequest;
import com.fa.dp.core.exception.SystemException;

public interface ClassificationAdapter {

	Map<String, String> appParamsToJsonConverter(RATenantAppParamsRequest req) throws SystemException;
	
	//RATenantAppParamsRequest jsonToAppParamConverter(JSONObject jsonObject) throws SystemException, JSONException;
}
