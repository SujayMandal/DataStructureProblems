package com.fa.dp.business.validation.file.header.constant;

public enum DPSopWeekNFileHeader {

    HEADER1("Asset #"),
    HEADER2("State"),
    HEADER3("Prop Type"),
    HEADER4("Status"),
    HEADER5("Asset Value"),
    HEADER6("AV set date"),
    HEADER7("REO Date"),
    HEADER8("List Price (106% of AV)"),
    HEADER9("Classification"),
    HEADER10("Assignment"),
    HEADER11("Notes");

    private DPSopWeekNFileHeader(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
