package com.fa.dp.business.sop.week0.dao;

import java.util.List;

import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;

public interface SOPWeek0FilterDao {

	public List<DPSopWeek0Param> getSOPWeek0FilteredRecords(String inputFileName, List<String> status,
			Long fromDate, Long toDate);

}
