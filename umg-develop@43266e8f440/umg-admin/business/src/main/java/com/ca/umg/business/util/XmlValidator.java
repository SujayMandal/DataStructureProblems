package com.ca.umg.business.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;

/**
 * Utility class to validate xml against xsd.
 * 
 * @author devasiaa
 *
 */
public final class XmlValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlValidator.class);

    private XmlValidator() {
    }

    /**
     * Validates xml against xsd.
     * 
     * @param xsd
     * @param xml
     * @return
     * @throws SystemException
     */
    public static boolean validate(InputStream xsd, InputStream xml) throws SystemException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try {
            schema = schemaFactory.newSchema(new StreamSource(xsd));
        } catch (SAXException e) {
            throw new SystemException(BusinessExceptionCodes.BSE000006, new Object[] { e.getLocalizedMessage() }, e);
        }

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setSchema(schema);
        docFactory.setNamespaceAware(true);

        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new SystemException(BusinessExceptionCodes.BSE000006, new Object[] { e.getLocalizedMessage() }, e);
        }
        docBuilder.setErrorHandler(new ErrorHandler() {
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }

            public void warning(SAXParseException exception) throws SAXException {
                throw exception;
            }
        });
        boolean isValid = false;
        try {
            docBuilder.parse(xml);
            isValid = true;
        } catch (SAXException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            isValid = false;
        } catch (IOException e) {
            throw new SystemException(BusinessExceptionCodes.BSE000006, new Object[] { e.getLocalizedMessage() }, e);
        }
        return isValid;
    }
}
