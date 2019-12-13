package com.ca.umg.modelet.common;

public enum DataType {

    DOUBLE("java.lang.Double", "double"),

    INTEGER("java.lang.Integer", "integer"),

    LONG("java.lang.Long", "long"),
    BIGINTEGER("java.math.BigInteger", "biginteger"),
    BIGDECIMAL("java.math.BigDecimal", "bigdecimal"),

    STRING("java.lang.String", "string"),
    CHARACTER("java.lang.String", "character"),

    OBJECT("java.lang.Object", "object"),

    BOOLEAN("java.lang.Boolean", "boolean"),

    DATE("java.lang.String", "date");

    private String javaType;
    private String umgType;

    private DataType(String javaType, String umgType) {
        this.javaType = javaType;
        this.umgType = umgType;
    }

    public String getJavaType() {
        return javaType;
    }

    public String getUmgType() {
        return umgType;
    }

}
