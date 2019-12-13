package com.fa.dp.business.constant;

public enum DPProcessParamAttributes {
	MODELED_ASSIGNMENT("Modeled"),
	BENCHMARK_ASSIGNMENT("Benchmark"),
	ERROR_ASSIGNMENT("Error"),
	ELIGIBLE("Eligible"),
	INELIGIBLE("Ineligible"),
	OCN("OCN"),
	NRZ("NRZ"),
	PHH("PHH"),
	NOTES_RA("RA Processing failure");

	private String value;

	private DPProcessParamAttributes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
