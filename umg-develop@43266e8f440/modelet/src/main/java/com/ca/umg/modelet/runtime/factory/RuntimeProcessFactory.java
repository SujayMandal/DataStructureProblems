package com.ca.umg.modelet.runtime.factory;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.umg.modelet.common.ModeletEngine;
import com.ca.umg.modelet.common.SystemInfo;
import com.ca.umg.modelet.runtime.RuntimeProcess;
import com.ca.umg.modelet.runtime.impl.ExcelRuntimeProcess;
import com.ca.umg.modelet.runtime.impl.MatlabRuntimeProcess;
import com.ca.umg.modelet.runtime.impl.RRuntimeProcess;

@Named
public class RuntimeProcessFactory {
	
	@Inject
    private SystemInfo systemInfo;

    public RuntimeProcess getRuntimeProcessInstance(final String processType, final RuntimeFactory runtimeFactory) {
        RuntimeProcess process = null;
        switch (ModeletEngine.valueOf(processType.toUpperCase(Locale.getDefault()))) {
        case MATLAB:
            MatlabRuntimeProcess mrProcess = new MatlabRuntimeProcess(runtimeFactory.getMatlabRuntime());
            process = mrProcess;
            break;
        case R:
        	if(systemInfo.getrMode() != null && StringUtils.equalsIgnoreCase(SystemConstants.R_SERVE, systemInfo.getrMode())) {
        		RRuntimeProcess rServeProcess = new RRuntimeProcess(runtimeFactory.getRserveRuntime());
                process = rServeProcess;
        	} else {
        		RRuntimeProcess rProcess = new RRuntimeProcess(runtimeFactory.getrRuntime());
                process = rProcess;
        	}
            
            break;
        case EXCEL:
            process = new ExcelRuntimeProcess(runtimeFactory.getExcelRuntime());
            break;
        default:
            break;
        }
        return process;
    }

}
