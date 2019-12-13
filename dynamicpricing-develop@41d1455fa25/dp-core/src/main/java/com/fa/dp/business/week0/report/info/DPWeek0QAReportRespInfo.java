package com.fa.dp.business.week0.report.info;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DPWeek0QAReportRespInfo implements Serializable {

	private static final long serialVersionUID = -2186640947243756241L;
	private int propertyCount;
	private double minimumPctAv;
	private double medianPctAv;
	private double maximumPctAv;
	private int voilationCount;
	private int missingReportCount;

	private List<DPWeek0ReportInfo> week0Reports;
}
