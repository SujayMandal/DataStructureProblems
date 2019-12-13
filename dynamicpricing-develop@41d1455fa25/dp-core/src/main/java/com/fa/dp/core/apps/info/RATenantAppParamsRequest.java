package com.fa.dp.core.apps.info;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;

@Data
public class RATenantAppParamsRequest {

	@NotBlank(message = "{DPSP001}")
	private String type;

	@NotBlank(message = "{DPSP002}")
	private String authToken;

	@NotBlank(message = "{DPSP003}")
	private String modelName;

	@NotBlank(message = "{DPSP004}")
	private String majorVersion;

	private String minorVersion;

	@NotBlank(message = "{DPSP005}")
	@Pattern(regexp = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})(?:;"
			+ "[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})" + ")*$", message = "Invalid Email To List")
	private String emailToList;

	@NotBlank(message = "{DPSP006}")
	@Pattern(regexp = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})(?:;"
			+ "[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})" + ")*$", message = "Invalid Email CC List")
	private String emailCcList;

	@NotBlank(message = "{DPSP007}")
	private String emailSubject;

	private String priceModeInput;

	private String emailContent;

	private String emailFrom;

	private String lastUpdatedBy;

	private String lastUpdatedOn;

	private Map<String, List<String>> errorMessages;
}
