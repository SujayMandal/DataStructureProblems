package com.fa.dp.business.validation.file.header.constant;

public enum DPWeekNProcessFileHeader {
    HEADER1("SELR_PROP_ID_VC_NN"),
    HEADER2("Most_Recent_List_End_Date"),
    HEADER3("Most_Recent_List_Status"),
    HEADER4("Most_Recent_Property_Status"),
    HEADER5("Most_Recent_List_Price"),
    HEADER6("List_Price_Dollar_Adjustment_Recommendation"),
    HEADER7("Model_Version"),
    HEADER8("Delivery_Date"),
    HEADER9("Manual Review"),
	HEADER10("Reason_for_Exclusion"),
    HEADER11("Auto Relist"),
    HEADER12("Old Asset Number"),
    HEADER13("First Market Value"),
    HEADER14("New List Price"),
    HEADER15("Property Id");

    private DPWeekNProcessFileHeader(String value) {
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
