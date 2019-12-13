package com.fa.dp.business.rr.migration.dao;

/**
 * 
 */

import com.fa.dp.business.rr.migration.entity.DPMigrationMap;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author mandasuj
 *
 */
public interface DPMigrationMapDao  extends JpaRepository<DPMigrationMap, String> {

}