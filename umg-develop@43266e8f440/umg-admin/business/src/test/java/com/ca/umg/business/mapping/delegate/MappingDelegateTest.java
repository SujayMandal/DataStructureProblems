package com.ca.umg.business.mapping.delegate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mapping.bo.MappingBO;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.helper.MappingHelper;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mapping.info.TidIoDefinition;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelDefinition;

import ma.glasnost.orika.impl.ConfigurableMapper;

public class MappingDelegateTest {
    @Mock
    MappingBO mappingBO;

    @Mock
    ConfigurableMapper mapper;

    @InjectMocks
    MappingDelegateImpl mappingDelegate = new MappingDelegateImpl();

    List<Mapping> mappings = null;
    Mapping mapping = null;
    MappingInfo mappingInfo = null;
    Model model = null;

    @Mock
    MappingHelper mappingHelper;

    private List<TidIoDefinition> tidIoDefinitions;
    private TidIoDefinition tidIoDefinition;

    public void createTidIoDefinitions() {
        tidIoDefinitions = new ArrayList<>();
        tidIoDefinition = new TidIoDefinition();
        tidIoDefinition.setArrayType(false);
        Map<String, Object> dataType = new HashMap<String, Object>();
        dataType.put("String", new Object());
        tidIoDefinition.setDatatype(dataType);
        tidIoDefinition.setDescription("takes only String");
        tidIoDefinition.setHtmlElement("text");
        tidIoDefinition.setValidationMethod("validate_string");
        tidIoDefinition.setName("city");
        tidIoDefinition.setValue("bangalore");
        tidIoDefinition.setMandatory(false);
        tidIoDefinitions.add(tidIoDefinition);
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mappings = new ArrayList<>();
        mapping = new Mapping();
        model = new Model();
        mappingInfo = new MappingInfo();
        model.setName("Model1x");
        model.setDescription("model 1x");
        model.setDocumentationName("DOC1x");
        model.setIoDefinitionName("iio file1");
        model.setCreatedDate(new DateTime());
        model.setLastModifiedDate(new DateTime());
        ModelDefinition modelDefinition = new ModelDefinition();
        modelDefinition.setModel(model);
        modelDefinition.setType("text/xml");
        modelDefinition.setIoDefinition("sample".getBytes());
        modelDefinition.setCreatedDate(new DateTime());
        modelDefinition.setLastModifiedDate(new DateTime());
        model.setModelDefinition(modelDefinition);
        model.setUmgName("umg-model-1");

        mapping.setModel(model);
        mapping.setName("test-model-mapping");
        mapping.setModelIO("Sample MID Json".getBytes());
        mapping.setCreatedDate(new DateTime());
        mapping.setLastModifiedDate(new DateTime());
        mappings.add(mapping);
        mappings.add(mapping);
        createTidIoDefinitions();
    }

    @Ignore
    @Test
    public void testGetMappingHierarchyInfos() {
        try {
            when(mappingBO.listAll()).thenReturn(mappings);
            assertNotNull(mappingDelegate.getMappingHierarchyInfos());
        } catch (SystemException | BusinessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFailGetMappingHierarchyInfos() {
        try {
            when(mappingBO.listAll()).thenReturn(null);
            assertNull(mappingDelegate.getMappingHierarchyInfos());
        } catch (SystemException | BusinessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateRuntimeInputJson() {
        try {
            mappingDelegate.createRuntimeInputJson(tidIoDefinitions, "test", 1, 1, "2014-04-25 00:00", Boolean.FALSE,Boolean.TRUE,Boolean.TRUE,Boolean.TRUE);
            doNothing().when(mappingHelper).setElementValue(tidIoDefinition);
            doNothing().when(mappingHelper).createObjectStructure(new HashMap<String, Object>(), new HashMap<String, Object>());
        } catch (SystemException | BusinessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
