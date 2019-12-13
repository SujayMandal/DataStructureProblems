/**
 * 
 */
package com.ca.umg.business.mapping.info;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.ca.umg.business.mid.extraction.info.MappingViews;
import com.ca.umg.business.mid.extraction.info.MidIOInfo;
import com.ca.umg.business.mid.extraction.info.TidIOInfo;
import com.ca.umg.business.validation.ValidationError;

/**
 * @author chandrsa
 * 
 */
public class MappingDescriptor implements Serializable {

    private static final long serialVersionUID = 1L;
    private String tidName;
    private String description;
    private String modelName;
    private String midName;
    private TidIOInfo tidTree;
    private MidIOInfo midTree;
    private MappingViews tidMidMapping;
    private List<ValidationError> validationErrors;
    private Map<String, String> queryInputs;
    private String copiedTidName;

    public String getCopiedTidName() {
        return copiedTidName;
    }

    public void setCopiedTidName(String copiedTidName) {
        this.copiedTidName = copiedTidName;
    }

    public TidIOInfo getTidTree() {
        return tidTree;
    }

    public void setTidTree(TidIOInfo tidTree) {
        this.tidTree = tidTree;
    }

    public MidIOInfo getMidTree() {
        return midTree;
    }

    public void setMidTree(MidIOInfo midTree) {
        this.midTree = midTree;
    }

    public MappingViews getTidMidMapping() {
        return tidMidMapping;
    }

    public void setTidMidMapping(MappingViews tidMidMapping) {
        this.tidMidMapping = tidMidMapping;
    }

    public String getTidName() {
        return tidName;
    }

    public void setTidName(String tidName) {
        this.tidName = tidName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getMidName() {
        return midName;
    }

    public void setMidName(String midName) {
        this.midName = midName;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public Map<String, String> getQueryInputs() {
        return queryInputs;
    }

    public void setQueryInputs(Map<String, String> queryInputs) {
        this.queryInputs = queryInputs;
    }

}
