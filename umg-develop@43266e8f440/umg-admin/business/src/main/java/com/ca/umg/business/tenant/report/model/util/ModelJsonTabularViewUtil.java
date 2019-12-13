package com.ca.umg.business.tenant.report.model.util;

import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.COLLECTION;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.COMPLEX_DATA;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.DEFAULT_KEY;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.MODEL_PARAMETER_NAME;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.NULL;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.SEQUENCE;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.VALUE;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.combinedKey;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.getPayload;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.inMap;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.isList;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.isListInsideList;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.isMapInsideList;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ca.umg.business.mid.extraction.info.MidParamInfo;

@SuppressWarnings("PMD")
public class ModelJsonTabularViewUtil {

	public static Map<String, Object> createModelJSONKeyMap(final Map<String, Object> modelJson, final Map<Integer, WeakReference<MidParamInfo>> sequenceMap,
			final Map<String, String> dataTypeFromJson, final Map<String, WeakReference<MidParamInfo>> modelDataType) {
		final Object data = getPayload(modelJson);
		final Map<String, Object> tabularViewMap = new LinkedHashMap<String, Object>();
		createTabularViewBasedOnObject(tabularViewMap, data, DEFAULT_KEY, false, sequenceMap, dataTypeFromJson, NULL, modelDataType);
		return tabularViewMap;
	}

	private static void createTabularViewBasedOnObject(final Map<String, Object> tabularViewMap, final Object data, final String parentKey,
			final boolean collection, final Map<Integer, WeakReference<MidParamInfo>> sequenceMap, final Map<String, String> dataTypeFromJson,
			final String dataType, final Map<String, WeakReference<MidParamInfo>> modelDataType) {
		if (data != null) {
			if (inMap(data)) {
				createTabularViewBasedOnMap(tabularViewMap, (Map<String, Object>) data, parentKey, sequenceMap, dataTypeFromJson, modelDataType);
			} else if (isList(data)) {
				createTabularViewBasedOnList(tabularViewMap, (List<Object>) data, parentKey, collection, sequenceMap, dataTypeFromJson, dataType, modelDataType);
			} else {
				if (isArrayOfObjects(parentKey, modelDataType)) {
					tabularViewMap.put(parentKey, COMPLEX_DATA);
					return;
				} else {
					tabularViewMap.put(parentKey, data);
					dataTypeFromJson.put(parentKey, dataType);
				}
			}
		}
	}

	private static void createTabularViewBasedOnMap(final Map<String, Object> tabularViewMap, final Map<String, Object> data, final String parentKey,
			final Map<Integer, WeakReference<MidParamInfo>> sequenceMap, final Map<String, String> dataTypeFromJson,
			final Map<String, WeakReference<MidParamInfo>> modelDataType) {
		final String fieldname = getFieldName(data, sequenceMap);
		final Object value = data.get(VALUE);
		final String dataType = data.get(TabularViewMapUtil.DATATYPE) != null ? data.get(TabularViewMapUtil.DATATYPE).toString() : NULL;
        boolean collection = Boolean.FALSE;
        if (data.get(COLLECTION) != null) {
            collection = Boolean.valueOf(data.get(COLLECTION).toString());
        }
		if (isArrayOfObjects(parentKey, modelDataType)) {
			tabularViewMap.put(parentKey, COMPLEX_DATA);
			return;
		}
		createTabularViewBasedOnObject(tabularViewMap, value, combinedKey(parentKey, fieldname), collection, sequenceMap, dataTypeFromJson, dataType,
				modelDataType);
	}

	private static void createTabularViewBasedOnList(final Map<String, Object> tabularViewMap, final List<Object> data, final String parentKey,
			final boolean collection, final Map<Integer, WeakReference<MidParamInfo>> sequenceMap, final Map<String, String> dataTypeFromJson,
			final String dataType, final Map<String, WeakReference<MidParamInfo>> modelDataType) {
		if (isArrayOfObjects(parentKey, modelDataType)) {
			tabularViewMap.put(parentKey, COMPLEX_DATA);
			return;
		}

		if (isListInsideList(data)) {
			if (TabularViewMapUtil.isValueInListAPrimitive(data)) {
				tabularViewMap.put(parentKey, data.toString());
				dataTypeFromJson.put(parentKey, dataType);
			} else {
				for (Object o : data) {
					createTabularViewBasedOnObject(tabularViewMap, o, parentKey, collection, sequenceMap, dataTypeFromJson, dataType, modelDataType);
				}
			}
		} else if (isMapInsideList(data)) {
			for (Object o : data) {
				createTabularViewBasedOnObject(tabularViewMap, o, parentKey, collection, sequenceMap, dataTypeFromJson, dataType, modelDataType);
			}
		} else {
			// Primitives
			tabularViewMap.put(parentKey, data.toString());
			dataTypeFromJson.put(parentKey, dataType);
		}
	}

	private static String getFieldName(final Map<String, Object> data, final Map<Integer, WeakReference<MidParamInfo>> sequenceMap) {
		final Object fieldname = data.get(MODEL_PARAMETER_NAME);
		if (fieldname != null) {
			return fieldname.toString();
		} else {
			final Integer seq = Integer.valueOf(data.get(SEQUENCE).toString());
			final MidParamInfo midParamInfo = sequenceMap.get(seq).get();
			return midParamInfo.getFlatenedName();
		}
	}

	private static boolean isArrayOfObjects(final String fieldName, final Map<String, WeakReference<MidParamInfo>> modelDataType) {
		if (modelDataType.containsKey(fieldName)) {
			final MidParamInfo midInfo = modelDataType.get(fieldName).get();
			if (midInfo.getDataTypeStr().contains("ARRAY") && midInfo.getDataTypeStr().contains("OBJECT")) {
				return true;
			}
		}

		return false;
	}
}