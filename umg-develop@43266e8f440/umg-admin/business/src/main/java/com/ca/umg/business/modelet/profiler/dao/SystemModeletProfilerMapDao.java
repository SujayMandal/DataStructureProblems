package com.ca.umg.business.modelet.profiler.dao;

import com.ca.umg.business.modelet.profiler.entity.SystemModeletProfilerMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SystemModeletProfilerMapDao extends JpaRepository<SystemModeletProfilerMap, String> {

	@Modifying
	@Query("DELETE FROM SystemModeletProfilerMap MPP where MPP.systemModelet.id = :modeletId")
	void deleteModeletProfilerParamByModeletId(@Param("modeletId") String modeletId);

	@Query("SELECT p FROM SystemModeletProfilerMap p where p.systemModelet.id = :modeletId")
	SystemModeletProfilerMap findBySystemModelet(@Param("modeletId") String modeletId);

	@Query("UPDATE SystemModeletProfilerMap p SET p.modeletProfiler.id = :profilerId where p.systemModelet.id = :modeletId")
	void updateProfiler(@Param("modeletId") String modeletId, @Param("profilerId") String profilerId);
}
