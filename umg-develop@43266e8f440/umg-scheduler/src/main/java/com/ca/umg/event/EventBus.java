/**
 * 
 */
package com.ca.umg.event;

import com.ca.framework.core.exception.SystemException;

/**
 * @author kamathan
 *
 */
public interface EventBus<T> {

    /**
     * 
     * @param event
     */
    public void add(T event);

    /**
     * 
     * @return
     * @throws SystemException
     */
    public T take() throws SystemException;

}
