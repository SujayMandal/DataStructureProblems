package com.ca.umg.business.tenant.report.model.util;

import static com.ca.umg.business.tenant.report.model.util.ModelDataTypeMapUtil.getModelInputDataTypeKeyMap;
import static com.ca.umg.business.tenant.report.model.util.ModelDataTypeMapUtil.getModelOutputDataTypeKeyMap;
import static com.ca.umg.business.tenant.report.model.util.ModelDataTypeMapUtil.getModelParentSequenceMap;
import static com.ca.umg.business.tenant.report.model.util.ModelJsonTabularViewUtil.createModelJSONKeyMap;
import static com.ca.umg.business.tenant.report.model.util.TenantDataTypeMapUtil.getTenantInputDataTypeKeyMap;
import static com.ca.umg.business.tenant.report.model.util.TenantDataTypeMapUtil.getTenantOutputDataTypeKeyMap;
import static com.ca.umg.business.tenant.report.model.util.TenantJsonTabularViewUtil.createTenantJSONKeyMap;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ca.umg.business.mapping.info.MappingDescriptor;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.tenant.report.model.TabularInfo;
import com.ca.umg.business.tenant.report.model.TenantModelReport;

@SuppressWarnings("PMD")
public class TabularViewMapUtil {

	public static final String DEFAULT_KEY = "";
	public static final String KEY_SEPERATOR = ".";
	public static final String COMPLEX_TYPE = "Complex";
	public static final String COMPLEX_DATA = "Complex Data";
	public static final String DATA = "data";
	public static final String PAYLOAD = "payload";
	public static final String  MODEL_PARAMETER_NAME = "modelParameterName";
	public static final String SEQUENCE = "sequence";
	public static final String DATATYPE = "dataType";
	public static final String COLLECTION = "collection";
	public static final String VALUE = "value";
	public static final String NOT_APPLICABLE = "NA";
	public static final String NULL = "null";


	public static final String combinedKey(final String parentKey, final String childKey) {
		if (parentKey.equals(DEFAULT_KEY)) {
			return childKey;
		} else {
			return parentKey + KEY_SEPERATOR + childKey;
		}
	}

	public static boolean isList(final Object o) {
		if (o instanceof List) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean inMap(final Object o) {
		if (o instanceof Map) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isListInsideList(final List list) {
		final Object valueInsideList = getFirstNotNullValueFromList(list);
		if (valueInsideList instanceof List) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isValueInListAPrimitive(final List list) {
		final Object valueInsideList = getFirstNotNullValueFromList(list);
		if (valueInsideList instanceof List) {
			final List valueInsideListList = (List) valueInsideList;
			final Object firstElement = getFirstNotNullValueFromList(valueInsideListList);
			if (firstElement == null || firstElement instanceof Double || firstElement instanceof Integer || firstElement instanceof Long
					|| firstElement instanceof String || firstElement instanceof Boolean) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private static Object getFirstNotNullValueFromList(final List list) {
		if (list != null) {
			for (Object v : list) {
				if (v != null) {
					return v;
				}
			}
		}

		return null;
	}

	public static boolean isMapInsideList(final List list) {
		final Object valueInsideList = list.size() > 0 ? list.get(0) : null;
		if (valueInsideList instanceof Map) {
			return true;
		} else {
			return false;
		}
	}

	public static Object getData(final Map<String, Object> mapObject) {
		if (mapObject != null) {
			return mapObject.get(DATA);
		} else {
			return null;
		}
	}

	public static Object getPayload(final Map<String, Object> mapObject) {
		if (mapObject != null) {
			return mapObject.get(PAYLOAD);
		} else {
			return null;
		}
	}

	public static List<TabularInfo> createInputTabularInfo(final TenantModelReport modelReport, final MappingDescriptor descriptor) {
		final List<TabularInfo> inputTabularInfo = new ArrayList<TabularInfo>();

		if (descriptor != null) {
			final Map<String, WeakReference<TidParamInfo>> tenantInputDataType = getTenantInputDataTypeKeyMap(descriptor.getTidTree());
			final Map<String, WeakReference<MidParamInfo>> modelInputDataType = getModelInputDataTypeKeyMap(descriptor.getMidTree());
			final Map<Integer, WeakReference<MidParamInfo>> sequenceMap = getModelParentSequenceMap(descriptor.getMidTree());
			final Map<String, String> dataTypeFromJson = new HashMap<String, String>();

			final Map<String, Object> tenantInputJsonKey = createTenantJSONKeyMap(modelReport.getTenantInput(), tenantInputDataType);
			final Map<String, Object> modelInputJsonKey = createModelJSONKeyMap(modelReport.getModelInput(), sequenceMap, dataTypeFromJson, modelInputDataType);


			final Set<String> tenantInputJsonKeySet = tenantInputJsonKey.keySet();
			for (String key : tenantInputJsonKeySet) {
				inputTabularInfo.add(createTenantTabularInfo(key, tenantInputJsonKey, modelInputJsonKey, tenantInputDataType, modelInputDataType,
						dataTypeFromJson));
			}

			final Set<String> modelInputJsonKeySet = modelInputJsonKey.keySet();
			for (String key : modelInputJsonKeySet) {
				if (!tenantInputJsonKeySet.contains(key)) {
					inputTabularInfo.add(createModelTabularInfo(key, tenantInputJsonKey, modelInputJsonKey, modelInputDataType, tenantInputDataType,
							dataTypeFromJson));
				}
			}
		}

		return inputTabularInfo;
	}

	public static List<TabularInfo> createOutputTabularInfo(final TenantModelReport modelReport, final MappingDescriptor descriptor) {
		final List<TabularInfo> outputTabularInfo = new ArrayList<TabularInfo>();

		if (descriptor != null) {

			final Map<String, WeakReference<TidParamInfo>> tenantOutputDataType = getTenantOutputDataTypeKeyMap(descriptor.getTidTree());
			final Map<String, WeakReference<MidParamInfo>> modelOutputDataType = getModelOutputDataTypeKeyMap(descriptor.getMidTree());
			final Map<Integer, WeakReference<MidParamInfo>> sequenceMap = getModelParentSequenceMap(descriptor.getMidTree());
			final Map<String, String> dataTypeFromJson = new HashMap<String, String>();

			final Map<String, Object> tenantOutputJsonKey = createTenantJSONKeyMap(modelReport.getTenantOutput(), tenantOutputDataType);
			final Map<String, Object> modelOutputJsonKey = createModelJSONKeyMap(modelReport.getModelOutput(), sequenceMap, dataTypeFromJson,
			        modelOutputDataType);

			final Set<String> tenantOutputJsonKeySet = tenantOutputJsonKey.keySet();
			for (String key : tenantOutputJsonKeySet) {
				outputTabularInfo.add(createTenantTabularInfo(key, tenantOutputJsonKey, modelOutputJsonKey, tenantOutputDataType, modelOutputDataType,
						dataTypeFromJson));
			}

			final Set<String> modelOutputJsonKeySet = modelOutputJsonKey.keySet();
			for (String key : modelOutputJsonKeySet) {
				if (!tenantOutputJsonKeySet.contains(key)) {
					outputTabularInfo.add(createModelTabularInfo(key, tenantOutputJsonKey, modelOutputJsonKey, modelOutputDataType, tenantOutputDataType,
							dataTypeFromJson));
				}
			}
		}

		return outputTabularInfo;
	}

	private static TabularInfo createTenantTabularInfo(final String key, final Map<String, Object> tenantJsonKey, final Map<String, Object> modelJsonKey,
			final Map<String, WeakReference<TidParamInfo>> tenantDataType, final Map<String, WeakReference<MidParamInfo>> modelDataType,
			final Map<String, String> dataTypeFromJson) {
		final String tenantValue = getValue(key, tenantJsonKey);
		final String modelValue = getValue(key, modelJsonKey);
		final String dataType = getTenantDataType(tenantValue, key, tenantDataType, modelDataType, dataTypeFromJson);
		return new TabularInfo(key, dataType, tenantValue, modelValue);
	}

	private static TabularInfo createModelTabularInfo(final String key, final Map<String, Object> tenantJsonKey, final Map<String, Object> modelJsonKey,
			final Map<String, WeakReference<MidParamInfo>> modelDataType, final Map<String, WeakReference<TidParamInfo>> tenantDataType,
			final Map<String, String> dataTypeFromJson) {
		final String tenantValue = getValue(key, tenantJsonKey);
		final String modelValue = getValue(key, modelJsonKey);
		final String dataType = getModelDataType(modelValue, key, modelDataType, tenantDataType, dataTypeFromJson);
		return new TabularInfo(key, dataType, tenantValue, modelValue);
	}

	private static String getValue(final String key, final Map<String, Object> map) {
		if (map.containsKey(key)) {
			if (map.get(key) == null) {
				return NULL;
			} else {
				return map.get(key).toString();
			}
		} else {
			return NOT_APPLICABLE;
		}
	}

	private static String getTenantDataType(final String value, final String key, final Map<String, WeakReference<TidParamInfo>> tenantInputDataType,
			final Map<String, WeakReference<MidParamInfo>> modelInputDataType, final Map<String, String> dataTypeFromJson) {
		if (value.equals(COMPLEX_DATA)) {
			return COMPLEX_TYPE;
		} else {
			if (tenantInputDataType.get(key) == null && modelInputDataType.get(key) == null) {
				if (dataTypeFromJson.containsKey(key)) {
					return dataTypeFromJson.get(key);
				}
				return NOT_APPLICABLE;
			}

			if (tenantInputDataType.get(key) != null) {
				return tenantInputDataType.get(key).get().getDatatype().getType();
			} else {
				return getModelDataType(value, key, modelInputDataType, tenantInputDataType, dataTypeFromJson);
			}
		}
	}

	private static String getModelDataType(final String value, final String key, final Map<String, WeakReference<MidParamInfo>> modelInputDataType,
			final Map<String, WeakReference<TidParamInfo>> tenantInputDataType, final Map<String, String> dataTypeFromJson) {
		if (value.equals(COMPLEX_DATA)) {
			return COMPLEX_TYPE;
		} else {
			if (modelInputDataType.get(key) == null && tenantInputDataType.get(key) == null) {
				if (dataTypeFromJson.containsKey(key)) {
					return dataTypeFromJson.get(key);
				}
				return NOT_APPLICABLE;
			}

			if (modelInputDataType.get(key) != null) {
				return modelInputDataType.get(key).get().getDatatype().getType();
			} else {
				return getTenantDataType(value, key, tenantInputDataType, modelInputDataType, dataTypeFromJson);
			}

		}
	}
}