package com.ca.umg.business.migration.delegate;

import static com.ca.framework.core.constants.SystemConstants.SAN_BASE;
import static java.io.File.separatorChar;
import static java.util.Locale.getDefault;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.bo.MappingBO;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.entity.MappingInput;
import com.ca.umg.business.mapping.entity.MappingOutput;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.migration.audit.entity.MigrationAudit;
import com.ca.umg.business.migration.audit.info.MigrationAuditInfo;
import com.ca.umg.business.migration.audit.info.VersionData;
import com.ca.umg.business.migration.bo.MigrationBO;
import com.ca.umg.business.migration.helper.VersionMigrationHelper;
import com.ca.umg.business.migration.info.ColumnMetaDataInfo;
import com.ca.umg.business.migration.info.MappingDetailsInfo;
import com.ca.umg.business.migration.info.MappingInputDetailsInfo;
import com.ca.umg.business.migration.info.MappingOutputDetailsInfo;
import com.ca.umg.business.migration.info.TableMetaDataInfo;
import com.ca.umg.business.migration.info.VersionDetail;
import com.ca.umg.business.migration.info.VersionImportInfo;
import com.ca.umg.business.migration.info.VersionMigrationInfo;
import com.ca.umg.business.migration.info.VersionMigrationWrapper;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.syndicatedata.bo.SyndicateDataBO;
import com.ca.umg.business.syndicatedata.delegate.SyndicateDataQueryDelegate;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryInfo;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.bo.VersionBO;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionInfo;

@SuppressWarnings("PMD")
@Named
public class VersionMigrationDelegateImpl extends AbstractDelegate implements VersionMigrationDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionMigrationDelegateImpl.class);

    private static final String SPACE = " ";

    private static final String TABLE_SEPERATOR = ",";

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", getDefault());

    private static final char FILE_EXT_SEPERATOR = '.';

    private static final char HYPHEN = '_';

    @Inject
    private VersionBO versionBO;

    @Inject
    private MigrationBO migrationBO;

    @Inject
    private SyndicateDataQueryDelegate syndicateDataQueryDelegate;

    @Inject
    private ModelDelegate modelDelegate;

    @Inject
    private MappingBO mappingBO;

    @Inject
    private SyndicateDataBO syndicateDataBO;

    @Inject
    private VersionMigrationHelper versionMigrationHelper;

    @Inject
    private UmgFileProxy umgFileProxy;

    @Inject
    private SystemParameterProvider systemParameterProvider;

    @Override
    public VersionMigrationWrapper exportVersion(String versionId, MigrationAuditInfo migrationAuditInfo)
            throws BusinessException, SystemException {
        Version version = versionBO.getVersionDetails(versionId);
        if (version == null) {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000068, new Object[] {});
        }
        VersionMigrationInfo versionMigrationInfo = exportVersionMigrationInfo(version);
        return buildVersionForExport(version, versionMigrationInfo, migrationAuditInfo);
    }

    public VersionMigrationWrapper buildVersionForExport(Version version, VersionMigrationInfo versionMigrationInfo,
            MigrationAuditInfo migrationAuditInfo) throws SystemException, BusinessException {
        List<ModelArtifact> modelArtifactList = modelDelegate.getModelArtifacts(version.getMapping().getModel().getId());
        List<ModelArtifact> modelLibraryArtifactList = modelDelegate.getModelLibraryArtifacts(version.getModelLibrary().getId());
        if (modelArtifactList == null || modelArtifactList.isEmpty() || modelLibraryArtifactList == null
                || modelLibraryArtifactList.isEmpty()) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000010, new Object[] {
                    "An error occurred while fetching artifacts for version %s.", version.getName() });
        }
        VersionMigrationWrapper versionMigrationWrapper = new VersionMigrationWrapper();
        versionMigrationWrapper.setModelDoc(modelArtifactList.get(0).getDataArray());
        versionMigrationWrapper.setModelDocName(modelArtifactList.get(0).getName());

        versionMigrationWrapper.setModelIODefinition(version.getMapping().getModel().getModelDefinition().getIoDefinition());
        versionMigrationWrapper.setModelDefinitionType(version.getMapping().getModel().getModelDefinition().getType());
        versionMigrationWrapper.setModelXMLName(version.getMapping().getModel().getIoDefinitionName());
        if (version.getMapping().getModel().getIoDefExcelName() != null) {
            versionMigrationWrapper.setModelExcelName(version.getMapping().getModel().getIoDefExcelName());
            versionMigrationWrapper.setModelExcelDefinition(modelDelegate.getModelExcel(version.getMapping().getModel()));
        }
        versionMigrationWrapper.setModelLibraryJar(modelLibraryArtifactList.get(0).getDataArray());
        versionMigrationWrapper.setModelLibraryJarName(version.getModelLibrary().getJarName());
        versionMigrationWrapper.setVersionMigrationInfo(versionMigrationInfo);
        versionMigrationWrapper.setMigrationAuditInfo(migrationAuditInfo);
        return versionMigrationWrapper;
    }

    public VersionMigrationInfo exportVersionMigrationInfo(Version version) throws BusinessException, SystemException {
        Mapping mapping = version.getMapping();
        Model model = mapping.getModel();
        ModelLibrary modelLibrary = version.getModelLibrary();
        List<SyndicateDataQueryInfo> inputMappingQueries = getQueryInfo(mapping.getName());
        return buildMigrationData(mapping, modelLibrary, model, inputMappingQueries);
    }

    public List<SyndicateDataQueryInfo> getQueryInfo(String mappingName) throws BusinessException, SystemException {
        return syndicateDataQueryDelegate.listByMappingNameAndType(mappingName, BusinessConstants.TYPE_INPUT_MAPPING);
    }

    public VersionMigrationInfo buildMigrationData(Mapping mapping, ModelLibrary modelLibrary, Model model,
            List<SyndicateDataQueryInfo> inputMappingQueries) throws SystemException, BusinessException {
        VersionMigrationInfo versionMigrationInfo = new VersionMigrationInfo();
        versionMigrationInfo.setModelName(model.getName());
        versionMigrationInfo.setModelDescription(model.getDescription());
        versionMigrationInfo.setAllowNull(model.isAllowNull());
        versionMigrationInfo.setModelLibraryName(modelLibrary.getName());
        versionMigrationInfo.setModelLibraryDescription(modelLibrary.getDescription());
        versionMigrationInfo.setExecutionLanguage(modelLibrary.getExecutionLanguage());
        versionMigrationInfo.setExecutionType(modelLibrary.getExecutionType());
        versionMigrationInfo.setModelExecEnvName(modelLibrary.getModelExecEnvName());
        versionMigrationInfo.setModelLibraryChecksum(modelLibrary.getChecksum());
        versionMigrationInfo.setModelLibraryChecksumAlgo(modelLibrary.getEncodingType());
        versionMigrationInfo.setQueryInfo(inputMappingQueries);
        versionMigrationInfo.setMappingInfo(buildMappingInfo(mapping));
        versionMigrationInfo.setTableMetaData(buildTableMetaData(inputMappingQueries));
        return versionMigrationInfo;
    }

    public List<TableMetaDataInfo> buildTableMetaData(List<SyndicateDataQueryInfo> inputMappingQueries) throws BusinessException,
            SystemException {
        List<TableMetaDataInfo> tableMetaDataList = new ArrayList<TableMetaDataInfo>();
        Set<String> tableNameList = getTableList(inputMappingQueries);
        for (String tableName : tableNameList) {
            String newTableName = tableName.toUpperCase(getDefault());
            List<SyndicateDataColumnInfo> tableColInfo = syndicateDataBO.getTableColumnInfo(newTableName);
            TableMetaDataInfo tableMetaData = new TableMetaDataInfo();
            tableMetaData.setTableName(tableName);
            List<ColumnMetaDataInfo> columnMetaDataList = buildTableColMetaData(tableColInfo);
            tableMetaData.setColumnMetaData(columnMetaDataList);
            tableMetaDataList.add(tableMetaData);
        }
        return tableMetaDataList;
    }

    public List<ColumnMetaDataInfo> buildTableColMetaData(List<SyndicateDataColumnInfo> tableColInfo) {
        List<ColumnMetaDataInfo> columnMetaDataList = new ArrayList<ColumnMetaDataInfo>();
        for (SyndicateDataColumnInfo syndicateDataColumnInfo : tableColInfo) {
            ColumnMetaDataInfo columnMetaData = new ColumnMetaDataInfo();
            columnMetaData.setName(syndicateDataColumnInfo.getDisplayName());
            columnMetaData.setDataType(syndicateDataColumnInfo.getColumnType());
            columnMetaData.setSize(String.valueOf(syndicateDataColumnInfo.getColumnSize()));
            columnMetaDataList.add(columnMetaData);
        }
        return columnMetaDataList;
    }

    public Set<String> getTableList(List<SyndicateDataQueryInfo> inputMappingQueries) {
        Set<String> tableNameList = new HashSet<String>();
        for (SyndicateDataQueryInfo syndicateDataQueryInfo : inputMappingQueries) {
            String tableNamesList = syndicateDataQueryInfo.getQueryObject().getFromString();
            String[] tableArray = tableNamesList.split(TABLE_SEPERATOR);
            for (String tableNameWithAlias : tableArray) {
                String tableName = tableNameWithAlias.trim().split(SPACE)[0];
                tableNameList.add(tableName);
            }
        }
        return tableNameList;
    }

    public MappingDetailsInfo buildMappingInfo(Mapping mapping) throws SystemException, BusinessException {
        MappingDetailsInfo mappingDetails = new MappingDetailsInfo();
        mappingDetails.setName(mapping.getName());
        mappingDetails.setDescription(mapping.getDescription());
        buildMappingInput(mapping, mappingDetails);
        buildMappingOutput(mapping, mappingDetails);
        return mappingDetails;
    }

    public void buildMappingOutput(Mapping mapping, MappingDetailsInfo mappingDetails) throws SystemException, BusinessException {
        MappingOutput mappingOutput = mappingBO.findOutputByMapping(mapping);
        if (mappingOutput != null) {
            MappingOutputDetailsInfo mappingOutputDetails = new MappingOutputDetailsInfo();
            mappingOutputDetails.setMappingJson(new String(mappingOutput.getMappingData()));
            mappingOutputDetails.setTidJson(new String(mappingOutput.getTenantInterfaceDefn()));
            mappingDetails.setMappingOutput(mappingOutputDetails);
        }
    }

    public void buildMappingInput(Mapping mapping, MappingDetailsInfo mappingDetails) throws SystemException, BusinessException {
        MappingInput mappingInput = mappingBO.findInputByMapping(mapping);
        if (mappingInput != null) {
            MappingInputDetailsInfo mappingInputDetails = new MappingInputDetailsInfo();
            mappingInputDetails.setMappingJson(new String(mappingInput.getMappingData()));
            mappingInputDetails.setTidJson(new String(mappingInput.getTenantInterfaceDefn()));
            if (mappingInput.getTenantInterfaceSysDefn() != null) {
                mappingInputDetails.setSystemParamsJson(new String(mappingInput.getTenantInterfaceSysDefn()));
            }
            mappingDetails.setMappingInput(mappingInputDetails);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ca.umg.business.migration.delegate.VersionMigrationDelegate#importVersion(com.ca.umg.business.migration.info.VersionDetail
     * , com.ca.umg.business.migration.info.VersionImportInfo)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public KeyValuePair<VersionInfo, KeyValuePair<List<String>, List<String>>> importVersion(VersionDetail versionDetail,
            VersionImportInfo versionImportInfo) throws BusinessException, SystemException {
        VersionInfo versionInfo = null;
        KeyValuePair<VersionInfo, KeyValuePair<List<String>, List<String>>> result = new KeyValuePair<VersionInfo, KeyValuePair<List<String>, List<String>>>();
        // perform validation here
        List<String> errors = new ArrayList<String>();
        List<String> messages = new ArrayList<String>();
        validateTableInfo(versionImportInfo.getVersionMigrationWrapper().getVersionMigrationInfo().getTableMetaData(), errors);

        if (CollectionUtils.isEmpty(errors)) {
            VersionMigrationWrapper versionMigrationWrapper = versionImportInfo.getVersionMigrationWrapper();
            MigrationAuditInfo migrationAuditInfo = versionMigrationWrapper.getMigrationAuditInfo();
            if (migrationAuditInfo != null) {
                byte[] byteVersionData = migrationAuditInfo.getVersionData();
                VersionData versionData = ConversionUtil.convertJson(byteVersionData, VersionData.class);
                messages.add(String.format("Source UMG Instance Name : %s", versionData.getInstanceName()));
                messages.add(String.format("Source UMG Release : %s", versionData.getReleaseVersion()));
                messages.add(String.format("Exporter's User ID : %s", migrationAuditInfo.getCreatedBy()));
            }

            ModelLibraryInfo modelLibraryInfo = versionMigrationHelper.importModelLibrary(versionMigrationWrapper, messages);

            // create model
            ModelInfo modelInfo = versionMigrationHelper.importModel(versionMigrationWrapper);
            messages.add(String.format("Model record : %s", modelInfo.getUmgName()));

            // create mapping
            MappingInfo mappingInfo = versionMigrationHelper.importMapping(versionMigrationWrapper, modelInfo);
            messages.add(String.format("Mapping : %s", mappingInfo.getName()));
            // create queries
            versionMigrationHelper.importQueries(versionMigrationWrapper.getVersionMigrationInfo(), mappingInfo);
            // create version
            versionInfo = versionMigrationHelper.importVersion(versionDetail, modelLibraryInfo, mappingInfo);
            messages.add(String.format("Tenant model name : %s", versionInfo.getName()));
            LOGGER.debug("Imported version {} successfully.", versionInfo.getName());

            messages.add(String.format("Version : %s.%s", versionInfo.getMajorVersion(), versionInfo.getMinorVersion()));
        }
        result.setKey(versionInfo);
        result.setValue(new KeyValuePair<List<String>, List<String>>(messages, errors));
        return result;
    }

    public void validateTableInfo(List<TableMetaDataInfo> tableMetaDataInfoList, List<String> errors) {
        for (TableMetaDataInfo tableMetaDataInfo : tableMetaDataInfoList) {
            String newTableName = tableMetaDataInfo.getTableName().toUpperCase(getDefault());
            List<SyndicateDataColumnInfo> tableColInfo = null;
            try {
                tableColInfo = syndicateDataBO.getTableColumnInfo(newTableName);
                if (tableColInfo.isEmpty()) {
                    tableNotFoundError(errors, newTableName);
                }
            } catch (BusinessException | SystemException e) {
                tableNotFoundError(errors, newTableName);
            }
            if (tableColInfo != null && !tableColInfo.isEmpty()) {
                List<ColumnMetaDataInfo> existingColumnMetaDataList = buildTableColMetaData(tableColInfo);
                List<ColumnMetaDataInfo> importedColumnMetaDataList = tableMetaDataInfo.getColumnMetaData();
                // UMG-4459 start
                updateColumn(importedColumnMetaDataList);
                // UMG-4459 end
                Collections.sort(existingColumnMetaDataList, ColumnMetaDataInfo.ALPHABETICAL_ORDER);
                Collections.sort(importedColumnMetaDataList, ColumnMetaDataInfo.ALPHABETICAL_ORDER);
                if (!existingColumnMetaDataList.equals(importedColumnMetaDataList)) {
                    List<ColumnMetaDataInfo> differenceList = new ArrayList<ColumnMetaDataInfo>();
                    differenceList.addAll(nonOverLap(existingColumnMetaDataList, importedColumnMetaDataList));
                    tableColumnsMismatchError(errors, newTableName, differenceList);
                }
            }
        }
    }

    // umg-4459 start
    private void updateColumn(List<ColumnMetaDataInfo> columnMetadata) {
        for (ColumnMetaDataInfo columnMetaDataInfo : columnMetadata) {
            String name = columnMetaDataInfo.getName();
            if (StringUtils.isNotBlank(name) && Character.isDigit(name.charAt(0))) {
                columnMetaDataInfo.setName(StringUtils.join(BusinessConstants.SYND_CLMN_NAME_ESC_CHAR, name));
            }
        }
    }
    // umg-4459 end

    private Collection<ColumnMetaDataInfo> union(Collection<ColumnMetaDataInfo> coll1, Collection<ColumnMetaDataInfo> coll2) {
        Set<ColumnMetaDataInfo> union = new HashSet<ColumnMetaDataInfo>(coll1);
        union.addAll(new HashSet<ColumnMetaDataInfo>(coll2));
        return union;
    }

    private Collection<ColumnMetaDataInfo> intersect(Collection<ColumnMetaDataInfo> coll1, Collection<ColumnMetaDataInfo> coll2) {
        Set<ColumnMetaDataInfo> intersection = new HashSet<ColumnMetaDataInfo>(coll1);
        intersection.retainAll(new HashSet<ColumnMetaDataInfo>(coll2));
        return intersection;
    }

    private Collection<ColumnMetaDataInfo> nonOverLap(Collection<ColumnMetaDataInfo> coll1, Collection<ColumnMetaDataInfo> coll2) {
        Collection<ColumnMetaDataInfo> result = union(coll1, coll2);
        result.removeAll(intersect(coll1, coll2));
        return result;
    }

    private void tableColumnsMismatchError(List<String> errors, String newTableName,
            List<ColumnMetaDataInfo> existingColumnMetaDataList) {
        errors.add("Columns in the table " + newTableName + " do not match");
        errors.add(existingColumnMetaDataList.toString());
    }

    private void tableNotFoundError(List<String> errors, String newTableName) {
        errors.add("Table Details Not Found : " + newTableName);
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getModelManageExportVersion())")
    public MigrationAuditInfo logVersionExport(String versionId) throws BusinessException, SystemException {
        Version version = versionBO.getVersionDetails(versionId);
        if (version == null) {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000068, new Object[] {});
        }
        String jsonVersionData = ConversionUtil.convertToJsonString(versionMigrationHelper.buildVersionData(version));
        MigrationAudit migrationAudit = new MigrationAudit();
        migrationAudit.setVersion(version);
        migrationAudit.setStatus(BusinessConstants.MIGRATION_STATUS_SUCCESSFUL);
        migrationAudit.setType(BusinessConstants.MIGRATION_TYPE_EXPORT);
        migrationAudit.setVersionData(jsonVersionData.getBytes());
        migrationAudit = migrationBO.createMigrationAudit(migrationAudit);
        return convert(migrationAudit, MigrationAuditInfo.class);
    }

    @Override
    public MigrationAuditInfo logVersionImport(String versionId, MigrationAuditInfo mai) throws BusinessException,
            SystemException {
        Version version = versionBO.getVersionDetails(versionId);
        if (version == null) {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000068, new Object[] {});
        }
        MigrationAudit migrationAudit = new MigrationAudit();
        migrationAudit.setVersion(version);
        migrationAudit.setStatus(BusinessConstants.MIGRATION_STATUS_SUCCESSFUL);
        migrationAudit.setType(BusinessConstants.MIGRATION_TYPE_IMPORT);
        migrationAudit.setVersionData(mai == null ? null : mai.getVersionData());
        migrationAudit = migrationBO.createMigrationAudit(migrationAudit);
        return convert(migrationAudit, MigrationAuditInfo.class);
    }

    @Override
    public void markExportAsFailed(String migrationId) {
        migrationBO.markExportAsFailed(migrationId);
    }

    @Override
    public void markImportAsFailed() throws BusinessException, SystemException {
        MigrationAudit migrationAudit = new MigrationAudit();
        migrationAudit.setStatus(BusinessConstants.MIGRATION_STATUS_FAILED);
        migrationAudit.setType(BusinessConstants.MIGRATION_TYPE_IMPORT);
        migrationAudit = migrationBO.createMigrationAudit(migrationAudit);
    }

    @Override
    public MigrationAuditInfo logVersionImport(MigrationAuditInfo mai, final String improtFileName) throws BusinessException,
            SystemException {
        MigrationAudit migrationAudit = new MigrationAudit();
        migrationAudit.setStatus(BusinessConstants.MIGRATION_PA_STATUS_SUCCESSFUL);
        migrationAudit.setType(BusinessConstants.MIGRATION_TYPE_PARTIAL_IMPORT);
        migrationAudit.setVersionData(mai == null ? null : mai.getVersionData());
        migrationAudit.setImportFileName(improtFileName);
        migrationAudit = migrationBO.createMigrationAudit(migrationAudit);
        return convert(migrationAudit, MigrationAuditInfo.class);
    }

    @Override
    public String getImportFilePath(final String importFileName) throws SystemException {
        final StringBuffer filePathBfr = new StringBuffer(umgFileProxy.getSanPath(systemParameterProvider.getParameter(SAN_BASE)));
        filePathBfr.append(separatorChar).append(RequestContext.getRequestContext().getTenantCode());
        filePathBfr.append(separatorChar).append("import");
        filePathBfr.append(separatorChar).append(importFileName);
        return filePathBfr.toString();
    }

    @Override
    public String getImportFileName(final MultipartFile importFile) {
        final StringBuffer filePathBfr = new StringBuffer();
        filePathBfr.append(getFileNameWithExtention(importFile.getOriginalFilename()));
        filePathBfr.append(HYPHEN);
        filePathBfr.append(AdminUtil.getDateFormatMillis(System.currentTimeMillis(), "yyyy-MM-dd-HH-mm-ss"));
        // filePathBfr.append(getFileExtention(importFile.getOriginalFilename()));
        return filePathBfr.toString();
    }

    @Override
    public void storeImportFileIntoSan(final String importFilePath, final MultipartFile importFile) throws BusinessException,
            SystemException {
        writeFileToDirectory(new File(importFilePath), importFile);
    }

    @Override
    public void deleteImportFilefromSan(final String filename) {
        final File importFile = new File(filename);
        importFile.deleteOnExit();
    }

    private void writeFileToDirectory(final File file, final MultipartFile importFile) throws BusinessException {
        OutputStream outputStream = null;
        // create directory for model if it does not exist
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            LOGGER.info("Saving import file {} to directory {}.", importFile.getOriginalFilename(), file.getPath());
            outputStream = new FileOutputStream(new File(file, importFile.getOriginalFilename()));
            outputStream.write(importFile.getBytes());
            LOGGER.info("Saved import file {} successfully to directory {}.", importFile.getOriginalFilename(), file.getPath());
        } catch (IOException e) {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000088, new String[] { "Corrupted Zip Imported" });
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Exception occured while closing Output Stream", e);
                }
            }
        }
    }

    private String getFileExtention(final String fileName) {
        final int index = fileName.lastIndexOf(FILE_EXT_SEPERATOR);
        return fileName.substring(index);
    }

    private String getFileNameWithExtention(final String fileName) {
        final int index = fileName.lastIndexOf(FILE_EXT_SEPERATOR);
        return fileName.substring(0, index);
    }

    @Override
    public void markImportAsFailed(final String migrationId) throws BusinessException, SystemException {
        migrationBO.markExportAsFailed(migrationId);
    }
}