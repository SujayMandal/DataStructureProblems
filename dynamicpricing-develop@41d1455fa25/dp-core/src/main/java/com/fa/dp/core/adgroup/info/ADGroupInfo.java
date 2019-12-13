/**
 * 
 */
package com.fa.dp.core.adgroup.info;

import com.fa.dp.core.base.info.BaseInfo;
import com.fa.dp.core.tenant.info.TenantInfo;

import lombok.Data;

/**
 *
 *
 */
@Data
public class ADGroupInfo extends BaseInfo {

	private static final long serialVersionUID = -3382043010323347384L;

	private String name;

	private String type;

	private TenantInfo tenant;

}
