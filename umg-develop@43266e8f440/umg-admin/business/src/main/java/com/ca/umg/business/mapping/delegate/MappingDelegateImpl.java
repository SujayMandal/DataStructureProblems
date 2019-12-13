package com.ca.umg.business.mapping.delegate;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.common.info.PagingInfo;
import com.ca.umg.business.common.info.ResponseWrapper;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.bo.MappingBO;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.entity.MappingInput;
import com.ca.umg.business.mapping.entity.MappingOutput;
import com.ca.umg.business.mapping.helper.MappingHelper;
import com.ca.umg.business.mapping.info.MappingDescriptor;
import com.ca.umg.business.mapping.info.MappingHierarchyInfo;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mapping.info.MappingStatus;
import com.ca.umg.business.mapping.info.MappingsCopyInfo;
import com.ca.umg.business.mapping.info.QueryLaunchInfo;
import com.ca.umg.business.mapping.info.TidIoDefinition;
import com.ca.umg.business.mapping.validation.MappingValidator;
import com.ca.umg.business.mid.extraction.MidExtractor;
import com.ca.umg.business.mid.extraction.info.DatatypeInfo.Datatype;
import com.ca.umg.business.mid.extraction.info.MappingViewInfo;
import com.ca.umg.business.mid.extraction.info.MappingViews;
import com.ca.umg.business.mid.extraction.info.MidIOInfo;
import com.ca.umg.business.mid.extraction.info.MidMappings;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.extraction.info.PartialMapping;
import com.ca.umg.business.mid.extraction.info.TenantIODefinition;
import com.ca.umg.business.mid.extraction.info.TidIOInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidSqlInfo;
import com.ca.umg.business.mid.mapping.MidMapper;
import com.ca.umg.business.model.bo.ModelBO;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.syndicatedata.bo.QueryMappingIntegration;
import com.ca.umg.business.syndicatedata.delegate.SyndicateDataQueryDelegate;
import com.ca.umg.business.tid.copy.TidCopy;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.validation.ValidationError;
import com.ca.umg.business.version.bo.VersionBO;

@Component
@SuppressWarnings({ "unchecked", "PMD" })

public class MappingDelegateImpl extends AbstractDelegate implements MappingDelegate {
    private static final String VALIDATE = "validate";

    @Inject
    private MappingBO mappingBO;

    @Inject
    private ModelBO modelBO;

    @Inject
    private MidExtractor midExtractor;

    @Inject
    private MidMapper midMapper;

    @Inject
    private MappingHelper mappingHelper;

    @Inject
    private MappingValidator mappingValidator;

    @Inject
    private SyndicateDataQueryDelegate syndicateDataQueryDelegate;

    @Inject
    private VersionBO versionBO;

    @Inject
    private TidCopy tidCopy;

    @Inject
    private QueryMappingIntegration queryMappingIntegration;

    @Override
    public List<MappingInfo> listAll() throws BusinessException, SystemException {
        List<Mapping> mappingList = mappingBO.listAll();
        return convertToList(mappingList, MappingInfo.class);
    }

    @Override
    public MappingInfo find(String identifier) throws BusinessException, SystemException {
        return convert(mappingBO.find(identifier), MappingInfo.class);
    }

    // @Override
    // public boolean deploy(MappingInfo mappingInfo) throws BusinessException,
    // SystemException {
    // Mapping mapping = convert(mappingInfo, Mapping.class);
    // return mappingBO.deploy(mapping);
    // }

    @Override
    public List<MappingHierarchyInfo> getMappingHierarchyInfos() throws BusinessException, SystemException {
        List<MappingHierarchyInfo> mappingHierarchyInfos = null;
        MappingInfo mappingInfo = null;
        List<MappingInfo> mappingInfos = null;
        MappingHierarchyInfo mappingHierarchyInfo = null;
        Map<String, MappingHierarchyInfo> map = null;
        List<Mapping> mappings = mappingBO.listAll();
        if (CollectionUtils.isNotEmpty(mappings)) {
            map = new HashMap<String, MappingHierarchyInfo>();
            for (Mapping mapping : mappings) {
                mappingInfo = convert(mapping, MappingInfo.class);
                mappingInfo
                        .setCreatedDateTime(AdminUtil.getDateFormatMillisForEst(mappingInfo.getCreatedDate().getMillis(), null));
                mappingInfo.setLastModifiedDateTime(
                        AdminUtil.getDateFormatMillisForEst(mappingInfo.getLastModifiedDate().getMillis(), null));
                final String modelName = mapping.getModel().getName();
                if (map.containsKey(modelName)) {
                    mappingHierarchyInfo = map.get(modelName);
                    mappingInfos = mappingHierarchyInfo.getMappingInfos();
                    mappingInfos.add(mappingInfo);
                } else {
                    mappingHierarchyInfo = new MappingHierarchyInfo();
                    mappingHierarchyInfo.setModelName(modelName);
                    mappingInfos = new ArrayList<>();
                    mappingInfos.add(mappingInfo);
                    mappingHierarchyInfo.setMappingInfos(mappingInfos);
                    map.put(modelName, mappingHierarchyInfo);
                }
            }
            mappingHierarchyInfos = new ArrayList<>(map.values());
            map.clear();
        }
        return mappingHierarchyInfos;
    }

    @Override
    public MappingDescriptor generateMapping(String derievedModelNm) throws BusinessException, SystemException {
        Model model = null;
        MidIOInfo midIOInfo = null;
        TidIOInfo tidIOInfo = null;
        MappingViews mappingViews = null;
        MappingDescriptor mappingDescriptor = null;
        if (StringUtils.isNotBlank(derievedModelNm)) {
            model = modelBO.getModelByName(derievedModelNm);
            if (model != null && model.getModelDefinition().getIoDefinition() != null) {
                midIOInfo = midExtractor.extractModelIO(new ByteArrayInputStream(model.getModelDefinition().getIoDefinition()));
                if (midIOInfo != null) {
                    mappingDescriptor = new MappingDescriptor();
                    tidIOInfo = new TidIOInfo();
                    tidIOInfo.copy(midIOInfo);
                    mappingViews = midMapper.createMappingViews(midIOInfo);
                    mappingDescriptor.setMidTree(midIOInfo);
                    mappingDescriptor.setTidTree(tidIOInfo);
                    mappingDescriptor.setTidMidMapping(mappingViews);
                }
            }
        }
        return mappingDescriptor;
    }

    public KeyValuePair<String, List<ValidationError>> getMappingDescription(MappingDescriptor mappingDescriptor,
            String derievedModelNm, String validate) throws BusinessException, SystemException {
        MidIOInfo midIOInfo = null;
        TidIOInfo tidIOInfo = null;
        TidIOInfo validationTid = null;
        MidMappings midMappings = null;
        MappingViews mappingViews = null;
        List<ValidationError> errors = null;
        KeyValuePair<String, List<ValidationError>> nameErrors = new KeyValuePair<>();
        String tidMappingName = null;
        Mapping mapping = null;
        Map<String, List<String>> mappingToDelete = null;

        if (mappingDescriptor != null && StringUtils.isNotBlank(derievedModelNm)) {
            midMappings = new MidMappings();
            tidIOInfo = mappingDescriptor.getTidTree();
            mappingViews = mappingDescriptor.getTidMidMapping();
            midMappings.prepare(mappingViews);

            tidMappingName = mappingDescriptor.getTidName();
            if (StringUtils.isNotBlank(tidMappingName)) {
                mapping = mappingBO.findByName(tidMappingName);
            }
            if(mapping != null) {
            	midIOInfo = ConversionUtil.convertJson(mapping.getModelIO(), MidIOInfo.class);
            }
            validationTid = validateQuerySysParameters(tidIOInfo, tidMappingName, mapping);
            if (StringUtils.equals(validate, VALIDATE)) {
                mappingToDelete = new HashMap<>();
                errors = mappingValidator.validateMidMapping(midIOInfo, validationTid, midMappings.getMidInputMappings(),
                        mappingToDelete);
            }

            if (CollectionUtils.isEmpty(errors)) {

                mappingHelper.removeTenantExposedMappings(mappingViews, mappingToDelete);

                // for WIP
                if(mapping == null) {
                	mapping = new Mapping();
                }
                if (org.apache.commons.lang.StringUtils.equals(validate, VALIDATE)) {
                    mapping.setStatus(MappingStatus.FINALIZED.getMappingStatus());
                    getMapping(tidIOInfo, mappingViews, mapping);
                    updateUmgVersion(mapping.getName());
                }
                nameErrors.setKey(mapping.getName());
            }
            nameErrors.setValue(errors);
        }
        return nameErrors;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public KeyValuePair<String, List<ValidationError>> saveMappingDescription(MappingDescriptor mappingDescriptor,
            String derievedModelNm, String validate) throws BusinessException, SystemException {
        Model model = null;
        MidIOInfo midIOInfo = null;
        TidIOInfo tidIOInfo = null;
        TidIOInfo validationTid = null;
        MidMappings midMappings = null;
        MappingViews mappingViews = null;
        List<ValidationError> errors = null;
        KeyValuePair<String, List<ValidationError>> nameErrors = new KeyValuePair<>();
        String tidMappingName = null;
        Mapping mapping = null;
        String description = mappingDescriptor.getDescription();
        Map<String, List<String>> mappingToDelete = null;

        if (mappingDescriptor != null && StringUtils.isNotBlank(derievedModelNm)) {
            midMappings = new MidMappings();
            tidIOInfo = mappingDescriptor.getTidTree();
            mappingViews = mappingDescriptor.getTidMidMapping();
            midMappings.prepare(mappingViews);

            tidMappingName = mappingDescriptor.getTidName();
            if (StringUtils.isNotBlank(tidMappingName)) {
                mapping = mappingBO.findByName(tidMappingName);
            }
            if (mapping == null) {
                mapping = new Mapping();
                model = modelBO.getModelByName(derievedModelNm);
                midIOInfo = midExtractor.extractModelIO(new ByteArrayInputStream(model.getModelDefinition().getIoDefinition()));

                mapping.setModel(model);
                mapping.setModelIO(ConversionUtil.convertToJsonString(midIOInfo).getBytes());
                mapping.setName(AdminUtil.generateUmgName(model.getName() + "-TID"));
            } else {
                midIOInfo = ConversionUtil.convertJson(mapping.getModelIO(), MidIOInfo.class);
            }

            validationTid = validateQuerySysParameters(tidIOInfo, tidMappingName, mapping);
            if (StringUtils.equals(validate, VALIDATE)) {
                mappingToDelete = new HashMap<>();
                errors = mappingValidator.validateMidMapping(midIOInfo, validationTid, midMappings.getMidInputMappings(),
                        mappingToDelete);
                if (CollectionUtils.isEmpty(errors) && StringUtils.isNotBlank(mappingDescriptor.getCopiedTidName())) {
                    copySystemParamQueries(mappingBO.findByName(mappingDescriptor.getCopiedTidName()).getId(), mapping.getId(),
                            tidIOInfo.getTidSystemInput());
                }
            }
            mapping.setDescription(description);
            if (CollectionUtils.isEmpty(errors)) {
                mappingHelper.removeTenantExposedMappings(mappingViews, mappingToDelete);
                // for WIP
                setMappingStatus(validate, mapping);
                getMapping(tidIOInfo, mappingViews, mapping);
                updateUmgVersion(mapping.getName());
                nameErrors.setKey(mapping.getName());
            }
            nameErrors.setValue(errors);
        }
        return nameErrors;
    }

    // added to avoid cyclomatic complexity
    private void getMapping(TidIOInfo tidIOInfo, MappingViews mappingViews, Mapping mapping)
            throws SystemException, BusinessException {
        try {
            saveMapping(tidIOInfo, mappingViews, mapping);
        } catch (DataAccessException e) {
            SystemException.newSystemException(
                    FrameworkExceptionCodes.BSE000009,
                    new Object[] {
                    String.format("Error occured while saving the mapping. Duplicate entry '%s'", mapping.getName()) });
        }
    }

    private TidIOInfo validateQuerySysParameters(TidIOInfo tidIOInfo, String tidMappingName, Mapping mapping)
            throws BusinessException, SystemException {
        List<TidSqlInfo> sqlInfos = null;
        TidIOInfo validationTid = null;
        if (StringUtils.isNotEmpty(mapping.getId())) {
            // Integration with SQL
            sqlInfos = syndicateDataQueryDelegate.getInterfaceDefinitionSqlInfos(tidMappingName,
                    BusinessConstants.TYPE_INPUT_MAPPING);
        }
        validationTid = mappingHelper.getSQLOutputsSave(sqlInfos, tidIOInfo);
        validationTid.setSqlInfos(sqlInfos);
        return validationTid;
    }

    protected Mapping saveMapping(TidIOInfo tidIOInfo, MappingViews mappingViews, Mapping mapping)
            throws SystemException, BusinessException {
        MappingInput existingMappingInput;
        MappingOutput existingMappingOutput;
        MappingInput mappingInput;
        MappingOutput mappingOutput;
        mappingHelper.clearModifiedByAndDate(mapping);
        Mapping savedMapping = mappingBO.create(mapping);

        mappingInput = mappingHelper.prepareInputMapping(savedMapping, tidIOInfo, mappingViews);
        existingMappingInput = mappingBO.findInputByMapping(savedMapping);
        mappingInput.setId(mappingHelper.getExistingId(existingMappingInput));
        if (existingMappingInput != null) {
            mappingInput.setTenantId(existingMappingInput.getTenantId());
        }
        mappingBO.createMappingInput(mappingInput);
        mappingOutput = mappingHelper.prepareOutputMapping(savedMapping, tidIOInfo, mappingViews);
        existingMappingOutput = mappingBO.findOutputByMapping(savedMapping);
        mappingOutput.setId(mappingHelper.getExistingId(existingMappingOutput));
        if (existingMappingOutput != null) {
            mappingOutput.setTenantId(existingMappingOutput.getTenantId());
        }
        mappingBO.createMappingOutput(mappingOutput);
        return savedMapping;
    }

    protected void updateUmgVersion(String tidName) throws BusinessException, SystemException {
        List<String> verIdList = versionBO.getTestedVersions(tidName);
        if (verIdList != null && !verIdList.isEmpty()) {
            for (String versionId : verIdList) {
                versionBO.markVersionAsSaved(versionId);
            }
        }
    }

    protected void setMappingStatus(String validate, Mapping mapping) throws BusinessException, SystemException {
        if (org.apache.commons.lang.StringUtils.equals(validate, "validate")) {
            mapping.setStatus(MappingStatus.FINALIZED.getMappingStatus());
        } else {
            mapping.setStatus(MappingStatus.SAVED.getMappingStatus());
        }
    }

    @Override
    public MappingDescriptor readMapping(String derievedTidName) throws BusinessException, SystemException {
        MappingDescriptor mappingDescriptor = null;
        Mapping mapping = null;
        MappingInput mappingInput = null;
        MappingOutput mappingOutput = null;
        MidIOInfo midIOInfo = null;
        TidIOInfo tidIOInfo = null;
        MappingViews mappingViews = null;
        PartialMapping<TidParamInfo> partialTidParams = null;
        PartialMapping<TidParamInfo> partialSysTidParams = null;
        PartialMapping<MappingViewInfo> partialMidMappings = null;

        if (StringUtils.isNotBlank(derievedTidName)) {
            mapping = mappingBO.findByName(derievedTidName);
            if (mapping != null && mapping.getModelIO() != null) {
                tidIOInfo = new TidIOInfo();
                mappingViews = new MappingViews();
                midIOInfo = ConversionUtil.convertJson(mapping.getModelIO(), MidIOInfo.class);
                mappingInput = mappingBO.findInputByMapping(mapping);
                mappingOutput = mappingBO.findOutputByMapping(mapping);

                if (mappingInput != null) {
                    partialMidMappings = ConversionUtil.convertJson(mappingInput.getMappingData(), PartialMapping.class,
                            MappingViewInfo.class);
                    partialTidParams = ConversionUtil.convertJson(mappingInput.getTenantInterfaceDefn(), PartialMapping.class,
                            TidParamInfo.class);
                    List<TidSqlInfo> sqlInfos = syndicateDataQueryDelegate.getInterfaceDefinitionSqlInfos(derievedTidName,
                            BusinessConstants.TYPE_INPUT_MAPPING);
                    if (ArrayUtils.isNotEmpty(mappingInput.getTenantInterfaceSysDefn())) {
                        partialSysTidParams = ConversionUtil.convertJson(mappingInput.getTenantInterfaceSysDefn(),
                                PartialMapping.class, TidParamInfo.class);
                    }
                    tidIOInfo.setTidInput(partialTidParams.getPartials());
                    tidIOInfo.setTidSystemInput(mergeSqlPartials(partialSysTidParams, sqlInfos));
                    mappingViews.setInputMappingViews(partialMidMappings.getPartials());
                } else {
                    tidIOInfo.setTidInput(
                            mappingHelper.getTidInfoInitial(midIOInfo, BusinessConstants.TYPE_INPUT_MAPPING).getPartials());
                    mappingViews.setInputMappingViews(
                            midMapper.createMappingViews(midIOInfo, BusinessConstants.TYPE_INPUT_MAPPING).getInputMappingViews());
                }

                if (mappingOutput != null) {
                    partialMidMappings = ConversionUtil.convertJson(mappingOutput.getMappingData(), PartialMapping.class,
                            MappingViewInfo.class);
                    partialTidParams = ConversionUtil.convertJson(mappingOutput.getTenantInterfaceDefn(), PartialMapping.class,
                            TidParamInfo.class);

                    tidIOInfo.setTidOutput(partialTidParams.getPartials());
                    mappingViews.setOutputMappingViews(partialMidMappings.getPartials());
                } else {
                    tidIOInfo.setTidOutput(
                            mappingHelper.getTidInfoInitial(midIOInfo, BusinessConstants.TYPE_OUTPUT_MAPPING).getPartials());
                    mappingViews.setOutputMappingViews(midMapper
                            .createMappingViews(midIOInfo, BusinessConstants.TYPE_OUTPUT_MAPPING).getOutputMappingViews());
                }
                mappingDescriptor = new MappingDescriptor();
                mappingDescriptor.setMidTree(midIOInfo);
                mappingDescriptor.setTidTree(tidIOInfo);
                mappingDescriptor.setTidMidMapping(mappingViews);
                mappingDescriptor.setTidName(derievedTidName);
                mappingDescriptor.setDescription(mapping.getDescription());
                mappingDescriptor.setMidName(mapping.getModel().getUmgName());
                mappingDescriptor.setModelName(mapping.getModel().getName());
            } else {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000048, new Object[] { derievedTidName });
            }
        }
        return mappingDescriptor;
    }

    private List<TidParamInfo> mergeSqlPartials(PartialMapping<TidParamInfo> partialMapping, List<TidSqlInfo> sqlInfos)
            throws BusinessException, SystemException {
        List<TidParamInfo> sqlParameters = null;
        TidParamInfo mergedParam = null;
        TidParamInfo childParam = null;
        List<TidSqlInfo> sqlInfosCopy = null;
        if (CollectionUtils.isNotEmpty(sqlInfos)) {
            sqlInfosCopy = new ArrayList<>(sqlInfos);
            sqlParameters = new ArrayList<>();
            if (partialMapping != null) {
                List<TidParamInfo> partials = partialMapping.getPartials();
                Iterator<TidSqlInfo> sqlIterator = sqlInfosCopy.iterator();
                Iterator<TidParamInfo> paramIterator = null;
                TidSqlInfo sqlInfo = null;
                TidParamInfo paramInfo = null;
                while (sqlIterator.hasNext()) {
                    sqlInfo = sqlIterator.next();
                    paramIterator = partials.iterator();
                    while (paramIterator.hasNext()) {
                        paramInfo = paramIterator.next();
                        if (sqlInfo.getSqlId().equals(paramInfo.getSqlId())) {
                            mergeSQLTidPartials(paramInfo, sqlInfo);
                            sqlParameters.add(paramInfo);
                            sqlIterator.remove();
                            paramIterator.remove();
                            break;
                        }
                    }
                }
            }
            for (TidSqlInfo tidSqlInfo : sqlInfosCopy) {
                mergedParam = new TidParamInfo();
                mergedParam.setSqlId(tidSqlInfo.getSqlId());
                mergedParam.setName(tidSqlInfo.getSqlName());
                mergedParam.setDatatype(tidSqlInfo.getDatatypeInfo());
                mergedParam.setFlatenedName(tidSqlInfo.getSqlName());
                List<TidParamInfo> children = new ArrayList<>();
                mergedParam.setChildren(children);
                for (TidParamInfo child : tidSqlInfo.getOutputParams()) {
                    childParam = new TidParamInfo();
                    childParam.setName(child.getName());
                    childParam.setApiName(child.getName() != null ? child.getName() : child.getApiName());
                    childParam.setDatatype(child.getDatatype());
                    childParam.setFlatenedName(child.getFlatenedName());
                    children.add(childParam);
                }
                sqlParameters.add(mergedParam);
            }
        }
        return sqlParameters;
    }

    private void mergeSQLTidPartials(TidParamInfo tidParamInfo, TidSqlInfo sqlInfo) {
        Iterator<TidParamInfo> tidParamInfos = tidParamInfo.getChildren().iterator();
        Iterator<TidParamInfo> queryOutputs = null;
        TidParamInfo tidParamInfoChild = null;
        TidParamInfo queryOutput = null;
        boolean paramExists = false;
        List<TidParamInfo> queryOutputsList = new ArrayList<TidParamInfo>(sqlInfo.getOutputParams());
        tidParamInfo.setDatatype(sqlInfo.getDatatypeInfo());
        while (tidParamInfos.hasNext()) {
            tidParamInfoChild = tidParamInfos.next();
            paramExists = false;
            queryOutputs = queryOutputsList.iterator();
            while (queryOutputs.hasNext()) {
                queryOutput = queryOutputs.next();
                if (StringUtils.equals(tidParamInfoChild.getApiName(), queryOutput.getApiName())) {
                    tidParamInfoChild.setDatatype(queryOutput.getDatatype());
                    tidParamInfoChild.setFlatenedName(queryOutput.getFlatenedName());
                    tidParamInfoChild.setSqlId(queryOutput.getSqlId());
                    paramExists = true;
                    queryOutputs.remove();
                    break;
                }
            }
            if (!paramExists) {
                tidParamInfos.remove();
            }
        }
        tidParamInfo.getChildren().addAll(queryOutputsList);
    }

    @Override
    public boolean deleteMapping(String tidName) throws BusinessException, SystemException {
        boolean success = false;
        try {
            success = mappingBO.deleteTidMapping(tidName);
        } catch (DataAccessException exception) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000049, new Object[] { "Deleting", tidName }, exception);
        }
        return success;
    }

    @Override
    public QueryLaunchInfo createInputMapForQuery(String type, String tidName) throws BusinessException, SystemException {
        MidIOInfo midIOInfo = null;
        TidIOInfo tidIOInfo = null;
        Map<String, TidParamInfo> tidInputMap = new HashMap<String, TidParamInfo>();
        Map<String, MidParamInfo> midOutputMap = new HashMap<String, MidParamInfo>();
        final QueryLaunchInfo queryLaunchInfo = new QueryLaunchInfo();

        MappingDescriptor readMapping = readMapping(tidName);
        queryLaunchInfo.setTidName(tidName);
        queryLaunchInfo.setType(type);

        if (readMapping != null) {
            if (StringUtils.equalsIgnoreCase(BusinessConstants.TYPE_INPUT_MAPPING, type)) {
                tidIOInfo = readMapping.getTidTree();
                tidInputMap = mappingHelper.getFlattenedInputMapping(tidIOInfo);
                queryLaunchInfo.setTidInput(tidInputMap);
            } else {
                midIOInfo = readMapping.getMidTree();
                midOutputMap = mappingHelper.getFlattenedOutputMapping(midIOInfo);
                queryLaunchInfo.setMidOutput(midOutputMap);
            }
        }
        return queryLaunchInfo;
    }

    @Override
    public String getMappingStatus(String tidName) throws BusinessException, SystemException {
        return mappingBO.getMappingStatus(tidName);
    }

    @Override
    public Map<String, Boolean> isReferenced(List<String> tidParamNames, String tidName, String mappingType)
            throws SystemException, BusinessException {
        Map<String, Boolean> referenceMap = null;
        Map<String, String> mappingDetailMap = null;
        Mapping mapping = null;
        MappingInput mappingInput = null;
        MappingOutput mappingOutput = null;
        PartialMapping<MappingViewInfo> partialMidMappings = null;
        if (StringUtils.isNotBlank(tidName) && CollectionUtils.isNotEmpty(tidParamNames)) {
            mapping = mappingBO.findByName(tidName);
            if (StringUtils.equalsIgnoreCase(BusinessConstants.TYPE_INPUT_MAPPING, mappingType)) {
                mappingInput = mappingBO.findInputByMapping(mapping);
                partialMidMappings = ConversionUtil.convertJson(mappingInput.getMappingData(), PartialMapping.class,
                        MappingViewInfo.class);
            } else {
                mappingOutput = mappingBO.findOutputByMapping(mapping);
                partialMidMappings = ConversionUtil.convertJson(mappingOutput.getMappingData(), PartialMapping.class,
                        MappingViewInfo.class);
            }
            if (partialMidMappings != null) {
                mappingDetailMap = mappingHelper.createMappingDetailMap(partialMidMappings.getPartials());
                if (MapUtils.isNotEmpty(mappingDetailMap)) {
                    referenceMap = new HashMap<>();
                    for (String params : tidParamNames) {
                        if (mappingDetailMap.containsKey(params.toUpperCase(Locale.getDefault()))) {
                            referenceMap.put(params, BusinessConstants.TRUE);
                        } else {
                            referenceMap.put(params, BusinessConstants.FALSE);
                        }
                    }
                }
            }
        }
        return referenceMap;
    }

    @Override
    public List<String> getListOfMappingNames(String modelName) throws SystemException, BusinessException {
        return mappingBO.getListOfMappingNames(modelName);
    }

    @Override
    public List<MappingInfo> findByModelName(String modelName) throws SystemException, BusinessException {
        // return convertToList(mappingBO.findByModelName(modelName), MappingInfo.class);
        return convertToList(mappingBO.findFinalizedMappings(modelName), MappingInfo.class);
    }

    @Override
    public MappingInfo findByName(String tidName) throws SystemException, BusinessException {
        return convert(mappingBO.findByName(tidName), MappingInfo.class);
    }

    @Override
    public List<MappingsCopyInfo> getTidListForCopy() throws SystemException, BusinessException {

        return mappingBO.getAllTidsForCopy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.mapping.delegate.MappingDelegate#generateMapping(java.lang.String, java.lang.String)
     */
    @Override
    public MappingDescriptor generateMapping(String derievedModelNm, String derievedTidNm, String tidNameToCopy, // NOPMD
            String description) throws BusinessException, SystemException {// NOPMD
        Model model = null;
        MidIOInfo midIOInfo = null;
        MappingDescriptor existingDesc = null;
        MappingDescriptor copiedDesc = null;
        List<TidSqlInfo> sqlInfos = null;
        String newMappingName = null;
        if (StringUtils.isNotBlank(derievedModelNm) && StringUtils.isNotBlank(tidNameToCopy)) {
            existingDesc = readMapping(tidNameToCopy);
            if (StringUtils.equalsIgnoreCase(derievedModelNm, existingDesc.getMidName())) {
                copiedDesc = new MappingDescriptor();
                copiedDesc.setMidTree(existingDesc.getMidTree());
                copiedDesc.setTidTree(existingDesc.getTidTree());
                copiedDesc.setTidMidMapping(existingDesc.getTidMidMapping());
                copiedDesc.setDescription(description);
                copiedDesc.setTidName(derievedTidNm);
                newMappingName = getMappingDescription(copiedDesc, derievedModelNm, null).getKey();
                copiedDesc.setTidName(newMappingName);
                sqlInfos = syndicateDataQueryDelegate.getInterfaceDefinitionSqlInfos(tidNameToCopy,
                        BusinessConstants.TYPE_INPUT_MAPPING);
                // copySystemParamQueries(sqlInfos, existingDesc.getTidName(),
                // mappingBO.getMappingId(derievedModelNm.replace(MID, TID)));
            } else {
                model = modelBO.getModelByName(derievedModelNm);
                if (model != null && model.getModelDefinition().getIoDefinition() != null) {
                    midIOInfo = midExtractor
                            .extractModelIO(new ByteArrayInputStream(model.getModelDefinition().getIoDefinition()));
                    if (midIOInfo != null) {
                        sqlInfos = syndicateDataQueryDelegate.getInterfaceDefinitionSqlInfos(tidNameToCopy,
                                BusinessConstants.TYPE_INPUT_MAPPING);
                        copiedDesc = tidCopy.copyTid(midIOInfo, existingDesc, sqlInfos);
                        copiedDesc.setDescription(description);
                        PartialMapping<TidParamInfo> systemParamsPartial = new PartialMapping<>();
                        systemParamsPartial.setPartials(existingDesc.getTidTree().getTidSystemInput());
                        copiedDesc.getTidTree().setTidSystemInput(mergeSqlPartials(systemParamsPartial, sqlInfos));
                        copiedDesc.setTidName(derievedTidNm);
                        newMappingName = getMappingDescription(copiedDesc, derievedModelNm, null).getKey();
                        // mappingHelper.getSQLOutputs(sqlInfos, copiedDesc.getTidTree());
                        // copySystemParamQueries(sqlInfos, existingDesc.getTidName(),
                        // mappingBO.getMappingId(derievedModelNm.replace(MID, TID)));
                    } else {
                        SystemException.newSystemException(BusinessExceptionCodes.BSE000067, new Object[] {
                                String.format("No model with record name as %s, is available!", derievedModelNm) });
                    }
                }
            }
        }
        return copiedDesc;
    }

    private void copySystemParamQueries(String frommappingId, String toMappingId, List<TidParamInfo> copiedSqlInfos)
            throws SystemException, BusinessException {
        queryMappingIntegration.copyQueries(frommappingId, toMappingId, copiedSqlInfos);

    }

    @Override
    public List<TidIoDefinition> getTidIoDefinitions(String tidName, boolean isTestBed)
            throws SystemException, BusinessException {
        return getTidIODefinitions(tidName, isTestBed);
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getModelManageTest())")
    public List<TidIoDefinition> getTidIoDeFnsTestBed(String tidName, boolean isTestBed)
            throws SystemException, BusinessException {
        return getTidIODefinitions(tidName, isTestBed);
    }

    private List<TidParamInfo> getTenantInputDefinition(String tidName) throws SystemException, BusinessException {
        Mapping mapping = null;
        MappingInput mappingInput = null;
        PartialMapping<TidParamInfo> partialTidParams = null;
        List<TidParamInfo> tidParamInfos = null;
        if (StringUtils.isNotBlank(tidName)) {
            mapping = mappingBO.findByName(tidName);
            if (mapping != null) {
                mappingInput = mappingBO.findInputByMapping(mapping);
                if (mappingInput != null) {
                    partialTidParams = ConversionUtil.convertJson(mappingInput.getTenantInterfaceDefn(), PartialMapping.class,
                            TidParamInfo.class);
                    tidParamInfos = partialTidParams.getPartials();
                    mappingHelper.removeTidParamsSkippedToTenant(tidParamInfos);
                }
            } else {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000048, new Object[] { tidName });
            }
        }
        return tidParamInfos;
    }

    private List<TidParamInfo> getTenantOutputDefinition(String tidName) throws SystemException, BusinessException {
        Mapping mapping = null;
        MappingOutput mappingOutput = null;
        PartialMapping<TidParamInfo> partialTidParams = null;
        List<TidParamInfo> tidParamInfos = null;
        if (StringUtils.isNotBlank(tidName)) {
            mapping = mappingBO.findByName(tidName);
            if (mapping != null) {
                mappingOutput = mappingBO.findOutputByMapping(mapping);
                if (mappingOutput != null) {
                    partialTidParams = ConversionUtil.convertJson(mappingOutput.getTenantInterfaceDefn(), PartialMapping.class,
                            TidParamInfo.class);
                    tidParamInfos = partialTidParams.getPartials();
                    mappingHelper.removeTidParamsSkippedToTenant(tidParamInfos);
                }
            } else {
                BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000048, new Object[] { tidName });
            }
        }
        return tidParamInfos;
    }

    private List<TidIoDefinition> getTidIODefinitions(String tidName, boolean isTestBed)
            throws SystemException, BusinessException {
        List<TidParamInfo> tidParamInfos = null;
        List<TidIoDefinition> tidIoDefinitions = null;
        tidParamInfos = getTenantInputDefinition(tidName);
        if (tidParamInfos != null) {
            tidIoDefinitions = new ArrayList<TidIoDefinition>();
            mappingHelper.getIoDefinitions(tidParamInfos, tidIoDefinitions, isTestBed);
        }
        return tidIoDefinitions;

    }

    @Override
    public String createRuntimeInputJson(List<TidIoDefinition> definitions, String modelName, int majorVersion, int minorVersion,
            String date, Boolean isTestForVerCreation, Boolean isOpValidation, Boolean isAcceptableValues, Boolean storeRLogs)
            throws SystemException, BusinessException {
        Map<String, Object> inputMap = null;
        Map<String, Object> complexMap = null;
        Map<String, Object> withHeaderInputMap = null;
        Map<String, Object> headeDataMap = null;
        String inputJson = null;
        // TODO this method would not allow running any model with no input.
        if (CollectionUtils.isNotEmpty(definitions)) {
            headeDataMap = new HashMap<>();
            inputMap = new HashMap<>();
            complexMap = new HashMap<>();
            headeDataMap.put("modelName", modelName);
            headeDataMap.put("majorVersion", majorVersion);
            headeDataMap.put("minorVersion", minorVersion);
            headeDataMap.put("storeRLogs",storeRLogs!=null? Boolean.valueOf(storeRLogs):Boolean.FALSE);
            // added to fix UMG-4500 Additional variables in Transaction header
            headeDataMap.put("user", SecurityContextHolder.getContext().getAuthentication() == null ? "SYSTEM"
                    : SecurityContextHolder.getContext().getAuthentication().getName());
            headeDataMap.put("transactionType", "test");
            getOpValidation(isOpValidation, headeDataMap, isAcceptableValues);
            if (date != null) {
                if (date.split(StringUtils.SPACE).length > BusinessConstants.NUMBER_ONE) {
                    Long millis = AdminUtil.getMillisFromEstToUtc(date, BusinessConstants.UMG_EST_DATE_TIME_FORMAT);
                    String newDate[] = AdminUtil.getFormattedDate(millis, BusinessConstants.UMG_EST_DATE_TIME_FORMAT)
                            .split(StringUtils.SPACE);
                    headeDataMap.put("date", newDate[0] + "T" + newDate[1] + ":00.000");
                } else {
                    headeDataMap.put("date", date + ":00.000");
                }
            }

            // added this check "versionCreationTest" for umg-4020 and umg-4251
            // to add clientTransactionId as Publishing-Test in case of sample test transaction during version creation
            if (isTestForVerCreation) {
                headeDataMap.put("transactionId", SystemConstants.VERSN_PUBLISH_CLIENT_TRAN_ID);
                headeDataMap.put(BusinessConstants.VERSION_CREATION_TEST, Boolean.TRUE);
            } else {
                headeDataMap.put("transactionId", UUID.randomUUID().toString());
                headeDataMap.put(BusinessConstants.VERSION_CREATION_TEST, Boolean.FALSE);
            }

            Map<String, Integer> objectMap = new HashMap<String, Integer>();

            for (TidIoDefinition tidIoDefinition : definitions) {
                if (tidIoDefinition.isArrayType() && StringUtils
                        .equalsIgnoreCase((String) tidIoDefinition.getDatatype().get("type"), Datatype.OBJECT.getDatatype())) {
                    objectMap.put(tidIoDefinition.getName(),
                            StringUtils.countMatches(tidIoDefinition.getName(), BusinessConstants.SLASH));
                } else {
                    mappingHelper.setElementValue(tidIoDefinition);
                    if (StringUtils.contains(tidIoDefinition.getName(), "/")) {
                        complexMap.put(tidIoDefinition.getName(), tidIoDefinition.getValue());
                    } else {
                        inputMap.put(tidIoDefinition.getName(), tidIoDefinition.getValue());
                    }
                }
            }
            mappingHelper.createObjectStructure(complexMap, inputMap);

            if (MapUtils.isNotEmpty(objectMap)) {
                TreeMap<String, Integer> sortedObjectStructure = sortMapByValue(objectMap);
                updateObjectNodes(inputMap, sortedObjectStructure);
            }

            if (MapUtils.isNotEmpty(inputMap)) {
                withHeaderInputMap = new LinkedHashMap<>();
                withHeaderInputMap.put("header", headeDataMap);
                withHeaderInputMap.put("data", inputMap);
                inputJson = ConversionUtil.convertToJsonString(withHeaderInputMap);
            }
        } else {
            SystemException.newSystemException(FrameworkExceptionCodes.BSE000009, new Object[] {
                    String.format("Error creating runtime input. No input data for the model %s found!", modelName) });
        }
        return inputJson;
    }

    private static void updateObjectNodes(Map input, Map<String, Integer> nodeIdentifier) {
        for (String objName : nodeIdentifier.keySet()) {
            if (StringUtils.contains(objName, BusinessConstants.SLASH)) {
                String rootNode = StringUtils.substring(objName, 0, StringUtils.indexOf(objName, BusinessConstants.SLASH));
                String childNode = StringUtils.substring(objName, StringUtils.indexOf(objName, BusinessConstants.SLASH) + 1);
                if (StringUtils.contains(childNode, BusinessConstants.SLASH)) {
                    Map rootNodeValue = (Map) input.get(rootNode);
                    Map intermediateDecisionMap = new HashMap();
                    intermediateDecisionMap.put(childNode, 1);
                    updateObjectNodes(rootNodeValue, intermediateDecisionMap);
                } else {
                    Map rootNodeValue = (Map) input.get(rootNode);
                    Object value = rootNodeValue.get(childNode);
                    List valueList = new ArrayList();
                    valueList.add(value);
                    rootNodeValue.put(childNode, valueList);
                }
            } else {
                Object value = input.get(objName);
                List valueList = new ArrayList();
                valueList.add(value);
                input.put(objName, valueList);
            }
        }
    }

    public static TreeMap<String, Integer> sortMapByValue(final Map<String, Integer> map) {

        // TreeMap is a map sorted by its keys.
        // The comparator is used to sort the TreeMap by keys.
        TreeMap<String, Integer> result = new TreeMap<String, Integer>(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                if (map.get(s1) >= map.get(s2)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        result.putAll(map);
        return result;
    }

    private void getOpValidation(Boolean isOpValidation, Map<String, Object> headeDataMap, Boolean isAcceptableValues) {
        List<String> addOnValidation = new ArrayList<String>();
        if (isOpValidation != null && isOpValidation) {
            addOnValidation.add(FrameworkConstant.MODEL_OUTPUT);
        }
        if (isAcceptableValues != null && isAcceptableValues) {
            addOnValidation.add(FrameworkConstant.ACCEPTABLE_VALUES);
        }
        if (!addOnValidation.isEmpty()) {
            headeDataMap.put(FrameworkConstant.ADD_ON_VALIDATION, addOnValidation);
        }
    }

    @Override
    public MappingInfo updateMappingStatus(String identifier, String status) throws SystemException, BusinessException {
        MappingInfo mappingInfo = null;
        Mapping mapping = mappingBO.find(identifier);
        if (mapping != null) {
            mapping.setStatus(status);
            mapping = mappingBO.create(mapping);
            mappingInfo = convert(mapping, MappingInfo.class);
        } else {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000086, new Object[] { identifier });
        }
        return mappingInfo;
    }

    /**
     * This method will retrieve all mapping and return paginated data back with pageinfo It will be grouped based on model
     */
    @Override
    public ResponseWrapper<List<MappingHierarchyInfo>> findAllMappings(SearchOptions searchOptions)
            throws BusinessException, SystemException {
        ResponseWrapper<List<MappingHierarchyInfo>> response = new ResponseWrapper<List<MappingHierarchyInfo>>();
        List<MappingHierarchyInfo> mappingHierarchyInfos = null;
        MappingInfo mappingInfo = null;
        List<MappingInfo> mappingInfos = null;
        MappingHierarchyInfo mappingHierarchyInfo = null;
        Map<String, MappingHierarchyInfo> map = null;

        List<Mapping> mappings = mappingBO.findAllMappings(searchOptions);

        if (CollectionUtils.isNotEmpty(mappings)) {
            map = new HashMap<String, MappingHierarchyInfo>();
            for (Mapping mapping : mappings) {
                mappingInfo = convert(mapping, MappingInfo.class);
                mappingInfo
                        .setCreatedDateTime(AdminUtil.getDateFormatMillisForEst(mappingInfo.getCreatedDate().getMillis(), null));
                mappingInfo.setLastModifiedDateTime(
                        AdminUtil.getDateFormatMillisForEst(mappingInfo.getLastModifiedDate().getMillis(), null));
                final String modelName = mapping.getModel().getName();
                if (map.containsKey(modelName)) {
                    mappingHierarchyInfo = map.get(modelName);
                    mappingInfos = mappingHierarchyInfo.getMappingInfos();
                    mappingInfos.add(mappingInfo);
                } else {
                    mappingHierarchyInfo = new MappingHierarchyInfo();
                    mappingHierarchyInfo.setModelName(modelName);
                    mappingInfos = new ArrayList<>();
                    mappingInfos.add(mappingInfo);
                    mappingHierarchyInfo.setMappingInfos(mappingInfos);
                    map.put(modelName, mappingHierarchyInfo);
                }
            }
            mappingHierarchyInfos = new ArrayList<>(map.values());
            map.clear();

            if (searchOptions.isDescending()) {
                Collections.sort(mappingHierarchyInfos, MappingHierarchyInfo.MODEL_DESCENDING);
            } else {
                Collections.sort(mappingHierarchyInfos, MappingHierarchyInfo.MODEL_ASCENDING);
            }

        }
        response.setPagingInfo(PagingInfo.setPagingForList(mappingHierarchyInfos, searchOptions));
        List<MappingHierarchyInfo> pagedMappingHierarchyInfos = PagingInfo.getPagedList(mappingHierarchyInfos,
                response.getPagingInfo());
        response.setResponse(pagedMappingHierarchyInfos);
        return response;
    }

    @Override
    public TenantIODefinition getTIDParams(String tidName) throws BusinessException, SystemException {
        List<TidParamInfo> tenantInputs = getTenantInputDefinition(tidName);
        List<TidParamInfo> tenantOutputs = getTenantOutputDefinition(tidName);

        TenantIODefinition tenantIODefinition = new TenantIODefinition();
        tenantIODefinition.setTenantInputs(tenantInputs);
        tenantIODefinition.setTenantOutputs(tenantOutputs);
        return tenantIODefinition;
    }

}