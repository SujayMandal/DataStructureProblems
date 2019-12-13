/*
 * FlowGenerator.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.flows.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;

/**
 * 
 **/
public final class FlowGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlowGenerator.class);
    private static final String CHANNEL_IN = "Channel-In-";
    private static final String CHANNEL_OUT = "Channel-Out-";
    
    private FlowGenerator() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param flowMetaData
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws SystemException
     *             DOCUMENT ME!
     * @throws BusinessException
     *             DOCUMENT ME!
     **/
    public static String generate(FlowMetaData flowMetaData) throws SystemException, BusinessException {
        String channelTemplate = loadTemplate("templates/flow-channel-bit.xml");
        String enricherTemplate = loadTemplate("templates/flow-enricher-bit.xml");
        String mainTemplate = loadTemplate("templates/flow-template.xml");
        StringBuffer runningContent = new StringBuffer();

        int i = 1;
        String content = null;

        if (!flowMetaData.isEmpty()) {
            mainTemplate = StringUtils.replaceOnce(mainTemplate, "${first-connection-channel}", CHANNEL_IN + i);

            content = StringUtils.replaceOnce(channelTemplate, "${name}", CHANNEL_IN + i);
            runningContent.append(content);
        } else {
            content = StringUtils.replaceOnce(channelTemplate, "${name}", CHANNEL_IN + 0);
            runningContent.append(content);
            mainTemplate = StringUtils.replaceOnce(mainTemplate, "${first-connection-channel}", CHANNEL_IN + 0);
            mainTemplate = StringUtils.replaceOnce(mainTemplate, "${last-connection-channel}", CHANNEL_IN + 0);
        }

        Iterator<Integer> keys = flowMetaData.keySet().iterator();

        while (keys.hasNext()) {
            Integer sequence = keys.next();

            QueryMetaData metaData = (QueryMetaData) flowMetaData.get(sequence);
            content = StringUtils.replaceOnce(channelTemplate, "${name}", CHANNEL_OUT + i);
            runningContent.append(content);

            String enricherContent = StringUtils.replaceOnce(enricherTemplate, "${id}", metaData.getId());

            if (i == 1)// NOPMD
            {
                enricherContent = StringUtils.replace(enricherContent, "${input-channel-name}", CHANNEL_IN + i);
            } else {
                enricherContent = StringUtils.replace(enricherContent, "${input-channel-name}", CHANNEL_OUT + (i - 1));
            }

            enricherContent = StringUtils.replace(enricherContent, "${output-channel-name}", CHANNEL_OUT + i);
            enricherContent = StringUtils.replace(enricherContent, "${request-channel-name}", "Request-Channel-" + i);
            enricherContent = StringUtils.replace(enricherContent, "${reply-channel-name}", "Reply-Channel-" + i);
            
            enricherContent = StringUtils.replace(enricherContent, "${gateway-name}", "jdbcgateway" + i);

            enricherContent = StringUtils.replaceOnce(enricherContent, "${jdbc-query-id}", metaData.getJdbcQueryId());

            enricherContent = StringUtils.replaceOnce(enricherContent, "${query-response-name}", metaData.getQueryResponseName());

            enricherContent = StringUtils.replaceOnce(enricherContent, "${sql}", metaData.getSql());

            enricherContent = StringUtils.replaceOnce(enricherContent, "${max-rows-per-poll}",
                    metaData.isMaxRowsPerPoll() ? "max-rows-per-poll=\"0\"" : "max-rows-per-poll=\"1\"");

            enricherContent = StringUtils.replaceOnce(enricherContent, "${row-mapper-condition}",
                    metaData.isRowMapperCondition() ? "row-mapper=\"arrayRowMapper\"" : "");

            runningContent.append(enricherContent);

            ++i;
        }

        if (!flowMetaData.isEmpty()) {
            mainTemplate = StringUtils.replaceOnce(mainTemplate, "${last-connection-channel}", CHANNEL_OUT + (i - 1));
        }

        return StringUtils.replaceOnce(mainTemplate, "${stuffer}", runningContent.toString());
    }
    
    /**
     * @param flowMetaData
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    public static String generateBatchFlow(Map<String, String> batchMetaData) throws SystemException, BusinessException {
        String tempBatchFlow = loadTemplate("batchTemplates/batch-flow-template.xml");
        
        if(MapUtils.isNotEmpty(batchMetaData) && StringUtils.isNotBlank(tempBatchFlow)){
            for (Entry<String, String> metadata : batchMetaData.entrySet()) {
                tempBatchFlow = StringUtils.replace(tempBatchFlow, "${" + metadata.getKey() + "}", metadata.getValue());
            }
        }
        return tempBatchFlow;
    }

    /**
     * DOCUMENT ME!
     *
     * @param flowTemplateName
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws BusinessException
     *             DOCUMENT ME!
     * @throws SystemException
     *             DOCUMENT ME!
     **/
    private static String loadTemplate(String flowTemplateName) throws BusinessException, SystemException {
        InputStream is = null;
        byte[] b = null;

        try {
            is = FlowGenerator.class.getClassLoader().getResourceAsStream(flowTemplateName);
            b = new byte[is.available()];
            is.read(b);
        } catch (IOException e) {
            throw new SystemException(RuntimeExceptionCode.RSE000003, new Object[] { flowTemplateName }, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e)// NOPMD
                {
                    LOGGER.error("Error closing   the input  stream", e);
                }
            }
        }

        return new String(b);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args
     *            DOCUMENT ME!
     * @throws BusinessException
     * @throws SystemException
     *
     * @throws Exception
     *             DOCUMENT ME!
     **/
    public static void main(String[] args) throws SystemException, BusinessException {
        long st = System.currentTimeMillis();

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
        query1.setSql("\"SELECT EVERYTHING FROM MY TABLE  AND ENJOY URSELF  IN THE  HELL \"");

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
        query1.setSql("\"SELECT EVERYTHING FROM MY TABLE  AND ENJOY URSELF  IN THE  HELL \"");

        flowMetaData.put(1, query1);
        flowMetaData.put(2, query2);
        flowMetaData.put(3, query3);

        String xml = generate(flowMetaData);

        long en = System.currentTimeMillis();

        LOGGER.debug("Time taken --- > {}", (en - st));

        LOGGER.debug("xml --> \n {}", xml);
    }

    public static boolean validateXMLSchema(String xsdPath, String xmlPath) {
        boolean status = false;
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlPath)));
            status = true;
        } catch (IOException | SAXException e) {
            LOGGER.debug("Exception: {}", e.getMessage());
        }
        return status;
    }

    /**
     * @param flowMetaData
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    public static String generateParentBatchFlow(Map<String, String> parentBatchData) throws SystemException,
            BusinessException {
        String tempBatchFlow = loadTemplate("/parent-batch-integration-context.xml");

        if (MapUtils.isNotEmpty(parentBatchData) && StringUtils.isNotBlank(tempBatchFlow)) {
            for (Entry<String, String> metadata : parentBatchData.entrySet()) {
                tempBatchFlow = StringUtils.replace(tempBatchFlow, "${" + metadata.getKey() + "}", metadata.getValue());
            }
        }
        return tempBatchFlow;
    }
    
    /**
     * @param flowMetaData
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    public static String generateFlow(Map<String, String> flowData, String resourcePath) throws SystemException,
            BusinessException {
        String tempBatchFlow = loadTemplate(resourcePath);

        if (MapUtils.isNotEmpty(flowData) && StringUtils.isNotBlank(tempBatchFlow)) {
            for (Entry<String, String> metadata : flowData.entrySet()) {
                tempBatchFlow = StringUtils.replace(tempBatchFlow, "${" + metadata.getKey() + "}", metadata.getValue());
            }
        }
        return tempBatchFlow;
    }
}
