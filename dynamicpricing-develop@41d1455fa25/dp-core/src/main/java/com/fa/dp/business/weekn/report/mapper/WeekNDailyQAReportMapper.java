package com.fa.dp.business.weekn.report.mapper;

import com.fa.dp.business.weekn.report.entity.SOPWeekNDailyQAReport;
import com.fa.dp.business.weekn.report.entity.WeekNDailyQAReport;
import com.fa.dp.business.weekn.report.info.WeekNDailyQAReportInfo;
import org.joda.time.DateTime;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WeekNDailyQAReportMapper {

	WeekNDailyQAReport infoToWeekNDailyQAReportMapper(WeekNDailyQAReportInfo weekNDailyQAReportInfo);

	WeekNDailyQAReportInfo WeekNDailyQAReportToInfoMapper(WeekNDailyQAReport weekNDailyQAReport);

	WeekNDailyQAReportInfo sopWeekNDailyQAReportToInfoMapper(SOPWeekNDailyQAReport sopWeekNDailyQAReport);

	DateTime map(Long date);



}
