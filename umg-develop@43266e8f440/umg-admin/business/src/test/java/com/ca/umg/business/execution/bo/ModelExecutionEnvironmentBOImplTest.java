/**
 * 
 */
package com.ca.umg.business.execution.bo;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.execution.dao.ModelExecutionEnvironmentDAO;
import com.ca.umg.business.execution.entity.ModelExecutionEnvironment;

/**
 * @author nigampra
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class ModelExecutionEnvironmentBOImplTest {
	
	@InjectMocks
    private ModelExecutionEnvironmentBO meeBO = new ModelExecutionEnvironmentBOImpl();
	
	@Mock
    private ModelExecutionEnvironmentDAO executionEnvDAO;
	
	private static ModelExecutionEnvironment rEnv;
	
	@Before
    public void setup() {
        initMocks(this);
        rEnv = buildModelExecutionEnvironment("R", "3.1.2");
    }
	
	@Test
	public void testGetModelExecutionEnvironment() throws BusinessException, SystemException{
		when(executionEnvDAO.findByExecutionEnvironmentAndEnvironmentVersion("R","3.1.2")).thenReturn(rEnv);
		ModelExecutionEnvironment mee = meeBO.getModelExecutionEnvironment("R", "3.1.2");
		Assert.assertNotNull(mee);
	    Assert.assertEquals("R", mee.getExecutionEnvironment());
	    Assert.assertEquals("3.1.2", mee.getEnvironmentVersion());
		
	}
	
	private ModelExecutionEnvironment buildModelExecutionEnvironment(String executionEnvironment, String environmentVersion){
		ModelExecutionEnvironment mee = new ModelExecutionEnvironment();
		mee.setExecutionEnvironment(executionEnvironment);
		mee.setEnvironmentVersion(environmentVersion);
		return mee;
	}

}
