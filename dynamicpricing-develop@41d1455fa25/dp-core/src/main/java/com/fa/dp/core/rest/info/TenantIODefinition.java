/**
 * 
 */
package com.fa.dp.core.rest.info;

import lombok.Data;

import java.io.Serializable;

/**
 *
 *
 */
@Data
public class TenantIODefinition implements Serializable {

	private static final long serialVersionUID = -3507945070923052449L;

	private String tenantInputs;

	private String tenantOutputs;

}
