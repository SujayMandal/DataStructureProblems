package com.fa.dp.business.sop.weekN.dao;

import com.fa.dp.business.sop.weekN.entity.DPSopWeekNProcessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author misprakh
 */
public interface DPSopWeekNProcessStatusDao extends JpaRepository<DPSopWeekNProcessStatus, String> {

	@Query("SELECT sysGnrtdInputFileName FROM DPSopWeekNProcessStatus WHERE id = :id ")
	String findSysGnrtdInputFileNameById(@Param("id") String id);

	List<DPSopWeekNProcessStatus> findByStatus(String fileStatus);

	@Modifying
	@Query("UPDATE DPSopWeekNProcessStatus SET status = :status WHERE id = :id")
	void updateRunningStatus(@Param("id") String id, @Param("status") String status);

}
