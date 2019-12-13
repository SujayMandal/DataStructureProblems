/**
 * 
 */
package com.ca.umg.rt.flows.container;

import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.repository.IntegrationFlow;
import com.ca.umg.rt.repository.IntegrationRepository;
import com.ca.umg.rt.support.RuntimeXmlApplicationContext;

/**
 * @author chandrsa
 *
 */
public class CommonWrapperDeployer implements WrapperDeployer {
    
    private String name;
    private String wrapperType;
    private IntegrationRepository integrationRepository;
    private ApplicationContext parentContext;
    private Resource wrapperResource;

    @Override
    public ApplicationContext deployWrapper(String wrapperType) throws SystemException, BusinessException {
        Properties properties = null;
        ApplicationContext wrapperContext = null;
        this.wrapperType = wrapperType;
        IntegrationFlow integrationFlow = integrationRepository.loadWrapperDetail(wrapperType);
        if(integrationFlow != null){
            properties = new Properties();
            wrapperResource = integrationFlow.getResource();
            wrapperContext = createApplicationContext(properties);
        }
        return wrapperContext;
    }
    
    private ApplicationContext createApplicationContext(Properties properties) {
        ConfigurableApplicationContext context = new RuntimeXmlApplicationContext(wrapperResource);

        ConfigurableEnvironment environment = new StandardEnvironment();
        final PropertiesPropertySource propertySoruce = new PropertiesPropertySource("umgBatchWrapperProp", properties);
        environment.getPropertySources().addLast(propertySoruce);
        context.setEnvironment(environment);
        context.setId(this.name + "--" + this.wrapperType);
        context.setParent(parentContext);
        context.refresh();
        context.registerShutdownHook();
        return context;
    }

    @Override
    public void setIntegrationRepository(IntegrationRepository integrationRepository) {
        this.integrationRepository = integrationRepository;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        
    }

    @Override
    public void setParentContext(ApplicationContext parentContext) {
        this.parentContext = parentContext;
    }
}