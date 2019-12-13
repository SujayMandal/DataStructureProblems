package com.ca.umg.business.modelet.profiler.param.info;

import com.ca.framework.core.info.BaseInfo;
import com.ca.umg.business.modelet.profiler.info.ModeletProfilerInfo;
import com.ca.umg.business.modelet.profiler.key.info.ModeletProfilerKeyInfo;

public class ModeletProfilerParamInfo extends BaseInfo {

	private static final long serialVersionUID = 1122082709115596424L;
	private ModeletProfilerInfo modeletProfiler;

	private ModeletProfilerKeyInfo modeletProfilerKey;

	private String paramValue;

	public ModeletProfilerInfo getModeletProfiler() {
		return modeletProfiler;
	}

	public void setModeletProfiler(ModeletProfilerInfo modeletProfiler) {
		this.modeletProfiler = modeletProfiler;
	}

	public ModeletProfilerKeyInfo getModeletProfilerKey() {
		return modeletProfilerKey;
	}

	public void setModeletProfilerKey(ModeletProfilerKeyInfo modeletProfilerKey) {
		this.modeletProfilerKey = modeletProfilerKey;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
}
