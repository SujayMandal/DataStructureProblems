package com.ca.umg.rt.core.deployment.bo;

import java.util.Properties;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.rt.core.deployment.info.DeploymentDescriptor;

//@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
//@RunWith(SpringJUnit4ClassRunner.class)
public class DeploymentBOImplTest  {
    
    @Inject
    private DeploymentBO deploymentBO;
    
    @Before
    public void setup() {
        Properties properties = new Properties();
        properties.put(RequestContext.TENANT_CODE, "localhost");
        new RequestContext(properties);
    }
    
    @Ignore
    @Test
    public void deployTest() {
        DeploymentDescriptor deploymentDescriptor = new DeploymentDescriptor();
        deploymentDescriptor.setName("version");
        deploymentDescriptor.setMajorVersion(1);
        deploymentDescriptor.setMinorVersion(0);
        try {
            deploymentBO.deploy(deploymentDescriptor);
        } catch (SystemException | BusinessException e) {
            Assert.fail();
        }
    }

}
