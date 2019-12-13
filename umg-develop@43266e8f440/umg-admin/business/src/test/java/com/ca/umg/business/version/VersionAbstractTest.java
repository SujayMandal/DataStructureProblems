package com.ca.umg.business.version;

import com.ca.umg.business.BaseTest;
import com.ca.umg.business.mapping.dao.AbstractMappingTest;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.model.AbstractModelTest;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionInfo;

public class VersionAbstractTest extends BaseTest {

    private ModelDependency modelDependency = new ModelDependency();

    private MappingDependency mappingDependency = new MappingDependency();

    public Version buildVersion(String name, String description, Integer majorVersion, Integer minorVersion, String status,
            Mapping mapping, ModelLibrary modelLibrary, String versionDescription) {
        Version version = new Version();
        version.setName(name);
        version.setDescription(description);
        version.setMajorVersion(majorVersion);
        version.setMinorVersion(minorVersion);
        version.setStatus(status);
        version.setMapping(mapping);
        version.setModelLibrary(modelLibrary);
        version.setVersionDescription(versionDescription);
        return version;
    }

    public Model buildModel(String name, String description, String documentationName, String ioDefinitionName, String type,
            String ioDefinition) {
        return modelDependency.buildModel(name, description, documentationName, ioDefinitionName, type, ioDefinition, true);
    }

    public ModelLibrary buildModelLibrary(String name, String description, String umgName, String jarName,
            String executionLanguage, String executionType, String checksum, String encodingType, String modelExecEnvName) {
        return modelDependency.buildModelLibrary(name, description, umgName, jarName, executionLanguage, executionType, checksum,
                encodingType, modelExecEnvName);
    }

    public Mapping buildMapping(String name, Model model, String tenantId, String description, String mappigIO) {
        return mappingDependency.buildMapping(name, model, tenantId, description, mappigIO);
    }

    private class ModelDependency extends AbstractModelTest {

        @Override
        public Model buildModel(String name, String description, String documentationName, String ioDefinitionName, String type,
                String ioDefinition, Boolean allowNull) {
            return super.buildModel(name, description, documentationName, ioDefinitionName, type, ioDefinition, allowNull);
        }

        @Override
        protected ModelLibrary buildModelLibrary(String name, String description, String umgName, String jarName,
                String executionLanguage, String executionType, String checksum, String encodingType, String modelExecEnvName) {
            return super.buildModelLibrary(name, description, umgName, jarName, executionLanguage, executionType, checksum,
                    encodingType, modelExecEnvName);
        }

    }

    private class MappingDependency extends AbstractMappingTest {

        @Override
        public Mapping buildMapping(String name, Model model, String tenantId, String description, String mappigIO) {
            return super.buildMapping(name, model, tenantId, description, mappigIO);
        }

    }

    protected VersionInfo buildPagingInfo(int page, int pageSize, boolean descending, String sortColumn) {
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setDescending(descending);
        versionInfo.setPage(page);
        versionInfo.setPageSize(pageSize);
        versionInfo.setSortColumn(sortColumn);
        return versionInfo;
    }
}
