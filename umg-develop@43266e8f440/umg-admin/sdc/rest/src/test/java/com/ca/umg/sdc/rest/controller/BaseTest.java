/**
 * 
 */
package com.ca.umg.sdc.rest.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.mock.web.MockMultipartFile;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.mapping.info.ModelMappingInfo;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.model.info.ModelLibraryHierarchyInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataVersionInfo;
import com.ca.umg.sdc.rest.exception.RESTExceptionCodes;

/**
 * @author nigampra
 * 
 */
public abstract class BaseTest {

    private List<ModelLibraryInfo> modelLibList;

    private List<ModelLibraryHierarchyInfo> modelLibHierarchyList;

    private ModelInfo modelInfo;

    private List<ModelInfo> modelInfoList;

    private List<String> derivedModelList;

    private List<SyndicateDataColumnInfo> metaData;

    private List<SyndicateDataInfo> versions;

    private List<String> allModelList;

    protected List<ModelLibraryInfo> buildModelLibraryInfoList() {
        modelLibList = new ArrayList<ModelLibraryInfo>();
        for (int i = 0; i < 3; i++) {
            ModelLibraryInfo modelLibInfo = new ModelLibraryInfo();
            modelLibInfo.setId("1234_" + i);
            modelLibInfo.setName("DummyLibName_" + i);
            modelLibInfo.setDescription("Dummy Description_" + i);
            modelLibInfo.setJarName("DummyJarName_" + i);
            modelLibInfo.setExecutionLanguage("MATLAB");
            modelLibInfo.setExecutionType("INTERNAL");
            modelLibList.add(modelLibInfo);
        }
        return modelLibList;
    }

    protected List<ModelLibraryHierarchyInfo> buildModelLibraryHierarchyInfoList() {
        List<ModelLibraryInfo> modelLibsList = modelLibList;
        ModelLibraryHierarchyInfo modelLibTagInfo = null;
        ModelLibraryHierarchyInfo modelLibTag = null;
        Map<String, ModelLibraryHierarchyInfo> tagMap = null;
        String modelTag = null;
        if (CollectionUtils.isNotEmpty(modelLibsList)) {
            tagMap = new HashMap<String, ModelLibraryHierarchyInfo>();
            for (ModelLibraryInfo modelLib : modelLibsList) {
                modelLibTag = new ModelLibraryHierarchyInfo();
                modelTag = modelLib.getName();
                modelLibTag.setName(modelLib.getUmgName());
                modelLibTag.setId(modelLib.getId());
                modelLibTag.setDescription(modelLib.getDescription());
                modelLibTag.setJarName(modelLib.getJarName());
                modelLibTag.setExecutionLanguage(modelLib.getExecutionLanguage());
                modelLibTag.setExecutionType(modelLib.getExecutionType());
                modelLibTag.setUmgName(modelLib.getUmgName());
                if (tagMap.containsKey(modelTag)) {
                    tagMap.get(modelTag).getChildren().add(modelLibTag);
                } else {
                    modelLibTagInfo = new ModelLibraryHierarchyInfo();
                    modelLibTagInfo.setName(modelTag);
                    modelLibTagInfo.getChildren().add(modelLibTag);
                    tagMap.put(modelTag, modelLibTagInfo);
                }
            }
            modelLibHierarchyList = new ArrayList<>(tagMap.values());
        }
        return modelLibHierarchyList;
    }

    protected List<ModelInfo> buildModelInfoList() {
        modelInfoList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            modelInfo = new ModelInfo();
            final String modelName = "test model";
            modelInfo.setName(modelName + i);
            modelInfo.setDescription("test model create for junit testing");
            modelInfo.setId("1_" + i);
            modelInfo.setUmgName("test model_" + i);
            modelInfoList.add(modelInfo);
        }
        return modelInfoList;
    }

    protected ModelArtifact buildArtifacts(MockMultipartFile multipartFile, String modelName) throws SystemException {
        ModelArtifact modelArtifact = new ModelArtifact();
        try {
            modelArtifact.setData(multipartFile.getInputStream());
            modelArtifact.setContentType(multipartFile.getContentType());
            modelArtifact.setName(multipartFile.getOriginalFilename());
            modelArtifact.setModelName(modelName);
        } catch (IOException e) {
            throw new SystemException(RESTExceptionCodes.RSE0000001, new Object[] { modelName, e });
        }
        return modelArtifact;
    }

    protected List<List<String>> buildDerivedModel() {
        List<List<String>> resultList = new ArrayList<>();
        modelInfo = new ModelInfo();
        derivedModelList = new ArrayList<String>();
        allModelList = new ArrayList<String>();
        final String modelName = "test model";
        modelInfo.setName(modelName);
        modelInfo.setDescription("test model create for junit testing");
        modelInfo.setId("1");
        modelInfo.setUmgName("test model");
        modelInfo.setName("1");
        derivedModelList.add(modelInfo.getUmgName());
        allModelList.add(modelInfo.getName());

        resultList.add(derivedModelList);
        resultList.add(allModelList);
        return resultList;
    }

    protected List<ModelInfo> buildModelInfos() {
        modelInfoList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            ModelInfo modelHierarchyInfo = new ModelInfo();
            final String modelName = "test model";
            modelHierarchyInfo.setName(modelName + i);
            modelHierarchyInfo.setDescription("test model hierarchy create for junit testing");
            modelHierarchyInfo.setId("1_" + i);
            modelInfoList.add(modelHierarchyInfo);
        }
        return modelInfoList;
    }
    
    protected PageRecord<ModelInfo> buildModelInfoPageRecord() {
    	PageRecord<ModelInfo> pageRecord = new PageRecord<>();
        modelInfoList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            ModelInfo modelHierarchyInfo = new ModelInfo();
            final String modelName = "test model";
            modelHierarchyInfo.setName(modelName + i);
            modelHierarchyInfo.setDescription("test model hierarchy create for junit testing");
            modelHierarchyInfo.setId("1_" + i);
            modelInfoList.add(modelHierarchyInfo);
        }
        pageRecord.setContent(modelInfoList);
        return pageRecord;
    }
    
    

    protected ModelMappingInfo buildModelMappingInfo() {
        ModelMappingInfo modelMappingInfo = new ModelMappingInfo();
        List<String> mappingNameList = new ArrayList<String>();
        List<String> versionNameList = new ArrayList<String>();
        mappingNameList.add("mapping name test1");
        mappingNameList.add("mapping name test2");
        versionNameList.add("version name test1");
        versionNameList.add("version name test1");
        modelMappingInfo.setMappingNameList(mappingNameList);
        modelMappingInfo.setVersionNameList(versionNameList);
        return modelMappingInfo;
    }

    protected List<SyndicateDataColumnInfo> buildSyndicateDataColumnInfoList() {
        metaData = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            SyndicateDataColumnInfo sdci = new SyndicateDataColumnInfo();
            sdci.setDisplayName("Col" + i);
            sdci.setDescription("Col" + i + " Desc");
            sdci.setColumnType("String");
            sdci.setColumnSize(20);
            metaData.add(sdci);
        }
        return metaData;
    }

    protected List<SyndicateDataInfo> buildSyndicateDataInfoList() {
        versions = new ArrayList<>();
        SyndicateDataInfo sdi;
        for (int i = 0; i < 2; i++) {
            sdi = new SyndicateDataInfo();
            sdi.setContainerName("Test Container");
            sdi.setDescription("Container Desc");
            sdi.setVersionId("VerId" + i);
            sdi.setVersionName("Ver" + i);
            sdi.setVersionDescription("Version Desc");
            versions.add(sdi);
        }
        return versions;
    }

    protected SyndicateDataContainerInfo buildSyndicateDataContainerInfo(String containerName, Long versionId,
            String versionName, String validFromString, List<Map<String, String>> syndicateVersionData) {
        SyndicateDataContainerInfo sdci = new SyndicateDataContainerInfo();
        sdci.setContainerName(containerName);
        sdci.setDescription("Container Desc");
        sdci.setVersionId(versionId);
        sdci.setVersionName(versionName);
        sdci.setVersionDescription("Version 1 Desc");
        sdci.setValidFromString(validFromString);
        sdci.setMetaData(metaData);
        sdci.setSyndicateVersionData(syndicateVersionData);
        sdci.setTotalRows(Long.valueOf(metaData.size()));
        return sdci;
    }

    protected SyndicateDataVersionInfo buildSyndicateDataVersionInfo() {
        SyndicateDataVersionInfo sdvi = new SyndicateDataVersionInfo();
        sdvi.setMetaData(metaData);
        sdvi.setVersions(versions);
        return sdvi;
    }
    
    protected SearchOptions buildSearchOptions(int page, int pageSize, String sortColumn, boolean descending){
    	SearchOptions searchOptions = new SearchOptions();
    	searchOptions.setPage(page);
    	searchOptions.setPageSize(pageSize);
    	searchOptions.setSortColumn(sortColumn);
    	searchOptions.setDescending(descending);
    	return searchOptions;
    }

}
