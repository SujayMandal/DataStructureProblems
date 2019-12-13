package com.ca.umg.modelet.transport.handler;

import com.ca.umg.modelet.common.ModelRequestInfo;
import com.ca.umg.modelet.runtime.RuntimeProcess;

public interface InboundRequestHandler {
	
	RuntimeProcess getRuntimeProcess(ModelRequestInfo modelRequestInfo);

}
