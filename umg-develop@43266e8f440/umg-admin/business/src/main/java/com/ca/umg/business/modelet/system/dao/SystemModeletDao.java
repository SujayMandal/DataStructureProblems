package com.ca.umg.business.modelet.system.dao;

import com.ca.umg.business.modelet.system.entity.SystemModelet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SystemModeletDao extends JpaRepository<SystemModelet, String> {
	List<SystemModelet> findByHostNameAndPort(String hostName, int port);

	Long countByHostNameAndPort(String hostName, int port);

	@Query("SELECT p from SystemModelet p WHERE p.modeletProfiler.id = :profilerId ")
	List<SystemModelet> fetchSystemModeletsByProfiler(@Param("profilerId") String profilerId);

	SystemModelet findById(String id);
}
