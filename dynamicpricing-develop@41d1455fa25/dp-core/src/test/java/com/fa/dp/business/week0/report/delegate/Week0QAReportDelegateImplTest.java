package com.fa.dp.business.week0.report.delegate;

import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.bo.DPProcessParamsBOImpl;
import com.fa.dp.business.sop.week0.bo.DPSopWeek0ParamBOImpl;
import com.fa.dp.business.week0.report.info.DPWeek0QAReportRespInfo;
import com.fa.dp.business.week0.report.info.DPWeek0ReportInfo;
import com.fa.dp.core.config.DPTestConfig;
import com.fa.dp.core.exception.SystemException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = DPTestConfig.class)
@Slf4j
public class Week0QAReportDelegateImplTest {

	@InjectMocks
	private Week0QAReportDelegateImpl week0QAReportDelegate;

	@Mock
	private DPSopWeek0ParamBOImpl dpSopWeek0ParamBO;

	@Mock
	private DPProcessParamsBOImpl dpProcessParamsBO;

	@Parameterized.Parameter(value = 0)
	public String startDate;

	@Parameterized.Parameter(value = 1)
	public String endDate;

	@Parameterized.Parameter(value = 2)
	public String occupancy;

	@Parameterized.Parameter(value = 3)
	public List<String> classifications;

	@Parameterized.Parameter(value = 4)
	public DPWeek0QAReportRespInfo expectedData;

	private static final String content = "{\"propertyCount\":1,\"minimumPctAv\":1.0,\"medianPctAv\":1.0,\"maximumPctAv\":1.0,\"voilationCount\":0,\"missingReportCount\":0,\"week0Reports\":[{\"assetNumber\":null,\"propTemp\":null,\"oldAssetNumber\":null,\"assetValue\":null,\"avSetDate\":null,\"classification\":\"OCN\",\"clientCode\":null,\"listPrice\":null,\"status\":null,\"assignment\":null,\"assignmentDate\":null,\"eligible\":null,\"notes\":null,\"week0Price\":null,\"state\":null,\"rtSource\":null,\"propertyType\":null,\"pctAV\":1,\"withinBusinessRules\":null}]}";

	private static DPWeek0QAReportRespInfo data;

	@Parameterized.Parameters
	public static Collection<Object[]> data() throws IOException {

		data = new ObjectMapper().readValue(content, new TypeReference<DPWeek0QAReportRespInfo>() {});

		Collection<Object[]> params = new ArrayList();
		params.add(new Object[] {"2019-05-11", "2019-05-15", "SOP", Arrays.asList(
				new String[] {DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.NRZ.getValue(),
						DPProcessParamAttributes.PHH.getValue()}), data});
		/*params.add(new Object[] {null, null, null, null, new DPWeek0QAReportRespInfo()});
		params.add(new Object[] {null, "2019-05-11", null, null, new DPWeek0QAReportRespInfo()});
		params.add(new Object[] {"2019-05-11", null, null, null, new DPWeek0QAReportRespInfo()});
		params.add(new Object[] {null, null, null, Arrays.asList(
				new String[] {DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.NRZ.getValue(),
						DPProcessParamAttributes.PHH.getValue()}), new DPWeek0QAReportRespInfo()});*/

		return params;

	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		List<DPWeek0ReportInfo> emptyList = new ArrayList<>();

		Mockito.when(dpSopWeek0ParamBO.fetchWeek0Report(Mockito.isNull(), Mockito.isNull(), Mockito.isNull())).thenReturn(emptyList);
		Mockito.when(dpSopWeek0ParamBO.fetchWeek0Report(Mockito.isNull(), Mockito.isNotNull(), Mockito.isNotNull())).thenReturn(emptyList);
		Mockito.when(dpSopWeek0ParamBO.fetchWeek0Report(Mockito.isNotNull(), Mockito.isNull(), Mockito.isNotNull())).thenReturn(emptyList);
		Mockito.when(dpSopWeek0ParamBO.fetchWeek0Report(Mockito.isNotNull(), Mockito.isNotNull(), Mockito.isNull())).thenReturn(emptyList);

		Mockito.when(dpSopWeek0ParamBO.fetchWeek0Report(Mockito.isNull(), Mockito.isNull(), Mockito.isNotNull())).thenReturn(emptyList);
		Mockito.when(dpSopWeek0ParamBO.fetchWeek0Report(Mockito.isNull(), Mockito.isNotNull(), Mockito.isNull())).thenReturn(emptyList);
		Mockito.when(dpSopWeek0ParamBO.fetchWeek0Report(Mockito.isNotNull(), Mockito.isNull(), Mockito.isNull())).thenReturn(emptyList);

		Mockito.when(dpSopWeek0ParamBO.fetchWeek0Report(Mockito.any(Long.class), Mockito.any(Long.class), Mockito.anyList()))
				.thenReturn(this.data.getWeek0Reports());

		Mockito.when(dpProcessParamsBO.fetchWeek0Report(Mockito.isNull(), Mockito.isNull(), Mockito.isNull())).thenReturn(emptyList);
		Mockito.when(dpProcessParamsBO.fetchWeek0Report(Mockito.isNull(), Mockito.isNotNull(), Mockito.isNotNull())).thenReturn(emptyList);
		Mockito.when(dpProcessParamsBO.fetchWeek0Report(Mockito.isNotNull(), Mockito.isNull(), Mockito.isNotNull())).thenReturn(emptyList);
		Mockito.when(dpProcessParamsBO.fetchWeek0Report(Mockito.isNotNull(), Mockito.isNotNull(), Mockito.isNull())).thenReturn(emptyList);

		Mockito.when(dpProcessParamsBO.fetchWeek0Report(Mockito.isNull(), Mockito.isNull(), Mockito.isNotNull())).thenReturn(emptyList);
		Mockito.when(dpProcessParamsBO.fetchWeek0Report(Mockito.isNull(), Mockito.isNotNull(), Mockito.isNull())).thenReturn(emptyList);
		Mockito.when(dpProcessParamsBO.fetchWeek0Report(Mockito.isNotNull(), Mockito.isNull(), Mockito.isNull())).thenReturn(emptyList);

		Mockito.when(dpProcessParamsBO.fetchWeek0Report(Mockito.any(Long.class), Mockito.any(Long.class), Mockito.anyList()))
				.thenReturn(this.data.getWeek0Reports());

		ReflectionTestUtils.invokeMethod(week0QAReportDelegate, "init");

	}

	@Test
	public void fetchWeek0Repots() throws SystemException {
		DPWeek0QAReportRespInfo result = week0QAReportDelegate.fetchWeek0Repots("2019-05-11", "2019-05-22", "SOP", new ArrayList<>());
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getWeek0Reports());
		Assert.assertEquals(result.getWeek0Reports().size(), 1);

		DPWeek0QAReportRespInfo actualReport = week0QAReportDelegate.fetchWeek0Repots(startDate, endDate, occupancy, classifications);

		Assert.assertEquals(expectedData.toString(), actualReport.toString());
	}
}