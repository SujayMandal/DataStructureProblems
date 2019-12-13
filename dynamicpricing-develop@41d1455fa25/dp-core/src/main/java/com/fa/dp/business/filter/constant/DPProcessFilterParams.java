package com.fa.dp.business.filter.constant;

public enum DPProcessFilterParams {
	
	ELIGIBLE("Eligible"),
	INELIGIBLE("Ineligible"),
	ELIGIBLE_OUT_OF_SCOPE("Out Of Scope"),
	ASSIGNMENT_BENCHMARK("Benchmark"),
	ASSIGNMENT_ERROR("Error"),
	RTSOURCE_RTNG("RTNG"),
	NOTES_RR_DB_FAIL("Unable to fetch from RR db"),
	NOTES_CLASSIFICATION("RR Classification is different from input"),
	NOTES_DUP("Previously Assigned"),
	NOTES_REV("Property Revalued on <%s>"),
	NOTES_INV("Special Servicing Investor"),
	NOTES_RR("Unable to fetch Loan details from Real Resolution. "),
	NOTES_RTNG("Unable to fetch Loan details from Real Trans. "),
	NOTES_RRTNG("Unable to fetch Loan details from Real Trans & Real Resolution. "),
	NOTES_AV("AV Outside Range. "),
	NOTES_PT("Unsupported Property Type. "),
	NOTES_TRANS("Transferred from <%s> to <%s>"),
	NOTES_W0NR("Week 0 not run. "),
	SPECIAL_SERVICE("Special Service"),
	PMI("PMI"),
	STATE_LAW("Excluded by state law #"),
	WEEK_ZERO_NOT_RUN("Week 0 not run"),
	SOP_EXCLUSION_REASON("SOP at First Listing"),
	VACANT_EXCLUSION_REASON("Vacant at First Listing"),
	MOST_RECENT_LISTING_EXCLUSION ("Most Recent Listing #"),
	HUBZU_DB_CALL_EXCLUSION("Hubzu DB call failed"),
	STAGE5_DB_CALL_EXCLUSION("Stage5 DB call failed"),
	SS_PMI_EXCLUSION("SS_PMI_HUBZU_QUERY failed"),
	RA_FAIL_EXCLUSION("RA call failed"),
	PAST_12_CYCLES_EXCLUSION("Properties past 12 cycles"),
	ODD_LISTINGS_EXCLUSION("Odd Listing"),
	ACTIVE_LISTINGS_EXCLUSION("Active Listing"),
	SUCCESFUL_UNDERREVIEW_EXCLUSION("Most recent listing status Succesful/Underreview"),
	ASSIGNMENT_DATE_EXCLUSION("List start dates of all listings are before assignment date of week0"),
	SOPWEEKN_NOT_RUN("SOP Week N not run");

    private String value;
	
	private DPProcessFilterParams(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
