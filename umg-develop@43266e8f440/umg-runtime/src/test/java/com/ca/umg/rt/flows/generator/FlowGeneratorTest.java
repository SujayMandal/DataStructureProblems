package com.ca.umg.rt.flows.generator;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Inject;
import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.rt.flows.container.ContainerManager;
import com.ca.umg.rt.repository.IntegrationFlow;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
public class FlowGeneratorTest {

    @Inject
    @Qualifier("flowContainerManager")
    private ContainerManager container;

    @Before
    public void init() {
        Properties properties = new Properties();
        properties.put(RequestContext.TENANT_CODE, "tenant1");
        new RequestContext(properties);
    }

    @Ignore
    @Test
    public final void test() throws SystemException, BusinessException, ServletException, IOException {
        container.start();

        IntegrationFlow flow = new IntegrationFlow();
        flow.setFlowName("Kumar");
        flow.setDescription("Testing ...");

        FlowMetaData flowMetaData = new FlowMetaData();
        flowMetaData.setModelName("My Model Name");

        QueryMetaData query1 = new QueryMetaData();
        query1.setId("Kumar");
        query1.setInputChannelName("InputChannelKumar");
        query1.setJdbcQueryId("kumar-jdbcquery");
        query1.setMaxRowsPerPoll(true);
        query1.setOutputChannelName("Kumaroutput channel");
        query1.setQueryResponseName("Kumarqueryresponse");
        query1.setReplyChannelName("KumarReplyChannelName");
        query1.setRequestChannelName("KumarRequestChannelName");
        query1.setRowMapperCondition(true);
        query1.setSql("\"SELECT EVERYTHING FROM MY TABLE  AND ENJOY URSELF  IN THE  HELL \"");

        QueryMetaData query2 = new QueryMetaData();
        query2.setId("Kumar");
        query2.setInputChannelName("InputChannelKumar");
        query2.setJdbcQueryId("kumar-jdbcquery");
        query2.setMaxRowsPerPoll(true);
        query2.setOutputChannelName("Kumaroutput channel");
        query2.setQueryResponseName("Kumarqueryresponse");
        query2.setReplyChannelName("KumarReplyChannelName");
        query2.setRequestChannelName("KumarRequestChannelName");
        query2.setRowMapperCondition(true);
        query2.setSql("\"SELECT EVERYTHING FROM MY TABLE  AND ENJOY URSELF  IN THE  HELL \"");

        QueryMetaData query3 = new QueryMetaData();
        query3.setId("Kumar");
        query3.setInputChannelName("InputChannelKumar");
        query3.setJdbcQueryId("kumar-jdbcquery");
        query3.setMaxRowsPerPoll(true);
        query3.setOutputChannelName("Kumaroutput channel");
        query3.setQueryResponseName("Kumarqueryresponse");
        query3.setReplyChannelName("KumarReplyChannelName");
        query3.setRequestChannelName("KumarRequestChannelName");
        query3.setRowMapperCondition(true);
        query3.setSql("\"SELECT EVERYTHING FROM MY TABLE  AND ENJOY URSELF  IN THE  HELL \"");

        flowMetaData.put(1, query1);
        flowMetaData.put(2, query2);
        flowMetaData.put(3, query3);

        String xml = FlowGenerator.generate(flowMetaData);

        Resource resource = new ByteArrayResource(xml.getBytes());
        flow.setResource(resource);
        flow.setFlowMetadata(flowMetaData);
        container.deployflow(flow , true);

        container.stop();
    }
}
