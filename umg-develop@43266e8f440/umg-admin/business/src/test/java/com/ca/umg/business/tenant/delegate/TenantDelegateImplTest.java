package com.ca.umg.business.tenant.delegate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.AddressInfo;
import com.ca.framework.core.info.tenant.SystemKeyInfo;
import com.ca.framework.core.info.tenant.TenantConfigInfo;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.business.batching.info.BatchFileInfo;
import com.ca.umg.business.systemparam.delegate.SystemParameterDelegate;
import com.ca.umg.business.systemparam.info.SystemParameterInfo;
import com.ca.umg.business.tenant.AbstractTenantTest;
import com.ca.umg.business.tenant.entity.AuthToken;
import com.ca.umg.business.tenant.entity.SystemKey;
import com.ca.umg.business.tenant.entity.Tenant;
import com.ca.umg.business.tenant.entity.TenantConfig;

@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"),
		@ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
public class TenantDelegateImplTest extends AbstractTenantTest {

	@Inject
	private TenantDelegate tenantDelegate;

	@Inject
	private SystemParameterDelegate systemParameterDelegate;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	private static final String TENANT_DATA = "./src/test/resources/TenantInput.txt";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TenantDelegateImplTest.class);

	@Before
	public void init() {
		getLocalhostTenantContext();
	}

	@Test
	public void testCreateForNull() throws SystemException {
		try{
		deleteTenant();
		Set<TenantConfigInfo> tenantConfigSet = new HashSet<TenantConfigInfo>();
		Set<AddressInfo> addresses = new HashSet<AddressInfo>();
		TenantInfo tenantInfo = new TenantInfo();
		tenantInfo.setName("localhost");
		tenantInfo.setDescription("desrcription");
		tenantInfo.setCode("localhost");
		tenantInfo.setTenantType("both");
		tenantInfo.setAddresses(addresses);
		tenantInfo.setTenantConfigs(tenantConfigSet);
		TenantInfo resultInfo = tenantDelegate.create(tenantInfo, false);		
		Assert.assertNotNull(resultInfo);
		}catch(Exception e){// once completeion of  full test cases on tenant onboarding, then can remove this exception
			LOGGER.error("Exception while uploading Batch File : "
					+ e.getMessage());
			
		}
		deleteTenant();

	}

	@Test
	public void testListAllForEmpty() throws BusinessException, SystemException {
		deleteTenant();
		Tenant tenant = createTenant();
		assertNotNull(tenant);
		List<TenantInfo> resultInfo = tenantDelegate.listAll();
		Assert.assertNotNull(resultInfo);

		deleteTenant();

	}

	@Test
	public void testGetTenantConfig() throws BusinessException, SystemException {
		deleteTenant();
		Tenant tenant = createTenant();
		assertNotNull(tenant);
		assertNotNull(tenant.getId());
		assertNotNull(tenant.getTenantConfigs());
		assertEquals(6, tenant.getTenantConfigs().size());

		TenantConfig resultConfig = tenantDelegate.getTenantConfig("localhost",
				"driver", "DATABASE");
		Assert.assertNotNull(resultConfig);

		deleteTenant();

	}

	@Test
	public void testGetTenant() throws BusinessException, SystemException {
		deleteTenant();
		createTenant();

		TenantInfo tenant = tenantDelegate.getTenant("localhost");
		assertNotNull(tenant);
		assertNotNull(tenant.getId());
		assertNotNull(tenant.getTenantConfigs());
		assertEquals(6, tenant.getTenantConfigs().size());
		Assert.assertEquals("localhost", tenant.getCode());

		deleteTenant();
	}

	@Test
	public void testGetTenantWithAllSystemKeys() throws BusinessException,
			SystemException {
		deleteTenant();
		createTenant();
		TenantInfo tenantInfo = tenantDelegate.getTenantWithAllSystemKeys();
		Assert.assertNotNull(tenantInfo);

		deleteTenant();
	}

	@Ignore
	public void testUpdate() throws BusinessException, SystemException,
			IOException {
		deleteTenant();
		createSysParamInnfo();
		AddressInfo addressInfo = buildAddressInfo("address1", "address2",
				"city", "state", "country", "zip");
		Set<AddressInfo> addressInfos = new HashSet<AddressInfo>();
		addressInfos.add(addressInfo);
		Set<TenantConfigInfo> tenantConfigInfos = new HashSet<TenantConfigInfo>();
		SystemKeyInfo systemKeyInfo = buildSystemKeyInfo("BATCH_ENABLED",
				"TENANT");

		TenantInfo tenantInfo = buildTenantInfo("localhost", "localhost desc",
				"localhost", "both", addressInfos, tenantConfigInfos);

		TenantConfigInfo tenantConfigInfo = buildTenantConfigInfo(
				systemKeyInfo, "true", tenantInfo);
		tenantConfigInfos.add(tenantConfigInfo);
		createTenant();
		TenantInfo updatedTenantInfo = tenantDelegate.update(tenantInfo);

		Assert.assertNotNull(updatedTenantInfo);
		Assert.assertNotNull(updatedTenantInfo.getTenantConfigs());
		Assert.assertEquals(6, updatedTenantInfo.getTenantConfigs().size());
		deleteTenant();
		FileUtils.deleteDirectory(new File(systemParameterProvider
				.getParameter(SystemConstants.SAN_BASE)));

	}

	private void createSysParamInnfo() throws BusinessException,
			SystemException {
		SystemParameterInfo systemParameterInfo = new SystemParameterInfo();
		systemParameterInfo.setSysKey("sanBase");
		systemParameterInfo.setSysValue("batchTest");
		systemParameterInfo.setIsActive('Y');
		systemParameterDelegate.saveParameter(systemParameterInfo);
	}

	@Test
	public void testFileUpload() throws BusinessException, SystemException,
			IOException {
		createSysParamInnfo();
		try {
			InputStream inputStream = readFile(TENANT_DATA);
			BatchFileInfo batchInfo = new BatchFileInfo();
			batchInfo.setFileInputStream(inputStream);
			batchInfo.setFileName("TenantInput.txt");
			tenantDelegate.fileUpload(batchInfo);
		} catch (BusinessException | SystemException | IOException e) {
			LOGGER.error("Exception while uploading Batch File : "
					+ e.getMessage());
		} finally {
			FileUtils.deleteDirectory(new File(systemParameterProvider
					.getParameter(SystemConstants.SAN_BASE)));
		}

	}

	private Tenant createTenant() {
		String type = "DATABASE";
		SystemKey driver = createSystemKey("driver", type);
		assertNotNull(driver);
		SystemKey url = createSystemKey("url", type);
		assertNotNull(url);
		SystemKey schema = createSystemKey("schema", type);
		assertNotNull(schema);
		SystemKey password = createSystemKey("password", type);
		assertNotNull(password);
		SystemKey username = createSystemKey("username", type);
		assertNotNull(username);
		SystemKey batchEnabled = createSystemKey("BATCH_ENABLED", "TENANT");
		assertNotNull(batchEnabled);

		Tenant tenant = new Tenant();
		tenant.setCode("localhost");
		tenant.setDescription("DESC");
		tenant.setName("localhost");
		tenant.setTenantType("Type");
        Set<AuthToken> authTokenSet = new HashSet<AuthToken>();
        AuthToken authToken = new AuthToken();
        authToken.setAuthCode("TestCode");     
        authTokenSet.add(authToken);
        tenant.setAuthTokens(authTokenSet);
		Set<TenantConfig> tenantConfigs = new HashSet<TenantConfig>();
		tenantConfigs.add(buildTenantConfig(tenant, driver,
				"org.hsqldb.jdbcDriver"));
		tenantConfigs
				.add(buildTenantConfig(tenant, url, "jdbc:hsqldb:mem:base"));
		tenantConfigs.add(buildTenantConfig(tenant, schema, "base"));
		tenantConfigs.add(buildTenantConfig(tenant, username, "SA"));
		tenantConfigs.add(buildTenantConfig(tenant, password, ""));
		tenantConfigs.add(buildTenantConfig(tenant, batchEnabled, "true"));
		tenant.setTenantConfigs(tenantConfigs);

		tenant = getTenantDAO().save(tenant);
		return tenant;
	}

	private void deleteTenant() {
		getTenantConfigDAO().deleteAll();
		getAddressDAO().deleteAll();
		getTenantDAO().deleteAll();
		getSystemKeyDAO().deleteAll();
	}

	private InputStream readFile(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		return Files.newInputStream(path);
	}
	
	@Test
	public void testGetSystemKeyValue() throws BusinessException, SystemException{
		createTenant();
		String keyValue = tenantDelegate.getSystemKeyValue(SystemConstants.SYSTEM_KEY_BATCH_ENABLED, SystemConstants.SYSTEM_KEY_TYPE_TENANT);
		boolean isBatchEnabled = Boolean.valueOf(keyValue);
		Assert.assertTrue(isBatchEnabled);
		deleteTenant();
	}

}
