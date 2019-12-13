/**
 * 
 */
package com.ca.umg.business.model.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.BaseTest;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelDefinition;

/**
 * @author kamathan
 *
 */
@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
public class ModelDAOTest extends BaseTest {

    private RequestContext requestContext;

    @Before
    public void setup() {
        requestContext = getLocalhostTenantContext();
    }

    /**
     * Test method for {@link com.ca.umg.business.model.dao.ModelDAO#findByName(java.lang.String)} .
     */
    @Test
    public final void testModelDAO() {

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

        model = getModelDAO().save(model);
        model = getModelDAO().save(model);
        assertNotNull(model);
        assertNotNull(model.getId());

        List<Model> savedModels = getModelDAO().findByName("Model1x");
        assertTrue(savedModels.size() > 0);

        Model savedModel = getModelDAO().findByUmgName(model.getUmgName());
        assertNotNull(savedModel);
        assertEquals(model, savedModel);
    }

}
