/**
 * 
 */
package com.fa.dp.core.tenant.info;

import com.fa.dp.core.base.info.BaseInfo;

import lombok.Data;

/**
 *
 *
 */
@Data
public class TenantInfo extends BaseInfo {

	private static final long serialVersionUID = 171221642387119640L;

	private String code;

	private String authCode;

	private String name;

}
