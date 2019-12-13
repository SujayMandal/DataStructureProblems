package com.fa.dp.business.validation.file.header.constant;

/**
 * @author misprakh
 *
 *         DPProcess input file Header column values
 *
 */
public enum DPSopWeek0FileHeader {
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

    private DPSopWeek0FileHeader(String value) {
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
