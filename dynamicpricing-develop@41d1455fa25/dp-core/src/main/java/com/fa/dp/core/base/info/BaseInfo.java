/**
 * 
 */
package com.fa.dp.core.base.info;

import java.io.Serializable;

import org.joda.time.DateTime;

import lombok.Data;

@Data
public class BaseInfo implements Serializable {

	private static final long serialVersionUID = -7433706843115058411L;

	private String id;

	private String createdBy;

	private DateTime createdDate;

	private String lastModifiedBy;

	private DateTime lastModifiedDate;

	private String createdDateTime;

	private String lastModifiedDateTime;

}
