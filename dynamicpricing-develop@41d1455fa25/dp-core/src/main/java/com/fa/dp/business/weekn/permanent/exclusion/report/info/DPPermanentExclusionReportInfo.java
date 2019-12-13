package com.fa.dp.business.weekn.permanent.exclusion.report.info;

import lombok.Data;
import org.joda.time.DateTime;

import java.io.Serializable;

@Data
public class DPPermanentExclusionReportInfo implements Serializable {

	private static final long serialVersionUID = 7862968063266797659L;
	private String propertyId;
	private String rrLoanNumber;
	private String eqLoanNumber;
	private String classiication;
	private String exclusionReason;
	private String updateDate;
}
