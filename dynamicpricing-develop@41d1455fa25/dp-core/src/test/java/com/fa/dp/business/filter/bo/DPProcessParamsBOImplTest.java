package com.fa.dp.business.filter.bo;

import com.fa.dp.business.command.dao.CommandDAO;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.validator.dao.DPProcessParamsDao;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.report.info.DPWeek0ReportInfo;
import com.fa.dp.business.week0.report.vacant.mapper.DPWeek0VacantReportMapperImpl;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.config.DPTestConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
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
public class DPProcessParamsBOImplTest {

	@Mock
	private DPProcessParamsDao dpProcessParamsDao;

	@Mock
	private CacheManager cacheManager;

	@Mock
	private CommandDAO commandDAO;

	@Parameterized.Parameter(value = 0)
	public Long startDate;

	@Parameterized.Parameter(value = 1)
	public Long endDate;

	@Parameterized.Parameter(value = 2)
	public List<String> classifications;

	@Parameterized.Parameter(value = 3)
	public List<DPProcessParam> expectedData;

	@InjectMocks
	private DPProcessParamsBOImpl processParamsBO;

	@Rule
	public final SpringMethodRule smr = new SpringMethodRule();

	@ClassRule
	public static final SpringClassRule scr = new SpringClassRule();

	private static final String content = "[{\"classification\":\"OCN\"}]";

	private static List<DPProcessParam> data;

	@Parameterized.Parameters
	public static Collection<Object[]> data() throws IOException {

		data = new ObjectMapper().readValue(content, new TypeReference<List<DPProcessParam>>() {
		});

		Collection<Object[]> params = new ArrayList();
		params.add(new Object[] { LocalDate.now().toEpochDay(), LocalDate.now().toEpochDay(), Arrays.asList(
				new String[] { DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.NRZ.getValue(),
						DPProcessParamAttributes.PHH.getValue() }), data });
		params.add(new Object[] { null, null, null, new ArrayList<>() });
		params.add(new Object[] { null, LocalDate.now().toEpochDay(), null, new ArrayList<>() });
		params.add(new Object[] { LocalDate.now().toEpochDay(), null, null, new ArrayList<>() });
		params.add(new Object[] { null, null, Arrays.asList(
				new String[] { DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.NRZ.getValue(),
						DPProcessParamAttributes.PHH.getValue() }), new ArrayList<>() });

		return params;

	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		List<DPProcessParam> emptyList = new ArrayList<>();

		Mockito.when(dpProcessParamsDao.findWeek0Report(Mockito.any(Long.class), Mockito.any(Long.class), Mockito.anyList())).thenReturn(data);

		Mockito.when(dpProcessParamsDao.findWeek0Report(Mockito.isNull(), Mockito.any(Long.class), Mockito.anyList())).thenReturn(emptyList);
		Mockito.when(dpProcessParamsDao.findWeek0Report(Mockito.isNull(), Mockito.isNull(), Mockito.anyList())).thenReturn(emptyList);
		Mockito.when(dpProcessParamsDao.findWeek0Report(Mockito.isNull(), Mockito.isNull(), Mockito.isNull())).thenReturn(emptyList);

		Mockito.when(dpProcessParamsDao.findWeek0Report(Mockito.any(Long.class), Mockito.isNull(), Mockito.anyList())).thenReturn(emptyList);
		Mockito.when(dpProcessParamsDao.findWeek0Report(Mockito.any(Long.class), Mockito.isNull(), Mockito.isNull())).thenReturn(emptyList);

		Mockito.when(dpProcessParamsDao.findWeek0Report(Mockito.isNull(), Mockito.any(Long.class), Mockito.isNull())).thenReturn(emptyList);
		Mockito.when(dpProcessParamsDao.findWeek0Report(Mockito.isNull(), Mockito.isNull(), Mockito.anyList())).thenReturn(emptyList);

		Class<?> clazz = DPProcessParamsBOImpl.class;

		Field f1 = processParamsBO.getClass().getDeclaredField("dpWeek0VacantReportMapper");
		f1.setAccessible(true);
		f1.set(processParamsBO, new DPWeek0VacantReportMapperImpl());

	}

	@Test
	public void saveDPProcessParam() {
	}

	@Test
	public void saveDPProcessParamInfo() {
	}

	@Test
	public void findInWeek0ForAssetNumber() {
	}

	@Test
	public void saveDPProcessParams() {
	}

	@Test
	public void saveDPProcessParamInfos() {
	}

	@Test
	public void searchByAssetNumber() {
	}

	@Test
	public void findByAssetNumberAndClassification() {
	}

	@Test
	public void isInEnum() {
	}

	@Test
	public void filterOnDuplicates() {
	}

	@Test
	public void filterOnInvestorCode() {
	}

	@Test
	public void filterOnAssetValue() {
	}

	@Test
	public void filterOnPropertyType() {
	}

	@Test
	public void findOcwenLoanBYAssetNumber() {
	}

	@Test
	public void findLatestNonDuplicateInWeek0ForGivenAsset() {
	}

	@Test
	public void fetchWeek0Report() throws IOException {
		List<DPProcessParam> actualReportList = dpProcessParamsDao.findWeek0Report(startDate, endDate, classifications);

		Assert.assertEquals(expectedData.size(), actualReportList.size());
		for (int i = 0; i < expectedData.size(); i++) {
			StringWriter sw1 = new StringWriter();
			StringWriter sw2 = new StringWriter();
			new ObjectMapper().writeValue(sw1, expectedData.get(i));
			new ObjectMapper().writeValue(sw2, actualReportList.get(i));
			Assert.assertEquals(sw1.toString(), sw2.toString());
		}

		List<DPWeek0ReportInfo> actualReportListBO = processParamsBO.fetchWeek0Report(startDate, endDate, classifications);

		Assert.assertEquals(actualReportListBO.size(), actualReportList.size());
		for (int i = 0; i < actualReportListBO.size(); i++) {
			StringWriter sw1 = new StringWriter();
			StringWriter sw2 = new StringWriter();
			new ObjectMapper().writeValue(sw1, expectedData.get(i));
			new ObjectMapper().writeValue(sw2, actualReportList.get(i));
			Assert.assertEquals(sw1.toString(), sw2.toString());
		}
	}
}