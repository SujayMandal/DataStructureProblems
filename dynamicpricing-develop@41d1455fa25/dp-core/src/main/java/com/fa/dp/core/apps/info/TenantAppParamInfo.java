package com.fa.dp.core.apps.info;

import lombok.Data;

@Data
public class TenantAppParamInfo {
	private String attrKey;
	private String attrValue;
	private TenantAppInfo tenantApp;
}
