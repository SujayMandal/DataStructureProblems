package com.fa.dp.business.sop.week0.input.info;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Data;

import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.core.base.info.BaseInfo;
import com.fa.dp.core.validate.util.constraints.CheckDateFormat;

@Data
public class DPSopWeek0ParamInfo extends BaseInfo {

	private static final long serialVersionUID = 4241943729380605583L;
	
	private static final String DATE_FORMAT = "^(0?[1-9]|1[012])[\\\\/\\\\-](0?[1-9]|[12][0-9]|3[01])[\\\\\\\\/\\\\\\\\-](\\d\\d)$";

	private static final String CLASSIFICATION_ALLOWED_VALUE = "^(?:OCN|NRZ|PHH)$";
	
	@NotBlank(message = "{DP013}")
	@Min(value = 0, message = "{DP014}")
	private String listPrice;

	@NotBlank(message = "{DP005}")
	@Min(value = 0, message = "{DP006}")
	private String assetNumber;

	private String oldAssetNumber;

	private String propTemp;

	private String state;

	private String propertyType;

	@NotBlank(message = "{DP008}")
	private String status;

	@NotNull(message = "{DP009}")
	@DecimalMin(value = "0", message = "{DP010}")
	private BigDecimal assetValue;

	@NotBlank(message = "{DP011}")
	@CheckDateFormat(message = "{DP012}", pattern = "mm/dd/yy", regexp = DATE_FORMAT)
	private String avSetDate;

	private String reoDate;

	@NotBlank(message = "{DP015}")
	@Pattern(regexp = CLASSIFICATION_ALLOWED_VALUE, message = "{DP016}")
	private String classification;

	private String eligible;

	private String assignment;

	private Long assignmentDate;

	private String notes;

	private String errorDetail;

	private String failedStepCommandName;

	private DPSopWeek0ProcessStatusInfo sopWeek0ProcessStatus;

	private int lowerSlab;

	private int higherSlab;

	private int lowerAssetValue;

	private int higherAssetValue;

	private int modeledCount;

	private int benchmarkCount;

	private String uploadFlag;
	
}
