package com.ca.umg.business.transaction.delegate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.transaction.bo.TransactionBO;
import com.ca.umg.business.transaction.entity.Transaction;
import com.ca.umg.business.transaction.info.AdvanceTransactionFilter;
import com.ca.umg.business.transaction.info.TransactionFilter;
import com.ca.umg.business.transaction.info.TransactionFilterForApi;
import com.ca.umg.business.transaction.info.TransactionWrapper;
import com.ca.umg.business.transaction.info.TransactionWrapperForApi;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.transaction.mongo.info.TransactionDocumentForApi;
import com.ca.umg.business.transaction.mongo.info.TransactionDocumentInfo;
import com.ca.umg.business.version.VersionAbstractTest;
import com.ca.umg.business.version.entity.Version;
import com.google.common.collect.Maps;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Ignore
// TODO fix ignored test cases
public class TransactionDelegateTest extends VersionAbstractTest {

	private static final String TENANT_DATA = "./src/test/resources/TenantInput.txt";

	private static final String MODEL_DATA = "./src/test/resources/ModelInput.txt";
	private static final String MODEL_DATA1 = "./src/test/resources/ModelInput1.txt";

	private static final String DATE_PATTERN = "yyyy-MMM-dd HH:mm";

	private Long ONE_DAY = Long.parseLong("86400000");

	private Long JUL_31_2014 = Long.parseLong("1406745000000");

	private RequestContext requestContext;

	private Model model;

	private ModelLibrary modelLibrary;

	private Mapping mapping;

	private Version version;

	private List<String> txnIdList = new ArrayList<String>();

	@Inject
	private TransactionDelegate transactionDelegate;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Inject
	private MongoTemplate mongoTemplate;

	@Inject
	private TransactionBO transactionBo;

	@Before
	public void setup() {

		Properties properties = new Properties();
		InputStream inputStream = null;
		try {
			URL filePath = this.getClass().getResource("/umg.properties");
			inputStream = new FileInputStream(new File(filePath.getFile()));
			properties.load(inputStream);
		} catch (IOException e) {
			fail(e.getMessage());
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
		Iterator iterator = properties.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			System.setProperty(key, properties.getProperty(key));
		}

		requestContext = getLocalhostTenantContext();
		// This is added as some of the previous test cases aren't clearing the entries
		// in DB which is causing issues in this test
		// case.
		clearDB();
		createVersion();
		try {
			createTxnList();
		} catch (IOException | SystemException ioe) {
			ioe.printStackTrace();
		}
	}

	private void clearDB() {
		getTransactionDAO().deleteAllInBatch();
		getTransactionDAO().flush();
		getVersionDAO().deleteAllInBatch();
		getVersionDAO().flush();
		getSyndicateDataQueryInputDAO().deleteAllInBatch();
		getSyndicateDataQueryInputDAO().flush();
		getSyndicateDataQueryOutputDAO().deleteAllInBatch();
		getSyndicateDataQueryOutputDAO().flush();
		getSyndicateDataQueryDAO().deleteAllInBatch();
		getVersionDAO().flush();
		getMappingInputDAO().deleteAllInBatch();
		getMappingInputDAO().flush();
		getMappingOutputDAO().deleteAllInBatch();
		getMappingOutputDAO().flush();
		getMappingDAO().deleteAllInBatch();
		getMappingDAO().flush();
		getModelDefinitionDAO().deleteAllInBatch();
		getModelDefinitionDAO().flush();
		getModelDAO().deleteAllInBatch();
		getModelDAO().flush();
		getModelLibraryDAO().deleteAllInBatch();
		getModelLibraryDAO().flush();
	}

	@After
	public void tearDown() {
		deleteMongoData(txnIdList);
		txnIdList.clear();
		getTransactionDAO().deleteAllInBatch();
		getTransactionDAO().flush();
		deleteTestData(model, modelLibrary, mapping, version);
	}

	Map readFile(String fileName) throws IOException, SystemException {
		return ConversionUtil.convertJson(FileUtils.readFileToString(new File(fileName)), Map.class);
	}

	private void deleteTestData(Model model, ModelLibrary modelLibrary, Mapping mapping, Version version) {
		getTransactionDAO().deleteAllInBatch();
		getTransactionDAO().flush();
		getVersionDAO().deleteAllInBatch();
		getVersionDAO().flush();
		getMappingDAO().delete(mapping);
		getMappingDAO().flush();
		getModelDAO().delete(model);
		getModelDAO().flush();
		getModelLibraryDAO().delete(modelLibrary);
		getModelLibraryDAO().flush();
	}

	private void deleteMongoData(List<String> txnIdList) {
		DB db = mongoTemplate.getDb();
		DBCollection collectionFrmTemplate = db
				.getCollection(RequestContext.getRequestContext().getTenantCode() + FrameworkConstant.DOCUMENTS);
		for (String tranId : txnIdList) {
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("transactionId", tranId);
			JSONObject params = new JSONObject(paramMap);
			DBObject dbObj = (DBObject) JSON.parse(params.toString());
			collectionFrmTemplate.remove(dbObj);
		}
	}

	private void createVersion() {
		model = buildModel("createModel1", "Modle1", "docName", "ioName", "text/xml", "sampleIo");
		model.getModelDefinition().setModel(model);
		model.setUmgName(model.getName());
		getModelDAO().save(model);
		modelLibrary = buildModelLibrary("DummyLib", "Dummy Library Description", "DummyLib", "DummyJarIO.jar",
				"MATLAB", "INTERNAL", "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256",
				"Matlab-7.16");
		getModelLibraryDAO().save(modelLibrary);
		mapping = buildMapping("Mapping1", model, requestContext.getTenantCode(), "Description", "mappigIO");
		getMappingDAO().save(mapping);
		for (int i = 0; i < 10; i++) {
			version = buildVersion("TestVersionName" + i, "TestVersionDesc", 1, 2, "SAVED", mapping, modelLibrary,
					"this is version description");
			getVersionDAO().save(version);
		}
	}

	private void createTxnList() throws IOException, SystemException {
		Map modelData = readFile(MODEL_DATA);
		Map tenantData = readFile(TENANT_DATA);
		Transaction txn;
		TransactionDocument txnDoc = null;
		Long date = JUL_31_2014;
		for (int i = 0; i < 10; i++) {
			if (i % 2 == 0) {
				txn = createTransactionData("TestTxn-" + i, "TestVersionName" + i, "TestLibName", 1, 2, "SUCCESS", date,
						false, null, null);
				txnDoc = createTransactionDocument("TestTxn-" + i, "TestVersionName" + i, "TestLibName", 1, 2,
						"SUCCESS", date, modelData, modelData, tenantData, tenantData, false, null, null);
			} else {
				txn = createTransactionData("TestTxn-" + i, "TestVersionName" + i, "TestLibName", 1, 2, "SUCCESS", date,
						true, null, null);
				txnDoc = createTransactionDocument("TestTxn-" + i, "TestVersionName" + i, "TestLibName", 1, 2,
						"SUCCESS", date, modelData, modelData, tenantData, tenantData, false, null, null);
			}

			getTransactionDAO().save(txn);
			txnIdList.add(txn.getId());
			txnDoc.setTransactionId(txn.getId());
			mongoTemplate.save(txnDoc, RequestContext.getRequestContext().getTenantCode() + "_documents");
			date += ONE_DAY;
		}

		txn = createTransactionData("TestTxn-11", "TestVersionName11", "TestLibName", 1, 2, "ERROR", date, false,
				"RVE0110", " RVE0110 this is error discription");
		txnDoc = createTransactionDocument("TestTxn-11", "TestVersionName11", "TestLibName", 1, 2, "ERROR", date,
				modelData, modelData, tenantData, tenantData, false, "RVE0110", " RVE0110 this is error discription");
		getTransactionDAO().save(txn);
		txnIdList.add(txn.getId());
		txnDoc.setTransactionId(txn.getId());
		mongoTemplate.save(txnDoc, RequestContext.getRequestContext().getTenantCode() + "_documents");

	}

	@Test
	public void findAllTest() throws BusinessException, SystemException {
		TransactionFilter transactionFilter = new TransactionFilter();
		transactionFilter.setPage(1);
		transactionFilter.setPageSize(100);
		TransactionWrapper txnWrapper = transactionDelegate.listAll(transactionFilter);
		assertNotNull(txnWrapper);
		assertNotNull(txnWrapper.getTenantModelNameList());
		assertNotNull(txnWrapper.getLibraryNameList());
		assertNotNull(txnWrapper.getTransactionInfoList());
		assertEquals(11, txnWrapper.getTransactionInfoList().size());
		assertEquals(11, txnWrapper.getTenantModelNameList().size());
		assertEquals(1, txnWrapper.getLibraryNameList().size());
	}

	/*
	 * UMG-2064 : without fromdate also we can query the transaction table
	 * 
	 * @Test(expected = BusinessException.class) public void
	 * filterByWithoutFromDateTest() throws BusinessException, SystemException {
	 * TransactionFilter transactionFilter = new TransactionFilter();
	 * transactionFilter.setPage(1); transactionFilter.setPageSize(100);
	 * SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_PATTERN); Date
	 * toDate = new Date(JUL_31_2014);
	 * transactionFilter.setRunAsOfDateToString(dateFormatter.format(toDate));
	 * transactionDelegate.filterList(transactionFilter); }
	 */

	@Test
	public void readModelInputTest() throws BusinessException, SystemException, IOException {
		Map modelData = readFile(MODEL_DATA1);
		Map tenantData = readFile(TENANT_DATA);
		/*
		 * Mockito.when(transactionBo.getTxnDocumentByTxnId(txnIdList.get(0))).
		 * thenReturn( createTransactionDocument("TestTxn-1", "TestVersionName-1",
		 * "TestLibName", 1, 2, "Success", 43543534l, modelData, modelData, tenantData,
		 * tenantData, false, null, null));
		 */
		TransactionDocument txnDocument = transactionDelegate.getTxnDocument(txnIdList.get(0));
		Map modelInputData = readFile(MODEL_DATA1);
		/*
		 * if (txnDocument.getModelInput().equals(modelInputData)) { assertTrue(true); }
		 */
		Boolean diff = Maps.difference(modelInputData, txnDocument.getModelInput()).areEqual();
		assertTrue(diff);

		/*
		 * ObjectMapper mapper = new ObjectMapper(); Map<String, Object>
		 * expectedmodelTransformermap = mapper.readValue(
		 * IOUtils.toByteArray(TransactionDelegateTest.class.getClassLoader().
		 * getResourceAsStream(EXPECTED_MODEL_REQUEST_PAYLD_TRANSLATED)), new
		 * TypeReference<HashMap<String, Object>>() { });
		 * 
		 * String jsonStr = convertToJsonString(txnDocument.getModelInput());
		 * Map<String, Object> transformedmodelRequest =
		 * mapper.readValue(jsonStr.getBytes(), new TypeReference<HashMap<String,
		 * Object>>() {});
		 * 
		 * 
		 * Boolean diff1 = Maps.difference(expectedmodelTransformermap,
		 * transformedmodelRequest).areEqual(); assertTrue(diff1);
		 */

	}

	// TODO chnage the test according to new method
	@Test
	public void testSearchTransactions() throws BusinessException, SystemException {
		/*
		 * List<BasicSearchCriteria> basicSearchCriterias = new
		 * ArrayList<BasicSearchCriteria>();
		 * basicSearchCriterias.add(buildBasicSearchCriteria("EQUAL",
		 * "tenantRequest.header.modelName", "Test_Version"));
		 */
		TransactionFilter transactionFilter = new TransactionFilter();
		transactionFilter.setTenantModelName("TestVersionName1");
		transactionFilter.setClientTransactionID("TestTxn-1");
		transactionFilter.setPage(1);
		transactionFilter.setPageSize(50);
		AdvanceTransactionFilter advanceTransactionFilter = new AdvanceTransactionFilter();
		TransactionWrapper transactionWrapper = transactionDelegate.searchTransactions(transactionFilter, null);
		TransactionDocumentInfo transactionDocumentInfo = transactionWrapper.getTransactionDocumentInfos().get(0);
		assertNotNull(transactionWrapper);
		assertEquals("TestVersionName1", transactionDocumentInfo.getVersionName());
	}

	@Test
	public void testSearchTransactionsForRaApiError() throws BusinessException, SystemException {
		/*
		 * List<BasicSearchCriteria> basicSearchCriterias = new
		 * ArrayList<BasicSearchCriteria>();
		 * basicSearchCriterias.add(buildBasicSearchCriteria("EQUAL",
		 * "tenantRequest.header.modelName", "Test_Version"));
		 */
		TransactionFilter transactionFilter = new TransactionFilter();
		transactionFilter.setTenantModelName("TestVersionName11");
		transactionFilter.setFullVersion("1.2");
		transactionFilter.setTransactionStatus("ERROR");
		transactionFilter.setErrorDescription("RVE0110");
		transactionFilter.setPage(1);
		transactionFilter.setPageSize(50);
		TransactionFilterForApi transactionFilterForApi = new TransactionFilterForApi();
		transactionFilterForApi.setIncludeTntOutput(Boolean.TRUE);
		transactionFilterForApi.setIncludeTntInput(Boolean.TRUE);
		TransactionWrapperForApi transactionWrapperApi = transactionDelegate
				.searchTransactionsForRaApi(transactionFilter, null, transactionFilterForApi);
		if (CollectionUtils.isNotEmpty(transactionWrapperApi.getTransactions())) {
			TransactionDocumentForApi transactionDocumentForApi = transactionWrapperApi.getTransactions().get(0);
			assertNotNull(transactionDocumentForApi);
			assertEquals("TestVersionName11", transactionDocumentForApi.getVersionName());
		}
	}

	@Test
	public void testSearchTransactionsForRaApi() throws BusinessException, SystemException {
		/*
		 * List<BasicSearchCriteria> basicSearchCriterias = new
		 * ArrayList<BasicSearchCriteria>();
		 * basicSearchCriterias.add(buildBasicSearchCriteria("EQUAL",
		 * "tenantRequest.header.modelName", "Test_Version"));
		 */
		TransactionFilter transactionFilter = new TransactionFilter();
		transactionFilter.setTenantModelName("TestVersionName1");
		transactionFilter.setFullVersion("1.2");
		transactionFilter.setRunAsOfDateFromString("2014-Jul-31 00:00");
		transactionFilter.setTransactionType("prod");
		transactionFilter.setTransactionStatus("Success");
		transactionFilter.setExecutionGroup("Modeled");
		transactionFilter.setCreatedBy("junitTest");
		transactionFilter.setClientTransactionID("TestTxn-1");
		transactionFilter.setRaTransactionID(txnIdList.get(1));
		transactionFilter.setPage(1);
		transactionFilter.setPageSize(50);
		TransactionFilterForApi transactionFilterForApi = new TransactionFilterForApi();
		transactionFilterForApi.setIncludeTntOutput(Boolean.TRUE);
		transactionFilterForApi.setIncludeTntInput(Boolean.TRUE);
		TransactionWrapperForApi transactionWrapperApi = transactionDelegate
				.searchTransactionsForRaApi(transactionFilter, null, transactionFilterForApi);
		TransactionDocumentForApi transactionDocumentForApi = transactionWrapperApi.getTransactions().get(0);
		assertNotNull(transactionDocumentForApi);
		assertEquals("TestVersionName1", transactionDocumentForApi.getVersionName());
	}

	/*
	 * private BasicSearchCriteria buildBasicSearchCriteria(String operator, String
	 * key, Object value) { BasicSearchCriteria basicSearchCriteria = new
	 * BasicSearchCriteria(); basicSearchCriteria.setSearchKey(key);
	 * basicSearchCriteria.setSearchValue(value);
	 * basicSearchCriteria.setSearchOperator(operator); return basicSearchCriteria;
	 * }
	 */

}
