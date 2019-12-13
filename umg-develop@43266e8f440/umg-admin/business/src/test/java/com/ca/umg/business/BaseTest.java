/**
 * 
 */
package com.ca.umg.business;

import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.mapping.dao.MappingDAO;
import com.ca.umg.business.mapping.dao.MappingInputDAO;
import com.ca.umg.business.mapping.dao.MappingOutputDAO;
import com.ca.umg.business.model.dao.ModelDAO;
import com.ca.umg.business.model.dao.ModelDefinitionDAO;
import com.ca.umg.business.model.dao.ModelLibraryDAO;
import com.ca.umg.business.syndicatedata.dao.SyndicateDataDAO;
import com.ca.umg.business.syndicatedata.dao.SyndicateDataQueryDAO;
import com.ca.umg.business.syndicatedata.dao.SyndicateDataQueryInputDAO;
import com.ca.umg.business.syndicatedata.dao.SyndicateDataQueryOutputDAO;
import com.ca.umg.business.syndicatedata.entity.SyndicateData;
import com.ca.umg.business.tenant.dao.AddressDAO;
import com.ca.umg.business.tenant.dao.SystemKeyDAO;
import com.ca.umg.business.tenant.dao.TenantConfigDAO;
import com.ca.umg.business.tenant.dao.TenantDAO;
import com.ca.umg.business.tenant.entity.SystemKey;
import com.ca.umg.business.tenant.entity.Tenant;
import com.ca.umg.business.tenant.entity.TenantConfig;
import com.ca.umg.business.transaction.dao.TransactionDAO;
import com.ca.umg.business.transaction.entity.Transaction;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.version.dao.VersionDAO;
import com.ca.umg.report.service.dao.ModelReportStatusDAO;

/**
 * @author kamathan
 *
 */
public abstract class BaseTest {

    @Inject
    private TenantDAO tenantDAO;

    @Inject
    private AddressDAO addressDAO;

    @Inject
    private SystemKeyDAO systemKeyDAO;

    @Inject
    private TenantConfigDAO tenantConfigDAO;

    @Inject
    private ModelDAO modelDAO;

    @Inject
    private ModelLibraryDAO modelLibraryDAO;

    @Inject
    private SyndicateDataDAO syndicateDataDAO;

    @Inject
    SyndicateDataQueryDAO syndicateDataQueryDAO;

    @Inject
    private MappingDAO mappingDAO;

    @Inject
    private MappingInputDAO mappingInputDAO;

    @Inject
    private MappingOutputDAO mappingOutputDAO;

    @Inject
    private VersionDAO versionDAO;

    @Inject
    private TransactionDAO transactionDAO;

    @Inject
    private SyndicateDataQueryInputDAO syndicateDataQueryInputDAO;

    @Inject
    private SyndicateDataQueryOutputDAO syndicateDataQueryOutputDAO;

    @Inject
    private ModelDefinitionDAO modelDefinitionDAO;
    
    @Inject
    private ModelReportStatusDAO modelReportStatusDAO;

    @Before
    public void setup() {

    }

    protected RequestContext getLocalhostTenantContext() {
        Tenant tenant = getTenantDAO().findByName("localhost");
        if (tenant == null) {
            String type = "DATABASE";
            SystemKey driver = createSystemKey(SystemConstants.SYSTEM_KEY_DB_DRIVER, type);
            SystemKey url = createSystemKey(SystemConstants.SYSTEM_KEY_DB_URL, type);
            SystemKey schema = createSystemKey(SystemConstants.SYSTEM_KEY_DB_SCHEMA, type);
            SystemKey password = createSystemKey(SystemConstants.SYSTEM_KEY_DB_PASSWORD, type);
            SystemKey username = createSystemKey(SystemConstants.SYSTEM_KEY_DB_USER, type);

            tenant = new Tenant();
            tenant.setName("localhost");
            tenant.setCode("localhost");
            tenant.setTenantType("both");
            tenant.setDescription("localhost");

            Set<TenantConfig> tenantConfigs = new HashSet<TenantConfig>();
            tenantConfigs.add(buildTenantConfig(tenant, driver, "org.hsqldb.jdbcDriver"));
            tenantConfigs.add(buildTenantConfig(tenant, url, "jdbc:hsqldb:mem:base"));
            tenantConfigs.add(buildTenantConfig(tenant, schema, "base"));
            tenantConfigs.add(buildTenantConfig(tenant, username, "SA"));
            tenantConfigs.add(buildTenantConfig(tenant, password, ""));
            tenant.setTenantConfigs(tenantConfigs);
            tenant.setTenantConfigs(tenantConfigs);
            tenant = getTenantDAO().save(tenant);
        }
        assertNotNull(tenant);
        Properties properties = new Properties();
        properties.put(RequestContext.TENANT_CODE, tenant.getCode());
        return new RequestContext(properties);
    }

    protected SystemKey createSystemKey(String key, String type) {
        SystemKey systemKey = getSystemKeyDAO().findByKey(key);
        if (systemKey == null) {
            systemKey = new SystemKey();
            systemKey.setKey(key);
            systemKey.setType(type);
            systemKey = getSystemKeyDAO().save(systemKey);
        }
        return systemKey;
    }

    public SyndicateData createSyndicateData(String containerName, String description, Long versionId, String tableName,
            String versionName, String versionDescription, Long validFrom, Long validTo) {
        SyndicateData syndicateData = new SyndicateData();
        syndicateData.setContainerName(containerName);
        syndicateData.setDescription(description);
        syndicateData.setVersionId(versionId);
        syndicateData.setTableName(tableName);
        syndicateData.setValidFrom(validFrom);
        syndicateData.setValidTo(validTo);
        syndicateData.setVersionName(versionName);
        syndicateData.setVersionDescription(versionDescription);
        return syndicateDataDAO.save(syndicateData);
    }

    protected TenantConfig buildTenantConfig(Tenant tenant, SystemKey systemKey, String value) {
        TenantConfig tenantConfig = new TenantConfig();
        tenantConfig.setTenant(tenant);
        tenantConfig.setSystemKey(systemKey);
        tenantConfig.setValue(value);
        return tenantConfig;
    }

    protected Transaction createTransactionData(String clientTxnId, String tenantModelName, String libName, Integer majorVersion,
            Integer minorVersion, String status, Long runAsOfDate, boolean isTestTxn, String errorCode, String errorDescription) {
        Transaction txn = new Transaction();
        txn.setLibraryName(libName);
        txn.setTenantModelName(tenantModelName);
        txn.setClientTransactionID(clientTxnId);
        txn.setMajorVersion(majorVersion);
        txn.setMinorVersion(minorVersion);
        txn.setRunAsOfDate(new DateTime(runAsOfDate.longValue()));
        txn.setCreatedDate(new DateTime(runAsOfDate.longValue()));
        txn.setStatus(status);
        txn.setTestTransaction(isTestTxn);
        txn.setErrorCode(errorCode);
        if (errorDescription != null) {
        	txn.setErrorDescription(errorDescription.getBytes());
        } else {
        	txn.setErrorDescription(null);
        }
        return txn;
    }
    
    protected TransactionDocument createTransactionDocument(String clientTxnId, String tenantModelName, String libName,
            Integer majorVersion, Integer minorVersion, String status, Long runAsOfDate, Map modelInput, Map modelOutput,
            Map tenantInput, Map tenantOutput, boolean isTestTxn, String errorCode, String errorDescription)
 {
        TransactionDocument txnDocument = new TransactionDocument();
        txnDocument.setLibraryName(libName);
        txnDocument.setVersionName(tenantModelName);
        txnDocument.setClientTransactionID(clientTxnId);
        txnDocument.setMajorVersion(majorVersion);
        txnDocument.setMinorVersion(minorVersion);
        txnDocument.setRunAsOfDate(runAsOfDate.longValue());
        txnDocument.setCreatedDate(runAsOfDate.longValue());
        txnDocument.setStatus(status);
        txnDocument.setExecutionGroup("Modeled");
        txnDocument.setCreatedBy("junitTest");
            txnDocument.setTenantInput(tenantInput);
            txnDocument.setTenantOutput(tenantOutput);
            txnDocument.setModelInput(modelInput);
            txnDocument.setModelOutput(modelOutput);
            txnDocument.setTest(isTestTxn);
        txnDocument.setErrorCode(errorCode);
        if (errorDescription != null) {
            txnDocument.setErrorDescription(errorDescription);
        } else {
            txnDocument.setErrorDescription(null);
        }
        return txnDocument;
    }

    protected SearchOptions buildSearchOptions(int page, int pageSize, String sortColumn, boolean descending){
    	SearchOptions searchOptions = new SearchOptions();
    	searchOptions.setPage(page);
    	searchOptions.setPageSize(pageSize);
    	searchOptions.setSortColumn(sortColumn);
    	searchOptions.setDescending(descending);
    	return searchOptions;
    }

    @After
    public void tearDown() {
    }

    public ModelDAO getModelDAO() {
        return modelDAO;
    }

    public TenantDAO getTenantDAO() {
        return tenantDAO;
    }

    public ModelReportStatusDAO getModelReportStatusDAO() {
        return modelReportStatusDAO;
    }

    public void setModelReportStatusDAO(ModelReportStatusDAO modelReportStatusDAO) {
        this.modelReportStatusDAO = modelReportStatusDAO;
    }

    public AddressDAO getAddressDAO() {
        return addressDAO;
    }

    public SystemKeyDAO getSystemKeyDAO() {
        return systemKeyDAO;
    }

    public TenantConfigDAO getTenantConfigDAO() {
        return tenantConfigDAO;
    }

    public SyndicateDataDAO getSyndicateDataDAO() {
        return syndicateDataDAO;
    }

    public ModelLibraryDAO getModelLibraryDAO() {
        return modelLibraryDAO;
    }

    public SyndicateDataQueryDAO getSyndicateDataQueryDAO() {
        return syndicateDataQueryDAO;
    }

    public MappingDAO getMappingDAO() {
        return mappingDAO;
    }

    public MappingInputDAO getMappingInputDAO() {
        return mappingInputDAO;
    }

    public MappingOutputDAO getMappingOutputDAO() {
        return mappingOutputDAO;
    }

    public VersionDAO getVersionDAO() {
        return versionDAO;
    }

    public TransactionDAO getTransactionDAO() {
        return transactionDAO;
    }

    public SyndicateDataQueryInputDAO getSyndicateDataQueryInputDAO() {
        return syndicateDataQueryInputDAO;
    }

    public SyndicateDataQueryOutputDAO getSyndicateDataQueryOutputDAO() {
        return syndicateDataQueryOutputDAO;
    }

    public ModelDefinitionDAO getModelDefinitionDAO() {
        return modelDefinitionDAO;
    }
}
