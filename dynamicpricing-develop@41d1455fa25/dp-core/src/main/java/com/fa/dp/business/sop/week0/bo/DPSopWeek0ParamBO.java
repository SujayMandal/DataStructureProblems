package com.fa.dp.business.sop.week0.bo;

import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.week0.report.info.DPWeek0ReportInfo;
import com.fa.dp.core.exception.SystemException;

import java.util.List;

public interface DPSopWeek0ParamBO {
	/**
	 * Fetch sop week0 data for given client code and assignment date between start date and end date.
	 *
	 * @param startDate
	 * @param endDate
	 * @param clientCode
	 *
	 * @return
	 */
	List<DPWeek0ReportInfo> fetchWeek0Report(Long startDate, Long endDate, List<String> clientCode);

	/**
	 * Search sop week0 record for given asset number and eligibility
	 *
	 * @param assetNumber
	 * @param eligible
	 *
	 * @return
	 *
	 * @throws SystemException
	 */
	List<DPSopWeek0ParamInfo> fetchSopWeek0ParamsRA(String assetNumber, String eligible) throws SystemException;
}
