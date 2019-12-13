package com.fa.dp.business.ssinvestor.info;

import com.fa.dp.core.base.info.BaseInfo;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SpclServicingInvestorInfo extends BaseInfo {

	private static final long serialVersionUID = -5793468312072274611L;
	private SpclServicingInvestorFileInfo ssInvestorFileId;

	@NotBlank(message = "{DP032}")
	private String investorCode;

	@NotBlank(message = "{DP033}")
	private String investorName;

}
