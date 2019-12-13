package com.fa.dp.business.weekn.dao;

import java.util.Optional;

import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DPWeekNProcessStatusRepo extends JpaRepository<DPWeekNProcessStatus, String> {
	
	/*@Query(value="select * from DPWeekNProcessStatus where status = :processStatus")
	List<DPWeekNProcessStatus> findByProcessStatus(@Param("processStatus") String processStatus);*/
	
	DPWeekNProcessStatus findByStatus(String status);

	@Query("SELECT sysGnrtdInputFileName FROM DPWeekNProcessStatus WHERE id = :id ")
	String findSysGnrtdInputFileNameById(@Param("id") String id);
	
	@Modifying
	@Query("UPDATE DPWeekNProcessStatus SET status = :status WHERE id = :id ")
	void updateWeekNProcessStatus(
            @Param("status") String status, @Param("id") String id);

	DPWeekNProcessStatus findFirstByOrderByLastModifiedDateDesc();
	
}
