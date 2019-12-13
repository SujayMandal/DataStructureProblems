package com.fa.dp.business.audit.bo;

import com.fa.dp.business.audit.dao.DPWeekNAuditReportsDao;
import com.fa.dp.business.audit.entity.DPWeekNAuditReports;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.RAClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Named
public class DPAuditReportBOImpl implements DPAuditReportBO {

	@Inject
	private DPWeekNAuditReportsDao dpWeekNAuditReportsDao;

	/**
	 * @param dpWeekNAuditReports
	 * @return void
	 * @throws SystemException
	 */
	@Override
	public void saveAuditReport(List<DPWeekNAuditReports> dpWeekNAuditReports) throws SystemException {
		// Check if record already present in Audit table
		Set<String> loanSet = dpWeekNAuditReports.stream().map(e -> e.getLoanNumber()).collect(Collectors.toSet());
		List<DPWeekNAuditReports> auditReports = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dpWeekNAuditReports)) {
			for (DPWeekNAuditReports audit : dpWeekNAuditReports) {
				log.info("Finding existing record for loan: " + audit.getLoanNumber());
				List<DPWeekNAuditReports> existingRecord = dpWeekNAuditReportsDao
						.findByLoanNumberAndRunDateAndDeliveryDateIsNull(audit.getLoanNumber(), audit.getRunDate());
				if (CollectionUtils.isNotEmpty(existingRecord)) {
					List<DPWeekNAuditReports> existingFailedRecord = dpWeekNAuditReportsDao
							.findByLoanNumberAndRunDateAndAction(audit.getLoanNumber(), audit.getRunDate(), audit.getAction());
					if (CollectionUtils.isEmpty(existingFailedRecord)) {
						existingRecord.get(0).setAction(audit.getAction());
						existingRecord.get(0).setDeliveryDate(audit.getDeliveryDate());
						dpWeekNAuditReportsDao.save(existingRecord.get(0));
					} else {
						dpWeekNAuditReportsDao.delete(existingRecord.get(0));
					}
				} else {
					existingRecord = dpWeekNAuditReportsDao
							.findByLoanNumberAndRunDateAndAction(audit.getLoanNumber(), audit.getRunDate(), audit.getAction());
					if (CollectionUtils.isEmpty(existingRecord) && loanSet.contains(audit.getLoanNumber())) {
						auditReports.add(audit);
					}
				}
				loanSet.remove(audit.getLoanNumber());
			}
		}

		if (CollectionUtils.isNotEmpty(auditReports)) {
			dpWeekNAuditReportsDao.saveAll(auditReports);
		}

	}

	/**
	 * @param priorRecommendation
	 * @return boolean
	 */
	@Override
	public boolean updatePriorRecommendationAuditReport(DPProcessWeekNParamInfo priorRecommendation, Long selectedDateMillis) {
		log.info("Finding existing record for loan: " + priorRecommendation.getAssetNumber());
		DPWeekNAuditReports existingRecord = dpWeekNAuditReportsDao
				.findByLoanNumberAndDeliveryDateAndAction(priorRecommendation.getAssetNumber(), priorRecommendation.getDeliveryDate(),
						"Old Recommendation");
		if (existingRecord != null) {
			existingRecord.setRunDate(selectedDateMillis);
			dpWeekNAuditReportsDao.save(existingRecord);
			return true;
		}
		return false;
	}

	/**
	 * @param exclusionEntry
	 * @return boolean
	 */
	@Override
	public boolean updateExclusionEntriesAuditReport(DPWeekNAuditReports exclusionEntry) {
		log.info("Finding existing record for loan: " + exclusionEntry.getLoanNumber());
		DPWeekNAuditReports existingRecord = dpWeekNAuditReportsDao
				.findByLoanNumberAndDeliveryDateAndRunDate(exclusionEntry.getLoanNumber(), exclusionEntry.getDeliveryDate(),
						exclusionEntry.getRunDate());
		if (existingRecord != null) {
			existingRecord.setAction(exclusionEntry.getAction());
			existingRecord.setPermanentExclusion(exclusionEntry.getPermanentExclusion());
			dpWeekNAuditReportsDao.save(existingRecord);
			return true;
		}
		return false;
	}

	@Override
	public DPWeekNAuditReports findExclusionReason(String assetNumber, LocalDate currentListEndDate) {
		DPWeekNAuditReports auditReport = null;

		if (currentListEndDate != null) {
			long endTimeEpoch = currentListEndDate.atStartOfDay(ZoneId.of(RAClientUtil.EST_TIME_ZONE)).toEpochSecond() * 1000;
			log.info("assetNumber : {}, endTimeEpoch : {}", assetNumber, endTimeEpoch);

			List<DPWeekNAuditReports> auditReportList = dpWeekNAuditReportsDao.findByAssetNumberANDDeliveryDate(assetNumber, endTimeEpoch);
			if (CollectionUtils.isNotEmpty(auditReportList)) {
				log.info("auditReportList data for asset number {} is : {}", assetNumber, auditReportList.toString());
				auditReport = auditReportList.get(0);
			}
		} else {
			log.error("currentListEndDate is null for asset number : {}", assetNumber);
		}
		return auditReport;
	}

	@Override
	public List<DPWeekNAuditReports> findPermanentExclusionList(List<String> clasificationList) {
		return dpWeekNAuditReportsDao.findExclusionReport(Boolean.TRUE, clasificationList);
	}
}
