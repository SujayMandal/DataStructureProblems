package com.fa.dp.core.transaction.info;

import com.fa.dp.core.base.info.BaseInfo;
import com.fa.dp.core.util.RAClientConstants;
import com.fa.dp.core.util.RAClientUtil;
import lombok.Data;

@Data
public class TransactionInfo extends BaseInfo {

	private static final long serialVersionUID = 7191199001692744603L;
	private String tenantCode;
	private String clientTransactionId;
	private String raTransactionId;
	private String status;
	private long transactionDate;
	private String user;
	private String modelName;
	private Integer majorVersion;
	private String minorVersion;
	private byte[] tenantInput;
	private byte[] tenantOutput;
	private String transactionDateStr;

	public String getTransactionDateStr() {
		return RAClientUtil.getDateFormatEpoch(this.transactionDate, RAClientConstants.RA_UTC_DATE_FORMAT);
	}

}
