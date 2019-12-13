package com.ca.pool.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.junit.Test;
import org.mockito.InjectMocks;

import com.ca.framework.core.exception.SystemException;
import com.ca.pool.model.TransactionCriteria;

public class PoolCriteriaUtilTest {
	
	@InjectMocks
	private PoolCriteriaUtil poolCriteriaUtil;
	
	/*@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}*/    

	@Test
	public void getReplacedPoolCriteriaTest() {
		String poolCritForTest = 
		"#TENANT# = localhost & #EXECUTION_LANGUAGE# = matlab & #TRANSACTION_TYPE# = batch & #MODEL# = test-model & "
		+ "#MODEL_VERSION# = 1.0 & #TRANSACTION_MODE# = prod & #CHANNEL# = online & #EXECUTION_ENVIRONMENT# = Linux"; 
		TransactionCriteria expctdTransactionCriteria = new TransactionCriteria();
		expctdTransactionCriteria.setTenantCode("localhost");
		expctdTransactionCriteria.setExecutionLanguage("matlab");
		expctdTransactionCriteria.setTransactionRequestType("batch");
		expctdTransactionCriteria.setModelName("test-model");
		expctdTransactionCriteria.setModelVersion("1.0");
		expctdTransactionCriteria.setTransactionRequestMode("prod");
		expctdTransactionCriteria.setTransactionRequestChannel("online");
		expctdTransactionCriteria.setExecutionEnvironment("Linux");
		expctdTransactionCriteria.setExecutionLanguageVersion("7.16");
		
		try {
			final String replacedStr = poolCriteriaUtil.getReplacedPoolCriteria(poolCritForTest, expctdTransactionCriteria);
			String expectedRslt = "'localhost' eq 'localhost' &and 'matlab' eq 'matlab' &and 'batch' eq 'batch' &and 'test-model' eq 'test-model' &and '1.0' eq '1.0' &and 'prod' eq 'prod' &and 'online' eq 'online' &and 'linux' eq 'linux'";
			assertNotNull(replacedStr);
			assertEquals(expectedRslt, replacedStr);
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getReplacedPoolCriteriaForAnyTest () {
		String poolCritForAnyTest = "'localhost' eq 'any' &and 'matlab' eq 'matlab' &and '7.16' eq '7.16' "
				+ "&and 'online' eq 'any' &and 'test-model' eq 'any' &and '1.0' eq 'any' &and 'test' eq 'any'";
		String replacedCritForAny = poolCriteriaUtil.getReplacedPoolCriteriaForAny(poolCritForAnyTest);
		String expectdRsltForAny = "true and 'matlab' eq 'matlab' and '7.16' eq '7.16' "
				+ "and true and true and true and true";
		
		assertNotNull(replacedCritForAny);
		assertEquals(expectdRsltForAny, replacedCritForAny);
	}
	
	@Test
	public void getCriteriaObjectTest () {
		String poolCritForObjectTest = "#TENANT# = localhost & #EXECUTION_LANGUAGE# = matlab & #TRANSACTION_TYPE# = batch & #MODEL# = test-model & "
				+ "#MODEL_VERSION# = 1.0 & #TRANSACTION_MODE# = prod & #CHANNEL# = online & #EXECUTION_ENVIRONMENT# = Linux"; 
		try {
			TransactionCriteria createdTransactionCriteria = poolCriteriaUtil.getCriteriaObject(poolCritForObjectTest);
			assertNotNull(createdTransactionCriteria);
		
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getCriteraValuesTest () {
		String poolCritForValuesTest = "#TENANT# = localhost & #ENVIRONMENT# = matlab & #ENVIRONMENT_VERSION# = 7.16 & "
				+ "#TRANSACTION_TYPE# = any & #MODEL# = test-model & #MODEL_VERSION# = 1.0 & #TRANSACTION_MODE# = any"; 
		Map<String, String> criteriaValueMap = new HashMap<>();
		poolCriteriaUtil.getCriteraValues(poolCritForValuesTest, criteriaValueMap);
		
		assertTrue(MapUtils.isNotEmpty(criteriaValueMap));
		assertEquals(criteriaValueMap.get("tenant"), "localhost");
		assertEquals(criteriaValueMap.get("environment"), "matlab");
		assertEquals(criteriaValueMap.get("model"), "test-model");
	}
	
}
