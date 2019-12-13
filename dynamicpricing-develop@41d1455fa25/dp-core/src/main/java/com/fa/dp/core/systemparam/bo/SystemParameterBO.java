/**
 * 
 */
package com.fa.dp.core.systemparam.bo;

import java.util.List;

import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.systemparam.domain.SystemParameter;

/**
 *
 *
 */
public interface SystemParameterBO {

    /**
     * Persists {@link SystemParameter} to the database.
     * 
     * @param systemParameter
     * @return
     * @throws SystemException
     */
    public SystemParameter save(SystemParameter systemParameter) throws SystemException;

    /**
     * Returns all system parameters defined in the system.
     * 
     * @return
     * @throws SystemException
     */
    public List<SystemParameter> getAllSystemParameters() throws SystemException;

    /**
     * Returns system parameter key identified by the given key.
     * 
     * @param key
     * @return
     * @throws SystemException
     */
    public SystemParameter getSystemParameterByKey(String key) throws SystemException;

}
