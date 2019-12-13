package com.fa.dp.business.weekn.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the DP_WEEKN_PRCS_STATUS database table.
 * 
 */
@Entity
@Setter
@Getter
@Table(name = "DP_WEEKN_HOLIDAYS")
public class DPWeekNHolidays extends AbstractAuditable {

	private static final long serialVersionUID = -6070408752485106556L;
	@Column(name = "HOLIDAY_TIMESTAMP")
	private String holidayTimestamp;

}
