package com.ca.umg.business.tenant.report.model.util;

import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.DEFAULT_KEY;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.combinedKey;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.umg.business.mid.extraction.info.TidIOInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;

@SuppressWarnings("PMD")
public class TenantDataTypeMapUtil {

	public static Map<String, WeakReference<TidParamInfo>> getTenantInputDataTypeKeyMap(final TidIOInfo tidIOInfo) {
		final Map<String, WeakReference<TidParamInfo>> dataTypeMap = new HashMap<String, WeakReference<TidParamInfo>>();
		createTidDataTypeKeyMap(dataTypeMap, tidIOInfo.getTidInput(), DEFAULT_KEY);
		return dataTypeMap;
	}

	public static Map<String, WeakReference<TidParamInfo>> getTenantOutputDataTypeKeyMap(final TidIOInfo tidIOInfo) {
		final Map<String, WeakReference<TidParamInfo>> dataTypeMap = new HashMap<String, WeakReference<TidParamInfo>>();
		createTidDataTypeKeyMap(dataTypeMap, tidIOInfo.getTidOutput(), DEFAULT_KEY);
		return dataTypeMap;
	}

	private static void createTidDataTypeKeyMap(final Map<String, WeakReference<TidParamInfo>> dataTypeMap, final List<TidParamInfo> tidParams,
			final String parentKey) {
		if (tidParams != null) {
			for (TidParamInfo tidParam : tidParams) {
				createTidDataTypeKeyMap(dataTypeMap, tidParam, parentKey);
			}
		}
	}

	private static void
	        createTidDataTypeKeyMap(final Map<String, WeakReference<TidParamInfo>> dataTypeMap, final TidParamInfo tidParam, final String parentKey) {
		final String childKey = combinedKey(parentKey, tidParam.getApiName());
		final WeakReference<TidParamInfo> weakTidParam = new WeakReference<TidParamInfo>(tidParam);
		dataTypeMap.put(childKey, weakTidParam);
		createTidDataTypeKeyMap(dataTypeMap, tidParam.getChildren(), childKey);
	}
}