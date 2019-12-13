package com.ca.umg.rt.core.deployment.dao;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;

//@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
//@RunWith(SpringJUnit4ClassRunner.class)
public class DeploymentDAOTest {

    @Inject
    private DeploymentDAOImpl deploymentDAO;
    
    @Ignore
    @Test
    public void getFlowDataTest() {
        /*MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue("NAME", "UMG_NAME", Types.VARCHAR);
        valueMap.addValue("MAJOR_VERSION", "1", Types.INTEGER);
        valueMap.addValue("MINOR_VERSION", "1", Types.INTEGER);
        FlowMetaData flowMetaData = deploymentDAO.getFlowData(valueMap);
        Assert.assertNotNull(flowMetaData);
        Assert.assertEquals("UMG_NAME", flowMetaData.getModelName());*/
    }
    
    @Ignore
    @Test
    public void getQueryMetaDataTest() {
        /*MapSqlParameterSource valueMap = new MapSqlParameterSource();
        valueMap.addValue("NAME", "UMG_NAME", Types.VARCHAR);
        Map<Integer, QueryMetaData> queries = deploymentDAO.getQueryMetaData(valueMap);
        Assert.assertTrue(MapUtils.isNotEmpty(queries));*/
    }
    
}
