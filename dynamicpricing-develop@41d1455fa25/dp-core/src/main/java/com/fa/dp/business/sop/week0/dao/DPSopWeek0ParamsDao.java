package com.fa.dp.business.sop.week0.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;

public interface DPSopWeek0ParamsDao extends JpaRepository<DPSopWeek0Param, String> {

	@Query("SELECT p.failedStepCommandName FROM DPSopWeek0Param p WHERE p.sopWeek0ProcessStatus.id = :fileId  and p.failedStepCommandName is not NULL")
	List<String> findSopFailedStepCommands(@Param("fileId") String fileId);

	@Query("SELECT p FROM DPSopWeek0Param p WHERE p.sopWeek0ProcessStatus.id = :fileId")
	List<DPSopWeek0Param> findByDPSopWeek0ProcessStatusId(@Param("fileId") String fileId);

	@Modifying
	@Query("UPDATE DPSopWeek0Param SET eligible = :eligibility, notes = :notes WHERE assetNumber = :assetNbr AND eligible = :eligible")
	void updateSopWeek0Record(@Param("eligibility") String eligiblity, @Param("notes") String notes, @Param("assetNbr") String assetNumber, @Param("eligible") String eligible);

	List<DPSopWeek0Param> findByAssetNumber(String assetNumber);


	@Query("SELECT DISTINCT p.assetNumber from DPSopWeek0Param p WHERE p.assetValue BETWEEN :lowerAsset AND :upperAsset AND p.state = :state AND p.assignment = :assignment AND p.classification = :classification AND (p.uploadFlag != :flag OR p.uploadFlag IS NULL)")
	List<DPSopWeek0Param> countAssignmentByStateAndAssetNumber(@Param("state") String state,
	                                                           @Param("lowerAsset") BigDecimal lowerAssetValue,
	                                                           @Param("upperAsset") BigDecimal upperAssetValue,
	                                                           @Param("assignment") String assignment,
	                                                           @Param("classification") String classification,
	                                                            @Param("flag") String flag);

	@Modifying
	@Query("UPDATE DPSopWeek0Param p SET p.errorDetail = :error WHERE p.id = :id")
	public void updateSopWeek0ErrorDetailsById(@Param("id") String id, @Param("error") String error);

	@Query("SELECT p FROM DPSopWeek0Param p WHERE p.classification IN :clientCode AND p.assignmentDate BETWEEN :startDate AND :endDate")
	List<DPSopWeek0Param> findWeek0Report(@Param("startDate") Long startDate, @Param("endDate") Long endDate,
			@Param("clientCode") List<String> clientCode);
	
	@Query("SELECT DISTINCT p FROM DPSopWeek0Param p WHERE p.sopWeek0ProcessStatus.id = :processStatusID")
	List<DPSopWeek0Param> findDPProcessParamByStatusID(@Param("processStatusID") String dpFileProcessStatusID);
	
	@Query("SELECT p FROM DPSopWeek0Param p WHERE p.sopWeek0ProcessStatus.id = :fileId")
	List<DPSopWeek0Param> findByDynamicPricingFilePrcs(@Param("fileId") String fileId);
	
	@SuppressWarnings("rawtypes")
	@Query("SELECT dp.classification, dpf.inputFileName, dpf.id as fileId, dpf.status, dpf.lastModifiedDate, dp.failedStepCommandName FROM com.fa.dp.business.sop.week0.entity.DPSopWeek0Param dp, com.fa.dp.business.sop.week0.entity.DPSopWeek0ProcessStatus dpf where dpf.id=dp.sopWeek0ProcessStatus")
	List findAllDashboardParams();

	@Query("SELECT p FROM DPSopWeek0Param p LEFT OUTER JOIN DPSopWeek0Param q ON p.assetNumber = q.assetNumber AND (q.notes != 'Previously Assigned' OR q.notes IS NULL) AND p.lastModifiedDate < q.lastModifiedDate WHERE q.lastModifiedDate IS NULL AND (p.notes != 'Previously Assigned' OR p.notes IS NULL) AND p.assetNumber IN :assetIds")
	List<DPSopWeek0Param> findLatestNonDuplicateInSopWeek0ForAsset(@Param("assetIds")  Set<String> assetFromHbz);

	List<DPSopWeek0Param> findByAssetNumberAndEligibleOrderByCreatedDateDesc(String assetNumber, String eligible);
	
	@Query("SELECT p FROM DPSopWeek0Param p LEFT OUTER JOIN DPSopWeek0Param q ON p.assetNumber = q.assetNumber AND (q.notes != 'Previously Assigned' OR q.notes IS NULL) AND p.lastModifiedDate < q.lastModifiedDate WHERE q.lastModifiedDate IS NULL AND (p.notes != 'Previously Assigned' OR p.notes IS NULL) AND p.assetNumber = :assetId")
	List<DPSopWeek0Param> findLatestNonDuplicateInSOPWeek0ForGivenAsset(@Param("assetId")	String loanNumber);

	@Query("SELECT DISTINCT p FROM DPSopWeek0Param p WHERE p.assetNumber = :assetNumber and p.classification ='OCN' and p.eligible = 'Eligible'")
	DPSopWeek0Param findOcwenLoanBYAssetNumber(@Param("assetNumber")String assetNumber);

	@Query("SELECT DISTINCT p FROM DPSopWeek0Param p WHERE p.assetNumber = :assetNumber and p.classification ='OCN' and p.eligible = 'Out of scope'")
	DPSopWeek0Param findOutOfScopeLoanByAssetNumber(@Param("assetNumber")String assetNumber);
}
