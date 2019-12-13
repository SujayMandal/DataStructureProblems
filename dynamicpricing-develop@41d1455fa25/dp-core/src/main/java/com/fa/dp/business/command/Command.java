/**
 * 
 */
package com.fa.dp.business.command;

import com.fa.dp.core.exception.SystemException;

/**
 * @author mandasuj
 *
 */
public interface Command {

	/**
     * This method executes the process for filtering the loans.
     * 
     * @param data
     *            has all information embedded that the excel file has provided.
     * 
     * @throws SystemException
     *             when system cannot complete the requested step.
     */
    void execute(Object data) throws SystemException;
    
}
