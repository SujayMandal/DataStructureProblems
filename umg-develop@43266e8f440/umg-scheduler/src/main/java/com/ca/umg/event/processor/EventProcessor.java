/**
 * 
 */
package com.ca.umg.event.processor;

import com.ca.framework.core.exception.SystemException;

/**
 * @author kamathan
 *
 */
public interface EventProcessor<T> {

    public void processEvent() throws SystemException;

}
