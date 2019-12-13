package com.fa.dp.business.weekn.run.status.dao;

import com.fa.dp.business.weekn.run.status.entity.SOPWeekNDailyRunStatus;
import com.fa.dp.business.weekn.run.status.entity.WeekNDailyRunStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SOPWeekNDailyRunStatusDao extends JpaRepository<SOPWeekNDailyRunStatus, String> {

    //@Query("SELECT p FROM WeekNDailyRunStatus p WHERE p.totalRecord != 0 ORDER BY p.lastModifiedDate DESC LIMIT 1")
    SOPWeekNDailyRunStatus findTopByTotalRecordGreaterThanOrderByLastRunDateDesc(int totalRecord);
}
