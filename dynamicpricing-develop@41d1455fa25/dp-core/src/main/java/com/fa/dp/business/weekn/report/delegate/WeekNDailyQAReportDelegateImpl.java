package com.fa.dp.business.weekn.report.delegate;

import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.filter.bo.DPProcessParamsBO;
import com.fa.dp.business.info.HubzuDBResponse;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.weekn.report.bo.DPQAReportUtil;
import com.fa.dp.business.weekn.report.bo.WeekNDailyQAReportBO;
import com.fa.dp.business.weekn.report.entity.SOPWeekNDailyQAReport;
import com.fa.dp.business.weekn.report.entity.WeekNDailyQAReport;
import com.fa.dp.business.weekn.report.info.WeekNDailyQAReportInfo;
import com.fa.dp.business.weekn.report.mapper.WeekNDailyQAReportMapper;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.util.DateConversionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Named
@Slf4j
public class WeekNDailyQAReportDelegateImpl implements WeekNDailyQAReportDelegate {

	@Inject
	private WeekNDailyQAReportBO weekNDailyQAReportBO;

	@Inject
	private WeekNDailyQAReportMapper weekNDailyQAReportMapper;

	@Inject
	private DPQAReportUtil dpQaReportUtil;

	@Inject
	private DPProcessParamsBO dpProcessParamsBO;

	@Value("${WEEKN_INITIAL_QUERY_IN_CLAUSE_COUNT}")
	private int listSplitCount;


	/**
	 * get report data from QA table
	 *
	 * @param startDate
	 * @param endDate
	 * @param occupancy
	 * @param client
	 * @return
	 * @throws SystemException
	 */
	@Override
	public List<WeekNDailyQAReportInfo> getConsolidatedQAReports(LocalDate startDate, LocalDate endDate, String occupancy, List<String> client) throws SystemException {
		List<WeekNDailyQAReportInfo> listOfQAReportInfo = new ArrayList<>();
		log.info("Retrieving QA daily reports from "+startDate+" to "+endDate);

		if (StringUtils.equals(occupancy, DPAConstants.VACANT)) {
			List<WeekNDailyQAReport> listOfQAReport = weekNDailyQAReportBO.fetchListingsForGivenDateRange(startDate, endDate, client);
			if (CollectionUtils.isNotEmpty(listOfQAReport)) {
				listOfQAReport.forEach(report -> {
					WeekNDailyQAReportInfo weekNDailyQAReportInfo = weekNDailyQAReportMapper.WeekNDailyQAReportToInfoMapper(report);
					if (!ObjectUtils.isEmpty(weekNDailyQAReportInfo))
						listOfQAReportInfo.add(weekNDailyQAReportInfo);
				});
			} else {
				log.error("QA report not found from " + startDate + " to " + endDate + ".");
				throw SystemException.newSystemException(CoreExceptionCodes.DPQA001, new Object[]{});
			}

		} else if (StringUtils.equals(occupancy, DPAConstants.OCCUPIED)) {
		//	log.error("Occupied  reports are in progress.");
			List<SOPWeekNDailyQAReport> listOfQAReportForSOP = weekNDailyQAReportBO.fetchListingsForGivenDateRangeSOP(startDate, endDate, client);
			if (CollectionUtils.isNotEmpty(listOfQAReportForSOP)) {
				listOfQAReportForSOP.forEach(report -> {
					WeekNDailyQAReportInfo weekNDailyQAReportInfo = weekNDailyQAReportMapper.sopWeekNDailyQAReportToInfoMapper(report);
					if (!ObjectUtils.isEmpty(weekNDailyQAReportInfo))
						listOfQAReportInfo.add(weekNDailyQAReportInfo);
				});
			} else {
				log.error("QA report not found from " + startDate + " to " + endDate + ".");
				throw SystemException.newSystemException(CoreExceptionCodes.DPQA001, new Object[]{});
			}

		}
		else{
			log.error("Unexpected error occurred.");
		}
		return listOfQAReportInfo;
	}

	@Override
	public void prepareAssignmentDate(HubzuDBResponse hubzuinfo, Map<String, String> migrationPropToLoanMap) {
		List<DPProcessParam> consolidatedListOfweek0Params = new ArrayList<>();

		List<List<HubzuInfo>> splitListHubzuInfos = ListUtils.partition(hubzuinfo.getHubzuInfos(), listSplitCount);

		splitListHubzuInfos.stream().forEach(listOfBatch -> {
			Set<String> propsFromHbz = listOfBatch.stream().map(HubzuInfo::getSelrPropIdVcNn).collect(Collectors.toSet());
			Set<String> assetFromHbz = propsFromHbz.stream().map(prop -> migrationPropToLoanMap.get(prop)).collect(Collectors.toSet());

			List<DPProcessParam> week0DBList = dpProcessParamsBO.findLatestNonDuplicateInWeek0ForGivenAsset(assetFromHbz);
			if (!week0DBList.isEmpty()) {
				consolidatedListOfweek0Params.addAll(week0DBList);
			}
		});

		Map<String, DPProcessParam> assetValueMap = consolidatedListOfweek0Params.stream()
				  .collect(Collectors.toMap(DPProcessParam::getPropTemp, Function.identity(), (r, s) -> r));

		hubzuinfo.getHubzuInfos().stream().forEach(item -> {
			if (assetValueMap.containsKey(item.getSelrPropIdVcNn()) && assetValueMap.get(item.getSelrPropIdVcNn()).getAssignmentDate() != null) {
				DateTime assignmentDate = DateConversionUtil.getEstDate(assetValueMap.get(item.getSelrPropIdVcNn()).getAssignmentDate());
				item.setAssignmentDate(assignmentDate.toDate());
			}
		});

		/*return hubzuinfo.getHubzuInfos().stream().filter(item -> {
			if (assetValueMap.containsKey(item.getSelrPropIdVcNn()) && assetValueMap.get(item.getSelrPropIdVcNn()).getAssignmentDate() != null
					&& item.getListStrtDateDtNn() != null) {
				DateTime assignmentDate = DateConversionUtil.getEstDate(assetValueMap.get(item.getSelrPropIdVcNn()).getAssignmentDate());
				DateTime listStartDate = new DateTime(item.getListStrtDateDtNn());
				if ((listStartDate.getYear() > assignmentDate.getYear()) || ((listStartDate.getYear() == assignmentDate.getYear()) && (
						listStartDate.getDayOfYear() >= assignmentDate.getDayOfYear())))
					return true;
				else
					return false;
			}
			return true;
		}).collect(Collectors.toList());*/
	}
}
