package com.fa.dp.business.weekn.report.bo;

import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.weekn.report.dao.WeekNDailyQAReportDao;
import com.fa.dp.business.weekn.report.entity.WeekNDailyQAReport;
import com.fa.dp.core.config.DPTestConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
//@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = DPTestConfig.class)
public class WeekNDailyQAReportBOImplTest {

	//@ClassRule
	//public static final SpringClassRule scr = new SpringClassRule();

	private static final String content = "[{\"classification\":\"OCN\"}]";

	private static List<WeekNDailyQAReport> data;

	//@Rule
	//public final SpringMethodRule smr = new SpringMethodRule();

	@Mock
	private WeekNDailyQAReportDao weekNDailyQAReportDao;

	@InjectMocks
	private WeekNDailyQAReportBOImpl weekNDailyQAReportBO;

	@Parameterized.Parameter(value = 0)
	public LocalDate startDate;

	@Parameterized.Parameter(value = 1)
	public LocalDate endDate;

	@Parameterized.Parameter(value = 2)
	public List<String> classifications;

	@Parameterized.Parameter(value = 3)
	public List<WeekNDailyQAReport> expectedData;

	@Parameterized.Parameters
	public static Collection<Object[]> data() throws IOException {

		data = new ObjectMapper().readValue(content, new TypeReference<List<WeekNDailyQAReport>>() {
		});

		Collection<Object[]> params = new ArrayList();
		params.add(new Object[] { LocalDate.now(), LocalDate.now(), Arrays.asList(
				new String[] { DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.NRZ.getValue(),
						DPProcessParamAttributes.PHH.getValue() }), data });
		params.add(new Object[] { null, null, null, new ArrayList<>() });
		params.add(new Object[] { null, LocalDate.now(), null, new ArrayList<>() });
		params.add(new Object[] { LocalDate.now(), null, null, new ArrayList<>() });
		params.add(new Object[] { null, null, Arrays.asList(
				new String[] { DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.NRZ.getValue(),
						DPProcessParamAttributes.PHH.getValue() }), new ArrayList<>() });

		return params;
	}

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

		List<WeekNDailyQAReport> emptyList = new ArrayList<>();

		Mockito.when(
				weekNDailyQAReportDao.findAllByStartTimeAndEndTime(Mockito.any(LocalDate.class), Mockito.any(LocalDate.class), Mockito.anyList()))
				.thenReturn(data);

		Mockito.when(weekNDailyQAReportDao.findAllByStartTimeAndEndTime(Mockito.isNull(), Mockito.any(LocalDate.class), Mockito.anyList()))
				.thenReturn(emptyList);
		Mockito.when(weekNDailyQAReportDao.findAllByStartTimeAndEndTime(Mockito.isNull(), Mockito.isNull(), Mockito.anyList())).thenReturn(emptyList);
		Mockito.when(weekNDailyQAReportDao.findAllByStartTimeAndEndTime(Mockito.isNull(), Mockito.isNull(), Mockito.isNull())).thenReturn(emptyList);

		Mockito.when(weekNDailyQAReportDao.findAllByStartTimeAndEndTime(Mockito.any(LocalDate.class), Mockito.isNull(), Mockito.anyList()))
				.thenReturn(emptyList);
		Mockito.when(weekNDailyQAReportDao.findAllByStartTimeAndEndTime(Mockito.any(LocalDate.class), Mockito.isNull(), Mockito.isNull()))
				.thenReturn(emptyList);

		Mockito.when(weekNDailyQAReportDao.findAllByStartTimeAndEndTime(Mockito.isNull(), Mockito.any(LocalDate.class), Mockito.isNull()))
				.thenReturn(emptyList);
		Mockito.when(weekNDailyQAReportDao.findAllByStartTimeAndEndTime(Mockito.isNull(), Mockito.isNull(), Mockito.anyList())).thenReturn(emptyList);
	}

	@Test
	public void fetchListingsForGivenDateRange() throws IOException {

		List<WeekNDailyQAReport> actualReportList = weekNDailyQAReportDao.findAllByStartTimeAndEndTime(startDate, endDate, classifications);

		Assert.assertEquals(expectedData.size(), actualReportList.size());
		for (int i = 0; i < expectedData.size(); i++) {
			StringWriter sw1 = new StringWriter();
			StringWriter sw2 = new StringWriter();
			new ObjectMapper().writeValue(sw1, expectedData.get(i));
			new ObjectMapper().writeValue(sw2, actualReportList.get(i));
			Assert.assertEquals(sw1.toString(), sw2.toString());
		}

		List<WeekNDailyQAReport> actualReportListBO = weekNDailyQAReportBO.fetchListingsForGivenDateRange(startDate, endDate, classifications);

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