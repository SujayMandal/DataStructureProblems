/**
 * 
 */
package com.ca.umg.business.mid.extraction.xml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Named;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mid.extraction.MidExtractor;
import com.ca.umg.business.mid.extraction.info.DatatypeInfo;
import com.ca.umg.business.mid.extraction.info.MidIOInfo;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.util.XmlUtil;

/**
 * @author chandrsa
 * 
 */
@Named
@SuppressWarnings("PMD")
public class MidXmlExtractor implements MidExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MidXmlExtractor.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.mid.extraction.MidExtractor#extractModelIO(java.io .InputStream)
     */
    @Override
    public MidIOInfo extractModelIO(InputStream modelIODefinition) throws BusinessException, SystemException {
        MidIOInfo midIOInfo = new MidIOInfo();
        DocumentBuilder builder = null;
        DocumentBuilderFactory factory = null;
        Document document = null;
        NodeList rootNodeList = null;
        NodeList childNodes = null;
        Node transfer = null;

        Node metadata = null;
        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            document = builder.parse(modelIODefinition);
            rootNodeList = document.getDocumentElement().getChildNodes();

            metadata = XmlUtil.getNode("metadata", rootNodeList);
            childNodes = metadata.getChildNodes();
            if (metadata != null && childNodes != null) {
                midIOInfo.setMetadata(XmlUtil.getValues(childNodes));
            }

            transfer = XmlUtil.getNode("input", rootNodeList);
            midIOInfo.setMidInput(getMappingParams(transfer, StringUtils.EMPTY));

            transfer = XmlUtil.getNode("output", rootNodeList);
            midIOInfo.setMidOutput(getMappingParams(transfer, StringUtils.EMPTY));

        } catch (ParserConfigurationException | SAXException | IOException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException | SystemException e) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000045, new Object[] { e.getLocalizedMessage() }, e);
        }
        return midIOInfo;
    }

    private List<MidParamInfo> getMappingParams(Node node, String parentName) throws InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, SystemException {
        List<MidParamInfo> paramInfos = null;
        List<Node> nodes = null;
        if (node != null) {
            paramInfos = new ArrayList<>();
            nodes = XmlUtil.getAllNodes("parameter", node.getChildNodes());
            for (Node paramNode : nodes) {
                paramInfos.add(createParam(paramNode, parentName));
            }
        }
        return paramInfos;
    }

    private MidParamInfo createParam(Node paramNode, String parentName) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SystemException {
        MidParamInfo paramInfo = null;
        Node datatype = null;

        if (paramNode != null) {
            paramInfo = XmlUtil.getNodeAttrForClass(MidParamInfo.class, paramNode);
            if(paramInfo.getName()!=null && paramInfo.getApiName()==null){
            	paramInfo.setApiName(paramInfo.getName());
            	paramInfo.setModelParamName(paramInfo.getName());            	
            }
            if (StringUtils.isNotEmpty(paramInfo.getAcceptableValues())) {
            	paramInfo.setAcceptableValueArr(getDefaultArray(paramInfo.getAcceptableValues(), paramInfo.getDataTypeStr()));
            }
          
            paramInfo.setFlatenedName(getFlatenedName(parentName, paramInfo.getApiName()));
            datatype = XmlUtil.getNode("datatype", paramNode.getChildNodes());
            paramInfo.setDatatype(readDatatype(datatype));
            getChildren(paramInfo, datatype);
        }
        return paramInfo;
    }

    private void getChildren(MidParamInfo paramInfo, Node datatype) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SystemException {
        Node arrayNode = null;
        Node type = null;
        Node object = null;
        Node objectProperties = null;
        if (StringUtils.equalsIgnoreCase(paramInfo.getDatatype().getType(), "object")) {
            if (paramInfo.getDatatype().isArray()) {
                arrayNode = XmlUtil.getNode("array", datatype.getChildNodes());
                type = XmlUtil.getNode("type", arrayNode.getChildNodes());
                object = XmlUtil.getNode("object", type.getChildNodes());
            } else {
                object = XmlUtil.getNode("object", datatype.getChildNodes());
            }
        }
        if (object != null && object.getChildNodes() != null) {
            objectProperties = XmlUtil.getNode("properties", object.getChildNodes());
            paramInfo.setChildren(getMappingParams(objectProperties, paramInfo.getFlatenedName()));
        }
    }

    private DatatypeInfo readDatatype(Node datatype) throws SystemException {
        DatatypeInfo datatypeInfo = null;
        Node type = null;
        if (datatype != null) {
            datatypeInfo = new DatatypeInfo();
            if (StringUtils.equalsIgnoreCase(XmlUtil.getFirstChildElementTag(datatype), "array")) {
                datatypeInfo.setArray(BusinessConstants.TRUE);
                parseArray(datatypeInfo, datatype.getChildNodes());
            } else {
                datatypeInfo.setType(XmlUtil.getFirstChildElementTag(datatype));
                type = XmlUtil.getNode(datatypeInfo.getType(), datatype.getChildNodes());
                datatypeInfo.setProperties(XmlUtil.getAttributeMap(type));
            }
        }
        return datatypeInfo;
    }

    private DatatypeInfo parseArray(DatatypeInfo datatypeInfo, NodeList nodeList) throws SystemException {
        Node arrayNode = null;
        Node dimensions = null;
        Node arrayType = null;
        Node type = null;
        Node defaultValue = null;
        NodeList arrayNodes = null;
        Node numeric = null;
        String val = null;
        if (nodeList != null) {
            arrayNode = XmlUtil.getNode("array", nodeList);
            arrayNodes = arrayNode.getChildNodes();
            dimensions = XmlUtil.getNode("dimensions", arrayNodes);
            type = XmlUtil.getNode("type", arrayNodes);
            defaultValue = XmlUtil.getNode("defaultValue", arrayNodes);
            if (defaultValue != null && defaultValue.hasChildNodes()) {
                val = XmlUtil.getNodeValue(defaultValue);
            }

            datatypeInfo.setType(XmlUtil.getFirstChildElementTag(type));
            if (StringUtils.equalsIgnoreCase(datatypeInfo.getType(), "numeric")) {
                numeric = XmlUtil.getNode("numeric", type.getChildNodes());
                if (numeric != null && numeric.getChildNodes() != null) {
                    datatypeInfo.getProperties().put("numerictype", XmlUtil.getNodeValue("numerictype", numeric.getChildNodes()));
                }
            } else {
                arrayType = XmlUtil.getNode(datatypeInfo.getType(), type.getChildNodes());
                datatypeInfo.setProperties(XmlUtil.getAttributeMap(arrayType));
            }
            Node length = XmlUtil.getNode("length", arrayNodes);
            if(length!=null){
            	datatypeInfo.getProperties().put("length",length.getTextContent());
            }
            datatypeInfo.getProperties().put("dimensions", getDimension(dimensions));
            datatypeInfo.getProperties().put("defaultValue",
                    getDefaultArray(val, StringUtils.upperCase(datatypeInfo.getType(), Locale.getDefault())));
        }
        return datatypeInfo;
    }

    private List<Integer> getDimension(Node dimension) {
        List<Integer> dimensions = new ArrayList<>();
        Node dim = null;
        NodeList dimensionNodes = dimension.getChildNodes();
        for (int i = 0; i < dimensionNodes.getLength(); i++) {
            dim = dimensionNodes.item(i);
            if (dim.getNodeType() == Node.ELEMENT_NODE && dim.getNodeName().equalsIgnoreCase("dim")) {
                dimensions.add(Integer.parseInt(XmlUtil.getNodeValue(dim)));
            }
        }
        return dimensions;
    }

    private String getFlatenedName(String parentName, String paramName) {
        String flatenedName = null;
        StringBuffer buff = null;
        if (StringUtils.isNotBlank(parentName)) {
            buff = new StringBuffer(parentName).append("/").append(paramName);
            flatenedName = buff.toString();
        } else {
            flatenedName = paramName;
        }
        return flatenedName;
    }

    private Object[] getDefaultArray(String arrayStr, String arrayType) throws SystemException {
        Object[] convertedArray = null;
        if (StringUtils.isNotBlank(arrayStr)) {
            try {
                Context context = Context.enter();
                Scriptable scope = context.initStandardObjects();
                Object result = context.evaluateString(scope, arrayStr, "<cmd>", 1, null);
                NativeArray arr = (NativeArray) result;
                Object[] array = new Object[(int) arr.getLength()];
                convertedArray = convertToArray(arr, array, arrayType);
            } catch (EcmaError ecmaErr) {
                LOGGER.error("Error while parsing default array value", ecmaErr);
                SystemException.newSystemException(BusinessExceptionCodes.BSE000137, new Object[] { ecmaErr.getErrorMessage() });
            } catch (Exception e) {// NOPMD
                LOGGER.error("Error while parsing default array value", e);
                SystemException.newSystemException(BusinessExceptionCodes.BSE000137, new Object[] {}, e);
            }
        }
        return convertedArray;
    }

    private Object[] convertToArray(NativeArray arr, Object[] array, String dataType) {
        for (Object o : arr.getIds()) {
            int index = (Integer) o;
            if (arr.get(index, null) instanceof NativeArray) {
                NativeArray childarr = (NativeArray) arr.get(index, null);
                Object[] childArray = new Object[(int) childarr.getLength()];
                array[index] = convertToArray(childarr, childArray, dataType);
            } else {
                if (StringUtils.equalsIgnoreCase(dataType, "DOUBLE")) {
                    if (arr.get(index, null) != null) {
                        array[index] = Double.parseDouble(arr.get(index, null).toString());
                    } else {
                        array[index] = arr.get(index, null);
                    }
                } else if (StringUtils.equalsIgnoreCase(dataType, "INTEGER")) {
                    if (arr.get(index, null) instanceof Double) {
                        array[index] = new Double(Double.parseDouble(arr.get(index, null).toString())).intValue();
                    } else {
                        array[index] = arr.get(index, null);
                    }
                }  else if (StringUtils.equalsIgnoreCase(dataType, "BIGINTEGER") ) {
                    if (arr.get(index, null) instanceof Double) {                    	
                        array[index] = new BigDecimal(Double.parseDouble(arr.get(index, null).toString())).toBigInteger();
                    } else {
                        array[index] = arr.get(index, null);
                    }
                }else if(StringUtils.equalsIgnoreCase(dataType, "LONG") ){
                	if (arr.get(index, null) instanceof Double) {                    	
                        array[index] = new Double(Double.parseDouble(arr.get(index, null).toString())).longValue();
                    } else {
                        array[index] = arr.get(index, null);
                    }
                }
                else {
                    array[index] = arr.get(index, null);
                }
            }
        }
        return array;
    }

}
