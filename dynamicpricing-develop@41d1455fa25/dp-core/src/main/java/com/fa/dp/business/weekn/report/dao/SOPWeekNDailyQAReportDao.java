package com.fa.dp.business.weekn.report.dao;

import com.fa.dp.business.weekn.report.entity.SOPWeekNDailyQAReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SOPWeekNDailyQAReportDao extends JpaRepository<SOPWeekNDailyQAReport, String> {

    //List<SOPWeekNDailyQAReport> findByStatusAndSelrPropIdVcNnInOrderByCreatedDateDesc(Boolean status, List<String> selrPropIdVcNns);
    SOPWeekNDailyQAReport save(List<SOPWeekNDailyQAReport> sopWeekNDailyQAReport);

    @Query("SELECT p FROM SOPWeekNDailyQAReport p WHERE p.currentListEndDate BETWEEN :startDate AND :endDate AND classification IN (:client)")
    List<SOPWeekNDailyQAReport> findAllByStartTimeAndEndTimeForSOP(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("client") List<String> client);
}
