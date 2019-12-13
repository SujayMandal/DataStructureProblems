package com.fa.dp.business.validation.input.base.info;

import lombok.Data;

import java.io.Serializable;

@Data
public class DPProcessParamEntryBaseInfo implements Serializable {

	private static final long serialVersionUID = -7217492033843801474L;

	private int columnCount;

	private boolean reprocess;
}
