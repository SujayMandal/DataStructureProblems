package com.fa.dp.business.sop.weekN.dao;

import java.util.List;

import com.fa.dp.business.sop.weekN.entity.DPSopWeekNParam;

public interface DPSopWeekNFilterDao {

	public List<DPSopWeekNParam> getSOPWeekNFilteredRecords(String inputFileName, List<String> status,
			Long fromDate, Long toDate);

}
