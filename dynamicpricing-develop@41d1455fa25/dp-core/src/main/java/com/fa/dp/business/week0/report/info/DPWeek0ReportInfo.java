package com.fa.dp.business.week0.report.info;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DPWeek0ReportInfo implements Serializable {

	private static final long serialVersionUID = -7345042107985742346L;

	private String assetNumber;

	private String propTemp;

	private String oldAssetNumber;

	private BigDecimal assetValue;

	private String avSetDate;

	private String classification;

	private String clientCode;

	private BigDecimal listPrice;

	private String status;

	private String assignment;

	private Long assignmentDate;

	private String eligible;

	private String notes;

	private BigDecimal week0Price;

	private String state;

	private String rtSource;

	private String propertyType;

	private String pctAV;

	private String withinBusinessRules;
}
