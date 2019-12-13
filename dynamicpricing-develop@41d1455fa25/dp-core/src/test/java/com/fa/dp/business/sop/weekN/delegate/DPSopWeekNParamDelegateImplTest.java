package com.fa.dp.business.sop.weekN.delegate;

import com.fa.dp.business.sop.week0.bo.DPSopWeek0ParamBO;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.weekN.bo.DPSopWeekNParamBO;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.core.config.DPTestConfig;
import com.fa.dp.core.exception.SystemException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = DPTestConfig.class)
public class DPSopWeekNParamDelegateImplTest {

	private static final String content = "[{\"classification\":\"OCN\"}]";

	private static List<DPSopWeekNParamInfo> data;

	@Mock
	private DPSopWeekNParamBO dpSopWeekNParamBO;

	@Mock
	private DPSopWeek0ParamBO sopWeek0ParamBO;

	@InjectMocks
	private DPSopWeekNParamDelegateImpl sopWeekNParamDelegate;

	@Before
	public void setUp() throws Exception {
		data = new ObjectMapper().readValue(content, new TypeReference<List<DPSopWeekNParamInfo>>() {});
		List<DPSopWeekNParamInfo> emptyList = new ArrayList<>();
		Mockito.when(dpSopWeekNParamBO.searchSopWeekNParamSuccesfulUnderRiview(Mockito.isNull())).thenReturn(emptyList);
		Mockito.when(dpSopWeekNParamBO.searchSopWeekNParamSuccesfulUnderRiview(Mockito.isNotNull())).thenReturn(data);

		//Mockito.when(dpSopWeekNParamBO.saveSopWeekNParamInfo(Mockito.isNull()));
		Mockito.doThrow(SystemException.class).when(dpSopWeekNParamBO).saveSopWeekNParamInfo(Mockito.isNull());
		Mockito.doThrow(SystemException.class).when(dpSopWeekNParamBO).saveSopWeekNParamInfoList(Mockito.isNull());

		Mockito.when(dpSopWeekNParamBO.findSopWeekNParamById(Mockito.isNull())).thenReturn(null);
		Mockito.when(dpSopWeekNParamBO.findSopWeekNParamById(Mockito.isNotNull())).thenReturn(new DPSopWeekNParamInfo());

		Mockito.when(sopWeek0ParamBO.fetchSopWeek0ParamsRA(Mockito.isNull(), Mockito.isNull())).thenReturn(new ArrayList<>());
		Mockito.when(sopWeek0ParamBO.fetchSopWeek0ParamsRA(Mockito.isNotNull(), Mockito.isNull())).thenReturn(new ArrayList<>());
		Mockito.when(sopWeek0ParamBO.fetchSopWeek0ParamsRA(Mockito.isNull(), Mockito.isNotNull())).thenReturn(new ArrayList<>());
		List<DPSopWeek0ParamInfo> data1 = new ArrayList<>();
		data1.add(new DPSopWeek0ParamInfo());
		data1.get(0).setClassification("OCN");
		Mockito.when(sopWeek0ParamBO.fetchSopWeek0ParamsRA(Mockito.isNotNull(), Mockito.isNotNull())).thenReturn(data1);

		Mockito.when(dpSopWeekNParamBO.findSopWeekNParamById(Mockito.isNull())).thenReturn(null);
		DPSopWeekNParamInfo sopWeeknParam = new DPSopWeekNParamInfo();
		sopWeeknParam.setClassification("OCN");
		Mockito.when(dpSopWeekNParamBO.findSopWeekNParamById(Mockito.isNotNull())).thenReturn(sopWeeknParam);


	}

	@Test
	public void searchSopWeekNParamSuccesfulUnderRiview() throws SystemException {

		List<DPSopWeekNParamInfo> data1 = sopWeekNParamDelegate.searchSopWeekNParamSuccesfulUnderRiview(null);
		List<DPSopWeekNParamInfo> data2 = sopWeekNParamDelegate.searchSopWeekNParamSuccesfulUnderRiview("123");

		Assert.assertEquals(data1.size(), 0);
		Assert.assertEquals(data2.size(), 1);
		Assert.assertEquals(data2.get(0).getClassification(), "OCN");

	}

	@Test
	public void saveSopWeekNParamInfo() throws SystemException {
		Assertions.assertThrows(SystemException.class, () -> {
			sopWeekNParamDelegate.saveSopWeekNParamInfo(null);
		});
		Assertions.assertDoesNotThrow(() -> {sopWeekNParamDelegate.saveSopWeekNParamInfo(new DPSopWeekNParamInfo());});
	}

	@Test
	public void saveSopWeekNParamInfoList() {
		Assertions.assertThrows(SystemException.class, () -> {
			sopWeekNParamDelegate.saveSopWeekNParamInfoList(null);
		});
		Assertions.assertDoesNotThrow(() -> {sopWeekNParamDelegate.saveSopWeekNParamInfoList(new ArrayList<>());});
	}

	@Test
	public void fetchSopWeek0ParamsRA() {
		Assertions.assertDoesNotThrow(()->{
			List<DPSopWeek0ParamInfo> data1 = sopWeekNParamDelegate.fetchSopWeek0ParamsRA(null, null);
			List<DPSopWeek0ParamInfo> data2 = sopWeekNParamDelegate.fetchSopWeek0ParamsRA(null, "ELIGIBLE");
			List<DPSopWeek0ParamInfo> data3 = sopWeekNParamDelegate.fetchSopWeek0ParamsRA("123", null);
			List<DPSopWeek0ParamInfo> data4 = sopWeekNParamDelegate.fetchSopWeek0ParamsRA("123", "ELIGIBLE");

			Assert.assertEquals(data1.size(), 0);
			Assert.assertEquals(data2.size(), 0);
			Assert.assertEquals(data3.size(), 0);
			Assert.assertEquals(data4.size(), 1);
			Assert.assertEquals(data4.get(0).getClassification(), "OCN");

		});
	}

	@Test
	public void findSopWeekNParamById() {
		Assertions.assertDoesNotThrow(()->{
			DPSopWeekNParamInfo data1 = sopWeekNParamDelegate.findSopWeekNParamById(null);
			DPSopWeekNParamInfo data2 = sopWeekNParamDelegate.findSopWeekNParamById("123");

			Assert.assertEquals(data1, null);
			Assert.assertNotEquals(data2, null);
			Assert.assertEquals(data2.getClassification(), "OCN");

		});
	}

	@Test
	public void populateSopWeekNOutputParam() {
	}
}