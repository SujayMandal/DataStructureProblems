/**
 * 
 */
package com.ca.umg.business.mid.extraction;

import java.io.InputStream;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mid.extraction.info.MidIOInfo;

/**
 * @author chandrsa
 *
 */
public interface MidExtractor {

    /**
     * This method would extract the model IO definition.
     * 
     * @param modelIODefinition
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    MidIOInfo extractModelIO(InputStream modelIODefinition) throws BusinessException, SystemException;
}
