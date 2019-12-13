package com.fa.dp.core.rest.info;

import lombok.Data;

import java.io.Serializable;

@Data
public class RaApiErrorResponse implements Serializable {

	private static final long serialVersionUID = -7034829941300702312L;
	private String errorCode;

	private String field;

	private String message;
}
