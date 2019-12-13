package com.fa.dp.business.week0.dao;

import java.util.List;

import com.fa.dp.business.week0.entity.DPProcessParam;

public interface Week0FilterDao {

	public List<DPProcessParam> getWeek0FilteredRecords(String inputFileName, List<String> status,
			Long fromDate, Long toDate);

}
