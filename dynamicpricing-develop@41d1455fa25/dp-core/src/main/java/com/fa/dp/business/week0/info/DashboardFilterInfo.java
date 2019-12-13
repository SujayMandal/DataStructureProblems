package com.fa.dp.business.week0.info;

import java.util.List;

import lombok.Data;

@Data
public class DashboardFilterInfo {

	private String weekType;
	private String fileName;
	private List<String> status;
	private Long fromDate;
	private Long toDate;
}
