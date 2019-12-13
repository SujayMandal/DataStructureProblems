package com.ca.umg.business.tenant.report.model.util;

import static com.ca.framework.core.util.ConversionUtil.convertJson;
import static org.junit.Assert.assertTrue;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;

@SuppressWarnings("PMD")
public class ModelJsonTabularViewUtilTest {

	private final Map<String, String> dataTypeMap = new HashMap<String, String>();
	final Map<String, WeakReference<MidParamInfo>> modelDataType = new HashMap<>();

	@Test
	public void testOneLevelWithOneSimpleValueWithNoCollection() throws SystemException {
		final String json = "{\"payload\" : " //
				+ "{ \"fieldName\" : \"key1\"," + "\"sequence\" : 1," + "\"dataType\" : \"object\"," + "\"collection\" : false, \"value\" : \"value1\"}}";
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = ModelJsonTabularViewUtil.createModelJSONKeyMap(map, null, dataTypeMap, modelDataType);
		assertTrue(keyMap.size() == 1);
		assertTrue(keyMap.get("key1").toString().equals("value1"));
	}

	@Test
	public void testOneLevelWithOneTwoValueWithNoCollection() throws SystemException {
		final String json = "{\"payload\" : [" //
				+ "{ \"fieldName\" : \"key1\"," + "\"sequence\" : 1," + "\"dataType\" : \"object\"," + "\"collection\" : false, \"value\" : \"value1\"}," //
				+ "{ \"fieldName\" : \"key2\"," + "\"sequence\" : 2," + "\"dataType\" : \"object\"," + "\"collection\" : false, \"value\" : \"value2\"}]}";
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = ModelJsonTabularViewUtil.createModelJSONKeyMap(map, null, dataTypeMap, modelDataType);
		assertTrue(keyMap.size() == 2);
		assertTrue(keyMap.get("key1").toString().equals("value1"));
		assertTrue(keyMap.get("key2").toString().equals("value2"));
	}

	@Test
	public void testOneLevelWithOneMoreValueWithNoCollection() throws SystemException {
		final String json = "{\"payload\" : [" //
				+ "{ \"fieldName\" : \"key1\"," + "\"sequence\" : 1," + "\"dataType\" : \"object\"," + "\"collection\" : false, \"value\" : \"value1\"}," //
				+ "{ \"fieldName\" : \"key2\"," + "\"sequence\" : 2," + "\"dataType\" : \"object\"," + "\"collection\" : false, \"value\" : \"value2\"}," //
				+ "{ \"fieldName\" : \"key3\"," + "\"sequence\" : 3," + "\"dataType\" : \"object\"," + "\"collection\" : false, \"value\" : \"value3\"}]}";
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = ModelJsonTabularViewUtil.createModelJSONKeyMap(map, null, dataTypeMap, modelDataType);
		assertTrue(keyMap.size() == 3);
		assertTrue(keyMap.get("key1").toString().equals("value1"));
		assertTrue(keyMap.get("key2").toString().equals("value2"));
		assertTrue(keyMap.get("key3").toString().equals("value3"));
	}

	@Test
	public void testSecondLevelWithOneSimpleValueWithNoCollection() throws SystemException {
		final String json = "{\"payload\" : " //
				+ "{ \"fieldName\" : \"key1\"," + "\"sequence\" : 1," + "\"dataType\" : \"object\"," + "\"collection\" : false, \"value\" : " //
				+ "{\"fieldName\" : \"key2\"," + "\"sequence\" : 1," + "\"dataType\" : \"object\"," + "\"collection\" : false, \"value\" : \"value1\"}}}"; //
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = ModelJsonTabularViewUtil.createModelJSONKeyMap(map, null, dataTypeMap, modelDataType);
		assertTrue(keyMap.size() == 1);
		assertTrue(keyMap.get("key1.key2").toString().equals("value1"));
	}

	@Test
	public void testSecondLevelWithOneTwoValueWithNoCollection() throws SystemException {
		final String json = "{\"payload\" : " //
				+ "{ \"fieldName\" : \"key1\"," + "\"sequence\" : 1, \"dataType\" : \"object\", \"collection\" : false, \"value\" : [" //
				+ "{\"fieldName\" : \"key2\"," + "\"sequence\" : 1, \"dataType\" : \"object\", \"collection\" : false, \"value\" : \"value1\"}," //
				+ "{\"fieldName\" : \"key3\"," + "\"sequence\" : 1, \"dataType\" : \"object\", \"collection\" : false, \"value\" : \"value2\"}]}}"; //
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = ModelJsonTabularViewUtil.createModelJSONKeyMap(map, null, dataTypeMap, modelDataType);
		assertTrue(keyMap.size() == 2);
		assertTrue(keyMap.get("key1.key2").toString().equals("value1"));
		assertTrue(keyMap.get("key1.key3").toString().equals("value2"));
	}

	@Test
	public void testSecondLevelWithOneMoreValueWithNoCollection() throws SystemException {
		final String json = "{\"payload\" : " //
				+ "{ \"fieldName\" : \"key1\"," + "\"sequence\" : 1, \"dataType\" : \"object\", \"collection\" : false, \"value\" : [" //
				+ "{\"fieldName\" : \"key2\"," + "\"sequence\" : 2, \"dataType\" : \"object\", \"collection\" : false, \"value\" : \"value1\"}," //
				+ "{\"fieldName\" : \"key3\"," + "\"sequence\" : 3, \"dataType\" : \"object\", \"collection\" : false, \"value\" : \"value2\"}," //
				+ "{\"fieldName\" : \"key4\"," + "\"sequence\" : 4, \"dataType\" : \"object\", \"collection\" : false, \"value\" : \"value3\"}]}}"; //
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = ModelJsonTabularViewUtil.createModelJSONKeyMap(map, null, dataTypeMap, modelDataType);
		assertTrue(keyMap.size() == 3);
		assertTrue(keyMap.get("key1.key2").toString().equals("value1"));
		assertTrue(keyMap.get("key1.key3").toString().equals("value2"));
		assertTrue(keyMap.get("key1.key4").toString().equals("value3"));
	}

	@Test
	public void testOneLevelWith1DArrayWithOneValue() throws SystemException {
		final String json = "{\"payload\" : " //
				+ "{ \"fieldName\" : \"key1\"," + "\"sequence\" : 1," + "\"dataType\" : \"object\"," + "\"collection\" : true, " //
				+ "\"value\" : [ 2.0, 480.0, 345185.23199999996 ]}}"; //
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = ModelJsonTabularViewUtil.createModelJSONKeyMap(map, null, dataTypeMap, modelDataType);
		assertTrue(keyMap.size() == 1);
		assertTrue(keyMap.get("key1").toString().equals("[2.0, 480.0, 345185.23199999996]"));
	}

	@Test
	public void testOneLevelWith2DArrayWithOneValue() throws SystemException {
		final String json = "{\"payload\" : " //
				+ "{ \"fieldName\" : \"key1\"," + "\"sequence\" : 1," + "\"dataType\" : \"object\"," + "\"collection\" : true, " //
				+ "\"value\" : [[ 2.0, 480.0, 345185.23199999996], [ 2.0, 480.0, 345185.23199999996]]}}"; //
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = ModelJsonTabularViewUtil.createModelJSONKeyMap(map, null, dataTypeMap, modelDataType);
		assertTrue(keyMap.size() == 1);
		// assertTrue(keyMap.get("key1").toString().equals(COMPLEX_DATA)); [2.0,
		// 480.0, 345185.23199999996]
	}

	@Test
	public void testSecondLevelWithOneMoreValueWithNoCollection1() throws SystemException {
		final String json = "{\"payload\" : " //
				+ "{ \"fieldName\" : \"key1\"," + "\"sequence\" : 1, \"dataType\" : \"object\", \"collection\" : true, \"value\" : [" //
				+ "{\"fieldName\" : \"key2\"," + "\"sequence\" : 2, \"dataType\" : \"object\", \"collection\" : false, \"value\" : \"value1\"}," //
				+ "{\"fieldName\" : \"key3\"," + "\"sequence\" : 3, \"dataType\" : \"object\", \"collection\" : false, \"value\" : \"value2\"}," //
				+ "{\"fieldName\" : \"key4\"," + "\"sequence\" : 4, \"dataType\" : \"object\", \"collection\" : false, \"value\" : \"value3\"}]}}"; //
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = ModelJsonTabularViewUtil.createModelJSONKeyMap(map, null, dataTypeMap, modelDataType);
		assertTrue(keyMap.size() == 3);
		assertTrue(keyMap.get("key1.key2").toString().equals("value1"));
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> createMapForJson(final String json) throws SystemException {
		return convertJson(json, Map.class);
	}
}
