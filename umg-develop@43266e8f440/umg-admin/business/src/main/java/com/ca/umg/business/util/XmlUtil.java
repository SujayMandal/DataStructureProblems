/**
 * 
 */
package com.ca.umg.business.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author chandrsa
 * 
 */
public final class XmlUtil {

    private XmlUtil() {
    }

    public static Node getNode(String tagName, NodeList nodes) {
        Node resultNode = null;
        for (int x = 0; x < nodes.getLength(); x++) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                resultNode = node;
                break;
            }
        }
        return resultNode;
    }

    public static List<Node> getAllNodes(String tagName, NodeList nodes) {
        List<Node> nodeList = new ArrayList<>();
        for (int x = 0; x < nodes.getLength(); x++) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                nodeList.add(node);
            }
        }
        return nodeList;
    }

    public static String getNodeValue(Node node) {
        String nodeValue = StringUtils.EMPTY;
        Node data = null;
        NodeList childNodes = node.getChildNodes();
        for (int x = 0; x < childNodes.getLength(); x++) {
            data = childNodes.item(x);
            if (data.getNodeType() == Node.TEXT_NODE) {
                nodeValue = data.getNodeValue();
                break;
            }
        }
        return nodeValue;
    }

    public static String getFirstChildElementTag(Node node) {
        NodeList nodeList = null;
        String tagName = null;
        Node child = null;
        if (node != null) {
            nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                child = nodeList.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    tagName = child.getNodeName();
                    break;
                }
            }
        }
        return tagName;
    }

    /**
     * Prepares a map of node name and value for single level data. Do not use for nested elements.
     * 
     * @param nodes
     * @return
     */
    public static Map<String, String> getValues(NodeList nodes) {
        Map<String, String> values = null;
        Node node = null;
        if (nodes != null) {
            values = new HashMap<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    values.put(node.getNodeName(), XmlUtil.getNodeValue(node));
                }
            }
        }
        return values;
    }

    public static String getNodeValue(String tagName, NodeList nodes) {
        String nodeValue = StringUtils.EMPTY;
        for (int x = 0; x < nodes.getLength(); x++) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                NodeList childNodes = node.getChildNodes();
                for (int y = 0; y < childNodes.getLength(); y++) {
                    Node data = childNodes.item(y);
                    if (data.getNodeType() == Node.TEXT_NODE) {
                        nodeValue = data.getNodeValue();
                        break;
                    }
                }
            }
            if (StringUtils.isNotEmpty(nodeValue)) {
                break;
            }
        }
        return nodeValue;
    }

    public static Map<String, Object> getAttributeMap(Node node) {
        NamedNodeMap attrs = null;
        Map<String, Object> attributeData = new HashMap<>();
        if (node != null) {
            attrs = node.getAttributes();
            for (int y = 0; y < attrs.getLength(); y++) {
                Node attr = attrs.item(y);
                attributeData.put(attr.getNodeName(), attr.getNodeValue());
            }
        }
        return attributeData;
    }

    /**
     * NOT completed. Don't Use
     * 
     * @param cls
     * @param node
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static <T> T getNodeAttrForClass(Class<T> cls, Node node) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Object value = null;
        T returnObj = cls.newInstance();
        XmlAttribute annotation = null;
        Map<String, Object> attributeData = getAttributeMap(node);
        Method[] methods = cls.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(XmlAttribute.class)) {
                annotation = method.getAnnotation(XmlAttribute.class);
                switch (annotation.type()) {
                case BOOLEAN:
                    value = Boolean.parseBoolean(String.valueOf(attributeData.get(annotation.name())));
                    break;
                case DOUBLE:
                    value = Double.parseDouble(String.valueOf(attributeData.get(annotation.name())));
                    break;
                case INT:
                    value = Integer.parseInt(String.valueOf(attributeData.get(annotation.name())));
                    break;
                case LONG:
                    value = Long.parseLong(String.valueOf(attributeData.get(annotation.name())));
                    break;
                default:
                    value = attributeData.get(String.valueOf(annotation.name()));
                    break;
                }
                method.invoke(returnObj, value);
            }
        }
        return returnObj;
    }
}