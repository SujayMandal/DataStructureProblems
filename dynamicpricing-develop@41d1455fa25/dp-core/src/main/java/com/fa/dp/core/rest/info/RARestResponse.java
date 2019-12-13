package com.fa.dp.core.rest.info;

import lombok.Data;

import java.io.Serializable;

@Data
public class RARestResponse<T> implements Serializable {

	private static final long serialVersionUID = -1170166854668946109L;

	private boolean error;

	private String errorCode;

	private String message;

	private T response;

}
