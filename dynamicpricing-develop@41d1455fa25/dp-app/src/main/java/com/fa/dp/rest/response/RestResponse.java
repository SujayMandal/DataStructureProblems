package com.fa.dp.rest.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class RestResponse<T> implements Serializable {

	private static final long serialVersionUID = 1749516772225464430L;
	private T response;

	private boolean success = true;

	private String errorCode;

	private String message;

}
