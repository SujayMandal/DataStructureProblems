package com.fa.dp.business.pmi.info;

import com.fa.dp.core.base.info.BaseInfo;

import lombok.Data;

@Data
public class PmiInsuranceCompaniesFileInfo extends BaseInfo {

	private static final long serialVersionUID = -1830708000298270309L;

	private String id;

	private String uploadedFileName;

	private boolean active;

}
