package com.fa.dp.business.weekn.run.status.info;

import com.fa.dp.core.base.info.BaseInfo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
public class WeekNDailyRunStatusInfo extends BaseInfo {

	private static final long serialVersionUID = 1643275159916694102L;
	private LocalDateTime lastRunDate;

	private Integer totalRecord;

	private Integer successCount;

	private Integer failCount;

	private String reportType;

	private Long startTime;

	private Long endTime;

	private LocalDate fetchStartDate;

	private LocalDate fetchEndDate;
}
