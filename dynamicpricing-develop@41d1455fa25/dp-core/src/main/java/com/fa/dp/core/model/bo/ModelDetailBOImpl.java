/**
 * 
 */
package com.fa.dp.core.model.bo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.model.dao.ModelDetailDao;
import com.fa.dp.core.model.domain.ModelDetail;

/**
 *
 *
 */
@Named
public class ModelDetailBOImpl implements ModelDetailBo {

    @Inject
    private ModelDetailDao modelDetailDao;

    /*
     * (non-Javadoc)
     * 
     * @see com.fa.ra.client.core.model.bo.ModelDetailBo#getAllModelDetails()
     */
    @Override
    public List<ModelDetail> getAllModelDetails() throws SystemException {
        return modelDetailDao.findAll();
    }

	@Override
	public List<ModelDetail> getMajorVersionDetails(String modelName) throws SystemException {
		// TODO Auto-generated method stub
		return modelDetailDao.findBymajorVersionIn(modelName);
	}

	@Override
	public List<ModelDetail> getMinorVersionDetails(String modelName) throws SystemException {
		// TODO Auto-generated method stub
		return modelDetailDao.findByminorVersionIn(modelName);
	}

}
