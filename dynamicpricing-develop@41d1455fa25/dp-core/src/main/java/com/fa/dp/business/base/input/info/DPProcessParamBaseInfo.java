package com.fa.dp.business.base.input.info;

import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.core.base.info.BaseInfo;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class DPProcessParamBaseInfo extends BaseInfo implements Serializable {

	protected static final String DATE_FORMAT = "^(0?[1-9]|1[012])[\\\\/\\\\-](0?[1-9]|[12][0-9]|3[01])[\\\\\\\\/\\\\\\\\-](\\d\\d)$";

	protected static final String CLASSIFICATION_ALLOWED_VALUE = "^(?:OCN|NRZ|PHH)$";
	private static final long serialVersionUID = 1229970225935273830L;

	@NotBlank(message = "{DP005}")
	@Min(value = 0, message = "{DP006}")
	private String assetNumber;

	private String oldAssetNumber;

	private String propTemp;

	@NotBlank(message = "{DP015}")
	@Pattern(regexp = CLASSIFICATION_ALLOWED_VALUE, message = "{DP016}")
	private String classification;

	private String eligible;

	private String assignment;

	private Long updateTimestamp;

	private String state;

	@NotBlank(message = "{DP007}")
	private String clientCode;

	private CommandInfo command;
}
