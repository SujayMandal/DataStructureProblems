package com.ca.umg.business.migration.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ca.umg.business.migration.audit.entity.MigrationAudit;

public interface MigrationAuditDAO extends JpaRepository<MigrationAudit, String> {

}
