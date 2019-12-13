/**
 * 
 */
package com.ca.umg.business.migration.helper;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.CheckSumUtil;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.delegate.MappingDelegate;
import com.ca.umg.business.mapping.info.MappingDescriptor;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mapping.info.MappingStatus;
import com.ca.umg.business.mid.extraction.info.MappingViewInfo;
import com.ca.umg.business.mid.extraction.info.MappingViews;
import com.ca.umg.business.mid.extraction.info.PartialMapping;
import com.ca.umg.business.mid.extraction.info.TidIOInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.migration.audit.info.VersionData;
import com.ca.umg.business.migration.info.MappingDetailsInfo;
import com.ca.umg.business.migration.info.MappingInputDetailsInfo;
import com.ca.umg.business.migration.info.MappingOutputDetailsInfo;
import com.ca.umg.business.migration.info.VersionDetail;
import com.ca.umg.business.migration.info.VersionMigrationInfo;
import com.ca.umg.business.migration.info.VersionMigrationWrapper;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelDefinitionInfo;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.syndicatedata.delegate.SyndicateDataQueryDelegate;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryInfo;
import com.ca.umg.business.validation.ValidationError;
import com.ca.umg.business.version.delegate.VersionDelegate;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionInfo;

/**
 * @author kamathan
 * 
 */
@Named
public class VersionMigrationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionMigrationHelper.class);

    @Inject
    private ModelDelegate modelDelegate;

    @Inject
    private MappingDelegate mappingDelegate;

    @Inject
    private SyndicateDataQueryDelegate syndicateDataQueryDelegate;

    @Inject
    private VersionDelegate versionDelegate;
    
    

    public ModelLibraryInfo importModelLibrary(VersionMigrationWrapper versionMigrationWrapper, List<String> messages)
            throws BusinessException, SystemException {
        ModelLibraryInfo modelLibraryInfo = null;
        VersionMigrationInfo versionMigrationInfo = versionMigrationWrapper.getVersionMigrationInfo();
        String libraryChecksum = CheckSumUtil.getCheckSumValue(versionMigrationWrapper.getModelLibraryJar(),
                versionMigrationInfo.getModelLibraryChecksumAlgo());
        if (StringUtils.equals(libraryChecksum, versionMigrationInfo.getModelLibraryChecksum())) {
            List<ModelLibraryInfo> modelLibraryInfos = modelDelegate.searchModelLibraryByJarAndChecksum(
                    versionMigrationWrapper.getModelLibraryJarName(), versionMigrationInfo.getModelLibraryChecksum());
            LOGGER.debug("Found {} model library with jar name {} and checksum {}.", modelLibraryInfos == null ? 0
                    : modelLibraryInfos.size(), versionMigrationWrapper.getModelLibraryJarName(), versionMigrationInfo
                    .getModelLibraryChecksum());
            if (CollectionUtils.isNotEmpty(modelLibraryInfos)) {
                modelLibraryInfo = modelLibraryInfos.get(0);
                if (modelLibraryInfo.getExecEnv() == null) {
                	modelLibraryInfo.setExecEnv(SystemConstants.LINUX_OS);
                }
                messages.add(String.format("Model library %s is not imported as this file already exists in the system.",
                        modelLibraryInfo.getJarName()));
                messages.add(String.format("Model library record :: %s ", modelLibraryInfo.getUmgName()));
            } else {
                // create model library
                modelLibraryInfo = modelDelegate.createModelLibrary(buildmodelLibraryInfo(versionMigrationWrapper));
                messages.add(String.format("Model library record :: %s ", modelLibraryInfo.getUmgName()));
            }
        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000083, new Object[] {});
        }
        return modelLibraryInfo;
    }

    public ModelInfo importModel(VersionMigrationWrapper versionMigrationWrapper) throws BusinessException, SystemException {
        return modelDelegate.createModel(buildModelinfo(versionMigrationWrapper));
    }

    public MappingInfo importMapping(VersionMigrationWrapper versionMigrationWrapper, ModelInfo modelInfo)
            throws SystemException, BusinessException {
    	MappingDescriptor mappingDescriptor = buildMappingDescriptor(versionMigrationWrapper);
        String mappingName = null;
        MappingInfo mappingInfo = null;
        KeyValuePair<String, List<ValidationError>> nameErrors = null;
        if (mappingDescriptor != null) {
            nameErrors = mappingDelegate.saveMappingDescription(mappingDescriptor, modelInfo.getUmgName(), null);
            mappingName = nameErrors == null ? null : nameErrors.getKey();
        }

        if (mappingDescriptor != null && StringUtils.isNotBlank(mappingName)) {
        	mappingDescriptor.setTidName(mappingName);
        }

        if (StringUtils.isNotBlank(mappingName)) {
            mappingInfo = mappingDelegate.findByName(mappingName);
            // update mapping status to finalized
            mappingInfo = mappingDelegate.updateMappingStatus(mappingInfo.getId(), MappingStatus.FINALIZED.getMappingStatus());
        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000084, new Object[] {});
        }
        return mappingInfo;
    }

    public void importQueries(VersionMigrationInfo versionMigrationInfo, MappingInfo mappingInfo) throws BusinessException,
            SystemException {
        List<SyndicateDataQueryInfo> syndicateDataQueryInfos = versionMigrationInfo.getQueryInfo();
        if (CollectionUtils.isNotEmpty(syndicateDataQueryInfos)) {
            for (SyndicateDataQueryInfo syndicateDataQueryInfo : syndicateDataQueryInfos) {
                syndicateDataQueryInfo.setId(null);
                syndicateDataQueryInfo.setMapping(mappingInfo);
                syndicateDataQueryInfo = syndicateDataQueryDelegate.createSyndicateDataQuery(syndicateDataQueryInfo);
                LOGGER.debug("Created query {} successfully.", syndicateDataQueryInfo.getName());
            }
        }
    }

    public VersionInfo importVersion(VersionDetail versionDetail, ModelLibraryInfo modelLibrary, MappingInfo mapping)
            throws BusinessException, SystemException {
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setName(versionDetail.getName());
        versionInfo.setDescription(versionDetail.getDescription());
        setMajorVersion(versionDetail, versionInfo);
        versionInfo.setMapping(mapping);
        versionInfo.setModelLibrary(modelLibrary);
        versionInfo.setVersionDescription(versionDetail.getVersionDescription());
        versionInfo.setModelType(versionDetail.getModelType());
        return versionDelegate.create(versionInfo);
    }

    public VersionData buildVersionData(Version version) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        VersionData versionData = new VersionData();
        versionData.setInstanceName(System.getProperty(BusinessConstants.UMG_ENV_KEY));
        versionData.setReleaseVersion(System.getProperty(BusinessConstants.UMG_VERSION_KEY));
        versionData.setExportedBy(auth == null ? "SYSTEM" : auth.getName());
        versionData.setExportedOn(new DateTime().toString());
        versionData.setTenantModelName(version.getName());
        versionData.setStatus(version.getStatus());
        return versionData;
    }

    private ModelLibraryInfo buildmodelLibraryInfo(VersionMigrationWrapper versionMigrationWrapper) {
        ModelLibraryInfo modelLibraryInfo = new ModelLibraryInfo();
        VersionMigrationInfo versionMigrationInfo = versionMigrationWrapper.getVersionMigrationInfo();
        modelLibraryInfo.setName(versionMigrationInfo.getModelLibraryName());
        modelLibraryInfo.setChecksum(versionMigrationInfo.getModelLibraryChecksum());
        modelLibraryInfo.setDescription(versionMigrationInfo.getModelLibraryDescription());
        modelLibraryInfo.setExecutionLanguage(versionMigrationInfo.getExecutionLanguage());
        modelLibraryInfo.setExecutionType(versionMigrationInfo.getExecutionType());
        if (versionMigrationInfo.getModelExecEnvName() == null) {
            if (StringUtils.equalsIgnoreCase(BusinessConstants.MATLAB_LANG, versionMigrationInfo.getExecutionLanguage())) {
                modelLibraryInfo.setModelExecEnvName(BusinessConstants.DEFAULT_MATLAB_ENV_NAME);
            } else if (StringUtils.equalsIgnoreCase(BusinessConstants.R_LANG, versionMigrationInfo.getExecutionLanguage())) {
                modelLibraryInfo.setModelExecEnvName(BusinessConstants.DEFAULT_R_ENV_NAME);
            }
        } else {
            modelLibraryInfo.setModelExecEnvName(versionMigrationInfo.getModelExecEnvName());
        }
        modelLibraryInfo.setJarName(versionMigrationWrapper.getModelLibraryJarName());
        modelLibraryInfo.setEncodingType(versionMigrationInfo.getModelLibraryChecksumAlgo());
        if (modelLibraryInfo.getExecEnv() == null) {
        	modelLibraryInfo.setExecEnv(SystemConstants.LINUX_OS);
        }
        ModelArtifact jar = new ModelArtifact();
        jar.setData(new ByteArrayInputStream(versionMigrationWrapper.getModelLibraryJar()));
        jar.setName(versionMigrationWrapper.getModelLibraryJarName());
        modelLibraryInfo.setJar(jar);
        return modelLibraryInfo;
    }

    private ModelInfo buildModelinfo(VersionMigrationWrapper versionMigrationWrapper) {
        ModelInfo modelInfo = new ModelInfo();
        VersionMigrationInfo versionMigrationInfo = versionMigrationWrapper.getVersionMigrationInfo();
        modelInfo.setName(versionMigrationInfo.getModelName());
        modelInfo.setDescription(versionMigrationInfo.getModelDescription());
        modelInfo.setDocumentationName(versionMigrationWrapper.getModelDocName());
        modelInfo.setIoDefinitionName(versionMigrationWrapper.getModelXMLName());
        modelInfo.setAllowNull(versionMigrationInfo.isAllowNull());
        modelInfo.setIoDefExcelName(versionMigrationWrapper.getModelExcelName());
        
        ModelDefinitionInfo modelDefinitionInfo = new ModelDefinitionInfo();
        modelDefinitionInfo.setType(versionMigrationWrapper.getModelDefinitionType());
        modelInfo.setModelDefinition(modelDefinitionInfo);
        ModelArtifact documentation = new ModelArtifact();
        documentation.setName(versionMigrationWrapper.getModelDocName());
        documentation.setData(new ByteArrayInputStream(versionMigrationWrapper.getModelDoc()));
        modelInfo.setDocumentation(documentation);
        ModelArtifact excelDefinition = null;
        if(versionMigrationWrapper.getModelExcelName() != null && versionMigrationWrapper.getModelExcelDefinition() != null){
	        excelDefinition = new ModelArtifact();
	        excelDefinition.setName(versionMigrationWrapper.getModelExcelName());
	        excelDefinition.setData(new ByteArrayInputStream(versionMigrationWrapper.getModelExcelDefinition()));
	        excelDefinition.setDataArray(versionMigrationWrapper.getModelExcelDefinition());
        }
        modelInfo.setExcel(excelDefinition);
        ModelArtifact xml = new ModelArtifact();
        xml.setData(new ByteArrayInputStream(versionMigrationWrapper.getModelIODefinition()));
        xml.setName(versionMigrationWrapper.getModelXMLName());
        modelInfo.setXml(xml);
        return modelInfo;
    }

    @SuppressWarnings("unchecked")
    private MappingDescriptor buildMappingDescriptor(VersionMigrationWrapper versionMigrationWrapper)
            throws SystemException {
        VersionMigrationInfo versionMigrationInfo = versionMigrationWrapper.getVersionMigrationInfo();
        MappingDetailsInfo mappingDetailsInfo = versionMigrationInfo.getMappingInfo();
        MappingDescriptor mappingDescriptor = new MappingDescriptor();

        TidIOInfo tidIOInfo = new TidIOInfo();
        String mappingJson = null;
        String tidjson = null;
        String paramJson = null;
        PartialMapping<TidParamInfo> partialTidParams = null;
        PartialMapping<TidParamInfo> partialSysTidParams = null;
        PartialMapping<MappingViewInfo> partialMidMappings = null;
        MappingViews mappingViews = new MappingViews();

        MappingInputDetailsInfo mappingInputDetailsInfo = mappingDetailsInfo.getMappingInput();
        MappingOutputDetailsInfo mappingOutputDetailsInfo = mappingDetailsInfo.getMappingOutput();

        if (mappingInputDetailsInfo != null) {
            mappingDescriptor.setDescription(mappingDetailsInfo.getDescription());

            mappingJson = mappingInputDetailsInfo.getMappingJson();
            tidjson = mappingInputDetailsInfo.getTidJson();
            paramJson = mappingInputDetailsInfo.getSystemParamsJson();
            partialMidMappings = ConversionUtil.convertJson(mappingJson.getBytes(), PartialMapping.class, MappingViewInfo.class);
            partialTidParams = ConversionUtil.convertJson(tidjson, PartialMapping.class, TidParamInfo.class);

            if (StringUtils.isNotBlank(paramJson)) {
                partialSysTidParams = ConversionUtil.convertJson(paramJson, PartialMapping.class, TidParamInfo.class);
                //tidIOInfo.setTidSystemInput(partialSysTidParams.getPartials());
                tidIOInfo.setTidSystemInput(setSqlIdForSyndData(partialSysTidParams.getPartials()));
            }

            tidIOInfo.setTidInput(partialTidParams.getPartials());
            mappingViews.setInputMappingViews(partialMidMappings.getPartials());
            mappingDescriptor.setTidMidMapping(mappingViews);
            mappingDescriptor.setTidTree(tidIOInfo);
        } 
        if (mappingOutputDetailsInfo != null) {
            mappingJson = mappingOutputDetailsInfo.getMappingJson();
            tidjson = mappingOutputDetailsInfo.getTidJson();

            if (StringUtils.isNotBlank(tidjson) && StringUtils.isNotBlank(mappingJson)) {
                partialTidParams = ConversionUtil.convertJson(tidjson, PartialMapping.class, TidParamInfo.class);
                tidIOInfo.setTidOutput(partialTidParams.getPartials());

                partialMidMappings = ConversionUtil.convertJson(mappingJson.getBytes(), PartialMapping.class,
                        MappingViewInfo.class);
                mappingViews.setOutputMappingViews(partialMidMappings.getPartials());

                mappingDescriptor.setTidMidMapping(mappingViews);
                mappingDescriptor.setTidTree(tidIOInfo);
            }
        }
        return mappingDescriptor;
    }

    private List<TidParamInfo> setSqlIdForSyndData (List<TidParamInfo> tidSystemInputPartials) {
    	for (TidParamInfo sysInputInfo : tidSystemInputPartials) {
    		if (sysInputInfo.getChildren() != null) {
    			setSqlIdForAllChildren (sysInputInfo.getChildren(),sysInputInfo.getSqlId());
    		}
    	}
    	return tidSystemInputPartials;
    }
    
    private void setSqlIdForAllChildren (List<TidParamInfo> tidSystemInputChildren, String sqlId) {
    	for (TidParamInfo child : tidSystemInputChildren) {
    		if (child.getChildren() == null) {
    			child.setSqlId(sqlId);
    		} else {
    			setSqlIdForAllChildren (child.getChildren(),sqlId);
    		}
    	}
    }
    
    private void setMajorVersion(VersionDetail versionDetail, VersionInfo version) throws BusinessException {
        if (isCreateMinorVersion(versionDetail)) {
            if (isMajorEmpty(versionDetail)) {
                throw new BusinessException(BusinessExceptionCodes.BSE000073, new Object[] {});
            } else {
                version.setMajorVersion(versionDetail.getMajorVersion());
            }
        }
    }

    private boolean isMajorEmpty(VersionDetail versionDetail) {
        return versionDetail.getMajorVersion() == null || versionDetail.getMajorVersion() == 0;
    }

    private boolean isCreateMinorVersion(VersionDetail versionDetail) {
        return versionDetail.getVersionType().equalsIgnoreCase(BusinessConstants.VERSION_TYPE_MINOR);
    }

}
