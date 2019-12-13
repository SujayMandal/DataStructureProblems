package com.fa.dp.business.weekn.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "DP_WEEKN_STATES")
public class DPProccessWeekNStates extends AbstractAuditable {

	private static final long serialVersionUID = 2397218862651403390L;
	@Column(name = "STATE")
	private String state;

	@Column(name = "ZIP_CODE")
	private String zipCode;

}
