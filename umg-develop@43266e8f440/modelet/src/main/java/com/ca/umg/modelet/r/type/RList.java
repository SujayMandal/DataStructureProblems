/*
 * RList.java
 * Author: Manasi Seshadri (manasi.seshadri@altisource.com)
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.modelet.r.type;

import static com.ca.framework.core.ioreduce.DataTypeValueEnum.getDataTypeValueEnum;
import static com.ca.framework.core.ioreduce.NativeDataTypeValueEnum.getNativeDataTypeValueEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rosuda.REngine.REXP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.ioreduce.BooleanValueEnum;
import com.ca.framework.core.ioreduce.FieldInfoEnum;
import com.ca.umg.modelet.common.FieldInfo;
import com.ca.umg.modelet.exception.ModeletExceptionCodes;
import com.ca.umg.modelet.lang.type.DataType;
import com.ca.umg.modelet.lang.type.LangTypeConstants;

/**
 * JSON representation
 * 
 * HashMap - Required - data, rDataType Optional - names
 * 
 * "payload":[ { "fieldName":"inList", "sequence":1, "dataType":"list", "collection":false, "value": { "data": [ { "data":
 * [12,3,4], "type": "integer", "rDataType": "vector" }, { "data": [2.3,-4.5,4.7], "type": "numeric", "rDataType": "vector" }, {
 * "data": ["abc","def","ghi","jkl", "mno"], "type": "character", "rDataType": "vector" }, { "data": [false, true,false,true],
 * "type": "logical", "rDataType": "vector" }, 15, "teststring", true, 67.6 ],
 * 
 * "names": [null, null, "name1", null, "name2", "name3", null, "name4"],
 * 
 * "rDataType":"list" } } ]
 * 
 * R representation: Usage: list(...) Eg: list( c(4,8.5,2,5,8), c(1,3,5), c("abc", "def", "efg"), c((TRUE), (TRUE), (TRUE),
 * (TRUE)), c(complex(real=5,imaginary=7), complex(real=7, imaginary=-4), complex(real=0, imaginary=2), complex(real=8,
 * imaginary=0)), named1 = c(5,7,3,5), named2 = c((TRUE), (TRUE), (TRUE), (TRUE)), named3 = c("qwerty", "asdfgh", "sdfsdf",
 * "okhefef"), 45, 5.6, (TRUE), "john", complex(real=4, imaginary=8), list (c(1,2,3), 6, 7), matrix(c(1,4,6,8), nrow=2, ncol=2),
 * array (1:27, c(3,3,3)) )
 * 
 **/
@SuppressWarnings("PMD")
public class RList extends AbstractRType {
    private static final Logger LOGGER = LoggerFactory.getLogger(RList.class);
    private List<DataType> list;
    private List<String> names;
    private boolean stringsAsFactors;

    /**
     * Creates a new RList object.
     *
     * @param objs
     *            - List of plain Object representation of components
     * @param names
     *            - Optional list of names of components for named list
     *
     * @throws BusinessException
     *             - if names are supplied and number of names are not equal to list length
     * 
     **/
    public RList(final List<?> objs, final List<String> names, final boolean stringsAsFactors) throws BusinessException {
        super();
        this.stringsAsFactors = stringsAsFactors;
        if (objs != null && !objs.isEmpty() && objs.get(0) instanceof AbstractRType) {
            list = (List<DataType>) objs;
        } else {
            list = new ArrayList<DataType>();

            for (Object obj : objs) {
                list.add(AbstractRType.createRDataTypeFromObject(new FieldInfo(obj), stringsAsFactors));
            }
        }

        if (names != null && list.size() != names.size()) {
            LOGGER.debug("In RList names is null or list and names size is not matching");
            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "names" });
        }
    }

    /**
     * Creates a new RList object.
     *
     * @param output
     *            - REXP from Rengine.eval
     *
     * @throws BusinessException
     *             - if REXP cannot be represented asList or asVector (at least one must work)
     **/
    public RList(final REXP output) throws BusinessException {
        super();
        LOGGER.debug("Entered RList output is {}  : ", output);
        list = new ArrayList<DataType>();
        try {
            org.rosuda.REngine.RList rList = output.asList();
            for (int i = 0; i < rList.capacity(); i++) {
                AbstractRType subtype = AbstractRType.createRDataTypeFromREXP(rList.at(i));
                list.add(subtype);
            }
        } catch (Exception e) {
            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "REXP" });
        }

        /*
         * org.rosuda.JRI.RList jrilist = output.asList(); RVector jrivector = output.asVector();
         * 
         * if (jrilist == null && jrivector == null) { LOGGER.debug("In RList jrilist and jrivector are null"); throw new
         * BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "REXP" }); }
         * 
         * String[] keys = null;
         * 
         * if (jrilist != null) { keys = jrilist.keys(); }
         * 
         * list = new ArrayList<DataType>();
         * 
         * int length = 0; boolean isList = true;
         * 
         * if (keys == null || keys.length == 0) { if (jrivector != null) { length = jrivector.size(); } else { LOGGER.debug(
         * "In RList jrivector is null"); // jrilist is not null but not useful, in this case we need jrivector to have the data
         * throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "REXP" }); } isList
         * = false; } else { length = keys.length; }
         * 
         * for (int x = 0; x < length; x++) { REXP subout = isList ? jrilist.at(x) : jrivector.at(x);
         * 
         * if (isList) { if (names == null) { names = new ArrayList<String>(); }
         * 
         * names.add(keys[x]); }
         * 
         * AbstractRType subtype = AbstractRType.createRDataTypeFromREXP(subout); list.add(subtype); }
         */
    }

    public RList(final org.rosuda.JRI.REXP output) throws BusinessException {
        super();
        LOGGER.debug("Entered RList output is {}  : ", output);
        org.rosuda.JRI.RList jrilist = output.asList();
        org.rosuda.JRI.RVector jrivector = output.asVector();

        if (jrilist == null && jrivector == null) {
            LOGGER.debug("In RList jrilist and jrivector are null");
            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "REXP" });
        }

        String[] keys = null;

        if (jrilist != null) {
            keys = jrilist.keys();
        }

        list = new ArrayList<DataType>();

        int length = 0;
        boolean isList = true;

        if (keys == null || keys.length == 0) {
            if (jrivector != null) {
                length = jrivector.size();
            } else {
                LOGGER.debug("In RList jrivector is null");
                // jrilist is not null but not useful, in this case we need jrivector to have the data
                throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "REXP" });
            }
            isList = false;
        } else {
            length = keys.length;
        }

        for (int x = 0; x < length; x++) {
            org.rosuda.JRI.REXP subout = isList ? jrilist.at(x) : jrivector.at(x);

            if (isList && keys != null) {
                if (names == null) {
                    names = new ArrayList<String>();
                }

                names.add(keys[x]);
            }

            AbstractRType subtype = AbstractRType.createRDataTypeFromREXP(subout);
            list.add(subtype);
        }
    }

    /**
     * Creates a new RList object.
     *
     * @param hmValue
     *            - HashMap
     *
     * @throws BusinessException
     *             - if data or rDataType is null or malformed - if map is null
     * 
     **/
    public RList(final Map<String, Object> hmValue, final boolean stringsAsFactors) throws BusinessException {
        super();
        this.stringsAsFactors = stringsAsFactors;
        list = new ArrayList<DataType>();
        names = new ArrayList<String>();

        if (hmValue != null) {
            Object oType = hmValue.get(LangTypeConstants.R_DATA_TYPE);
            Object oData = hmValue.get(LangTypeConstants.R_LIST_DATA);
            Object oNames = hmValue.get(LangTypeConstants.R_LIST_NAMES);

            if (oData instanceof List<?> && oType instanceof String && oType.equals(getRDataType().getName())) {
                List<Object> objs = (List<Object>) oData;

                for (Object obj : objs) {
                    list.add(AbstractRType.createRDataTypeFromObject(new FieldInfo(obj), stringsAsFactors));
                }

                // names are completely optional so just ignore if there is nothing in the HashMap
                if (oNames instanceof List<?> && !((List<?>) oNames).isEmpty()) {
                    List<Object> lstNames = (List<Object>) oNames;

                    if (lstNames.size() != list.size()) {
                        LOGGER.debug("Received malformed list as names for RList, ignoring names");
                    } else {
                        for (Object item : lstNames) {
                            if (item == null) {
                                names.add(null);
                            } else if (item instanceof String) {
                                names.add((String) item);
                            } else {
                                LOGGER.debug("Received malformed item of type " + item.getClass().toString()
                                        + " in names for RList, ignoring item");
                                names.add(null);
                            }
                        }
                    }
                }
            } else {
                LOGGER.debug("In RList odata not instance of list, string");
                throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(),
                        LangTypeConstants.R_LIST_DATA + "/" + LangTypeConstants.R_DATA_TYPE });
            }
        } else {
            LOGGER.debug("In RList hmvalue is null");
            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "HashMap" });
        }
    }

    public RList(final Map<String, Object> hmValue) throws BusinessException {
        super();
        list = new ArrayList<DataType>();
        names = new ArrayList<String>();

        if (hmValue != null) {
            Object oData = hmValue.get("value");

            list.add(AbstractRType.createRDataTypeFromObject(new FieldInfo(hmValue), stringsAsFactors));
        } else {
            LOGGER.debug("In RList hmvalue is null");
            throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "HashMap" });
        }
    }

    /**
     * String representation
     *
     * @return String
     **/
    @Override
    public String toNative() {
        String strRet = null;

        if (list.isEmpty()) {
            String brackets = "()";
            strRet = getRDataType().getName() + brackets;
        } else {
            String x = "(";
            StringBuffer sb = new StringBuffer(getRDataType().getName());
            sb.append(x);

            int intSize = list.size();
            int index = 0;

            for (DataType dt : list) {
                if (names != null && !names.isEmpty() && names.get(index) != null) {
                    String eq = "=";
                    sb.append(names.get(index)).append(eq);
                }

                if (dt == null) {
                    String na = "NA";
                    sb.append(na);
                } else {
                    sb.append(dt.toNative());
                }

                if (index < intSize - 1) {
                    String comma = ",";
                    sb.append(comma);
                }

                index++;
            }

            String bracket = ")";
            sb.append(bracket);
            strRet = sb.toString();
        }

        return strRet;
    }

    /**
     * returns false
     *
     * @return false
     **/
    @Override
    public boolean isPrimitive() {
        return false;
    }

    /**
     * returns "list"
     *
     * @return "list"
     **/
    @Override
    public RDataTypes getRDataType() {
        return RDataTypes.R_LIST;
    }

    /**
     * Returns Plain Object Representation HashMap
     *
     * @return HashMap
     **/
    // @Override
    // public Object toJava()
    // {
    // HashMap<String, Object> hmValues = new HashMap<String, Object>();
    // hmValues.put(LangTypeConstants.R_DATA_TYPE, getRDataType().getName());
    // hmValues.put(LangTypeConstants.R_LIST_NAMES, names);
    //
    // List<Object> objs = new ArrayList<Object>();
    //
    // for (DataType type : list)
    // {
    // objs.add(type.toJava());
    // }
    //
    // hmValues.put(LangTypeConstants.R_LIST_DATA, objs);
    //
    // return hmValues;
    // }

    @Override
    public FieldInfo toUmgType(final String name, final String sequence) {
        final FieldInfo fi = new FieldInfo();
        fi.setCollection(false);
        fi.setDataType(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType());
        fi.setModelParameterName(name);
        fi.setSequence(sequence);
        fi.setNativeDataType(getRDataType().getName());

        final List<FieldInfo> objs = new ArrayList<FieldInfo>();

        int subSequence = 1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null) {
                objs.add(list.get(i).toUmgType(name, String.valueOf(subSequence)));
            } else {
                objs.add(null);
            }
            subSequence++;
        }

        final ArrayList<Object> alValues = new ArrayList<Object>();

        FieldInfo f = new FieldInfo();
        f.setCollection(false);
        f.setDataType(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType());
        f.setModelParameterName(LangTypeConstants.R_LIST_DATA);
        f.setSequence("1");
        f.setValue(objs.toArray());

        alValues.add(f);

        if (names != null) {
            f = new FieldInfo();
            f.setCollection(false);
            f.setDataType(com.ca.umg.modelet.common.DataType.STRING.getUmgType());
            f.setModelParameterName(LangTypeConstants.R_LIST_NAMES);
            f.setSequence("2");
            f.setValue(names.toArray());

            alValues.add(f);
        }

        fi.setValue(alValues.toArray());

        return fi;
    }

    @Override
    public Object getPrimitive() {
        // TODO Auto-generated method stub
        return null;
    }

    public RList(final List<Object> hmValue, final boolean stringsAsFactors) throws BusinessException {
        super();
        this.stringsAsFactors = stringsAsFactors;
        if (hmValue != null) {
            HashMap<String, Object> attribute;
            for (final Object objAttribute : hmValue) {
                if (objAttribute instanceof List) {
                    List<HashMap<String, Object>> list1 = (List<HashMap<String, Object>>) objAttribute;
                    for (int i = 0; i < list1.size(); i++) {
                        HashMap<String, Object> hm = (HashMap<String, Object>) list.get(i);
                        attribute = (HashMap<String, Object>) objAttribute;
                        if (attribute.get("modelParameterName").toString().equalsIgnoreCase(LangTypeConstants.R_LIST_DATA)) {
                            list = marshallData((List<Object>) attribute.get("value"));
                        } else if (attribute.get("modelParameterName").toString()
                                .equalsIgnoreCase(LangTypeConstants.R_LIST_NAMES)) {
                            names = convertValueToList((List<Object>) attribute.get("value"));
                        }
                    }
                } else {
                    attribute = (HashMap<String, Object>) objAttribute;
                    if (attribute.get("modelParameterName").toString().equalsIgnoreCase(LangTypeConstants.R_LIST_DATA)) {
                        if ((boolean) attribute.get("collection")) {
                            list = new ArrayList<DataType>();
                            List<Object> arrayObjs = (List<Object>) attribute.get("value");
                            for (Object object : arrayObjs) {
                                List<DataType> dataTypes = marshallData((List<Object>) object);
                                list.add(new RList(dataTypes));
                            }
                        } else {
                            list = marshallData((List<Object>) attribute.get("value"));
                        }
                    } else if (attribute.get("modelParameterName").toString().equalsIgnoreCase(LangTypeConstants.R_LIST_NAMES)) {
                        names = convertValueToList((List<Object>) attribute.get("value"));
                    }
                }
            }
        } else {
            LOGGER.debug("In RList List,stringsAsFactors hmvalue is null");
            throw new BusinessException(ModeletExceptionCodes.MOBE000002,
                    new String[] { this.getClass().getName(), "FieldInfo value" });
        }
    }

    public RList(List<DataType> datatypes) {
        this.list = datatypes;
    }

    private List<DataType> marshallData(final List<Object> hmValue) throws BusinessException {
        final List<DataType> obj = new ArrayList<DataType>();

        for (Object value : hmValue) {
            if (value instanceof List) {
                List<Object> listValue = (List<Object>) value;
                for (int i = 0; i < listValue.size(); i++) {
                    obj.add(createRDataTypeFromObject(new FieldInfo(listValue.get(i)), stringsAsFactors));
                }
            } else {
                // AbstractRType abstractRType = createRDataTypeFromObject(new FieldInfo(value), stringsAsFactors);
                // if (StringUtils.equalsIgnoreCase((String) ((Map) value).get("nativeDataType"), "list")
                // && (Boolean) ((Map) value).get("collection")) {
                // if (((RList) abstractRType).list.size() == 1) {
                // obj.addAll(((RList) abstractRType).list);
                // } else {
                // obj.add(abstractRType);
                // }
                // } else {
                obj.add(createRDataTypeFromObject(new FieldInfo(value), stringsAsFactors));
                // }
            }
        }

        return obj;
    }

    private List<String> convertValueToList(final List<Object> hmValue) throws BusinessException {
        if (hmValue == null) {
            return null;
        } else {
            final List<String> names = new ArrayList<String>();

            for (Object value : hmValue) {
                names.add(value.toString());
            }

            return names;
        }
    }

    @Override
    public Map<String, Object> toUmgType1(final String name, final String sequence) {

        final Map<String, Object> newElement = new HashMap<String, Object>();

        final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);

        final Object dummy = new Object();
        for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
            parameterList.add(dummy);
        }

        newElement.put("p", parameterList);

        parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), name);
        parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), sequence);
        parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
        parameterList.set(FieldInfoEnum.P.getIndex(), null);

        parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(),
                getDataTypeValueEnum(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType()).getIntValue());
        parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(),
                getNativeDataTypeValueEnum(getRDataType().getName()).getIntValue());

        final FieldInfo fi = new FieldInfo();
        fi.setCollection(false);
        fi.setDataType(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType());
        fi.setModelParameterName(name);
        fi.setSequence(sequence);
        fi.setNativeDataType(getRDataType().getName());

        final List<Map<String, Object>> objs = new ArrayList<Map<String, Object>>();

        int subSequence = 1;
        for (int i = 0; i < list.size(); i++) {
            objs.add(list.get(i).toUmgType1(name, String.valueOf(subSequence)));
            subSequence++;
        }

        final ArrayList<Object> alValues = new ArrayList<Object>();

        alValues.add(createListData(objs));

        if (names != null) {
            alValues.add(createListNames());
        }

        parameterList.set(FieldInfoEnum.VALUE.getIndex(), alValues.toArray());

        return newElement;
    }

    private Map<String, Object> createListData(final List<Map<String, Object>> objs) {

        final Map<String, Object> newElement = new HashMap<String, Object>();

        final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);

        final Object dummy = new Object();
        for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
            parameterList.add(dummy);
        }

        newElement.put("p", parameterList);

        parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), LangTypeConstants.R_LIST_DATA);
        parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), "1");
        parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
        parameterList.set(FieldInfoEnum.P.getIndex(), null);

        parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(),
                getDataTypeValueEnum(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType()).getIntValue());
        parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), null);
        parameterList.set(FieldInfoEnum.VALUE.getIndex(), objs.toArray());

        return newElement;
    }

    private Map<String, Object> createListNames() {

        final Map<String, Object> newElement = new HashMap<String, Object>();

        final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);

        final Object dummy = new Object();
        for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
            parameterList.add(dummy);
        }

        newElement.put("p", parameterList);

        parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), LangTypeConstants.R_LIST_NAMES);
        parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), "2");
        parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
        parameterList.set(FieldInfoEnum.P.getIndex(), null);

        parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(),
                getDataTypeValueEnum(com.ca.umg.modelet.common.DataType.STRING.getUmgType()).getIntValue());
        parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), null);
        parameterList.set(FieldInfoEnum.VALUE.getIndex(), names.toArray());

        return newElement;
    }
}