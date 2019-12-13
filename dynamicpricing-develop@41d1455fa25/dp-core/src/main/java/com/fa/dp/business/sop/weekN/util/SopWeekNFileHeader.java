package com.fa.dp.business.sop.weekN.util;

/**
 * @author misprakh
 */
public enum SopWeekNFileHeader {
	HEADER1("Asset #"),
	HEADER2("Client Code"),
	HEADER3("Status"),
	HEADER4("Asset Value"),
	HEADER5("AV set date"),
	HEADER6("List Price (106% of AV)"),
	HEADER7("Classification"),
	HEADER8("Eligible"),
	HEADER9("Assignment"),
	HEADER10("Week0Price"),
	HEADER11("State"),
	HEADER12("RTSource"),
	HEADER13("Notes"),
	HEADER14("Property_Type"),
	HEADER15("Assignment_Date"),
	HEADER16("PctOfAV"),
	HEADER17("Within Business Rules"),
	HEADER18("Latest List End date"),
	HEADER19("Latest Status"),
	HEADER20("Date Of Last Reduction"),
	HEADER21("Delivery Date"),
	HEADER22("Vacant Status"),
	HEADER23("Reason"),
	HEADER24("Last List Cycle"),
	HEADER25("List Cycle 12 End Date"),
	HEADER26("Last Recommendation Date"),
	HEADER27("Recommended Reduction"),
	HEADER28("Auto Relist"),
	HEADER29("Old Asset #"),
	HEADER30("Property Id"),
	HEADER31("Prop Type"),
	HEADER32("REO Date"),
	HEADER33("SELR_PROP_ID_VC_NN"),
	HEADER34("Most_Recent_List_End_Date"),
	HEADER35("Most_Recent_List_Status"),
	HEADER36("Most_Recent_Property_Status"),
	HEADER37("Most_Recent_List_Price"),
	HEADER38("List_Price_Dollar_Adjustment_Recommendation"),
	HEADER39("New List Price"),
	HEADER40("Model_Version"),
	HEADER41("Delivery_Date"),
	HEADER42("Manual Review"),
	HEADER43("First Market Value"),
	HEADER44("Reason_for_Exclusion");

	private SopWeekNFileHeader(String value) {
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
