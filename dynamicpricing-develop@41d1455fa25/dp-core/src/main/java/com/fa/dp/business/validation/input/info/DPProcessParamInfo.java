package com.fa.dp.business.validation.input.info;

import com.fa.dp.business.base.input.info.DPProcessParamBaseInfo;
import com.fa.dp.business.info.RTNGResponse;
import com.fa.dp.business.info.RrResponse;
import com.fa.dp.core.validate.util.constraints.CheckDateFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class DPProcessParamInfo extends DPProcessParamBaseInfo implements Serializable {

	private static final long serialVersionUID = -5339176426494256875L;

	private String id;

	@NotBlank(message = "{DP008}")
	private String status;

	@NotBlank(message = "{DP009}")
	@Min(value = 0, message = "{DP010}")
	private String assetValue;

	@NotBlank(message = "{DP011}")
	@CheckDateFormat(message = "{DP012}", pattern = "mm/dd/yy", regexp = DATE_FORMAT)
	private String avSetDate;

	@NotBlank(message = "{DP013}")
	@Min(value = 0, message = "{DP014}")
	private String listPrice;

	private Long assignmentDate;

	private String notes;

	private String errorDetail;

	private String propertyType;

	private String rtSource;

	private BigDecimal week0Price;

	private String pctAV;

	private String withinBusinessRules;

	private DPFileProcessStatusInfo dynamicPricingFilePrcsStatus;

	private String ensemble;

	private String notesRa;

	private String prMode;

	private RrResponse rrResponse;

	private RTNGResponse rtngResponse;

	private String zip;

	private String address;

	private String timestamp;

	private String message;

	private String estimated;

	private String fsd;

	private String generatedZip;

	private String timestampREO;

	private String messageREO;

	private String estimatedREO;

	private String fsdREO;

	private String generatedZipREO;

	private BigInteger startTime;

	private BigInteger endTime;

	private int lowerSlab;

	private int higherSlab;

	private int lowerAssetValue;

	private int higherAssetValue;

	private int modeledCount;

	private int benchmarkCount;

	private String uploadFlag;



}
