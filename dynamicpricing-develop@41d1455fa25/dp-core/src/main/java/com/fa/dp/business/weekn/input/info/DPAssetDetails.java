package com.fa.dp.business.weekn.input.info;

import lombok.Data;

import java.io.Serializable;

@Data
public class DPAssetDetails implements Serializable {

	private static final long serialVersionUID = 5958773967334163774L;
	private String loanNumber;
	private String oldLoanNumber;
	private String propTemp;
	private String classification;
	private String assignmentDate;
	private String eligible;
	private String assignment;
	private String recommendedValue;
	private String week;
	private String notes;
	private String initialValuation;

}
