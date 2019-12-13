package com.ca.umg.modelet.common;

public enum ModeletEngine {

    MATLAB("MATLAB"),

    R("R"),

    EXCEL("EXCEL");

    private String engineName;

    private ModeletEngine(final String engineName) {
        this.engineName = engineName;
    }

    public String getEngineName() {
        return engineName;
    }

}
