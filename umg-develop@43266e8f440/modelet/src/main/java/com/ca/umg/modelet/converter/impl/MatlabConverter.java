package com.ca.umg.modelet.converter.impl;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.modelet.common.DataType;
import com.ca.umg.modelet.common.FieldInfo;
import com.ca.umg.modelet.common.ModelRequestInfo;
import com.ca.umg.modelet.converter.Converter;
import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWCellArray;
import com.mathworks.toolbox.javabuilder.MWCharArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWLogicalArray;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import com.mathworks.toolbox.javabuilder.MWStructArray;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked", "PMD.CyclomaticComplexity" })
public class MatlabConverter implements Converter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatlabConverter.class);
    private static final int NUMBER_TWO = 2;

    public Object marshall(final ModelRequestInfo modelRequestInfo) {
        LOGGER.info("Started model request conversion for model {}.", modelRequestInfo.getHeaderInfo().getModelLibraryVersionName());
        final List<Object> modelInput = new ArrayList<Object>(modelRequestInfo.getPayload().size());
        final List<FieldInfo> payLoad = modelRequestInfo.getPayload();
        Collections.sort(payLoad);
        Object input = null;
        for (int index = 0; index < payLoad.size(); index++) {
            input = payLoad.get(index).isCollection() ? getCollectionValue(payLoad.get(index).getDataType(), payLoad.get(index)
                    .getValue()) : getValue(payLoad.get(index).getDataType(), payLoad.get(index).getValue());
            modelInput.add(input);
        }
        LOGGER.info("Model request conversion completed for model {}.", modelRequestInfo.getHeaderInfo().getModelLibraryVersionName());
        return modelInput;
    }

    public Object getValue(final String dataType, final Object object) {
        Object resp = null;
        switch (dataType.toLowerCase(Locale.getDefault())) {
        case "double":
        case "long":
        case "biginteger":
        case "bigdecimal":
            resp = Double.parseDouble(String.valueOf(object));
            break;
        case "integer":
            resp = Integer.parseInt(String.valueOf(object));
            break;
        case "string":
            resp = String.valueOf(object);
            break;
        case "date":
            resp = String.valueOf(object);
            break;
        case "boolean":
            resp = Boolean.valueOf(object.toString());
            break;
        case "object":
            CollectionUtils.transform((List) object, new Transformer() {

                @Override
                public Object transform(Object input) {
                    FieldInfo fieldInfo = new FieldInfo();
                    try {
                        BeanUtils.populate(fieldInfo, (Map) input);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        LOGGER.error("Exception occured mapping properties");
                    }
                    return syncWithFieldInfoValue(fieldInfo);
                }
            });
            ((List) object).removeAll(Collections.singleton(null));
            Collections.sort((List) object);
            resp = buildStructArray((List) object);
            break;
        default:
            break;
        }
        return resp;
    }

    public Object getCollectionValue(final String dataType, final Object object) {
        MWArray mwArray = null;
        switch (dataType.toLowerCase(Locale.getDefault())) {
        case "double":
        case "long":
        case "biginteger":
        case "bigdecimal":
            mwArray = new MWNumericArray(buildArray((List) object, getInnerArrayDataType(object)), MWClassID.DOUBLE);
            break;
        case "integer":
            mwArray = new MWNumericArray(buildArray((List) object, getInnerArrayDataType(object)), MWClassID.INT32);
            break;
        case "string":
            mwArray = new MWCharArray(buildArray((List) object, getInnerArrayDataType(object)));
            break;
        case "date":
            mwArray = new MWCharArray(buildArray((List) object, getInnerArrayDataType(object)));
            break;
        case "boolean":
            mwArray = new MWLogicalArray(buildArray((List) object, getInnerArrayDataType(object)));
            break;
        default:
            break;
        }
        return mwArray;
    }

    private Object getInnerArrayDataType(Object obj) {
        Object objType = null;
        if (CollectionUtils.isNotEmpty((List) obj)) {
            if (((List) obj).get(0) instanceof List) {
                objType = new Object();
            } else if (((List) obj).get(0) instanceof Double) {
                objType = new Double(0);
            } else if (((List) obj).get(0) instanceof Integer) {
                objType = Integer.valueOf(0);
            } else if (((List) obj).get(0) instanceof Boolean) {
                objType = Boolean.TRUE;
            } else if (((List) obj).get(0) instanceof String) {
                objType = "";
            }

        }
        return objType;
    }

    public MWStructArray buildStructArray(final List list) {
        String[] keys = new String[list.size()];
        BeanToPropertyValueTransformer transformer = new BeanToPropertyValueTransformer("modelParameterName");
        Collection sequences = CollectionUtils.collect(list, transformer);
        sequences.toArray(keys);
        MWStructArray mwStructArray = new MWStructArray(1, 1, keys);
        FieldInfo fieldInfo = null;
        Object input = null;
        for (Object obj : list) {
            fieldInfo = (FieldInfo) obj;
            input = fieldInfo.isCollection() ? getCollectionValue(fieldInfo.getDataType(), fieldInfo.getValue()) : getValue(
                    fieldInfo.getDataType(), fieldInfo.getValue());
            mwStructArray.set(fieldInfo.getModelParameterName(), 1, input);
            if (input != null && input instanceof MWArray) { // NOPMD
                MWArray.disposeArray((MWArray) input);
                if (input instanceof MWStructArray) {
                    ((MWStructArray) input).dispose();
                } else if (input instanceof MWCellArray) {
                    ((MWCellArray) input).dispose();
                } else if (input instanceof MWCharArray) {
                    ((MWCharArray) input).dispose();
                } else if (input instanceof MWArray) {
                    ((MWArray) input).dispose();
                }
                input = null;// NOPMD
            }
        }
        return mwStructArray;
    }

    private <T> Object[] buildArray(List<Object> list, T t) {
        Object[] array = null;
        if (t instanceof Double) {
            array = (T[]) new Double[list.size()];
        } else if (t instanceof Integer) {
            array = (T[]) new Integer[list.size()];
        } else if (t instanceof Boolean) {
            array = (T[]) new Boolean[list.size()];
        } else if (t instanceof Character) {
            array = (T[]) new Object[list.size()];
        } else if (t instanceof String) {
            array = (T[]) new String[list.size()];
        } else {
            array = new Object[list.size()];
        }
        int index = 0;
        for (Object obj : list) {
            if (obj instanceof List) {
                array[index] = buildArray((List) obj, getInnerArrayDataType(obj));
            } else {
                array[index] = (T) obj;
            }
            index = index + 1;
        }
        return array;
    }

    public Map buildMapFromMWStructArray(final MWStructArray mwStructArray) {
        String[] keys = mwStructArray.fieldNames();
        Object object = null;
        Map map = new HashMap();
        for (String key : keys) {
            object = mwStructArray.get(key, new int[] { 1, 1 });
            if (object instanceof MWStructArray) {
                map.put(key, buildMapFromMWStructArray((MWStructArray) object));
            } else if (object instanceof MWArray) {
                map.put(key, ((MWArray) object).toArray());
            } else {
                map.put(key, object);
            }
        }
        return map;
    }

    public Object unmarshall(final Object response) {
        List output = (List) response;
        List<FieldInfo> fieldInfos = new ArrayList<>();
        FieldInfo fieldInfo = null;
        Object value = null;
        for (int i = 0; i < output.size(); i++) {
            fieldInfo = new FieldInfo();
            fieldInfo.setSequence(String.valueOf(i + 1));
            if (output.get(i) instanceof MWStructArray) {
                fieldInfo.setDataType(DataType.OBJECT.getUmgType());
                fieldInfo.setCollection(true);
                value = buildObjectFromMWStructArray((MWStructArray) output.get(i));
                release((MWStructArray) output.get(i));
            } else if (output.get(i) instanceof MWCharArray) {
                fieldInfo.setDataType(DataType.STRING.getUmgType());
                value = buildObjectCharArray(((MWCharArray) output.get(i)).toArray(),
                        ((MWCharArray) output.get(i)).getDimensions());
                release((MWCharArray) output.get(i));
            } else if (output.get(i) instanceof MWCellArray) {
                fieldInfo.setCollection(true);
                value = buildObjectFromMWCellArray((MWCellArray) output.get(i));
                release((MWCellArray) output.get(i));
            } else if (output.get(i) instanceof MWArray) {
                fieldInfo.setCollection(true);
                value = buildObjectFromMWArray((MWArray) output.get(i));
                release((MWArray) output.get(i));
            } else {
                value = output.get(i);
            }
            fieldInfo.setValue(value);
            fieldInfos.add(fieldInfo);
        }
        return fieldInfos;
    }

    @SuppressWarnings("PMD.AvoidReassigningParameters")
    private void release(MWArray mwArray) {
        if (mwArray != null) {
            MWArray.disposeArray(mwArray);
            if (mwArray instanceof MWStructArray) {
                ((MWStructArray) mwArray).dispose();
            } else if (mwArray instanceof MWCharArray) {
                ((MWCharArray) mwArray).dispose();
            } else if (mwArray instanceof MWCellArray) {
                ((MWCellArray) mwArray).dispose();
            } else {
                mwArray.dispose();
            }
        }
        mwArray = null; // NOPMD
    }

    public Object buildObjectFromMWStructArray(MWStructArray mwStructArray) {
        String[] keys = mwStructArray.fieldNames();
        int noOfElements = mwStructArray.numberOfElements();
        Object value = null;
        Object respValue = null;
        List<List<FieldInfo>> fieldInfos = new ArrayList<>();
        List<FieldInfo> children = null;
        FieldInfo fieldInfo = null;
        for (int index = 1; index <= noOfElements; index++) {
            children = new ArrayList<>();
            fieldInfos.add(children);
            for (String key : keys) {
                fieldInfo = new FieldInfo();
                fieldInfo.setModelParameterName(key);
                value = mwStructArray.getField(key, index);
                if (value instanceof MWStructArray) {
                    fieldInfo.setDataType(DataType.OBJECT.getUmgType());
                    fieldInfo.setCollection(true);
                    respValue = buildObjectFromMWStructArray((MWStructArray) value);
                    release((MWStructArray) value);
                } else if (value instanceof MWCharArray) {
                    fieldInfo.setDataType(DataType.STRING.getUmgType());
                    respValue = buildObjectCharArray(((MWCharArray) value).toArray(), ((MWCharArray) value).getDimensions());
                    release((MWCharArray) value);
                } else if (value instanceof MWArray) {
                    fieldInfo.setCollection(true);
                    respValue = buildObjectFromMWArray((MWArray) value);
                    if (respValue != null && ((Object[]) respValue).length != 0) {
                        fieldInfo.setDataType(getArrayDataType(respValue));
                    }
                    release((MWArray) value);
                }
                fieldInfo.setValue(respValue);
                children.add(fieldInfo);
            }
        }
        return fieldInfos;
    }

    public Object buildObjectFromMWCellArray(MWCellArray cellArray) {
        Object value = null;
        List values = new ArrayList();
        for (int i = 1; i <= cellArray.numberOfElements(); i++) {
            value = cellArray.getCell(i);
            buildObject(value, values);
        }
        return values.toArray();
    }

    public Object buildObjectFromMWArray(MWArray mwArray) {
        Object value = null;

        int[] dimensions = mwArray.getDimensions();
        List values = new ArrayList();
        if (dimensions != null && dimensions.length == NUMBER_TWO && dimensions[0] * dimensions[1] > 1) {
            List rowValues = null;
            for (int i = 1; i <= dimensions[0]; i++) {
                rowValues = new ArrayList();
                for (int j = 1; j <= dimensions[1]; j++) {
                    value = mwArray.get(new int[] { i, j });
                    buildObject(value, rowValues);
                }
                if (CollectionUtils.isNotEmpty(rowValues)) {
                    values.add(rowValues);
                }
            }
        } else {
            for (int i = 0; i < mwArray.numberOfElements(); i++) {
                value = mwArray.get(i + 1);
                buildObject(value, values);
            }
        }
        return values.toArray();
    }

    private void buildObject(Object value, List rowValues) {
        if (value instanceof MWStructArray) {
            rowValues.add(buildObjectFromMWStructArray((MWStructArray) value));
            release((MWStructArray) value);
        } else if (value instanceof MWCellArray) {
            rowValues.add(buildObjectFromMWCellArray((MWCellArray) value));
            release((MWCellArray) value);
        } else if (value instanceof MWArray) {
            rowValues.add(buildObjectFromMWArray((MWArray) value));
            release((MWArray) value);
        } else {
            rowValues.add(value);
        }
    }

    public Object buildObjectCharArray(Object[] charArray, int[] dimensions) {
        Object[] respArray = new Object[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            if (dimensions.length == NUMBER_TWO) {
                respArray[i] = String.valueOf((char[]) charArray[i]);
            } else if (dimensions.length > NUMBER_TWO) {
                respArray[i] = buildObjectCharArray((Object[]) charArray[i], Arrays.copyOfRange(dimensions, 1, dimensions.length));
            } else {
                respArray[i] = String.valueOf((Character[]) charArray);
            }
        }
        return respArray;
    }

    private FieldInfo syncWithFieldInfoValue(FieldInfo fieldInfo) {
        return null == fieldInfo.getValue() ? null : fieldInfo;
    }

    private String getArrayDataType(Object object) {
        Object[] array = (Object[]) object;
        String dataType = null;
        if (array[0] instanceof List) {
            dataType = getArrayDataType(((List) array[0]).toArray());
        } else if (array[0] instanceof Double) {
            dataType = DataType.DOUBLE.getUmgType();
        } else if (array[0] instanceof Integer) {
            dataType = DataType.INTEGER.getUmgType();
        } else if (array[0] instanceof String) {
            dataType = DataType.STRING.getUmgType();
        }
        return dataType;
    }
	
	@Override
    public Object unmarshall(final Object response, final boolean reduceModelSize) throws BusinessException {
    	return unmarshall(response);
    }
}
