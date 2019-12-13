package com.fa.dp.business.info;

import lombok.Data;

import java.io.Serializable;

@Data
public class Response implements Serializable {

	private static final long serialVersionUID = -5776628979422571545L;

	private String transactionStatus;

	private String errorMsg;

}
