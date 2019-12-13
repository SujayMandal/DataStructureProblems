package com.ca.umg.modelet.runtime;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.common.ModelResponseInfo;
import com.ca.umg.modelet.common.SystemInfo;

public interface RuntimeProcess {

    public ModelResponseInfo execute(Object input, SystemInfo systemInfo) throws BusinessException, SystemException;

    public ModelResponseInfo loadModel(Object input) throws BusinessException, SystemException;

    public ModelResponseInfo unloadModel(Object input) throws BusinessException, SystemException;

    public ModelResponseInfo getLoadedModels(Object input) throws BusinessException, SystemException;

    public void stopRServeProcess();

    public void startRServeProcess() throws SystemException;
}
