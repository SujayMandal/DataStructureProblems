package com.ca.umg.business.tenant.report.model.util;

import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.DEFAULT_KEY;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.combinedKey;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.umg.business.mid.extraction.info.MidIOInfo;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;

@SuppressWarnings("PMD")
public class ModelDataTypeMapUtil {

	public static Map<String, WeakReference<MidParamInfo>> getModelInputDataTypeKeyMap(final MidIOInfo midIOInfo) {
		final Map<String, WeakReference<MidParamInfo>> dataTypeMap = new HashMap<String, WeakReference<MidParamInfo>>();
		createMidDataTypeKeyMap(dataTypeMap, midIOInfo.getMidInput(), DEFAULT_KEY);
		return dataTypeMap;
	}

	public static Map<String, WeakReference<MidParamInfo>> getModelOutputDataTypeKeyMap(final MidIOInfo midIOInfo) {
		final Map<String, WeakReference<MidParamInfo>> dataTypeMap = new HashMap<String, WeakReference<MidParamInfo>>();
		createMidDataTypeKeyMap(dataTypeMap, midIOInfo.getMidOutput(), DEFAULT_KEY);
		return dataTypeMap;
	}

	public static Map<Integer, WeakReference<MidParamInfo>> getModelParentSequenceMap(final MidIOInfo midIOInfo) {
		final Map<Integer, WeakReference<MidParamInfo>> sequenceMap = new HashMap<Integer, WeakReference<MidParamInfo>>();
		final List<MidParamInfo> midParams = midIOInfo.getMidOutput();
		if (midParams != null) {
			for (MidParamInfo midParam : midParams) {
				final WeakReference<MidParamInfo> weakMidParam = new WeakReference<MidParamInfo>(midParam);
				sequenceMap.put(midParam.getSequence(), weakMidParam);
			}
		}
		return sequenceMap;
	}

	private static void createMidDataTypeKeyMap(final Map<String, WeakReference<MidParamInfo>> dataTypeMap, final List<MidParamInfo> midParams,
			final String parentKey) {
		if (midParams != null) {
			for (MidParamInfo midParam : midParams) {
				createMidDataTypeKeyMap(dataTypeMap, midParam, parentKey);
			}
		}
	}

	private static void
	createMidDataTypeKeyMap(final Map<String, WeakReference<MidParamInfo>> dataTypeMap, final MidParamInfo midParam, final String parentKey) {
		final String childKey = combinedKey(parentKey, midParam.getApiName());
		final WeakReference<MidParamInfo> weakMidParam = new WeakReference<MidParamInfo>(midParam);
		dataTypeMap.put(childKey, weakMidParam);
		createMidDataTypeKeyMap(dataTypeMap, midParam.getChildren(), childKey);
	}

}
