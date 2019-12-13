package com.fa.dp.business.sop.week0.bo;

import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.sop.week0.dao.DPSopWeek0ParamsDao;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.week0.report.info.DPWeek0ReportInfo;
import com.fa.dp.business.week0.report.sop.mapper.DPWeek0SopReportMapperImpl;
import com.fa.dp.core.config.DPTestConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
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
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = DPTestConfig.class)
public class DPSopWeek0ParamBOImplTest {

	@Mock
	private DPSopWeek0ParamsDao dpSopWeek0ParamsDao;

	@InjectMocks
	private DPSopWeek0ParamBOImpl sopWeek0ParamBO;

	@Rule
	public final SpringMethodRule smr = new SpringMethodRule();

	@ClassRule
	public static final SpringClassRule scr = new SpringClassRule();

	@Parameterized.Parameter(value = 0)
	public Long startDate;

	@Parameterized.Parameter(value = 1)
	public Long endDate;

	@Parameterized.Parameter(value = 2)
	public List<String> classifications;

	@Parameterized.Parameter(value = 3)
	public List<DPSopWeek0Param> expectedData;

	@Parameterized.Parameter(value = 4)
	public String assetNumber;

	@Parameterized.Parameter(value = 5)
	public String eligible;

	private static final String content = "[{\"classification\":\"OCN\"}]";

	private static List<DPSopWeek0Param> data;

	@Parameterized.Parameters
	public static Collection<Object[]> data() throws IOException {

		data = new ObjectMapper().readValue(content, new TypeReference<List<DPSopWeek0Param>>() {});

		Collection<Object[]> params = new ArrayList();
		params.add(new Object[] {LocalDate.now().toEpochDay(), LocalDate.now().toEpochDay(), Arrays.asList(
				new String[] {DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.NRZ.getValue(),
						DPProcessParamAttributes.PHH.getValue()}), data, "123", "ELIGIBLE"});
		params.add(new Object[] {null, null, null, new ArrayList<>(), "123", "ELIGIBLE"});
		params.add(new Object[] {null, LocalDate.now().toEpochDay(), null, new ArrayList<>(), "123", "ELIGIBLE"});
		params.add(new Object[] {LocalDate.now().toEpochDay(), null, null, new ArrayList<>(), "123", "ELIGIBLE"});
		params.add(new Object[] {null, null, Arrays.asList(
				new String[] {DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.NRZ.getValue(),
						DPProcessParamAttributes.PHH.getValue()}), new ArrayList<>(), "123", "ELIGIBLE"});

		return params;

	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		List<DPSopWeek0Param> emptyList = new ArrayList<>();

		Mockito.when(dpSopWeek0ParamsDao.findWeek0Report(Mockito.any(Long.class), Mockito.any(Long.class), Mockito.anyList())).thenReturn(data);

		Mockito.when(dpSopWeek0ParamsDao.findWeek0Report(Mockito.isNull(), Mockito.any(Long.class), Mockito.anyList())).thenReturn(emptyList);
		Mockito.when(dpSopWeek0ParamsDao.findWeek0Report(Mockito.isNull(), Mockito.isNull(), Mockito.anyList())).thenReturn(emptyList);
		Mockito.when(dpSopWeek0ParamsDao.findWeek0Report(Mockito.isNull(), Mockito.isNull(), Mockito.isNull())).thenReturn(emptyList);

		Mockito.when(dpSopWeek0ParamsDao.findWeek0Report(Mockito.any(Long.class), Mockito.isNull(), Mockito.anyList())).thenReturn(emptyList);
		Mockito.when(dpSopWeek0ParamsDao.findWeek0Report(Mockito.any(Long.class), Mockito.isNull(), Mockito.isNull())).thenReturn(emptyList);

		Mockito.when(dpSopWeek0ParamsDao.findWeek0Report(Mockito.isNull(), Mockito.any(Long.class), Mockito.isNull())).thenReturn(emptyList);
		Mockito.when(dpSopWeek0ParamsDao.findWeek0Report(Mockito.isNull(), Mockito.isNull(), Mockito.anyList())).thenReturn(emptyList);

		//DPSopWeek0ParamBOImpl.class.getField("dpWeek0SopReportMapper").setAccessible(true);

		Class<?> clazz = DPSopWeek0ParamBOImpl.class;
		//Object cc = sopWeek0ParamBO.getClass().

		Field f1 = sopWeek0ParamBO.getClass().getDeclaredField("dpWeek0SopReportMapper");
		f1.setAccessible(true);
		f1.set(sopWeek0ParamBO, new DPWeek0SopReportMapperImpl());

		//Mockito.when(dpWeek0SopReportMapper).thenReturn(new DPWeek0SopReportMapperImpl());
		//dpWeek0SopReportMapper = new DPWeek0SopReportMapperImpl();

		/*List<DPWeek0ReportInfo> emptyInfos = new ArrayList<>();
		List<DPSopWeek0Param> emptyParams = new ArrayList<>();

		Mockito.when(dpWeek0SopReportMapper.mapDomainToLinfoList(Mockito.isNull())).thenReturn(null);
		Mockito.when(dpWeek0SopReportMapper.mapDomainToLinfoList(Mockito.)).thenReturn(emptyInfos);


		List<DPWeek0ReportInfo> infos = new ArrayList<>();
		DPWeek0ReportInfo reportInfo = new DPWeek0ReportInfo();
		reportInfo.setClassification("OCN");
		infos.add(reportInfo);

		Mockito.when(dpWeek0SopReportMapper.mapDomainToLinfoList(Mockito.anyList())).thenReturn(infos);*/
	}

	@Test
	public void fetchWeek0Report() throws IOException {

		List<DPSopWeek0Param> actualReportList = dpSopWeek0ParamsDao.findWeek0Report(startDate, endDate, classifications);

		Assert.assertEquals(expectedData.size(), actualReportList.size());
		for (int i = 0; i < expectedData.size(); i++) {
			StringWriter sw1 = new StringWriter();
			StringWriter sw2 = new StringWriter();
			new ObjectMapper().writeValue(sw1, expectedData.get(i));
			new ObjectMapper().writeValue(sw2, actualReportList.get(i));
			Assert.assertEquals(sw1.toString(), sw2.toString());
		}

		List<DPWeek0ReportInfo> actualReportListBO = sopWeek0ParamBO.fetchWeek0Report(startDate, endDate, classifications);

		Assert.assertEquals(actualReportListBO.size(), actualReportList.size());
		for (int i = 0; i < actualReportListBO.size(); i++) {
			StringWriter sw1 = new StringWriter();
			StringWriter sw2 = new StringWriter();
			new ObjectMapper().writeValue(sw1, expectedData.get(i));
			new ObjectMapper().writeValue(sw2, actualReportList.get(i));
			Assert.assertEquals(sw1.toString(), sw2.toString());
		}
	}

	@Test
	public void fetchSopWeek0ParamsRA() throws IOException {
		List<DPSopWeek0Param> actualData = dpSopWeek0ParamsDao.findByAssetNumberAndEligibleOrderByCreatedDateDesc(assetNumber, eligible);
		for (int i = 0; i < actualData.size(); i++) {
			StringWriter sw1 = new StringWriter();
			StringWriter sw2 = new StringWriter();
			new ObjectMapper().writeValue(sw1, expectedData.get(i));
			new ObjectMapper().writeValue(sw2, actualData.get(i));
			Assert.assertEquals(sw1.toString(), sw2.toString());
		}
	}

}
