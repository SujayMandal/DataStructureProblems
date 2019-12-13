package com.fa.dp.rest;

import com.fa.dp.business.week0.report.delegate.Week0QAReportDelegate;
import com.fa.dp.business.week0.report.info.DPWeek0QAReportRespInfo;
import com.fa.dp.business.week0.report.info.DPWeek0ReportInfo;
import com.fa.dp.business.weekn.report.bo.DPQAReportUtil;
import com.fa.dp.business.weekn.report.delegate.WeekNDailyQAReportDelegate;
import com.fa.dp.business.weekn.report.info.WeekNDailyQAReportInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.rest.response.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

/**
 * @author misprakh
 */

@Slf4j
@Controller
public class DPReportsController {

	@Inject
	private WeekNDailyQAReportDelegate weekNDailyQAReportDelegate;

	@Inject
	private Week0QAReportDelegate week0QAReportDelegate;

	@Inject
	private DPQAReportUtil dpqaReportUtil;

	@GetMapping(value = "/consolidatedReports")
	@ResponseBody
	public RestResponse<List<WeekNDailyQAReportInfo>> consolidatedReports(@RequestParam(required = true) String startDate,
			@RequestParam(required = true) String endDate, @RequestParam(required = true) String occupancy,
			@RequestParam(required = true) List<String> client) {
		RestResponse<List<WeekNDailyQAReportInfo>> response = new RestResponse<>();
		List<WeekNDailyQAReportInfo> qaReportInfoList = null;
		log.info("consolidatedReports begins");
		try {
			if (startDate != null && endDate != null) {
				qaReportInfoList = weekNDailyQAReportDelegate
						.getConsolidatedQAReports(LocalDate.parse(startDate), LocalDate.parse(endDate), occupancy, client);
			}
			if (CollectionUtils.isNotEmpty(qaReportInfoList)) {
				response.setResponse(qaReportInfoList);
			} else {
				response.setResponse(null);
				response.setMessage("QA report not found from " + startDate + " to " + endDate + ".");
			}
		} catch (SystemException se) {
			log.error("consolidatedReports fails ", se);
			response.setSuccess(Boolean.FALSE);
			response.setMessage(se.getLocalizedMessage());
			response.setErrorCode(se.getCode());
		} catch (Exception e) {
			log.error("consolidatedReports fails ", e);
			response.setSuccess(Boolean.FALSE);
			response.setMessage(e.getMessage());
		}
		log.info("consolidatedReports ends");
		return response;
	}

	@GetMapping(value = "/week0-qa-report")
	@ResponseBody
	public RestResponse<DPWeek0QAReportRespInfo> wek0QAReport(@RequestParam(required = true) String startDate,
			@RequestParam(required = true) String endDate, @RequestParam(required = true) String occupancy,
			@RequestParam(required = true) List<String> client) {
		RestResponse<DPWeek0QAReportRespInfo> response = new RestResponse<>();
		DPWeek0QAReportRespInfo qaReport = null;
		log.info("consolidatedReports begins");
		try {
			if (startDate != null && endDate != null) {
				log.debug("Start date : {},End date : {} , occupancy : {} , client : {}", startDate,endDate,occupancy,client.toString());
				qaReport = week0QAReportDelegate.fetchWeek0Repots(startDate, endDate, occupancy, client);
			}
			if (ObjectUtils.isEmpty(qaReport)) {
				response.setResponse(null);
				response.setMessage("QA report not found from " + startDate + " to " + endDate + ".");
			} else {
				response.setResponse(qaReport);
			}
		} catch (SystemException se) {
			log.error("consolidatedReports fails ", se);
			response.setSuccess(Boolean.FALSE);
			response.setMessage(se.getLocalizedMessage());
			response.setErrorCode(se.getCode());
		} catch (Exception e) {
			log.error("consolidatedReports fails ", e);
			response.setSuccess(Boolean.FALSE);
			response.setMessage(e.getMessage());
		}
		log.info("consolidatedReports ends");
		return response;
	}
}
