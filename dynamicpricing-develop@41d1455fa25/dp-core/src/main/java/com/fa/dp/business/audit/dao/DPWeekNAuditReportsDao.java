package com.fa.dp.business.audit.dao;

import com.fa.dp.business.audit.entity.DPWeekNAuditReports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DPWeekNAuditReportsDao extends JpaRepository<DPWeekNAuditReports, String> {

	List<DPWeekNAuditReports> findByLoanNumberAndRunDateAndDeliveryDateIsNull(String loanNumber, Long runDate);

	DPWeekNAuditReports findByLoanNumberAndDeliveryDateAndAction(String loanNumber, Long deliveryDate, String action);

	DPWeekNAuditReports findByLoanNumberAndDeliveryDateAndRunDate(String loanNumber, Long deliveryDate, Long runDate);

	List<DPWeekNAuditReports> findByLoanNumberAndRunDateAndAction(String loanNumber, Long runDate, String action);

	@Query("SELECT DISTINCT p.loanNumber FROM DPWeekNAuditReports p WHERE p.propTemp IS NULL")
	List<String> findNonUpdatedAssets();

	@Query("SELECT DISTINCT p.oldLoanNumber FROM DPWeekNAuditReports p WHERE p.propTemp IS NULL AND p.oldLoanNumber IS NOT NULL")
	List<String> findNonUpdatedMigratedOldAssets();

	@Query("SELECT DISTINCT p.propTemp FROM DPWeekNAuditReports p WHERE p.oldLoanNumber IS NULL")
	List<String> findNonMigratedAssets();

	@Modifying
	@Transactional
	@Query("DELETE FROM DPWeekNAuditReports WHERE loanNumber IS NULL")
	void deleteByAssetNumberIsNull();

	@Modifying
	@Transactional
	@Query("UPDATE DPWeekNAuditReports SET propTemp = loanNumber WHERE propTemp IS NULL")
	void updatePropTempNullAsAssetNumber();

	@Query("SELECT p FROM DPWeekNAuditReports p WHERE p.propTemp = :propTemp AND p.lastModifiedDate >= :listEndDate ORDER BY p.lastModifiedDate ASC")
	List<DPWeekNAuditReports> findByAssetNumberANDDeliveryDate(@Param("propTemp") String propTemp, @Param("listEndDate") Long listEndDate);

	@Query("SELECT p FROM DPWeekNAuditReports p WHERE p.permanentExclusion = :permanentExclusion AND p.classification IN :clasifications")
	List<DPWeekNAuditReports> findExclusionReport(@Param("permanentExclusion") Boolean permanentExclusion,
			@Param("clasifications") List<String> clasifications);
}
