package com.ca.umg.business.tenant.report.model.util;

import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.COMPLEX_DATA;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.DEFAULT_KEY;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.combinedKey;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.getData;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.inMap;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.isList;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.isListInsideList;
import static com.ca.umg.business.tenant.report.model.util.TabularViewMapUtil.isMapInsideList;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ca.umg.business.mid.extraction.info.TidParamInfo;

@SuppressWarnings("PMD")
public class TenantJsonTabularViewUtil {

	public static Map<String, Object> createTenantJSONKeyMap(final Map<String, Object> tenantJsonMap,
			final Map<String, WeakReference<TidParamInfo>> tenantDataType) {
		final Object data = getData(tenantJsonMap);
		final Map<String, Object> tabularViewMap = new LinkedHashMap<String, Object>();
		createTabularViewBasedOnObject(tabularViewMap, data, DEFAULT_KEY, tenantDataType);
		return tabularViewMap;
	}

	private static void createTabularViewBasedOnMap(final Map<String, Object> tabularViewMap, final Map<String, Object> data, final String parentKey,
			final Map<String, WeakReference<TidParamInfo>> tenantDataType) {
		final Set<String> keySet = data.keySet();
		for (String key : keySet) {
			final Object value = data.get(key);
			if (isArrayOfObjects(parentKey, tenantDataType)) {
				tabularViewMap.put(parentKey, COMPLEX_DATA);
				continue;
			}
			createTabularViewBasedOnObject(tabularViewMap, value, combinedKey(parentKey, key), tenantDataType);
		}
	}

	private static void createTabularViewBasedOnList(final Map<String, Object> tabularViewMap, final List<Object> data, final String parentKey,
			final Map<String, WeakReference<TidParamInfo>> tenantDataType) {

		if (isArrayOfObjects(parentKey, tenantDataType)) {
			tabularViewMap.put(parentKey, COMPLEX_DATA);
			return;
		}

		if (isListInsideList(data)) {
			if (TabularViewMapUtil.isValueInListAPrimitive(data)) {
				tabularViewMap.put(parentKey, data.toString());
			} else {
				for (Object o : data) {
					createTabularViewBasedOnObject(tabularViewMap, o, parentKey, tenantDataType);
				}
			}
		} else if (isMapInsideList(data)) {
			createTabularViewBasedOnList(tabularViewMap, data, parentKey, tenantDataType);
		} else {
			// Primitives
			tabularViewMap.put(parentKey, data.toString());
		}
	}

	@SuppressWarnings("unchecked")
	private static void createTabularViewBasedOnObject(final Map<String, Object> tabularViewMap, final Object data, final String parentKey,
			final Map<String, WeakReference<TidParamInfo>> tenantDataType) {
		if (inMap(data)) {
			createTabularViewBasedOnMap(tabularViewMap, (Map<String, Object>) data, parentKey, tenantDataType);
		} else if (isList(data)) {
			createTabularViewBasedOnList(tabularViewMap, (List<Object>) data, parentKey, tenantDataType);
		} else {

			if (isArrayOfObjects(parentKey, tenantDataType)) {
				tabularViewMap.put(parentKey, COMPLEX_DATA);
			} else {
				tabularViewMap.put(parentKey, data);
			}
		}
	}

	private static boolean isArrayOfObjects(final String fieldName, final Map<String, WeakReference<TidParamInfo>> tenantDataType) {
		if (tenantDataType.containsKey(fieldName)) {
			final TidParamInfo tidInfo = tenantDataType.get(fieldName).get();
			if (tidInfo.getDataTypeStr().contains("ARRAY") && tidInfo.getDataTypeStr().contains("OBJECT")) {
				return true;
			}
		}

		return false;
	}
}