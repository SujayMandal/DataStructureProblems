package com.fa.dp.business.info;

import lombok.Data;

import java.util.List;

@Data
public class RTNGResponse extends Response {

	private static final long serialVersionUID = -549958717924720638L;

	private List<RtngInfo> rtngInfos;

}
