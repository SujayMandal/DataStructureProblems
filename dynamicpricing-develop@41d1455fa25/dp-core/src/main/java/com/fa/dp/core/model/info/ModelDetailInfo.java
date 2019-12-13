/**
 * 
 */
package com.fa.dp.core.model.info;

import com.fa.dp.core.base.info.BaseInfo;
import com.fa.dp.core.tenant.info.TenantInfo;

import lombok.Data;

/**
 *
 *
 */
@Data
public class ModelDetailInfo extends BaseInfo {

	private static final long serialVersionUID = 6127562099851142737L;

	private String name;

	private Integer majorVersion;

	private String minorVersion;

	private TenantInfo tenant;

	private String description;

}
