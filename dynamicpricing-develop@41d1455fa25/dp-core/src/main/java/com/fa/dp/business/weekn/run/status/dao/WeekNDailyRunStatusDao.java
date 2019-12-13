package com.fa.dp.business.weekn.run.status.dao;

import com.fa.dp.business.weekn.run.status.entity.WeekNDailyRunStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WeekNDailyRunStatusDao extends JpaRepository<WeekNDailyRunStatus, String> {

	//@Query("SELECT p FROM WeekNDailyRunStatus p WHERE p.totalRecord != 0 ORDER BY p.lastModifiedDate DESC LIMIT 1")
	WeekNDailyRunStatus findTopByTotalRecordGreaterThanOrderByLastRunDateDesc(int totalRecord);
}
