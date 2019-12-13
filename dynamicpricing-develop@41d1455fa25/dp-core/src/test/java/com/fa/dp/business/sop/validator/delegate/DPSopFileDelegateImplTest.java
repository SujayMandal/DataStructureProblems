package com.fa.dp.business.sop.validator.delegate;

import com.fa.dp.business.sop.week0.bo.DPSopProcessBO;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNProcessStatusInfo;
import com.fa.dp.core.config.DPTestConfig;
import com.fa.dp.core.exception.business.BusinessException;
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
public class DPSopFileDelegateImplTest {

	@InjectMocks
	private DPSopFileDelegateImpl sopFileDelegate;

	@Mock
	private DPSopProcessBO dpSopProcessBO;

	@Before
	public void setUp() throws Exception {
		//Mockito.when(dpSopProcessBO.findSopWeekNFileStatus(Mockito.isNull())).thenThrow(SystemException.class);
		Mockito.when(dpSopProcessBO.findSopWeekNFileStatus(Mockito.isNull())).thenReturn(new ArrayList<>());
		List<DPSopWeekNProcessStatusInfo> data1 = new ArrayList<>();
		data1.add(new DPSopWeekNProcessStatusInfo());
		data1.get(0).setInputFileName("Test");
		data1.get(0).setStatus("COMPLETED");

		Mockito.when(dpSopProcessBO.findSopWeekNFileStatus(Mockito.isNotNull())).thenReturn(data1);

	}

	@Test
	public void checkForFileStatus() {
		Assertions.assertDoesNotThrow(() -> {
			sopFileDelegate.checkForFileStatus(null);
		});
		Assertions.assertThrows(BusinessException.class, () -> {
			sopFileDelegate.checkForFileStatus("COMPLETED");
		});
	}

	@Test
	public void saveSopWeekNProcess() {

	}

	@Test
	public void findSopWeekNParamsData() {
	}

	@Test
	public void getSOPWeekNParams() {
	}

	@Test
	public void uploadSopWeekNExcel() {
	}

	@Test
	public void updateSopWeeknRunningStatus() {
	}

	@Test
	public void sopWeekNProcessCommand() {
	}
}