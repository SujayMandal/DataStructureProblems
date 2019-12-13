package com.ca.umg.business.migration.info;

import java.io.Serializable;

public class MappingDetailsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private MappingInputDetailsInfo mappingInput;

    private MappingOutputDetailsInfo mappingOutput;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MappingInputDetailsInfo getMappingInput() {
        return mappingInput;
    }

    public void setMappingInput(MappingInputDetailsInfo mappingInput) {
        this.mappingInput = mappingInput;
    }

    public MappingOutputDetailsInfo getMappingOutput() {
        return mappingOutput;
    }

    public void setMappingOutput(MappingOutputDetailsInfo mappingOutput) {
        this.mappingOutput = mappingOutput;
    }
}
