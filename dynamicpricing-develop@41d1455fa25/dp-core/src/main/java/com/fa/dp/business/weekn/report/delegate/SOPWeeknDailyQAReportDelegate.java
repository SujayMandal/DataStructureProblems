package com.fa.dp.business.weekn.report.delegate;

import com.fa.dp.business.info.HubzuDBResponse;

import java.util.Map;

public interface SOPWeeknDailyQAReportDelegate {

    void prepareAssignmentDate(HubzuDBResponse hubzuinfo, Map<String, String> migrationPropToLoanMap);
}
