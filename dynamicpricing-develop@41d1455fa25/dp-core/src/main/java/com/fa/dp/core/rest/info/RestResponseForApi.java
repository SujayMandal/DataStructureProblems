package com.fa.dp.core.rest.info;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RestResponseForApi<T> implements Serializable {

	private static final long serialVersionUID = 7321765769875813369L;

	private boolean error;

	private String message;

	private List<RaApiErrorResponse> errorResponse;

	private T response;
}
