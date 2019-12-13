package com.ca.umg.business.tenant.report.usage;

import java.util.List;

import com.ca.umg.business.common.info.PagingInfo;

public class UsageTransactionWrapper {

	private UsageReportPageInfo pagingInfo;

	private List<UsageTransactionInfo> transactionInfoList;

	public List<UsageTransactionInfo> getTransactionInfoList() {
		return transactionInfoList;
	}

	public PagingInfo getPagingInfo() {
		return pagingInfo;
	}

	public void setPagingInfo(final UsageReportPageInfo pagingInfo) {
		this.pagingInfo = pagingInfo;
	}

	public void setTransactionInfoList(final List<UsageTransactionInfo> transactionInfoList) {
		this.transactionInfoList = transactionInfoList;
	}
}