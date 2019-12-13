package com.fa.dp.business.info;

import java.io.Serializable;

import lombok.Data;

/**
 * misprakh
 */

@Data
public class StageFiveInfo implements Serializable {

	private static final long serialVersionUID = -4590885079063062532L;

	private String loanNumber;
	private String apprTyp;
	private String reViewDt;
	private String asIsLowMktVal;
	private String asIsMidMktVal;
	private String conditionCde;
	private String livingArea;
	private String totRepairAmt;
}
