/**
 * 
 */
package com.ca.umg.modelet.converter.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.modelet.common.FieldInfo;
import com.ca.umg.modelet.common.ModelRequestInfo;
import com.ca.umg.modelet.common.ModelResponseInfo;
import com.ca.umg.modelet.converter.Converter;

/**
 * @author kamathan
 *
 */
public class ExcelConverter implements Converter {

    // private static final Logger LOGGER = LoggerFactory.getLogger(ExcelConverter.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.modelet.converter.Converter#marshall(com.ca.umg.modelet.common.ModelRequestInfo)
     */
    @Override
    public Object marshall(ModelRequestInfo modelRequestInfo) throws BusinessException {
        Map<String, Object> excelModelInput = new HashMap<String, Object>();
        List<FieldInfo> fieldInfos = modelRequestInfo.getPayload();
        if (CollectionUtils.isNotEmpty(fieldInfos)) {
            for (FieldInfo fieldInfo : fieldInfos) {
                excelModelInput.put(fieldInfo.getModelParameterName(), fieldInfo);
            }
        }
        return excelModelInput;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.modelet.converter.Converter#unmarshall(java.lang.Object)
     */
    @Override
    public Object unmarshall(Object response) throws BusinessException {
        ModelResponseInfo modelResponseInfo = new ModelResponseInfo();
        modelResponseInfo.setPayload(response);
        return modelResponseInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.modelet.converter.Converter#unmarshall(java.lang.Object, boolean)
     */
    @Override
    public Object unmarshall(Object response, boolean reduceModelSize) throws BusinessException {
        // TODO Auto-generated method stub
        return null;
    }

}
