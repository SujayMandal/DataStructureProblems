package com.fa.dp.business.weekn.dao;

import com.fa.dp.business.sop.weekN.entity.DPSopWeekNProcessStatus;
import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DPSopWeeknProcessStatusRepo extends JpaRepository<DPSopWeekNProcessStatus, String>  {

/*
    DPSopWeekNProcessStatus findByStatus(String status);

    @Query("SELECT sysGnrtdInputFileName FROM DPSopWeekNProcessStatus WHERE id = :id ")
    String findSysGnrtdInputFileNameById(@Param("id") String id);

    @Modifying
    @Query("UPDATE DPSopWeekNProcessStatus SET status = :status WHERE id = :id ")
    void updateSOPWeekNProcessStatus(@Param("status") String status, @Param("id") String id);*/

    DPSopWeekNProcessStatus findFirstByOrderByLastModifiedDateDesc();

}
