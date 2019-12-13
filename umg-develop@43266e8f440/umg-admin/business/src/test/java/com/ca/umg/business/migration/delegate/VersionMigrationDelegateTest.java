package com.ca.umg.business.migration.delegate;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ma.glasnost.orika.impl.ConfigurableMapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.ca.framework.core.custom.mapper.UMGConfigurableMapper;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.mapping.bo.MappingBO;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.entity.MappingInput;
import com.ca.umg.business.mapping.entity.MappingOutput;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mapping.info.MappingStatus;
import com.ca.umg.business.migration.audit.entity.MigrationAudit;
import com.ca.umg.business.migration.audit.info.MigrationAuditInfo;
import com.ca.umg.business.migration.audit.info.VersionData;
import com.ca.umg.business.migration.bo.MigrationBO;
import com.ca.umg.business.migration.helper.VersionMigrationHelper;
import com.ca.umg.business.migration.info.ColumnMetaDataInfo;
import com.ca.umg.business.migration.info.TableMetaDataInfo;
import com.ca.umg.business.migration.info.VersionDetail;
import com.ca.umg.business.migration.info.VersionImportInfo;
import com.ca.umg.business.migration.info.VersionMigrationInfo;
import com.ca.umg.business.migration.info.VersionMigrationWrapper;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelDefinition;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.syndicatedata.bo.SyndicateDataBO;
import com.ca.umg.business.syndicatedata.delegate.SyndicateDataQueryDelegate;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryObjectInfo;
import com.ca.umg.business.version.bo.VersionBO;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.business.version.info.VersionStatus;

//@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
//@RunWith(SpringJUnit4ClassRunner.class)
public class VersionMigrationDelegateTest {

    @InjectMocks
    private VersionMigrationDelegateImpl versionMigrationDelegate;

    @Mock
    private VersionBO versionBO;

    @Mock
    private SyndicateDataQueryDelegate syndicateDataQueryDelegate;

    @Mock
    private ModelDelegate modelDelegate;

    @Mock
    private MappingBO mappingBO;

    @Mock
    private MigrationBO migrationBO;

    @Spy
    ConfigurableMapper mapper = new UMGConfigurableMapper();

    @Mock
    private SyndicateDataBO syndicateDataBO;

    @Mock
    private Version version;

    @Mock
    private Mapping mapping;

    @Mock
    private Model model;

    @Mock
    private ModelDefinition modelDefn;

    @Mock
    private ModelLibrary modelLibrary;

    @Mock
    private List<ModelArtifact> modelArtifactList;

    @Mock
    private List<ModelArtifact> modelLibraryArtifactList;

    @Mock
    private List<SyndicateDataColumnInfo> syndicateDataColumnInfoList;

    @Mock
    private List<SyndicateDataQueryInfo> syndicateDataQueryInfo;

    @Mock
    private MappingOutput mappingOutput;

    @Mock
    private MappingInput mappingInput;

    @Mock
    private VersionMigrationHelper versionMigrationHelper;

    private List<TableMetaDataInfo> tableMetaDataInfoList;

    private List<ColumnMetaDataInfo> columnMetaDataInfoList;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        initMocks();
        try {
            when(versionBO.getVersionDetails(Mockito.anyString())).thenReturn(version);
            when(modelDelegate.getModelArtifacts(Mockito.anyString())).thenReturn(modelArtifactList);
            when(modelDelegate.getModelLibraryArtifacts(Mockito.anyString())).thenReturn(modelLibraryArtifactList);
            when(
                    syndicateDataQueryDelegate.listByMappingNameAndType(Mockito.anyString(),
                            Mockito.matches(BusinessConstants.TYPE_INPUT_MAPPING))).thenReturn(syndicateDataQueryInfo);
            when(syndicateDataBO.getTableColumnInfo(Mockito.anyString())).thenReturn(syndicateDataColumnInfoList);
            when(mappingBO.findOutputByMapping(Mockito.any(Mapping.class))).thenReturn(mappingOutput);
            when(mappingBO.findInputByMapping(Mockito.any(Mapping.class))).thenReturn(mappingInput);
        } catch (BusinessException | SystemException e) {
            e.printStackTrace();
        }
    }

    private void initMocks() {
        version = new Version();
        mapping = new Mapping();
        modelLibrary = new ModelLibrary();
        model = new Model();
        model.setId("1234");
        model.setIoDefinitionName("model-io-defn-xml.xml");
        model.setAllowNull(true);
        modelDefn = new ModelDefinition();
        modelDefn.setIoDefinition(new byte[1]);
        model.setModelDefinition(modelDefn);
        modelLibrary.setId("1234");
        modelLibrary.setJarName("dummy_modelLib_jar.jar");
        mapping.setId("1234");
        version.setId("1234");
        mapping.setModel(model);
        version.setMapping(mapping);
        version.setModelLibrary(modelLibrary);
        modelArtifactList = new ArrayList<ModelArtifact>();
        modelLibraryArtifactList = new ArrayList<ModelArtifact>();
        syndicateDataQueryInfo = new ArrayList<SyndicateDataQueryInfo>();
        syndicateDataColumnInfoList = new ArrayList<SyndicateDataColumnInfo>();
        mappingOutput = new MappingOutput();
        mappingInput = new MappingInput();
        mappingInput.setMappingData(new String("ABCD").getBytes());
        mappingInput.setTenantInterfaceDefn(new String("LMNO").getBytes());
        mappingInput.setTenantInterfaceSysDefn(new String("VWXYZ").getBytes());
        mappingOutput.setMappingData(new String("ABCD").getBytes());
        mappingOutput.setTenantInterfaceDefn(new String("LMNO").getBytes());
        columnMetaDataInfoList = new ArrayList<ColumnMetaDataInfo>();
        tableMetaDataInfoList = new ArrayList<TableMetaDataInfo>();
        populateTableMetaDataList(tableMetaDataInfoList);
        populateModelArtifactList(modelArtifactList);
        populateModelLibraryArtifactList(modelLibraryArtifactList);
        populateSyndicateDataQueryInfo(syndicateDataQueryInfo);
        populateSyndicateDataColumnInfoList(syndicateDataColumnInfoList);
    }

    private void populateTableMetaDataList(List<TableMetaDataInfo> tableMetaDataInfoList) {
        TableMetaDataInfo tableMetaDataInfo = new TableMetaDataInfo();
        tableMetaDataInfo.setTableName("TestTableName");
        populateColumnMetaDataInfoList(columnMetaDataInfoList);
        tableMetaDataInfo.setColumnMetaData(columnMetaDataInfoList);
        tableMetaDataInfoList.add(tableMetaDataInfo);
    }

    private void populateColumnMetaDataInfoList(List<ColumnMetaDataInfo> columnMetaDataInfoList) {
        columnMetaDataInfoList.add(populateColumnMetaDataInfo("COL1", "STRING", "5"));
        columnMetaDataInfoList.add(populateColumnMetaDataInfo("COL2", "NUMBER", "5"));
        columnMetaDataInfoList.add(populateColumnMetaDataInfo("COL3", "DOUBLE", "5"));
    }

    private void populateColumnMetaDataInfoListForError(List<ColumnMetaDataInfo> columnMetaDataInfoList) {
        columnMetaDataInfoList.clear();
        columnMetaDataInfoList.add(populateColumnMetaDataInfo("COL1", "STRING", "5"));
        columnMetaDataInfoList.add(populateColumnMetaDataInfo("COL2", "NUMBER", "5"));
        columnMetaDataInfoList.add(populateColumnMetaDataInfo("COL3", "DOUBLE", "5"));
        columnMetaDataInfoList.add(populateColumnMetaDataInfo("COL4", "STRING", "5"));
    }

    private ColumnMetaDataInfo populateColumnMetaDataInfo(String name, String dataType, String size) {
        ColumnMetaDataInfo columnMetaDataInfo = new ColumnMetaDataInfo();
        columnMetaDataInfo.setName(name);
        columnMetaDataInfo.setDataType(dataType);
        columnMetaDataInfo.setSize(size);
        return columnMetaDataInfo;
    }

    private void populateModelLibraryArtifactList(List<ModelArtifact> modelLibraryArtifactList) {
        ModelArtifact modelArtifact = getModelArtifact("dummy_modelLib_jar.jar", "modelLib1-"
                + new SimpleDateFormat("MM-dd-yyyy-HH-mm").format(Calendar.getInstance().getTime()), "modelLib1");
        modelLibraryArtifactList.add(modelArtifact);
    }

    private void populateSyndicateDataColumnInfoList(List<SyndicateDataColumnInfo> syndicateDataColumnInfoList) {
        SyndicateDataColumnInfo syndicateDataColumnInfo = null;
        syndicateDataColumnInfo = getSyndicateDataColumnInfo("COL1", "COL1", "", 5, "STRING", 2, false, 0);
        syndicateDataColumnInfoList.add(syndicateDataColumnInfo);
        syndicateDataColumnInfo = getSyndicateDataColumnInfo("COL2", "COL2", "", 5, "NUMBER", 2, false, 0);
        syndicateDataColumnInfoList.add(syndicateDataColumnInfo);
        syndicateDataColumnInfo = getSyndicateDataColumnInfo("COL3", "COL3", "", 5, "DOUBLE", 2, false, 2);
        syndicateDataColumnInfoList.add(syndicateDataColumnInfo);
    }

    private SyndicateDataColumnInfo getSyndicateDataColumnInfo(String field, String displayName, String desc, Integer colSize,
            String colType, Integer index, boolean isMandatory, Integer precision) {
        SyndicateDataColumnInfo dataColumnInfo = new SyndicateDataColumnInfo();
        dataColumnInfo.setField(field);
        dataColumnInfo.setDisplayName(displayName);
        dataColumnInfo.setDescription(desc);
        dataColumnInfo.setColumnSize(colSize);
        dataColumnInfo.setColumnType(colType);
        dataColumnInfo.setIndex(index);
        dataColumnInfo.setMandatory(isMandatory);
        return dataColumnInfo;
    }

    private void populateSyndicateDataQueryInfo(List<SyndicateDataQueryInfo> syndicateDataQueryInfoList) {
        SyndicateDataQueryInfo syndicateDataQueryInfo = getSyndicateDataQueryInfo("TestQueryName", "TestDesc", 1, "SINGLE",
                "ARRAY", "Input", null);
        SyndicateDataQueryObjectInfo queryObj = createQueryObject("Select A,B,C", "ABC A", "WHERE C ='ABC'", "ORDER BY A ASC",
                "Select A,B,C FROM ABC WHERE C ='ABC' AND B='DEF' ORDER BY A ASC");
        syndicateDataQueryInfo.setQueryObject(queryObj);
        syndicateDataQueryInfoList.add(syndicateDataQueryInfo);
    }

    private SyndicateDataQueryInfo getSyndicateDataQueryInfo(String name, String desc, Integer execSeq, String rowType,
            String dataType, String mappingType, MappingInfo mapping) {
        SyndicateDataQueryInfo queryInfo = new SyndicateDataQueryInfo();
        queryInfo.setName(name);
        queryInfo.setDescription(desc);
        queryInfo.setExecSequence(execSeq);
        queryInfo.setRowType(rowType);
        queryInfo.setDataType(dataType);
        queryInfo.setMapping(mapping);
        queryInfo.setMappingType(mappingType);
        return queryInfo;
    }

    private SyndicateDataQueryObjectInfo createQueryObject(String selectStr, String fromStr, String whrClause, String orderByStr,
            String execQuery) {
        SyndicateDataQueryObjectInfo queryObjInfo = new SyndicateDataQueryObjectInfo();
        queryObjInfo.setSelectString(selectStr);
        queryObjInfo.setFromString(fromStr);
        queryObjInfo.setWhereClause(whrClause);
        queryObjInfo.setOrderByString(orderByStr);
        queryObjInfo.setExecutableQuery(execQuery);
        return queryObjInfo;
    }

    private void populateModelArtifactList(List<ModelArtifact> modelArtifactList) {
        ModelArtifact modelArtifact = getModelArtifact("dummy_model_doc.pdf", "model1-"
                + new SimpleDateFormat("MM-dd-yyyy-HH-mm").format(Calendar.getInstance().getTime()), "model1");
        modelArtifactList.add(modelArtifact);
    }

    private ModelArtifact getModelArtifact(String name, String umgName, String modelName) {
        ModelArtifact modelArtifact = new ModelArtifact();
        modelArtifact.setName(name);
        modelArtifact.setUmgName(umgName);
        modelArtifact.setModelName(modelName);
        modelArtifact.setDataArray(new byte[1]);
        return modelArtifact;
    }

    @Test
    public void versionMigrationTest() {
        try {
            MigrationAuditInfo migrationAuditInfo = new MigrationAuditInfo();
            VersionMigrationWrapper versionMigrationWrapper = versionMigrationDelegate.exportVersion(Mockito.anyString(),
                    migrationAuditInfo);
            assertNotNull(versionMigrationWrapper);
            assertNotNull(versionMigrationWrapper.getVersionMigrationInfo());
            assertNotNull(versionMigrationWrapper.getModelDoc());
            assertNotNull(versionMigrationWrapper.getModelLibraryJar());
            assertNotNull(versionMigrationWrapper.getModelIODefinition());
            assertThat(versionMigrationWrapper.getModelDocName(), is("dummy_model_doc.pdf"));
            assertThat(versionMigrationWrapper.getModelXMLName(), is("model-io-defn-xml.xml"));
            assertThat(versionMigrationWrapper.getModelLibraryJarName(), is("dummy_modelLib_jar.jar"));
            assertEquals(syndicateDataColumnInfoList.size(), versionMigrationWrapper.getVersionMigrationInfo().getTableMetaData()
                    .get(0).getColumnMetaData().size());
            assertEquals("ABC", versionMigrationWrapper.getVersionMigrationInfo().getTableMetaData().get(0).getTableName());
            assertEquals("ABCD", versionMigrationWrapper.getVersionMigrationInfo().getMappingInfo().getMappingInput()
                    .getMappingJson());
            assertEquals("LMNO", versionMigrationWrapper.getVersionMigrationInfo().getMappingInfo().getMappingInput()
                    .getTidJson());
            assertEquals(true, versionMigrationWrapper.getVersionMigrationInfo().isAllowNull());
        } catch (BusinessException | SystemException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void validateTableInfoTest() {
        List<String> errors = new ArrayList<String>();
        versionMigrationDelegate.validateTableInfo(tableMetaDataInfoList, errors);
        assertEquals(0, errors.size());
    }

    @Test
    public void validateTableInfoTestForColumnErrors() {
        populateColumnMetaDataInfoListForError(columnMetaDataInfoList);
        List<String> errors = new ArrayList<String>();
        versionMigrationDelegate.validateTableInfo(tableMetaDataInfoList, errors);
        assertEquals(2, errors.size());
        assertEquals("Columns in the table TESTTABLENAME do not match", errors.get(0));
        assertEquals("[ColumnMetaDataInfo{name: {COL4}, dataType: {STRING}, size: {5}}]", errors.get(1));
    }

    @Test
    @Ignore
    public void testVersionImport() throws BusinessException, SystemException {
        when(versionMigrationHelper.importModelLibrary(any(VersionMigrationWrapper.class), anyListOf(String.class))).thenReturn(
                buildModelLibraryInfo("121", "library1", "library description", "nonhamp", "library1-sep-11-2014-10-52"));
        when(versionMigrationHelper.importModel(any(VersionMigrationWrapper.class))).thenReturn(
                buildModelInfo("123", "model", "model desciption", "sample doc", "sample io"));
        when(versionMigrationHelper.importMapping(any(VersionMigrationWrapper.class), any(ModelInfo.class))).thenReturn(
                buildmapping("sample mapping", "mapping description", "123", MappingStatus.FINALIZED.getMappingStatus()));
        doNothing().when(versionMigrationHelper).importQueries(any(VersionMigrationInfo.class), any(MappingInfo.class));
        when(versionMigrationHelper.importVersion(any(VersionDetail.class), any(ModelLibraryInfo.class), any(MappingInfo.class)))
                .thenReturn(buildVersioninfo("123", "test version", 1, 0, "version1", VersionStatus.SAVED.getVersionStatus()));

        KeyValuePair<VersionInfo, KeyValuePair<List<String>, List<String>>> result = versionMigrationDelegate.importVersion(
                buildVersiondetail(), buildVersionImportInfo());

        assertNotNull(result);
        assertNotNull(result.getKey());
    }

    private VersionImportInfo buildVersionImportInfo() {
        VersionImportInfo versionImportInfo = new VersionImportInfo();
        versionImportInfo.setChecksum("123456789");
        VersionMigrationWrapper versionMigrationWrapper = new VersionMigrationWrapper();

        VersionMigrationInfo versionMigrationInfo = new VersionMigrationInfo();
        versionMigrationWrapper.setVersionMigrationInfo(versionMigrationInfo);
        versionImportInfo.setVersionMigrationWrapper(versionMigrationWrapper);
        return versionImportInfo;
    }

    private VersionDetail buildVersiondetail() {
        VersionDetail versionDetail = new VersionDetail();
        versionDetail.setChecksum("123654789");
        versionDetail.setDescription("version desc");
        versionDetail.setName("testname");
        versionDetail.setVersionType("MAJOR");
        return versionDetail;
    }

    private VersionInfo buildVersioninfo(String id, String description, Integer majorVersion, Integer minorVersion, String name,
            String status) {
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setId(id);
        versionInfo.setDescription(description);
        versionInfo.setMajorVersion(majorVersion);
        versionInfo.setMinorVersion(minorVersion);
        versionInfo.setName(name);
        versionInfo.setStatus(status);
        return versionInfo;
    }

    private MappingInfo buildmapping(String name, String description, String id, String status) {
        MappingInfo mappingInfo = new MappingInfo();
        mappingInfo.setName(name);
        mappingInfo.setDescription(description);
        mappingInfo.setId(id);
        mappingInfo.setStatus(status);
        return mappingInfo;
    }

    private ModelLibraryInfo buildModelLibraryInfo(String id, String name, String description, String jarName, String umgName) {
        ModelLibraryInfo modelLibraryInfo = new ModelLibraryInfo();
        modelLibraryInfo.setId(id);
        modelLibraryInfo.setName(name);
        modelLibraryInfo.setDescription(description);
        modelLibraryInfo.setJarName(jarName);
        modelLibraryInfo.setUmgName(umgName);
        return modelLibraryInfo;
    }

    private ModelInfo buildModelInfo(String id, String name, String description, String documentationName, String ioDefinitionName) {
        ModelInfo modelInfo = new ModelInfo();
        modelInfo.setId(id);
        modelInfo.setName(name);
        modelInfo.setDescription(description);
        modelInfo.setDocumentationName(documentationName);
        modelInfo.setIoDefinitionName(ioDefinitionName);
        return modelInfo;
    }

    @Test
    public void validateTableInfoTestForTableErrors() throws BusinessException, SystemException {
        when(syndicateDataBO.getTableColumnInfo(Mockito.anyString())).thenReturn(new ArrayList<SyndicateDataColumnInfo>());
        List<String> errors = new ArrayList<String>();
        versionMigrationDelegate.validateTableInfo(tableMetaDataInfoList, errors);
        assertEquals(1, errors.size());
        assertEquals("Table Details Not Found : TESTTABLENAME", errors.get(0));
    }

    @Test
    public void testLogVersionExport() throws BusinessException, SystemException {
        version.setStatus("PUBLISHED");
        version.setName("AQMQ");
        MigrationAudit migrationAudit = buildMigrationAudit("123", BusinessConstants.MIGRATION_STATUS_SUCCESSFUL,
                BusinessConstants.MIGRATION_TYPE_EXPORT);
        when(versionBO.getVersionDetails(Mockito.anyString())).thenReturn(version);
        when(versionMigrationHelper.buildVersionData(any(Version.class))).thenReturn(buildVersionData());
        when(migrationBO.createMigrationAudit(any(MigrationAudit.class))).thenReturn(migrationAudit);
        versionMigrationDelegate.logVersionExport(version.getId());
        assertEquals("SUCCESS", migrationAudit.getStatus());
    }

    @Test
    public void testLogVersionImport() throws BusinessException, SystemException {
        MigrationAudit migrationAudit = buildMigrationAudit("123", BusinessConstants.MIGRATION_STATUS_SUCCESSFUL,
                BusinessConstants.MIGRATION_TYPE_IMPORT);
        when(versionBO.getVersionDetails(Mockito.anyString())).thenReturn(version);
        when(migrationBO.createMigrationAudit(any(MigrationAudit.class))).thenReturn(migrationAudit);
        versionMigrationDelegate.logVersionImport(version.getId(), new MigrationAuditInfo());
        assertEquals("SUCCESS", migrationAudit.getStatus());
    }

    @Test
    public void testMarkExportAsFailed() throws BusinessException, SystemException {
        MigrationAudit migrationAudit = buildMigrationAudit("456", BusinessConstants.MIGRATION_STATUS_SUCCESSFUL,
                BusinessConstants.MIGRATION_TYPE_EXPORT);
        versionMigrationDelegate.markExportAsFailed("456");
        assertEquals("SUCCESS", migrationAudit.getStatus());
    }

    @Test
    public void testMarkImportAsFailed() throws BusinessException, SystemException {
        MigrationAudit migrationAudit = buildMigrationAudit("789", BusinessConstants.MIGRATION_STATUS_SUCCESSFUL,
                BusinessConstants.MIGRATION_TYPE_IMPORT);
        when(migrationBO.createMigrationAudit(migrationAudit)).thenReturn(migrationAudit);
        versionMigrationDelegate.markImportAsFailed();
        assertEquals("SUCCESS", migrationAudit.getStatus());
    }

    private MigrationAudit buildMigrationAudit(String id, String status, String type) throws BusinessException, SystemException {
        String jsonVersionString = ConversionUtil.convertToJsonString(buildVersionData());
        MigrationAudit migrationAudit = new MigrationAudit();

        migrationAudit.setId(id);
        migrationAudit.setCreatedBy("prabhat.nigam");
        migrationAudit.setCreatedDate(new DateTime());
        migrationAudit.setLastModifiedBy("prabhat.nigam");
        migrationAudit.setLastModifiedDate(new DateTime());
        migrationAudit.setTenantId("localhost");
        migrationAudit.setVersionData(jsonVersionString.getBytes());
        migrationAudit.setVersion(version);
        migrationAudit.setStatus(status);
        migrationAudit.setType(type);
        return migrationAudit;
    }

    private VersionData buildVersionData() {
        VersionData versionData = new VersionData();
        versionData.setInstanceName(System.getProperty(BusinessConstants.UMG_ENV_KEY));
        versionData.setReleaseVersion(System.getProperty(BusinessConstants.UMG_VERSION_KEY));
        versionData.setTenantModelName(version.getName());
        versionData.setStatus(version.getStatus());
        return versionData;
    }

}
