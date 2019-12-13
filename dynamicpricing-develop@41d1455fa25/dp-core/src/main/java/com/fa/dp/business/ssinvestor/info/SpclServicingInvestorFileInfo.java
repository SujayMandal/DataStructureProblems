package com.fa.dp.business.ssinvestor.info;

import java.io.Serializable;

import lombok.Data;

@Data
public class SpclServicingInvestorFileInfo implements Serializable {

	private static final long serialVersionUID = 3675996062618153899L;
	private String id;

	private String uploadedFileName;

	private boolean active;

	private String createdBy;

	private Long createdDate;

}
