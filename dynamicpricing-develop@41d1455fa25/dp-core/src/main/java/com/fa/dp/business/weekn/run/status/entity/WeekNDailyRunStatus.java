package com.fa.dp.business.weekn.run.status.entity;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "WEEKN_DAILY_RUN_STATUS")
public class WeekNDailyRunStatus extends AbstractAuditable {

	private static final long serialVersionUID = -8718244729056805115L;
	@Column(name = "LAST_RUN_DATE")
	private LocalDateTime lastRunDate;

	@Column(name = "TOTAL_RECORD")
	private Integer totalRecord;

	@Column(name = "SUCCESS_COUNT")
	private Integer successCount;

	@Column(name = "FAIL_COUNT")
	private Integer failCount;

	@Column(name = "REPORT_TYPE")
	private String reportType;

	@Column(name = "START_TIME")
	private Long startTime;

	@Column(name = "END_TIME")
	private Long endTime;

	@Column(name = "FETCH_START_DATE")
	private LocalDate fetchStartDate;

	@Column(name = "FETCH_END_DATE")
	private LocalDate fetchEndDate;

}
