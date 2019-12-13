package com.fa.dp.business.sop.weekN.dao;

import com.fa.dp.business.sop.weekN.entity.DPSopWeekNParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DPSopWeekNParamDao extends JpaRepository<DPSopWeekNParam, String> {

	@Query("SELECT p FROM DPSopWeekNParam p WHERE p.sopWeekNProcessStatus.id = :fileId")
	List<DPSopWeekNParam> findBySopWeekNProcessStatusId(@Param("fileId") String fileId);

	DPSopWeekNParam findFirstByOrderByMostRecentListEndDateDesc();

	@SuppressWarnings("rawtypes")
	@Query("SELECT dp.classification, dpf.inputFileName, dpf.id as fileId, dpf.status, dpf.lastModifiedDate, dp.failedStepCommandName FROM com.fa.dp.business.sop.weekN.entity.DPSopWeekNParam dp, com.fa.dp.business.sop.weekN.entity.DPSopWeekNProcessStatus dpf where dpf.id=dp.sopWeekNProcessStatus")
	List findAllDashboardParams();

	List<DPSopWeekNParam> findByAssetNumberAndDeliveryDateNullAndModelVersionNotNull(String assetNumber);

	@Query("SELECT dp.assetNumber FROM DPSopWeekNParam dp WHERE dp.modelVersion IS NOT NULL AND dp.deliveryDate IS NULL AND dp.assetNumber IN :assetNumbers")
	List<String> findAssetNumbersForSuccessUnderRiview(@Param("assetNumbers") List<String> assetNumbers);

	List<DPSopWeekNParam> findByAssetNumberAndDeliveryDateIsNull(String assetNumber);

	List<DPSopWeekNParam> findByAssetNumberAndDeliveryDateNotNull(String assetNumber);

	@Query("SELECT p FROM DPSopWeekNParam p WHERE FROM_UNIXTIME(p.deliveryDate/1000,'%Y %D %M') = (SELECT FROM_UNIXTIME(MAX(q.deliveryDate)/1000,'%Y %D %M') FROM DPSopWeekNParam q)")
	List<DPSopWeekNParam> findAssetNumberForProiorRecommendation();

	@Query("SELECT DISTINCT p FROM DPSopWeekNParam p WHERE p.sopWeekNProcessStatus.id = :processStatusID")
	List<DPSopWeekNParam> findSopWeekNParamByStatusID(@Param("processStatusID") String dpFileProcessStatusID);

	List<DPSopWeekNParam> findAllByDeliveryDateIsNullAndPropSoldDateDtIsNull();

	@Query("SELECT p FROM DPSopWeekNParam p WHERE p.isPriorRecommended = 'Y' AND p.classification = :classification")
	List<DPSopWeekNParam> findPriorRecommendedAssets(@Param("classification") String classification);

	@Query("SELECT p FROM DPSopWeekNParam p WHERE p.sopWeekNProcessStatus.id = :fileId")
	List<DPSopWeekNParam> findByDynamicPricingFilePrcs(@Param("fileId") String fileId);

	@Query("SELECT p.failedStepCommandName FROM DPSopWeekNParam p WHERE p.sopWeekNProcessStatus.id = :fileId  and p.failedStepCommandName is not NULL")
	List<String> findSopFailedStepCommands(String fileId);

	@Query("SELECT p FROM DPSopWeekNParam p WHERE (p.propTemp = :propTemp OR oldAssetNumber = :oldLoanNumber) "
			+ "AND (p.deliveryDate IS NULL OR p.deliveryDate >= :endDate) ORDER BY p.lastModifiedDate ASC")
	List<DPSopWeekNParam> findByRbidPropIdAndDeliveryDate(@Param("propTemp") String propTemp, @Param("oldLoanNumber") String oldLoanNumber,
															  @Param("endDate") Long endDate);
}
