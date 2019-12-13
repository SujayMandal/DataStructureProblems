package com.fa.dp.business.info;

import lombok.Data;

import java.util.List;

@Data
public class StageFiveDBResponse extends Response {

	private static final long serialVersionUID = 6969821784982686613L;

	private List<StageFiveInfo> stageFiveInfos;

}
