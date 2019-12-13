/**
 * 
 */
package com.ca.umg.event;

import com.ca.framework.core.exception.SystemException;

/**
 * @author kamathan
 *
 */
public interface EventBusPoller<T> {

    public T take() throws SystemException;

}
