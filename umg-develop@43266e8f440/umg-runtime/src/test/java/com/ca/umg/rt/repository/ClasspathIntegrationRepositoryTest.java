package com.ca.umg.rt.repository;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ClasspathIntegrationRepositoryTest {

	@Autowired
	IntegrationRepository integrationRepository;
	
	@Ignore
    @Test
	public void testLoadIntegrationFlow() throws SystemException, BusinessException {
		assertTrue(integrationRepository.loadIntegrationFlow().size()>0);
	}
	
	@Ignore
    @Test
	public void testLoadIntegrationFlowAllPath() throws SystemException, BusinessException {
		ClasspathIntegrationRepository repo = (ClasspathIntegrationRepository)integrationRepository;
		repo.setFlowPattern("classpath*:com/ca/umg/rt/repository/**/*-integration-flow.xml");
		assertTrue(integrationRepository.loadIntegrationFlow().size()>0);
	}
	
	@Ignore
    @Test
	public void testLoadIntegrationFlowRooWildSearch() throws SystemException, BusinessException {
		ClasspathIntegrationRepository repo = (ClasspathIntegrationRepository)integrationRepository;
		repo.setFlowPattern("classpath*:**/*-integration-flow.xml");
		assertTrue(integrationRepository.loadIntegrationFlow().size()>0);
	}
}
