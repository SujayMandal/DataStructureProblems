package com.fa.dp.business.weekn.dao;

import java.util.List;

import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;

public interface WeekNFilterDao {

	public List<DPProcessWeekNParam> getWeekNFilteredRecords(List<String> status,
			Long fromDate, Long toDate);

}
