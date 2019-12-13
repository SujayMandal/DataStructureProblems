package com.fa.dp.business.pmi.info;

import javax.validation.constraints.NotBlank;

import com.fa.dp.core.base.info.BaseInfo;

import lombok.Data;

@Data
public class PmiInsuranceCompanyInfo extends BaseInfo {

	private static final long serialVersionUID = -127302506006242573L;
	
	private PmiInsuranceCompaniesFileInfo pmiCompaniesFileId;

	@NotBlank(message = "{DP032}")
	private String insuranceCompany;

	@NotBlank(message = "{DP033}")
	private String companyCode;

}
