package com.ca.umg.business.tenant.report.model.util;

import static com.ca.framework.core.util.ConversionUtil.convertJson;
import static com.ca.umg.business.tenant.report.model.util.TenantJsonTabularViewUtil.createTenantJSONKeyMap;
import static org.junit.Assert.assertTrue;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;

@SuppressWarnings("PMD")
public class TenantJsonTabularViewUtilTest {

	final Map<String, WeakReference<TidParamInfo>> tenantDataType = new HashMap<>();

	@Test
	public void testOneLevelWithOneSimpleValue() throws SystemException {
		final String json = "{\"data\" : { \"key1\" : \"value1\"}}";
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = createTenantJSONKeyMap(map, tenantDataType);
		assertTrue(keyMap.size() == 1);
		assertTrue(keyMap.get("key1").toString().equals("value1"));
	}

	@Test
	public void testOneLevelWithTwoSimpleValues() throws SystemException {
		final String json = "{\"data\" : { \"key1\" : \"value1\", \"key2\" : \"value2\"}}";
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = createTenantJSONKeyMap(map, tenantDataType);
		assertTrue(keyMap.size() == 2);
		final Object[] keyOrder = keyMap.keySet().toArray();
		assertTrue(keyOrder[0].toString().equals("key1"));
		assertTrue(keyOrder[1].toString().equals("key2"));
		assertTrue(keyMap.get("key1").toString().equals("value1"));
		assertTrue(keyMap.get("key2").toString().equals("value2"));
	}

	@Test
	public void testOneLevelWithMoreSimpleValues() throws SystemException {
		final String json = "{\"data\" : { \"key1\" : \"value1\", \"key2\" : \"value2\", \"key3\" : \"value3\"}}";
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = createTenantJSONKeyMap(map, tenantDataType);
		assertTrue(keyMap.size() == 3);
		final Object[] keyOrder = keyMap.keySet().toArray();
		assertTrue(keyOrder[0].toString().equals("key1"));
		assertTrue(keyOrder[1].toString().equals("key2"));
		assertTrue(keyOrder[2].toString().equals("key3"));
		assertTrue(keyMap.get("key1").toString().equals("value1"));
		assertTrue(keyMap.get("key2").toString().equals("value2"));
		assertTrue(keyMap.get("key3").toString().equals("value3"));
	}

	@Test
	public void testSecondLevelWithOneSimpleValue() throws SystemException {
		final String json = "{\"data\" : { \"key1\" : { \"key11\" : \"value1\"}}}";
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = createTenantJSONKeyMap(map, tenantDataType);
		assertTrue(keyMap.size() == 1);
		assertTrue(keyMap.get("key1.key11").toString().equals("value1"));
	}

	@Test
	public void testSecondLevelWithTwoSimpleValues() throws SystemException {
		final String json = "{\"data\" : { \"key1\" : { \"key11\" : \"value1\", \"key12\" : \"value2\"}}}";
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = createTenantJSONKeyMap(map, tenantDataType);
		assertTrue(keyMap.size() == 2);

		final Object[] keyOrder = keyMap.keySet().toArray();
		assertTrue(keyOrder[0].toString().equals("key1.key11"));
		assertTrue(keyOrder[1].toString().equals("key1.key12"));

		assertTrue(keyMap.get("key1.key11").toString().equals("value1"));
		assertTrue(keyMap.get("key1.key12").toString().equals("value2"));
	}

	@Test
	public void testSecondLevelWithMoreSimpleValues() throws SystemException {
		final String json = "{\"data\" : { \"key1\" : { \"key11\" : \"value1\", \"key12\" : \"value2\", \"key13\" : \"value3\"}}}";
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = createTenantJSONKeyMap(map, tenantDataType);
		assertTrue(keyMap.size() == 3);

		final Object[] keyOrder = keyMap.keySet().toArray();
		assertTrue(keyOrder[0].toString().equals("key1.key11"));
		assertTrue(keyOrder[1].toString().equals("key1.key12"));
		assertTrue(keyOrder[2].toString().equals("key1.key13"));

		assertTrue(keyMap.get("key1.key11").toString().equals("value1"));
		assertTrue(keyMap.get("key1.key12").toString().equals("value2"));
		assertTrue(keyMap.get("key1.key13").toString().equals("value3"));
	}

	@Test
	public void testSecondLevelWithOneObjectValue() throws SystemException {
		final String json = "{\"data\" : { \"key1\" : { \"key11\" : \"value1\", \"key12\" : \"value2\"}}}";
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = createTenantJSONKeyMap(map, tenantDataType);
		assertTrue(keyMap.size() == 2);

		final Object[] keyOrder = keyMap.keySet().toArray();
		assertTrue(keyOrder[0].toString().equals("key1.key11"));
		assertTrue(keyOrder[1].toString().equals("key1.key12"));

		assertTrue(keyMap.get("key1.key11").toString().equals("value1"));
		assertTrue(keyMap.get("key1.key12").toString().equals("value2"));
	}

	@Test
	public void testSecondLevelWithTwoObjectValue() throws SystemException {
		final String json = "{\"data\" : { \"key1\" : { \"key11\" : \"value1\", \"key12\" : \"value2\"}, \"key2\" : { \"key21\" : \"value1\", \"key22\" : \"value2\"}}}";
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = createTenantJSONKeyMap(map, tenantDataType);
		assertTrue(keyMap.size() == 4);

		final Object[] keyOrder = keyMap.keySet().toArray();
		assertTrue(keyOrder[0].toString().equals("key1.key11"));
		assertTrue(keyOrder[1].toString().equals("key1.key12"));
		assertTrue(keyOrder[2].toString().equals("key2.key21"));
		assertTrue(keyOrder[3].toString().equals("key2.key22"));

		assertTrue(keyMap.get("key1.key11").toString().equals("value1"));
		assertTrue(keyMap.get("key1.key12").toString().equals("value2"));

		assertTrue(keyMap.get("key2.key21").toString().equals("value1"));
		assertTrue(keyMap.get("key2.key22").toString().equals("value2"));
	}

	@Test
	public void testOneLevelWith1DArrayWithOneValue() throws SystemException {
		final String json = "{\"data\" : { \"key1\" : [\"value1\"]}}";
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = createTenantJSONKeyMap(map, tenantDataType);
		assertTrue(keyMap.size() == 1);
		assertTrue(keyMap.get("key1").toString().equals("[value1]"));
	}

	@Test
	public void testOneLevelWith1DArrayWithTwoValues() throws SystemException {
		final String json = "{\"data\" : { \"key1\" : [\"value1\", \"value2\"]}}";
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = createTenantJSONKeyMap(map, tenantDataType);
		assertTrue(keyMap.size() == 1);
		assertTrue(keyMap.get("key1").toString().equals("[value1, value2]"));
	}

	@Test
	public void testOneLevelWith1DArrayWithMoreValues() throws SystemException {
		final String json = "{\"data\" : { \"key1\" : [\"value1\", \"value2\", \"value3\", \"value4\"]}}";
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = createTenantJSONKeyMap(map, tenantDataType);
		assertTrue(keyMap.size() == 1);
		assertTrue(keyMap.get("key1").toString().equals("[value1, value2, value3, value4]"));
	}

	/*
	 * @Test public void testOneLevelWith1DArrayWithOneObjectValue() throws
	 * SystemException { final String json =
	 * "{\"data\" : { \"key1\" : [{\"key2\" : \"value1\"}]}}"; final Map<String,
	 * Object> map = createMapForJson(json); final Map<String, Object> keyMap =
	 * createTenantJSONKeyMap(map, tenantDataType); assertTrue(keyMap.size() ==
	 * 1); // assertTrue(keyMap.get("key1").toString().equals(COMPLEX_DATA)); }
	 */

	/*
	 * @Test public void testOneLevelWith1DArrayWithTwoObjectValue() throws
	 * SystemException { final String json =
	 * "{\"data\" : { \"key1\" : [{\"key2\" : \"value1\"}, {\"key3\" : \"value2\"}]}}"
	 * ; final Map<String, Object> map = createMapForJson(json); final
	 * Map<String, Object> keyMap = createTenantJSONKeyMap(map, tenantDataType);
	 * assertTrue(keyMap.size() == 1); //
	 * assertTrue(keyMap.get("key1").toString().equals(COMPLEX_DATA)); }
	 */

	/*
	 * @Test public void testOneLevelWith1DArrayWithMoreObjectValue() throws
	 * SystemException { final String json =
	 * "{\"data\" : { \"key1\" : [{\"key2\" : \"value1\"}, {\"key3\" : \"value2\"}, {\"key4\" : \"value3\"}]}}"
	 * ; final Map<String, Object> map = createMapForJson(json); final
	 * Map<String, Object> keyMap = createTenantJSONKeyMap(map, tenantDataType);
	 * assertTrue(keyMap.size() == 1); //
	 * assertTrue(keyMap.get("key1").toString().equals(COMPLEX_DATA)); }
	 */

	@Test
	public void testOneLevelWith2DArrayWithOneValue() throws SystemException {
		final String json = "{\"data\" : { \"key1\" : [[\"value1\"], [\"value2\"]]}}";
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = createTenantJSONKeyMap(map, tenantDataType);
		assertTrue(keyMap.size() == 1);
		// assertTrue(keyMap.get("key1").toString().equals(COMPLEX_DATA));
	}

	@Test
	public void testOneLevelWith2DArrayWithTwoValues() throws SystemException {
		final String json = "{\"data\" : { \"key1\" : [[\"value1\", \"value2\"]]}}";
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = createTenantJSONKeyMap(map, tenantDataType);
		assertTrue(keyMap.size() == 1);
		// assertTrue(keyMap.get("key1").toString().equals(COMPLEX_DATA));
	}

	@Test
	public void testOneLevelWith2DArrayWithTwoValues1() throws SystemException {
		final String json = "{\"data\" : { \"key1\" : [[\"value1\", \"value2\"], [\"value1\", \"value2\"]]}}";
		final Map<String, Object> map = createMapForJson(json);
		final Map<String, Object> keyMap = createTenantJSONKeyMap(map, tenantDataType);
		assertTrue(keyMap.size() == 1);
		// assertTrue(keyMap.get("key1").toString().equals(COMPLEX_DATA));
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> createMapForJson(final String json) throws SystemException {
		return convertJson(json, Map.class);
	}
}
