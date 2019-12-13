package com.fa.dp.business.info;

import lombok.Data;

import java.util.List;

@Data
public class HubzuDBResponse extends Response {

	private static final long serialVersionUID = -2083801162787702250L;

	private List<HubzuInfo> hubzuInfos;

}
