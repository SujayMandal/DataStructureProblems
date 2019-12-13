/**
 * 
 */
package com.fa.dp.core.model.bo;

import java.util.List;

import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.model.domain.ModelDetail;

/**
 *
 *
 */
public interface ModelDetailBo {

    /**
     * 
     * @return
     * @throws SystemException
     */
    public List<ModelDetail> getAllModelDetails() throws SystemException;
    
    public List<ModelDetail> getMajorVersionDetails(String modelName)throws SystemException;
    
    public List<ModelDetail> getMinorVersionDetails(String modelName)throws SystemException;
}
