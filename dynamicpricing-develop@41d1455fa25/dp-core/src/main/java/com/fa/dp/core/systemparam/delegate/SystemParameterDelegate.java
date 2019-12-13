/**
 * 
 */
package com.fa.dp.core.systemparam.delegate;

import java.util.List;

import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.systemparam.info.SystemParameterInfo;

/**
 *
 *
 */
public interface SystemParameterDelegate {

    /**
     * 
     * @param systemParameterInfo
     * @return
     * @throws SystemException
     */
    public SystemParameterInfo save(SystemParameterInfo systemParameterInfo) throws SystemException;

    /**
     * 
     * @return
     * @throws SystemException
     */
    public List<SystemParameterInfo> getAllSystemParamters() throws SystemException;

    /**
     * Returns the system parameter identified by the given key.
     * 
     * @param key
     * @return
     * @throws SystemException
     */
    public SystemParameterInfo getSystemParameterByKey(String key) throws SystemException;

}
