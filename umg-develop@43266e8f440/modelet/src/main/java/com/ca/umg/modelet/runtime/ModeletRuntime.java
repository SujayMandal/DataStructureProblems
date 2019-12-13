package com.ca.umg.modelet.runtime;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.common.HeaderInfo;

public interface ModeletRuntime {

    /**
     * Checks and returns if underlying modelet runtime is initialized or not.
     * 
     * @return
     */
    boolean isInitialized();

    /**
     * Initializes underlying modelet runtime
     * 
     * @throws SystemException
     */
    void initializeRuntime() throws SystemException;

    /**
     * Destroys the underlying modelet runtime.
     */
    void destroyRuntime();

    /**
     * Returns the model to be executed on underlying modelet runtime.
     * 
     * @param headerInfo
     * @return
     * @throws SystemException
     */
    Object getModel(HeaderInfo headerInfo) throws SystemException;

    void destroyConnection();

}
