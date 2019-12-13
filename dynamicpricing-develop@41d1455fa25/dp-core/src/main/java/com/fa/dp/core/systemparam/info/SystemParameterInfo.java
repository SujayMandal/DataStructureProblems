/**
 * 
 */
package com.fa.dp.core.systemparam.info;

import com.fa.dp.core.base.info.BaseInfo;

import lombok.Data;

/**
 *
 *
 */
@Data
public class SystemParameterInfo extends BaseInfo {

	private static final long serialVersionUID = -2411493478026570079L;

	private String key;

	private String value;

	private String description;

}
