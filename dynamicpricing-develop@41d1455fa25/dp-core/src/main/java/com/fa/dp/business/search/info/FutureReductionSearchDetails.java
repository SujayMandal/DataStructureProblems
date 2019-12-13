package com.fa.dp.business.search.info;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class FutureReductionSearchDetails implements Serializable {
	private static final long serialVersionUID = 6049794748823222698L;
	private String loanNumber;
	private String oldLoanNumber;
	private String propTemp;
	private String listStatus;
	private String listEndDate;
	private String lastReductionDate;
	private String sopFlag;
	private String listType;
	private String ssPmiFlag;
	private String week0RunDate;
	private String week0Assignment;
	private Long listingCount;
	private String futureReductionFlag;
	private String state;
	private String classification;
	private String week0Eligibility;
	private String soldDate;
	private Map<String,String> reason;
	private String partOfDp;
}
