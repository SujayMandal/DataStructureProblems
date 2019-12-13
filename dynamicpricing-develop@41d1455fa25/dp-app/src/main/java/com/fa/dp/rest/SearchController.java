package com.fa.dp.rest;

import com.fa.dp.business.audit.delegate.DPAuditReportDelegate;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.rr.migration.RRMigration;
import com.fa.dp.business.search.delegate.SearchDelegate;
import com.fa.dp.business.search.info.FutureReductionSearchDetails;
import com.fa.dp.business.week0.delegate.DPFileProcessDelegate;
import com.fa.dp.business.weekn.input.info.DPAssetDetails;
import com.fa.dp.business.weekn.permanent.exclusion.report.info.DPPermanentExclusionReportInfo;
import com.fa.dp.core.systemparam.util.AppType;
import com.fa.dp.core.util.RAClientConstants;
import com.fa.dp.rest.response.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class SearchController {

	@Inject
	private SearchDelegate searchDelegate;

	@Inject
	private DPFileProcessDelegate dpFileProcessDelegate;

	@Inject
	private DPAuditReportDelegate dpAuditReportDelegate;

	@Inject
	private RRMigration rRMigration;

	@RequestMapping(value = "/searchFutureRecommendations", method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<FutureReductionSearchDetails> searchFutureRecommendations(@RequestParam(required = true) String assetNumber,
			@RequestParam(required = true) String occupancy) {
		log.info("Search future recommendations controller starts");
		RestResponse<FutureReductionSearchDetails> response = new RestResponse<>();
		try {
			MDC.put(RAClientConstants.APP_CODE, AppType.DPA.getAppCode());
			MDC.put(RAClientConstants.PRODUCT_TYPE, "SearchFuture");

			//dp-384
			String oldAssetNumber = null;
			String propTemp = null;
			Map<String, String> assetMap = new HashMap<String, String>();
			assetMap.put(RRMigration.LOAN_NUM, assetNumber.trim());
			assetMap.put(RRMigration.OLD_RR_LOAN_NUM, oldAssetNumber);
			assetMap.put(RRMigration.PROP_TEMP, propTemp);
			rRMigration.checkForMigration(assetMap);

			FutureReductionSearchDetails result = searchDelegate
					.getFutureReductionDetails(assetMap.get(RRMigration.LOAN_NUM), assetMap.get(RRMigration.OLD_RR_LOAN_NUM),
							assetMap.get(RRMigration.PROP_TEMP), occupancy);
			if (!ObjectUtils.isEmpty(result)) {
				result.setLoanNumber(assetMap.get(RRMigration.LOAN_NUM));
				result.setOldLoanNumber(assetMap.get(RRMigration.OLD_RR_LOAN_NUM));
				result.setPropTemp(assetMap.get(RRMigration.PROP_TEMP));
				response.setResponse(result);
				response.setSuccess(Boolean.TRUE);
				/*response.setMessage("Successfully found record for asset number");*/
			} else {
				response.setMessage("No records found for this asset number");
				response.setSuccess(Boolean.FALSE);
			}
		} catch (Exception e) {
			log.error("Exception occured while fetching future recommendations : ", e);
			response.setSuccess(Boolean.FALSE);
			response.setMessage("Error occurred during search. Please contact administrator ");

		} finally {
			MDC.remove(RAClientConstants.APP_CODE);
			MDC.remove(RAClientConstants.PRODUCT_TYPE);
		}
		log.info("Search future recommendations controller ends");
		return response;
	}

	@RequestMapping(value = "/searchAssetId", method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<List<DPAssetDetails>> searchAssetDetails(@RequestParam(required = true) String assetNumber,
			@RequestParam(required = true) String occupancy) {
		log.info("Search Asset details controller starts");
		RestResponse<List<DPAssetDetails>> response = new RestResponse<>();
		MDC.put(RAClientConstants.APP_CODE, AppType.DPA.getAppCode());
		MDC.put(RAClientConstants.PRODUCT_TYPE, "Search");
		try {
			List<DPAssetDetails> result = dpFileProcessDelegate.searchAssetDetails(assetNumber, occupancy);
			if (!ObjectUtils.isEmpty(result)) {
				response.setResponse(result);
			} else {
				response.setMessage("This Loan ID isn't available in DPA");
			}
		} catch (Exception e) {
			log.error("Exception occured while fetching Loan details : ", e);
			response.setSuccess(Boolean.FALSE);
			response.setMessage("Error occurred during search. Please contact administrator ");

		} finally {
			MDC.remove(RAClientConstants.APP_CODE);
			MDC.remove(RAClientConstants.PRODUCT_TYPE);
		}
		log.info("Search Asset details controller ends");
		return response;
	}

	@GetMapping(value = "/removeLoanFromDPA")
	@ResponseBody
	public RestResponse<DPAssetDetails> removeLoanFromDPA(@RequestParam(required = true) String assetNumber,
			@RequestParam(required = true) String occupancy, @RequestParam(required = true) String reason) {
		log.info("removeLoanFromDP controller starts");
		RestResponse<DPAssetDetails> response = new RestResponse<>();
		try {
			DPAssetDetails result = dpFileProcessDelegate.removeLoanFromDP(assetNumber, occupancy, reason);
			if (!ObjectUtils.isEmpty(result)) {
				if(result.getNotes().equalsIgnoreCase(DPAConstants.OUT_OF_SCOPE)) {
					response.setMessage("Loan is already Out of scope");
				} else {
					response.setResponse(result);
					response.setSuccess(Boolean.TRUE);
				}
			} else {
				response.setMessage(
						"Loan cannot be removed from Dynamic Pricing because it is either NRZ property or Week 0 is not run for this property");
			}
		} catch (Exception e) {
			log.error("Exception occured while fetching Loan details : ", e);
			response.setSuccess(Boolean.FALSE);
			response.setMessage("Error occurred while removing loan number. Please contact administrator ");

		} finally {
			MDC.remove(RAClientConstants.APP_CODE);
			MDC.remove(RAClientConstants.PRODUCT_TYPE);
		}
		log.info("removeLoanFromDP controller ends");
		return response;
	}

	@GetMapping(value = "/permanent-exclusion-report")
	@ResponseBody
	public RestResponse<List<DPPermanentExclusionReportInfo>> fetchPermanentExclusionReport(
			@RequestParam(required = true) List<String> classifications) {
		log.info("fetchPermanentExclusionReport controller starts");
		RestResponse<List<DPPermanentExclusionReportInfo>> response = new RestResponse<>();
		try {
			List<DPPermanentExclusionReportInfo> result = dpAuditReportDelegate.findPermanentExclusionList(classifications);
			if (!ObjectUtils.isEmpty(result)) {
				response.setResponse(result);
				response.setSuccess(Boolean.TRUE);
			} else {
				response.setMessage("There is some problem in fetching permanent exclusion report.");
			}
		} catch (Exception e) {
			log.error("Exception occured while fetching permanent exclusion report : ", e);
			response.setSuccess(Boolean.FALSE);
			response.setMessage("Error occurred while fetching permanent exclusion report. Please contact administrator ");
		} finally {
			MDC.remove(RAClientConstants.APP_CODE);
			MDC.remove(RAClientConstants.PRODUCT_TYPE);
		}
		log.info("fetchPermanentExclusionReport controller ends");

		return response;
	}
}