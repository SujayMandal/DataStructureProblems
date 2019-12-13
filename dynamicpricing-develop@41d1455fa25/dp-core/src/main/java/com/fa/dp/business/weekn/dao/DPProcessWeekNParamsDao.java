package com.fa.dp.business.weekn.dao;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface DPProcessWeekNParamsDao extends JpaRepository<DPProcessWeekNParam, String> {

	@Query("SELECT DISTINCT p FROM DPProcessWeekNParam p WHERE p.deliveryDate=:deliveryDate " + "AND p.classification = :classification "
			+ "AND p.eligible = :eligible")
	List<DPProcessWeekNParam> findWeekNParamByDelioveryDate(@Param("deliveryDate") Long deliveryDate, @Param("eligible") String eligible,
			@Param("classification") String classification);

	@Query("SELECT DISTINCT p FROM DPProcessWeekNParam p WHERE p.modelVersion = :modelVersion " + "AND p.classification = :classification")
	List<DPProcessWeekNParam> findPast12CycleList(@Param("modelVersion") String modelVersion, @Param("classification") String classification);

	List<DPProcessWeekNParam> findByAssetNumberAndClassificationOrderByLastModifiedDateDesc(String assetNumber, String classification);

	List<DPProcessWeekNParam> findByDeliveryDate(String deliveryDate);

	@Query("SELECT p FROM DPProcessWeekNParam p WHERE p.dpWeekNProcessStatus.id = :weekNId")
	List<DPProcessWeekNParam> findByDpWeekNProcessStatus(@Param("weekNId") String weekNId);

	@Query("SELECT DISTINCT p FROM DPProcessWeekNParam p WHERE p.dpWeekNProcessStatus.id = :processStatusID")
	List<DPProcessWeekNParam> findDPProcessWeekNParamByStatusID(@Param("processStatusID") String dpFileProcessStatusID);

	@Query("SELECT p.command FROM DPProcessWeekNParam p WHERE p.dpWeekNProcessStatus.id = :processId")
	List<Command> findFailedStepCommands(@Param("processId") String processId);

	List<DPProcessWeekNParam> findAllByOrderByLastModifiedDateDesc();

	@Query("SELECT p FROM DPProcessWeekNParam p WHERE FROM_UNIXTIME(p.deliveryDate/1000,'%Y %D %M') = (SELECT FROM_UNIXTIME(MAX(q.deliveryDate)/1000,'%Y %D %M') FROM DPProcessWeekNParam q)")
	List<DPProcessWeekNParam> findAssetNumberForProiorRecommendation();

	List<DPProcessWeekNParam> findByAssetNumberAndDeliveryDateNotNull(String assetNumber);

	//Fetch those records from WeekN, which has got recommendation from RA but has most recent listing status SUCCESSFUL/UNDERREVIEW
	List<DPProcessWeekNParam> findByAssetNumberAndDeliveryDateNullAndModelVersionNotNull(String assetNumber);

	List<DPProcessWeekNParam> findAllByDeliveryDateIsNullAndPropSoldDateDtIsNull();

	List<DPProcessWeekNParam> findByAssetNumberAndDeliveryDateIsNull(String selrPropIdVcNn);

	@Query("SELECT DISTINCT p.assetNumber FROM DPProcessWeekNParam p WHERE p.propTemp IS NULL")
	List<String> findNonUpdatedAssets();

	@Query("SELECT DISTINCT p.oldAssetNumber FROM DPProcessWeekNParam p WHERE p.propTemp IS NULL AND p.oldAssetNumber IS NOT NULL")
	List<String> findNonUpdatedMigratedOldAssets();

	@Query("SELECT DISTINCT p.propTemp FROM DPProcessWeekNParam p WHERE p.oldAssetNumber IS NULL")
	List<String> findNonMigratedAssets();

	@Query("SELECT p FROM DPProcessWeekNParam p WHERE p.isPriorRecommended = 'Y' AND p.classification = :classification")
	List<DPProcessWeekNParam> findPriorRecommendedAssets(@Param("classification") String classification);

	@Modifying
	@Transactional
	@Query("DELETE FROM DPProcessWeekNParam WHERE assetNumber IS NULL")
	void deleteByAssetNumberIsNull();

	@Modifying
	@Transactional
	@Query("UPDATE DPProcessWeekNParam SET propTemp = assetNumber WHERE propTemp IS NULL")
	void updatePropTempNullAsAssetNumber();

	@Query("SELECT p FROM DPProcessWeekNParam p WHERE (p.propTemp = :propTemp OR oldAssetNumber = :oldLoanNumber) "
			+ "AND (p.deliveryDate IS NULL OR p.deliveryDate >= :endDate) ORDER BY p.lastModifiedDate ASC")
	List<DPProcessWeekNParam> findByRbidPropIdAndDeliveryDate(@Param("propTemp") String propTemp, @Param("oldLoanNumber") String oldLoanNumber,
			@Param("endDate") Long endDate);

	//@Query("SELECT p FROM DPProcessWeekNParam p ORDER BY p.createdDate DESC LIMIT 1")
	DPProcessWeekNParam findFirstByOrderByLastModifiedDateDesc();

	DPProcessWeekNParam findFirstByOrderByMostRecentListEndDateDesc();


	@Query("SELECT dp.classification,dpf.sysGnrtdInputFileName,dpf.status,dpf.id as fileId, c.name as commandName, dpf.fetchedDate, dpf.lastModifiedDate FROM DPProcessWeekNParam dp LEFT JOIN Command c on dp.command=c.id, DPWeekNProcessStatus dpf where dpf.id=dp.dpWeekNProcessStatus")
	List findAllDashboardParams();
}