/**
 * 
 */
package com.ca.umg.business.mapping.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.info.MappingStatus;
import com.ca.umg.business.model.entity.Model;

/**
 * @author kamathan
 *
 */
@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MappingDAOTest extends AbstractMappingTest {

    private String tenantCode = null;

    @Before
    public void setup() {
        RequestContext requestContext = getLocalhostTenantContext();
        tenantCode = requestContext.getTenantCode();
    }

    @Test
    public void testMappingDAO() {
        Mapping mapping = createMapping("TIDMAPPING-1", createModel("tifModel", "desc", "doc", "sample", "text/xml", "sample"),
                tenantCode, "tid for model_1", "Sample MID Json");
        assertNotNull(mapping);
        assertNotNull(mapping.getId());

        Mapping savedMapping = getMappingDAO().findByName("TIDMAPPING-1");
        assertNotNull(savedMapping);
        assertEquals(mapping, savedMapping);
    }

    @Test
    public void testFindByModel() {
        Model model1 = createModel("tidModel2", "desc2", "doc2", "sample2", "text/xml", "sample2");
        Mapping mapping = createMapping("TIDMAPPING-2", model1, tenantCode, "tid for model_12", "Sample MID Json2");
        assertNotNull(mapping);
        assertNotNull(mapping.getId());

        List<Mapping> listMapping = getMappingDAO().findByModel(model1);
        assertNotNull(listMapping);
        assertEquals("TIDMAPPING-2", listMapping.get(0).getName());
    }

    @Test
    public void testGetListOfMappingNames() {
        Model model1 = createModel("tidModel3", "desc3", "doc3", "sample3", "text/xml", "sample3");
        Mapping mapping = createMapping("TIDMAPPING-3", model1, tenantCode, "tid for model_13", "Sample MID Json2");
        mapping = createMapping("TIDMAPPING-4", model1, tenantCode, "tid for model_14", "Sample MID Json4");

        List<String> listMappingNames = getMappingDAO().getListOfMappingNames("tidModel3");
        assertNotNull(listMappingNames);
        assertEquals(2, listMappingNames.size());
    }

    @Test
    public void testFindByModelName() {
        Model model1 = createModel("tidModel4", "desc4", "doc4", "sample4", "text/xml", "sample4");
        Mapping mapping = createMapping("TIDMAPPING-5", model1, tenantCode, "tid for model_15", "Sample MID Json5");
        mapping = createMapping("TIDMAPPING-6", model1, tenantCode, "tid for model_16", "Sample MID Json6");

        List<Mapping> listMapping = getMappingDAO().findByModelName("tidModel4");
        assertNotNull(listMapping);
        assertEquals(2, listMapping.size());
    }

    @Test
    public void testGetListOfMappingNamesById() {
        Model model1 = createModel("tidModel5", "desc5", "doc5", "sample5", "text/xml", "sample5");
        Mapping mapping = createMapping("TIDMAPPING-7", model1, tenantCode, "tid for model_17", "Sample MID Json7");
        mapping = createMapping("TIDMAPPING-8", model1, tenantCode, "tid for model_18", "Sample MID Json8");

        List<String> listMappingNames = getMappingDAO().getListOfMappingNamesById(model1.getId());
        assertNotNull(listMappingNames);
        assertEquals(2, listMappingNames.size());
    }

    @Test
    public void testFindListOfMappingsForTidCopy() {
        Model model1 = createModel("tidModel6", "desc6", "doc6", "sample6", "text/xml", "sample6");
        Mapping mapping = createMapping("TIDMAPPING-9", model1, tenantCode, "tid for model_19", "Sample MID Json9");

        List<String[]> listMappingNames = getMappingDAO().findListOfMappingsForTidCopy();
        assertNotNull(listMappingNames);
        String mappingNameFrmQry = ((Object[]) listMappingNames.get(0))[0].toString();
        assertEquals("TIDMAPPING-9", mappingNameFrmQry);
    }
    
    @Test
    public void testFindByModelNameAndStatus() {
        Model model1 = createModel("tidModel10", "desc10", "doc10", "sample10", "text/xml", "sample10");
        Mapping mapping = createMapping("TIDMAPPING-10", model1, tenantCode, "tid for model_110", "Sample MID Json 10");

        List<Mapping> listMapping = getMappingDAO().findByModelNameAndStatus("tidModel10",MappingStatus.FINALIZED.getMappingStatus());
        assertNotNull(listMapping);
        assertEquals(MappingStatus.FINALIZED.getMappingStatus(), listMapping.get(0).getStatus());
    }
    
    @Test
    public void testGetMappingStatus() {
        Model model1 = createModel("tidModel11", "desc11", "doc11", "sample11", "text/xml", "sample11");
        Mapping mapping = createMapping("TIDMAPPING-11", model1, tenantCode, "tid for model_111", "Sample MID Json 11");

        String status = getMappingDAO().getMappingStatus("TIDMAPPING-11");
        assertNotNull(status);
        assertEquals(MappingStatus.FINALIZED.getMappingStatus(), status);
    }

}
