package com.fa.dp.business.weekn.report.dao;

import com.fa.dp.business.weekn.report.entity.WeekNDailyQAReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface WeekNDailyQAReportDao extends JpaRepository<WeekNDailyQAReport, String> {

	List<WeekNDailyQAReport> findByStatusAndSelrPropIdVcNnInOrderByCreatedDateDesc(Boolean status, List<String> selrPropIdVcNns);
	WeekNDailyQAReport save(List<WeekNDailyQAReport> weekNDailyQAReport);

	@Query("SELECT p FROM WeekNDailyQAReport p WHERE p.currentListEndDate BETWEEN :startDate AND :endDate AND classification IN (:client)")
	List<WeekNDailyQAReport> findAllByStartTimeAndEndTime(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("client") List<String> client);
}
