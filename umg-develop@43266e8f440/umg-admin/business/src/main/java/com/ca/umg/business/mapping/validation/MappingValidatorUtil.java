/**
 * 
 */
package com.ca.umg.business.mapping.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;

import com.ca.umg.business.mid.extraction.info.DatatypeInfo.Datatype;
import com.ca.umg.business.mid.extraction.info.MidMapping;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.extraction.info.ParamInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.validation.ValidationError;

/**
 * @author chandrsa
 * 
 */
@Named
@SuppressWarnings({ "PMD.CyclomaticComplexity" })
public class MappingValidatorUtil {

    protected void prepareChildMidMappingMap(Map<String, MidMapping> midMappingMap, List<ValidationError> errors,
            TidParamInfo tidParamInfo, MidParamInfo midParamInfo) {

        if (!(isObject(midParamInfo) || isObject(tidParamInfo))) {
            if(tidParamInfo.isExposedToTenant()){
                midMappingMap.remove(midParamInfo.getFlatenedName());
            }else{
                MidMapping midMapping = midMappingMap.get(midParamInfo.getFlatenedName());
                if (midMapping == null) {
                    midMapping = new MidMapping();
                    midMapping.setMappedTo(midParamInfo.getFlatenedName());
                    midMapping.setInputs(new ArrayList<String>());
                    midMappingMap.put(midParamInfo.getFlatenedName(), midMapping);
                }
                midMapping.getInputs().add(tidParamInfo.getFlatenedName());
            }
        } else if (isObject(midParamInfo) && isObject(tidParamInfo) && CollectionUtils.isNotEmpty(midParamInfo.getChildren())
                && CollectionUtils.isNotEmpty(tidParamInfo.getChildren())) {
            if (midParamInfo.getChildren().size() != tidParamInfo.getChildren().size()) {
                errors.add(new ValidationError(midParamInfo.getFlatenedName(), midParamInfo.getFlatenedName() + " and "
                        + tidParamInfo.getFlatenedName() + " are not similar object"));
            } else {
                int count = 0;
                for (MidParamInfo child : midParamInfo.getChildren()) {
                    prepareChildMidMappingMap(midMappingMap, errors, tidParamInfo.getChildren().get(count++), child);
                }
            }
        } else {
            errors.add(new ValidationError(midParamInfo.getFlatenedName(), midParamInfo.getFlatenedName() + " and "
                    + tidParamInfo.getFlatenedName() + " are not similar object"));
        }

    }

    private boolean isObject(ParamInfo info) {
        boolean isObject = false;
        if (info != null && Datatype.OBJECT.getDatatype().equalsIgnoreCase(info.getDatatype().getType())) {
            isObject = true;
        }

        return isObject;
    }

}
