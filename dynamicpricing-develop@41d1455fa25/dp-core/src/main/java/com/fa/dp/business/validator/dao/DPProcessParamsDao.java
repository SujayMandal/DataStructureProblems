package com.fa.dp.business.validator.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.week0.entity.DPProcessParam;

public interface DPProcessParamsDao extends JpaRepository<DPProcessParam, String> {

	public List<DPProcessParam> findByAssetNumber(String assetNumber);

	public List<DPProcessParam> findByAssetNumberAndClassificationOrderByLastModifiedDateDesc(
			String assetNumber,
			String classification);
	
	@Modifying
	@Query("UPDATE DPProcessParam SET eligible = :eligibility, notes = :notes, updateTimestamp=:updateTimeStamp WHERE assetNumber = :assetNbr AND eligible = :eligible")
	void updateWeek0Record(
			@Param("eligibility") String eligibility, @Param("notes") String notes,
			@Param("updateTimeStamp") Long updateTimeStamp, @Param("assetNbr") String assetNbr,
			@Param("eligible") String eligible);

	@Query("SELECT DISTINCT p.assetNumber from DPProcessParam p WHERE p.assetValue BETWEEN :lowerAsset AND :upperAsset AND p.state = :state AND p.assignment = :assignment AND p.classification = :classification AND (p.uploadFlag != :flag OR p.uploadFlag IS NULL)")
	List<DPProcessParam> countAssignmentByStateAndAssetNumber(
			@Param("state") String state,
			@Param("lowerAsset") BigDecimal lowerAssetValue,
			@Param("upperAsset") BigDecimal upperAssetValue,
			@Param("assignment") String assignment,
			@Param("classification") String classification,
			@Param("flag") String flag);

	@Query("SELECT DISTINCT p FROM DPProcessParam p WHERE p.dynamicPricingFilePrcsStatus.id = :processStatusID")
	List<DPProcessParam> findDPProcessParamByStatusID(@Param("processStatusID") String dpFileProcessStatusID);

	@Modifying
	@Query("UPDATE DPProcessParam p SET p.eligible = :eligible WHERE p.id = :id")
	void updateDPProcessParamEligibleByID(@Param("id") String id, @Param("eligible") String eligible);

	@Modifying
	@Query("UPDATE DPProcessParam p SET p.notes = :notes WHERE p.id = :id")
	void updateDPProcessNotesByID(@Param("id") String id, @Param("notes") String notes);

	@Modifying
	@Query("UPDATE DPProcessParam p SET p.rtSource = :rtSource WHERE p.id = :id")
	void updateDPProcessRTSourceByID(@Param("id") String id, @Param("rtSource") String rtSource);

	@Modifying
	@Query("UPDATE DPProcessParam p SET p.errorDetail = :error WHERE p.id = :id")
	public void updateDPProcessErrorDetailByID(@Param("id") String id, @Param("error") String error);

	DPProcessParam findByAssetNumberAndEligible(String loanNumber, String eligible);
	
	List<DPProcessParam> findByAssetNumberAndEligibleOrderByCreatedDateDesc(String loanNumber, String eligible);

	@Query("SELECT p FROM DPProcessParam p LEFT OUTER JOIN DPProcessParam q ON p.assetNumber = q.assetNumber AND (q.notes != 'Previously Assigned' OR q.notes IS NULL) AND p.updateTimestamp < q.updateTimestamp WHERE q.updateTimestamp IS NULL AND (p.notes != 'Previously Assigned' OR p.notes IS NULL) AND p.assetNumber = :assetId")
	List<DPProcessParam> findLatestNonDuplicateInWeek0ForGivenAsset(@Param("assetId")	String loanNumber);

	@Query("SELECT p FROM DPProcessParam p LEFT OUTER JOIN DPProcessParam q ON p.assetNumber = q.assetNumber AND (q.notes != 'Previously Assigned' OR q.notes IS NULL) AND p.updateTimestamp < q.updateTimestamp WHERE q.updateTimestamp IS NULL AND (p.notes != 'Previously Assigned' OR p.notes IS NULL) AND p.assetNumber IN :loanNumbers")
	List<DPProcessParam> findLatestNonDuplicateInWeek0ForGivenAssetList(@Param("loanNumbers") List<String> loanNumbers);

	@Query("SELECT p FROM DPProcessParam p WHERE p.dynamicPricingFilePrcsStatus.id = :fileId")
	List<DPProcessParam> findByDynamicPricingFilePrcs(@Param("fileId") String fileId);

	@Query("SELECT p.command FROM DPProcessParam p WHERE p.dynamicPricingFilePrcsStatus.id = :fileId")
	List<Command> findfailedStepCommands(@Param("fileId") String fileId);

	public List<DPProcessParam> findAllByOrderByLastModifiedDateDesc();

	@Query("SELECT p FROM DPProcessParam p LEFT OUTER JOIN DPProcessParam q ON p.assetNumber = q.assetNumber AND (q.notes != 'Previously Assigned' OR q.notes IS NULL) AND p.updateTimestamp < q.updateTimestamp WHERE q.updateTimestamp IS NULL AND (p.notes != 'Previously Assigned' OR p.notes IS NULL) AND p.assetNumber IN :assetIds")
	List<DPProcessParam> findLatestNonDuplicateInWeek0ForGivenAsset(@Param("assetIds")  Set<String> assetFromHbz);

	@Query("SELECT DISTINCT p FROM DPProcessParam p WHERE p.assetNumber = :loanNumber and p.classification ='OCN' and p.eligible = 'Eligible'")
	DPProcessParam findOcwenLoanBYAssetNumber(@Param("loanNumber")String assetNumber);
	
	@Query("SELECT DISTINCT p.assetNumber FROM DPProcessParam p WHERE p.propTemp IS NULL")
	List<String> findNonUpdatedAssets();
	
	@Query("SELECT DISTINCT p.oldAssetNumber FROM DPProcessParam p WHERE p.propTemp IS NULL AND p.oldAssetNumber IS NOT NULL")
	List<String> findNonUpdatedMigratedOldAssets();
	
	@Query("SELECT DISTINCT p.propTemp FROM DPProcessParam p WHERE p.oldAssetNumber IS NULL")
	List<String> findNonMigratedAssets();
	
	@Modifying
    @Transactional
    @Query("DELETE FROM DPProcessParam WHERE assetNumber IS NULL")
	public void deleteByAssetNumberIsNull();
	
	@Modifying
    @Transactional
    @Query("UPDATE DPProcessParam SET propTemp = assetNumber WHERE propTemp IS NULL")
	public void updatePropTempNullAsAssetNumber();

	@Query("SELECT p FROM DPProcessParam p WHERE p.classification IN :clientCode AND p.assignmentDate BETWEEN :startDate AND :endDate")
	List<DPProcessParam> findWeek0Report(@Param("startDate") Long startDate, @Param("endDate") Long endDate, @Param("clientCode") List<String> clientCode);

	@Query("SELECT dp.classification,dpf.inputFileName,dpf.status,dpf.id as fileId, dpf.uploadTimestamp,c.name as commandName FROM com.fa.dp.business.week0.entity.DPProcessParam dp LEFT JOIN com.fa.dp.business.command.entity.Command c on dp.command=c.id, com.fa.dp.business.week0.entity.DynamicPricingFilePrcsStatus dpf where dpf.id=dp.dynamicPricingFilePrcsStatus")
	List findAllDashboardParams();

	@Query("SELECT DISTINCT p FROM DPProcessParam p WHERE p.assetNumber = :assetNumber and p.classification ='OCN' and p.eligible = 'Out of scope'")
	DPProcessParam findOutOfScopeLoanByAssetNumber(@Param("assetNumber")String assetNumber);
}