package com.fa.dp.core.apps.info;

import com.fa.dp.core.adgroup.info.ADGroupInfo;
import com.fa.dp.core.base.info.BaseInfo;
import lombok.Data;

@Data
public class AdGroupAppMappingInfo extends BaseInfo {

	private static final long serialVersionUID = 1171946635330336321L;

	private ADGroupInfo raTntAdGroup;

	private TenantAppInfo raTntApp;

}