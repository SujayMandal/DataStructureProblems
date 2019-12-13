/**
 * 
 */
package com.fa.dp.business.test.delegate;

import java.util.List;

import com.fa.dp.business.test.info.TestInfo;
import com.fa.dp.core.exception.SystemException;

/**
 *
 *
 */
public interface TestDelegate {

	/**
	 * 
	 * @param
	 * @return
	 * @throws SystemException
	 */
	List<TestInfo> getAll() throws SystemException;

}
