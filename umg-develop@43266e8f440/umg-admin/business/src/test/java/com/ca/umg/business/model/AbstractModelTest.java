package com.ca.umg.business.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import com.ca.umg.business.BaseTest;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelDefinition;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelDefinitionInfo;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionStatus;

public abstract class AbstractModelTest extends BaseTest {

    protected Model buildModel(String name, String description, String documentationName, String ioDefinitionName, String type,
            String ioDefinition, Boolean allowNull) {
        Model model = new Model();
        model.setName(name);
        model.setDescription(description);
        model.setDocumentationName(documentationName);
        model.setIoDefinitionName(ioDefinitionName);
        ModelDefinition modelDefinition = new ModelDefinition();
        modelDefinition.setType(type);
        modelDefinition.setIoDefinition(ioDefinition.getBytes());
        model.setModelDefinition(modelDefinition);
        return model;
    }

    protected Model createModel(String name, String description, String documentationName, String ioDefinitionName, String type,
            String ioDefinition, Boolean allowNull) {
        Model model = buildModel(name, description, documentationName, ioDefinitionName, type, ioDefinition, allowNull);
        model.setUmgName(name + "-" + Calendar.getInstance().getTime());
        model.getModelDefinition().setModel(model);
        return getModelDAO().save(model);
    }

    protected ModelLibrary buildModelLibrary(String name, String description, String umgName, String jarName,
            String executionLanguage, String executionType, String checksum, String encodingType, String modelExecEnvName) {
        ModelLibrary modelLibrary = new ModelLibrary();
        modelLibrary.setName(name);
        modelLibrary.setDescription(description);
        modelLibrary.setUmgName(umgName);
        modelLibrary.setJarName(jarName);
        modelLibrary.setExecutionLanguage(executionLanguage);
        modelLibrary.setExecutionType(executionType);
        modelLibrary.setChecksum(checksum);
        modelLibrary.setEncodingType(encodingType);
        modelLibrary.setModelExecEnvName(modelExecEnvName);
        return modelLibrary;
    }

    protected ModelLibrary createModelLibrary(String name, String description, String umgName, String jarName,
            String executionLanguage, String executionType,String checksum,String encodingType,String modelExecEnvName) {
        ModelLibrary modelLibrary = buildModelLibrary(name, description, umgName, jarName, executionLanguage, executionType,
                checksum, encodingType, modelExecEnvName);
        return getModelLibraryDAO().saveAndFlush(modelLibrary);
    }

    protected ModelInfo buildModelInfo(String name, String description, String documentationName, String ioDefinitionName,
            String type, ModelArtifact documentation, ModelArtifact xml) {
        ModelInfo modelInfo = new ModelInfo();
        modelInfo.setName(name);
        modelInfo.setDescription(description);
        modelInfo.setDocumentationName(documentationName);
        modelInfo.setIoDefinitionName(ioDefinitionName);
        ModelDefinitionInfo modelDefinition = new ModelDefinitionInfo();
        modelDefinition.setType(type);
        modelInfo.setModelDefinition(modelDefinition);
        modelInfo.setDocumentation(documentation);
        modelInfo.setXml(xml);
        return modelInfo;
    }

    protected ModelArtifact buildModelArtifact(String name, String artifactPath) throws FileNotFoundException {
        ModelArtifact modelArtifact = new ModelArtifact();
        modelArtifact.setName(name);
        URL url = this.getClass().getResource(artifactPath);
        InputStream data = new FileInputStream(url.getFile());
        modelArtifact.setData(data);
        return modelArtifact;
    }

    protected void createModels() {
        createModel("Model1x", "model 1x", "DOC1x", "iio file1", "text/xml", "sample", true);
        createModel("Model2x", "model 2x", "DOC2x", "iio file2x", "text/xml", "sample2x", true);
    }

    protected void createModelLibraryList() {
        createModelLibrary("test505", "testing", "test505", "antlr-2.7.2", "MATLAB", "INTERNAL",
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
        createModelLibrary("test506", "testing", "test506", "antlr-2.7.2", "MATLAB", "INTERNAL",
                "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4", "SHA256", "Matlab-7.16");
    }
    
    protected void deleteModels(List<Model> modelList) {
        for (Model model : modelList)
        {
            getModelDAO().delete(model);
        }
    }
    
    protected void deleteModelLibrary(List<ModelLibrary> modelLibraryList) {
        for (ModelLibrary modelLibrary : modelLibraryList)
        {
            getModelLibraryDAO().delete(modelLibrary);
        }
    }
    
    protected Mapping createMapping(String name, Model model, String tenantId, String description, String mappigIO) {
        Mapping mapping = getMappingDAO().findByName(name);
        if (mapping == null) {
            mapping = new Mapping();
            mapping.setModel(model);
            mapping.setName(name);
            mapping.setDescription(description);
            mapping.setModelIO(mappigIO.getBytes());
            mapping.setStatus("FINALIZED");
            mapping = getMappingDAO().save(mapping);
        }
        return mapping;
    }
    
    protected Version createVersion(Mapping mapping, ModelLibrary modelLib) {
        Version version = new Version();
        version.setName("verStat11");
        version.setDescription("verStat11");
        version.setMajorVersion(11);
        version.setMinorVersion(11);
        version.setStatus(VersionStatus.TESTED.getVersionStatus());
        version.setMapping(mapping);
        version.setModelLibrary(modelLib);
        version.setVersionDescription("this is version desc v11");
        getVersionDAO().saveAndFlush(version);
        return version;
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
