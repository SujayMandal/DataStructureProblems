package com.fa.dp.business.week0.report.operation;

import com.fa.dp.business.week0.report.info.DPWeek0ReportInfo;

import java.util.List;

public interface FetchWeek0ReportOperation {
	List<DPWeek0ReportInfo> apply(Long startDate, Long endDate, List<String> clientCode);
}
