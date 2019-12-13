package com.ca.umg.business.modelet.profiler.param.dao;

import com.ca.umg.business.modelet.profiler.param.entity.ModeletProfilerParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ModeletProfilerParamDao extends JpaRepository<ModeletProfilerParam, String> {
	@Query("SELECT p from ModeletProfilerParam p WHERE p.modeletProfiler.id = :profilerId ")
	List<ModeletProfilerParam> findModeletProfilerParamsByProfilerId(@Param("profilerId") String profilerId);

	List<ModeletProfilerParam> findById(String id);

	@Modifying
	@Query("DELETE FROM ModeletProfilerParam MPP where MPP.modeletProfiler.id = :profilerId")
	void deleteModeletProfilerParamByProfilerId(@Param("profilerId") String profilerId);
}
