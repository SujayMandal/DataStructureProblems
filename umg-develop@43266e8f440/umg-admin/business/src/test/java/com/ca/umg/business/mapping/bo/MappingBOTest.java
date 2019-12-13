package com.ca.umg.business.mapping.bo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.mapping.dao.MappingDAO;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelDefinition;

public class MappingBOTest {

    @Mock
    MappingDAO mappingDao;

    @InjectMocks
    MappingBOImpl mappingBO = new MappingBOImpl();

    List<Mapping> mappings = null;
    Mapping mapping = null;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mappings = new ArrayList<>();
        mapping = new Mapping();
        mapping.setName("TIDMAPPING-1");

        Model model = new Model();
        model.setName("Model1x");
        model.setDescription("model 1x");
        model.setDocumentationName("DOC1x");
        model.setIoDefinitionName("iio file1");
        ModelDefinition modelDefinition = new ModelDefinition();
        modelDefinition.setModel(model);
        modelDefinition.setType("text/xml");
        modelDefinition.setIoDefinition("sample".getBytes());
        model.setModelDefinition(modelDefinition);
        model.setUmgName("umg-model-1");

        mapping.setModel(model);
        mapping.setModelIO("Sample MID Json".getBytes());
        mappings.add(mapping);
    }

    @Test
    public void testListAll() {
        when(mappingDao.findAll(any(Sort.class))).thenReturn(mappings);
        try {
            List<Mapping> mappingList = mappingBO.listAll();
            assertNotNull(mappingList);
            verify(mappingDao, times(1)).findAll(new Sort(Sort.Direction.DESC, BusinessConstants.CREATED_BY));
        } catch (SystemException | BusinessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFailListAll() {
        when(mappingDao.findAll(any(Sort.class))).thenReturn(null);
        try {
            List<Mapping> mappingList = mappingBO.listAll();
            assertNull(mappingList);
            verify(mappingDao, times(1)).findAll(new Sort(Sort.Direction.DESC, BusinessConstants.CREATED_BY));
        } catch (SystemException | BusinessException e) {
            e.printStackTrace();
        }
    }
}
