package com.ca.umg.business.tenant.report.model.util;

import static com.ca.umg.business.tenant.report.model.util.TenantDataTypeMapUtil.getTenantInputDataTypeKeyMap;
import static com.ca.umg.business.tenant.report.model.util.TenantDataTypeMapUtil.getTenantOutputDataTypeKeyMap;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.ca.umg.business.mid.extraction.info.DatatypeInfo;
import com.ca.umg.business.mid.extraction.info.TidIOInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;

@SuppressWarnings("PMD")
public class TenantDataTypeMapUtilTest {

	@Test
	public void testGetTenantInputDataTypeKeyMap() {
		final TidIOInfo tidIOInfo = mock(TidIOInfo.class);
		final List<TidParamInfo> tidInput = new ArrayList<TidParamInfo>();

		final TidParamInfo tidParam1 = mock(TidParamInfo.class);
		when(tidParam1.getApiName()).thenReturn("tid1");
		final DatatypeInfo intDataType = new DatatypeInfo();
		intDataType.setType("integer");
		when(tidParam1.getDatatype()).thenReturn(intDataType);
		tidInput.add(tidParam1);

		when(tidIOInfo.getTidInput()).thenReturn(tidInput);
		final Map<String, WeakReference<TidParamInfo>> dataTypeMap = getTenantInputDataTypeKeyMap(tidIOInfo);
		assertTrue(dataTypeMap != null);
		assertTrue(dataTypeMap.get("tid1") != null);
		assertTrue(dataTypeMap.get("tid1").get().getDatatype().readDataTypeString().equals("INTEGER"));
	}

	@Test
	public void testGetTenantOuputDataTypeKeyMap() {
		final TidIOInfo tidIOInfo = mock(TidIOInfo.class);
		final List<TidParamInfo> tidOuput = new ArrayList<TidParamInfo>();

		final TidParamInfo tidParam1 = mock(TidParamInfo.class);
		when(tidParam1.getApiName()).thenReturn("tid1");
		final DatatypeInfo intDataType = new DatatypeInfo();
		intDataType.setType("integer");
		when(tidParam1.getDatatype()).thenReturn(intDataType);
		tidOuput.add(tidParam1);

		when(tidIOInfo.getTidOutput()).thenReturn(tidOuput);
		final Map<String, WeakReference<TidParamInfo>> dataTypeMap = getTenantOutputDataTypeKeyMap(tidIOInfo);
		assertTrue(dataTypeMap != null);
		assertTrue(dataTypeMap.get("tid1") != null);
		assertTrue(dataTypeMap.get("tid1").get().getDatatype().readDataTypeString().equals("INTEGER"));
	}
}
