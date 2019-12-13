package com.ca.umg.business.tenant.report.model.util;

import static com.ca.umg.business.tenant.report.model.util.ModelDataTypeMapUtil.getModelInputDataTypeKeyMap;
import static com.ca.umg.business.tenant.report.model.util.ModelDataTypeMapUtil.getModelOutputDataTypeKeyMap;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.ca.umg.business.mid.extraction.info.DatatypeInfo;
import com.ca.umg.business.mid.extraction.info.MidIOInfo;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;

@SuppressWarnings("PMD")
public class ModelDataTypeMapUtilTest {

	@Test
	public void testGetModelInputDataTypeKeyMap() {
		final MidIOInfo midIOInfo = mock(MidIOInfo.class);
		final List<MidParamInfo> midInput = new ArrayList<MidParamInfo>();

		final MidParamInfo midParam1 = mock(MidParamInfo.class);
		when(midParam1.getApiName()).thenReturn("mid1");
		final DatatypeInfo intDataType = new DatatypeInfo();
		intDataType.setType("integer");
		when(midParam1.getDatatype()).thenReturn(intDataType);
		midInput.add(midParam1);

		when(midIOInfo.getMidInput()).thenReturn(midInput);
		final Map<String, WeakReference<MidParamInfo>> dataTypeMap = getModelInputDataTypeKeyMap(midIOInfo);
		assertTrue(dataTypeMap != null);
		assertTrue(dataTypeMap.get("mid1") != null);
		assertTrue(dataTypeMap.get("mid1").get().getDatatype().readDataTypeString().equals("INTEGER"));
	}

	@Test
	public void testgetModelOutputDataTypeKeyMap() {
		final MidIOInfo midIOInfo = mock(MidIOInfo.class);
		final List<MidParamInfo> midOutput = new ArrayList<MidParamInfo>();

		final MidParamInfo midParam1 = mock(MidParamInfo.class);
		when(midParam1.getApiName()).thenReturn("mid1");
		final DatatypeInfo intDataType = new DatatypeInfo();
		intDataType.setType("integer");
		when(midParam1.getDatatype()).thenReturn(intDataType);
		midOutput.add(midParam1);

		when(midIOInfo.getMidOutput()).thenReturn(midOutput);
		final Map<String, WeakReference<MidParamInfo>> dataTypeMap = getModelOutputDataTypeKeyMap(midIOInfo);
		assertTrue(dataTypeMap != null);
		assertTrue(dataTypeMap.get("mid1") != null);
		assertTrue(dataTypeMap.get("mid1").get().getDatatype().readDataTypeString().equals("INTEGER"));
	}
}
