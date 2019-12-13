/**
 * 
 */
package com.ca.umg.business.mapping.dao;

import java.util.Calendar;

import com.ca.umg.business.BaseTest;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.entity.MappingInput;
import com.ca.umg.business.mapping.entity.MappingOutput;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelDefinition;
import com.ca.umg.business.model.entity.ModelLibrary;

/**
 * @author kamathan
 *
 */
public abstract class AbstractMappingTest extends BaseTest {

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

    protected Mapping buildMapping(String name, Model model, String tenantId, String description, String mappigIO) {
        Mapping mapping = new Mapping();
        mapping.setModel(model);
        mapping.setName(name);
        mapping.setDescription(description);
        mapping.setModelIO(mappigIO.getBytes());
        mapping.setStatus("SAVED");
        return mapping;
    }

    protected Model createModel(String name, String description, String documentationName, String ioDefinitionName, String type,
            String ioDefinition) {
        Model model = new Model();
        model.setName(name);
        model.setDescription(description);
        model.setDocumentationName(documentationName);
        model.setIoDefinitionName(ioDefinitionName);
        model.setAllowNull(false);
        ModelDefinition modelDefinition = new ModelDefinition();
        modelDefinition.setType(type);
        modelDefinition.setIoDefinition(ioDefinition.getBytes());
        model.setModelDefinition(modelDefinition);
        model.setUmgName(name + "-" + Calendar.getInstance().getTime());
        model.getModelDefinition().setModel(model);
        return getModelDAO().save(model);
    }

    protected MappingInput createMappingInput(Mapping mapping, byte[] mappingData, byte[] tenantInterfaceDefn, String tenantId) {
        MappingInput mappingInput = new MappingInput();
        mappingInput.setMapping(mapping);
        mappingInput.setMappingData(mappingData);
        mappingInput.setTenantInterfaceDefn(tenantInterfaceDefn);
        return getMappingInputDAO().save(mappingInput);
    }

    protected MappingOutput createMappingOutput(Mapping mapping, byte[] mappingData, byte[] tenantInterfaceDefn, String tenantId) {
        MappingOutput mappingOutput = new MappingOutput();
        mappingOutput.setMapping(mapping);
        mappingOutput.setMappingData(mappingData);
        mappingOutput.setTenantInterfaceDefn(tenantInterfaceDefn);
        return getMappingOutputDAO().save(mappingOutput);
    }

    protected void deleteMapping(Mapping mapping) {
        getMappingDAO().delete(mapping);
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

}
