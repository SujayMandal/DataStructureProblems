/**
 * 
 */
package com.fa.dp.core.model.delegate;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.model.bo.ModelDetailBo;
import com.fa.dp.core.model.domain.ModelDetail;
import com.fa.dp.core.model.info.ModelDetailInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
@Named
public class ModelDetailDelegateImpl extends AbstractDelegate implements ModelDetailDelegate {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModelDetailDelegateImpl.class);

	@Inject
	private ModelDetailBo modelDetailBO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fa.ra.client.core.model.delegate.ModelDetailDelegate#
	 * getAllModelDetails()
	 */
	@Override
	public List<ModelDetailInfo> getAllModelDetails() throws SystemException {
		List<ModelDetail> modelDetails = modelDetailBO.getAllModelDetails();
		return convertToList(modelDetails, ModelDetailInfo.class);
	}

	@Override
	public List<ModelDetailInfo> getMajorVersionDetails(String modelName) throws SystemException {
		// TODO Auto-generated method stub
		List<ModelDetail> modelDetails = modelDetailBO.getMajorVersionDetails(modelName);
		return convertToList(modelDetails, ModelDetailInfo.class);
	}

	@Override
	public List<ModelDetailInfo> getMinorVersionDetails(String modelName) throws SystemException {
		// TODO Auto-generated method stub
		List<ModelDetail> modelDetails = modelDetailBO.getMinorVersionDetails(modelName);
		return convertToList(modelDetails, ModelDetailInfo.class);
	}

}
