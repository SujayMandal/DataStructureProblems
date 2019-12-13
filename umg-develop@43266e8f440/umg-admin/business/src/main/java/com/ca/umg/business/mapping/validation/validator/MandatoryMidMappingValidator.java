package com.ca.umg.business.mapping.validation.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.ca.umg.business.mapping.validation.core.AbstractValidator;
import com.ca.umg.business.mapping.validation.core.MappingValidatorConstants;
import com.ca.umg.business.mapping.validation.core.MappingValidatorContainer;
import com.ca.umg.business.mid.extraction.info.MidMapping;
import com.ca.umg.business.validation.ValidationError;

@Component(MappingValidatorConstants.MAND_MID_MAPPING_VALIDATOR)
public class MandatoryMidMappingValidator extends AbstractValidator {
    private static final int ONE = 1;

    @Override
    public boolean execute(MappingValidatorContainer inputs, List<ValidationError> errors, Map<String,List<String>> mappingToDelete) {
        boolean isValid = true;
        int count = 0;
        MidMapping midMapping = inputs.getMidMapping();
        if (CollectionUtils.isNotEmpty(midMapping.getInputs())) {
            for (String tidName : midMapping.getInputs()) {
                if (inputs.isMandatory(inputs.getTidParamInfoMap().get(tidName)) 
                		&& !inputs.isExposedToTenant(inputs.getTidParamInfoMap().get(tidName))) {
                    count++;
                } else if (inputs.isExposedToTenant(inputs.getTidParamInfoMap().get(tidName))) {
                	populateMappingsToDelete(midMapping, mappingToDelete, tidName);
                }
            }
        }
        if (count < ONE) {
            isValid = false;
            errors.add(new ValidationError(midMapping.getMappedTo(), MappingValidatorConstants.TID_MANDATORY_ERROR_MSG));
        }
        if(count >ONE){
        	isValid = false;
            errors.add(new ValidationError(midMapping.getMappedTo(), MappingValidatorConstants.TID_ONLY_ONE_MAND_ERROR_MSG));
        }
        return isValid;
    }
    
    private void populateMappingsToDelete (MidMapping midMapping, Map<String,List<String>> mappingToDelete, 
    		String tidName) {
    	List<String>  listMappingToDelete = null;
    	if (mappingToDelete.containsKey(midMapping.getMappedTo())) {
    		listMappingToDelete = mappingToDelete.get(midMapping.getMappedTo());
    		listMappingToDelete.add(tidName);
    	} else {
    		listMappingToDelete = new ArrayList<String>();
    		listMappingToDelete.add(tidName);
    		mappingToDelete.put(midMapping.getMappedTo(),listMappingToDelete);
    	}
    }

}
