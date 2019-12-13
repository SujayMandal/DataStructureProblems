package com.ca.umg.business.testbed.delegate;

import static com.ca.umg.business.constants.BusinessConstants.NUMBER_ONE;
import static com.ca.umg.business.constants.BusinessConstants.NUMBER_ZERO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.custom.mapper.UMGConfigurableMapper;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProviderImpl;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.bo.MappingBO;
import com.ca.umg.business.mapping.delegate.MappingDelegate;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.entity.MappingInput;
import com.ca.umg.business.mapping.entity.MappingOutput;
import com.ca.umg.business.mapping.helper.MappingHelper;
import com.ca.umg.business.mapping.info.TidIoDefinition;
import com.ca.umg.business.mapping.info.VersionTestContainer;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.transaction.bo.TransactionBO;
import com.ca.umg.business.transaction.entity.Transaction;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.version.bo.VersionBO;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionAPIContainer;
import com.ca.umg.business.versiontest.delegate.VersionTestDelegate;
import com.ca.umg.business.versiontest.delegate.VersionTestDelegateImpl;

import ma.glasnost.orika.impl.ConfigurableMapper;

public class VersionTestDelegateImplTest {

	private static final String TENANT_DATA = "./src/test/resources/TenantInput.txt";
	private static final String MAPPING_TENANT_INPUT_DATA = "./src/test/resources/tid_mapping_input.txt";
	private static final String BATCH_TENANT_DATA = "./src/test/resources/BatchTenantInput.json";
	private static final Logger LOGGER = LoggerFactory.getLogger(VersionTestDelegateImplTest.class);

	@Spy
	ConfigurableMapper mapper = new UMGConfigurableMapper();

	@InjectMocks
	private VersionTestDelegate versionTestDelegate = new VersionTestDelegateImpl();

	@Mock
	private TransactionBO transactionBO;

	@Mock
	private VersionBO versionBO;

	@Mock
	private MappingBO mappingBO;

	@Mock
	private MappingDelegate mappingDelegate;

	@Spy
	private MappingHelper mappingHelper;

	@Mock
	private SystemParameterProviderImpl systemParameterProvider;

	private static final String tidName = "IMPPORT";
	private static final String TXN_ID = "1";

	@Before
	public void setUp() {
		initMocks(this);
		Properties properties = new Properties();
		properties.put(RequestContext.TENANT_CODE, "localhost");
		new RequestContext(properties);
	}

	@Test
	@Ignore
	public void testGetVersionTestContainer() throws SystemException, BusinessException, IOException {
		byte[] tenantData = readFile(TENANT_DATA);
		Transaction transaction = createTransaction(tenantData);
		Mapping mapping = createMapping();
		Version versionTest = createVersion(mapping);
		MappingInput mappingInput = createMappingInput(mapping);

		Mockito.when(transactionBO.getTransactionByTxnId(TXN_ID)).thenReturn(transaction);
		Mockito.when(versionBO.findByNameAndVersion(transaction.getTenantModelName(), transaction.getMajorVersion(),
				transaction.getMinorVersion())).thenReturn(versionTest);
		Mockito.when(mappingBO.findInputByMapping(mapping)).thenReturn(mappingInput);

		VersionTestContainer versionTestContainer = versionTestDelegate.getVersionTestContainer(TXN_ID);

		assertNotNull(versionTestContainer);
		assertTrue(versionTestContainer.getDefaultValuesList().size() == 7);
		assertTrue(versionTestContainer.getAdditionalPropsList().size() > 40);
		assertTrue(versionTestContainer.getMajorVersion() == 1);
		assertTrue(versionTestContainer.getMinorVersion() == 0);
	}

	@Test
	public void testGetVersionTestContainerfromFile() throws SystemException, BusinessException, IOException {

		byte[] tenantData = readFile(TENANT_DATA);
		Mapping mapping = createMapping();
		Version versionTest = createVersion(mapping);
		MappingInput mappingInput = createMappingInput(mapping);

		Mockito.when(versionBO.findByNameAndVersion("IMPORT_VERSION", 1, 0)).thenReturn(versionTest);
		Mockito.when(mappingBO.findInputByMapping(mapping)).thenReturn(mappingInput);

		VersionTestContainer versionTestContainer = versionTestDelegate.getVersionTestContainerFromFile(tenantData);

		assertNotNull(versionTestContainer);
		assertTrue(versionTestContainer.getDefaultValuesList().size() == 7);
		assertTrue(versionTestContainer.getAdditionalPropsList().size() > 40);
		assertTrue(versionTestContainer.getMajorVersion() == 1);
		assertTrue(versionTestContainer.getMinorVersion() == 0);
	}

	@Test
	@Ignore
	public void testGetVersionTestContainerWithSysExep() throws SystemException, BusinessException, IOException {
		byte[] tenantData = readFile(TENANT_DATA);
		byte[] tenantInput = new byte[] { 1, 2, 3, };
		Transaction transaction = createTransaction(tenantData);
		transaction.setTenantInput(tenantInput);
		Mockito.when(transactionBO.getTransactionByTxnId(TXN_ID)).thenReturn(transaction);
		try {
			versionTestDelegate.getVersionTestContainer(TXN_ID);
		} catch (BusinessException ex) {
			assertEquals(BusinessExceptionCodes.BSE000087, ex.getCode());
		}
	}

	@Test
	@Ignore
	public void testGetVersionTestContainerWithBusExep() throws SystemException, BusinessException, IOException {
		byte[] tenantData = readFile(TENANT_DATA);
		Transaction transaction = createTransaction(tenantData);
		Mapping mapping = createMapping();
		Version versionTest = createVersion(mapping);
		Mockito.when(versionBO.findByNameAndVersion(transaction.getTenantModelName(), transaction.getMajorVersion(),
				transaction.getMinorVersion())).thenReturn(versionTest);
		Mockito.when(transactionBO.getTransactionByTxnId(TXN_ID)).thenReturn(transaction);
		try {
			versionTestDelegate.getVersionTestContainer(TXN_ID);
		} catch (BusinessException ex) {
			assertEquals(BusinessExceptionCodes.BSE000048, ex.getCode());

		}
	}

	@Test
	public void testGetVersionTestContainerFromFileWithSysExep()
			throws SystemException, BusinessException, IOException {
		byte[] tenantInput = new byte[] { 1, 2, 3, };
		try {
			versionTestDelegate.getVersionTestContainerFromFile(tenantInput);
		} catch (BusinessException ex) {
			assertEquals(BusinessExceptionCodes.BSE000089, ex.getCode());

		}
	}

	@Test
	public void testGetVersionTestContainerFromFileWithBusExep()
			throws SystemException, BusinessException, IOException {
		byte[] tenantData = readFile(TENANT_DATA);
		Mockito.when(versionBO.findByNameAndVersion("IMPORT_VERSION", 1, 0)).thenReturn(null);
		try {
			versionTestDelegate.getVersionTestContainerFromFile(tenantData);
		} catch (BusinessException ex) {
			assertEquals(BusinessExceptionCodes.BSE000087, ex.getCode());

		}
	}

	@Test
	public void testGetVersionAPI() throws SystemException, BusinessException, IOException {
		List<TidIoDefinition> value = new ArrayList<TidIoDefinition>();
		String dummyJson = "ABCD";

		Mapping mapping = createMapping();
		Version versionTest = createVersion(mapping);
		MappingInput mappingInput = createMappingInput(mapping);
		MappingOutput mappingOutput = createMappingOutput(mapping);

		Mockito.when(versionBO.getVersionDetails("1")).thenReturn(versionTest);
		Mockito.when(mappingBO.findInputByMapping(mapping)).thenReturn(mappingInput);
		Mockito.when(mappingBO.findOutputByMapping(mapping)).thenReturn(mappingOutput);
		Mockito.when(mappingDelegate.getTidIoDefinitions("IMPORTT", true)).thenReturn(value);
		Mockito.when(mappingDelegate.createRuntimeInputJson(value, "IMPORT_VERSION", 1, 0, "", Boolean.FALSE,
				Boolean.TRUE, Boolean.TRUE,Boolean.TRUE)).thenReturn(dummyJson);

		VersionAPIContainer versionAPIContainer = versionTestDelegate.getVersionAPI("1");
		assertNotNull(versionAPIContainer);
		assertNotNull(versionAPIContainer.getTenantInputSchema());
		assertNotNull(versionAPIContainer.getTenantInputSchemaName());
		assertNotNull(versionAPIContainer.getTenantOutputSchema());
		assertNotNull(versionAPIContainer.getTenantOutputSchemaName());
	}

	@Test
	@Ignore
	public void testCreateZip() {
		Mockito.when(systemParameterProvider.getParameter(SystemConstants.SAN_BASE))
				.thenReturn("/home/umgadmin/umg-tomcat/SAN_UMG");
		List<Map<String, Object>> jsonList = new ArrayList<Map<String, Object>>();
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			String inputMessage = FileUtils.readFileToString(new File(BATCH_TENANT_DATA));
			jsonList = convertJson(inputMessage, List.class);
			String fileName = versionTestDelegate.createZip(jsonList, true);
			assertNotNull(fileName);
			File downloadedFile = new File(fileName);

			fos = new FileOutputStream(downloadedFile);
		    zos = new ZipOutputStream(fos);
			versionTestDelegate.getZipFile(zos, fileName);
			File file = new File(new StringBuffer("/home/umgadmin/umg-tomcat/SAN_UMG/localhost").append(File.separator)
					.append("exceltozip").append(File.separator).append(fileName).append(".zip").toString());
			if (file.exists()) {
				file.delete();
			}
			if (downloadedFile.exists()) {
				downloadedFile.delete();

			}

		} catch (SystemException | BusinessException | IOException e) {
			LOGGER.error("Exception while creatinng the zip :" + e.getMessage());

		} finally {
			try {
				if(zos !=null) {
					zos.finish();
					zos.close();
					
				}
				if(fos !=null) {
					fos.flush();
					fos.close();
					
				}
			} catch (Exception exp) {
				LOGGER.error("Error while closing the resources :" + exp.getMessage());
			}
		}
	}

	@Test
	@Ignore
	public void testCreateZipWithMultipleFile() {
		Mockito.when(systemParameterProvider.getParameter(SystemConstants.SAN_BASE))
				.thenReturn("/home/umgadmin/umg-tomcat/SAN_UMG");
		List<Map<String, Object>> jsonList = new ArrayList<Map<String, Object>>();
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			String inputMessage = FileUtils.readFileToString(new File(BATCH_TENANT_DATA));
			jsonList = convertJson(inputMessage, List.class);
			String fileName = versionTestDelegate.createZip(jsonList, false);
			assertNotNull(fileName);
			File downloadedFile = new File(fileName);

		    fos = new FileOutputStream(downloadedFile);
		    zos = new ZipOutputStream(fos);
			versionTestDelegate.getZipFile(zos, fileName);
			File file = new File(new StringBuffer("/home/umgadmin/umg-tomcat/SAN_UMG/localhost").append(File.separator)
					.append("exceltozip").append(File.separator).append(fileName).append(".zip").toString());
			if (file.exists()) {
				file.delete();
			}
			if (downloadedFile.exists()) {
				downloadedFile.delete();
			}

		} catch (SystemException | BusinessException | IOException e) {
			LOGGER.error("Exception while creatinng the zip :" + e.getMessage());

		}
		finally {

			try {
				if(zos !=null) {
					zos.finish();
					zos.close();
					
				}
				if(fos !=null) {
					fos.flush();
					fos.close();
					
				}
			} catch (Exception exp) {
				LOGGER.error("Error while closing the resources :" + exp.getMessage());
			}
		
		}

	}

	private Model createModel() {
		Model model = new Model();
		model.setName("DummyModelName");
		return model;
	}

	private MappingInput createMappingInput(Mapping mapping) throws IOException {
		MappingInput mappingInput = new MappingInput();
		mappingInput.setTenantInterfaceDefn(readFile(MAPPING_TENANT_INPUT_DATA));
		mappingInput.setMapping(mapping);
		return mappingInput;
	}

	private MappingOutput createMappingOutput(Mapping mapping) throws IOException {
		MappingOutput mappingOutput = new MappingOutput();
		// Change this to output
		mappingOutput.setTenantInterfaceDefn(readFile(MAPPING_TENANT_INPUT_DATA));
		mappingOutput.setMapping(mapping);
		return mappingOutput;
	}

	private Version createVersion(Mapping mapping) {
		Version versionTest = new Version();
		versionTest.setName("IMPORT_VERSION");
		versionTest.setMajorVersion(NUMBER_ONE);
		versionTest.setMinorVersion(NUMBER_ZERO);
		versionTest.setId("11");
		versionTest.setMapping(mapping);
		return versionTest;
	}

	private Mapping createMapping() {
		Mapping mapping = new Mapping();
		mapping.setName(tidName);
		mapping.setId("11");
		mapping.setModel(createModel());
		return mapping;
	}

	private Transaction createTransaction(byte[] tenantData) {
		Transaction transaction = new Transaction();
		transaction.setTenantModelName("IMPORT_TEST");
		transaction.setMajorVersion(NUMBER_ONE);
		transaction.setMinorVersion(NUMBER_ZERO);
		transaction.setTenantInput(tenantData);
		return transaction;
	}

	byte[] readFile(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		return Files.readAllBytes(path);
	}

	private <T> T convertJson(String jsonString, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		T resultObject = null;
		resultObject = objectMapper.readValue(jsonString, clazz);
		return resultObject;

	}

	private TransactionDocument createTransactionData(String txnId, String clientTxnId, String tenantModelName,
			String libName, Integer majorVersion, Integer minorVersion, String status, Long runAsOfDate,
			Map<String, Object> data) {
		TransactionDocument txnDocument = new TransactionDocument();
		txnDocument.setLibraryName(libName);
		txnDocument.setClientTransactionID(clientTxnId);
		txnDocument.setMajorVersion(majorVersion);
		txnDocument.setMinorVersion(minorVersion);
		txnDocument.setRunAsOfDate(runAsOfDate.longValue());
		txnDocument.setStatus(status);
		txnDocument.setModelInput(data);
		txnDocument.setModelOutput(data);
		txnDocument.setTenantInput(data);
		txnDocument.setTenantOutput(data);

		return txnDocument;
	}

}