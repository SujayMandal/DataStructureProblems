package com.fa.dp.business.validation.file.header.constant.weekn;

/**
 * @author yogeshku
 * 
 *         DPProcess input file Header column values
 *
 */
public enum DPProcessWeekNFileHeader {

	SELR_PROP_ID_VC_NN("SELR_PROP_ID_VC_NN"), MOST_RECENT_LIST_END_DATE(
			"Most_Recent_List_End_Date"), MOST_RECENT_LIST_STATUS(
					"Most_Recent_List_Status"), MOST_RECENT_PROPERTY_STATUS(
							"Most_Recent_Property_Status"), MOST_RECENT_LIST_PRICE(
									"Most_Recent_List_Price"), LP_DOLLAR_ADJUSTMENT_RECOMMENDATION(
											"List_Price_Dollar_Adjustment_Recommendation"), MODEL_VERSION(
													"Model_Version"), DELIVERY_DATE(
															"Delivery_Date"), LIST_CYCLE_12_END_DATE(
																	"List Cycle 12 End Date"), REASON_FOR_EXCLUSION(
																			"Reason_for_Exclusion");

	private DPProcessWeekNFileHeader(String value) {
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
