package com.ca.umg.business.tenant.report.model.dao;

import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;
import static java.lang.System.currentTimeMillis;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.tenant.report.model.TenantModelReport;
import com.ca.umg.business.tenant.report.model.TenantModelReportEnum;

public class TenantModelReportDAOTest {

	private static final String TRANSACTION_ID = "1";
	private static final String CLIENT_TRANSACTION_ID = "2";
	private static final Long CREATED_DATE = currentTimeMillis();
	private static final String TENANT_INPUT = "tenant-input";
	private static final String TENANT_OUTPUT = "tenant-output";
	private static final String MODEL_INPUT = "model-input";
	private static final String MODEL_OUTPUT = "model-output";
	private static final String ENC_KY = "key";

	@InjectMocks
	private final TenantModelReportDAO dao = new TenantModelReportDAOImpl();

	@Mock
	private MongoTemplate mongoTemplate;

	private TenantModelReport tenantModelReport;

	@Before
	public void setRequestContext() {
		initMocks(this);
		createRequestContext();
		// ((TenantModelReportDAOImpl) dao).setMongoTemplate(mongoTemplate);
		tenantModelReport = createTenantModelReport();
	}

	private void createRequestContext() {
		final Properties p = new Properties();
		p.setProperty("TENANT_CODE", "localhost");
		new RequestContext(p);
	}

	private TenantModelReport createTenantModelReport() {
		final TenantModelReport tenantModelReport = new TenantModelReport();
		tenantModelReport.setTransactionId(TRANSACTION_ID);
		tenantModelReport.setClientTransactionID(CLIENT_TRANSACTION_ID);
		tenantModelReport.setCreatedDate(CREATED_DATE);
		tenantModelReport.setTenantInput(createDataMap(ENC_KY, TENANT_INPUT));
		tenantModelReport.setTenantOutput(createDataMap(ENC_KY, TENANT_OUTPUT));
		tenantModelReport.setModelInput(createDataMap(ENC_KY, MODEL_INPUT));
		tenantModelReport.setModelInput(createDataMap(ENC_KY, MODEL_OUTPUT));
		return tenantModelReport;
	}

	private Map<String, Object> createDataMap(final String key, final Object value) {
		final Map<String, Object> data = new HashMap<>();
		data.put(key, value);
		return data;
	}

	private void saveTransaction(final TenantModelReport transaction) {
		mongoTemplate.save(transaction, getRequestContext().getTenantCode() + FrameworkConstant.DOCUMENTS);
	}

	@After
	public void deleteTransactions() {
		mongoTemplate.dropCollection(getRequestContext().getTenantCode() + FrameworkConstant.DOCUMENTS);
		tenantModelReport = null;
	}

	@Test
	public void testViewTransactionInputAndOutputs() {
		try {
			saveTransaction(tenantModelReport);
			when(dao.viewTransactionInputAndOutputs(TRANSACTION_ID)).thenReturn(tenantModelReport);
			// final TenantModelReport report =
			// dao.viewTransactionInputAndOutputs(TRANSACTION_ID);
			dao.viewTransactionInputAndOutputs(TRANSACTION_ID);
			// TODO
			// assertTrue(report != null);
			// assertTrue(report.getTransactionId().equals(TRANSACTION_ID));
		} catch (SystemException se) {
			fail("Should not get exception in this case");
		}
	}

	@Test
	public void testExportTransactionInputAndOutputs() {
		try {
			saveTransaction(tenantModelReport);
			when(dao.exportTransactionInputAndOutputs(TRANSACTION_ID, TenantModelReportEnum.TENANT_INPUT)).thenReturn(tenantModelReport);
			// final TenantModelReport report =
			// dao.viewTransactionInputAndOutputs(TRANSACTION_ID);
			dao.exportTransactionInputAndOutputs(TRANSACTION_ID, TenantModelReportEnum.TENANT_INPUT);
			// TODO
			// assertTrue(report != null);
			// assertTrue(report.getTransactionId().equals(TRANSACTION_ID));
		} catch (SystemException se) {
			fail("Should not get exception in this case");
		}
	}
}