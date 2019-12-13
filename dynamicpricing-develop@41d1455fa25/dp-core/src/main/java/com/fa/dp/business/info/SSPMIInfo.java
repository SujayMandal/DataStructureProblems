package com.fa.dp.business.info;

import java.io.Serializable;

import lombok.Data;

@Data
public class SSPMIInfo implements Serializable {

	private static final long serialVersionUID = -4252061173313590323L;

	private String assetNumber;

	private boolean pmiFlag;

	private String insuranceId;

	private String clientCode;

}
