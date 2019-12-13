package com.fa.dp.core.apps.info;

import com.fa.dp.core.base.info.BaseInfo;

import lombok.Data;

@Data
public class TenantAppInfo extends BaseInfo {

	private static final long serialVersionUID = -6410171254518455734L;

	private String name;
	private String code;
	private String description;
	private String appLaunchUrl;
	private String appLaunchPage;
}
