/**
 * 
 */
package com.ca.umg.business.mid.extraction.info;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ca.umg.business.constants.BusinessConstants;

/**
 * @author chandrsa
 * 
 */
public class MidMapping implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<String> inputs;
    private String mappedTo;

    public List<String> getInputs() {
        return inputs;
    }

    public void setInputs(List<String> inputs) {
        this.inputs = inputs;
    }

    public String getMappedTo() {
        return mappedTo;
    }

    public void setMappedTo(String mappedTo) {
        this.mappedTo = mappedTo;
    }

    public MappingTypes getMappingType() {
        MappingTypes type = MappingTypes.ONE_TO_ONE;
        if (CollectionUtils.isNotEmpty(inputs)) {
            if (inputs.size() == BusinessConstants.NUMBER_TWO) {
                type = MappingTypes.OPTIONAL;
            }
            if (inputs.size() > BusinessConstants.NUMBER_TWO) {
                type = MappingTypes.INVALID;
            }
        } else {
            type = MappingTypes.NONE;
        }
        return type;
    }

}
