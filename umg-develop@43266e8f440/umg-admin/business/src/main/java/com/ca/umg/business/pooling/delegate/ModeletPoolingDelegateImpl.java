package com.ca.umg.business.pooling.delegate;

import java.util.List;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import com.ca.framework.core.entity.ModeletRestartInfo;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.ModeletClientInfo;
import com.ca.umg.business.pooling.bo.ModeletPoolingBO;
import com.ca.umg.business.pooling.info.ModeletRestartDetails;
import com.ca.umg.business.pooling.model.CompletePoolDetails;
import com.ca.umg.business.pooling.model.ModeletPoolingDetails;

@Named
public class ModeletPoolingDelegateImpl implements ModeletPoolingDelegate {

	@Autowired
	private ModeletPoolingBO bo;

	@Override
	@PreAuthorize("hasRole(T(com.ca.umg.business.constants.BusinessConstants).ROLE_SUPER_ADMIN)")
	public List<CompletePoolDetails> getAllPoolDetails() throws SystemException {
		return bo.getAllPoolDetails();
	}

	@Override
	@PreAuthorize("hasRole(T(com.ca.umg.business.constants.BusinessConstants).ROLE_SUPER_ADMIN)")
	public List<ModeletClientInfo> getAllModeletClientDetails() throws SystemException {
		return bo.fetchAllModeletClients();
	}
	
	@Override
	@PreAuthorize("hasRole(T(com.ca.umg.business.constants.BusinessConstants).ROLE_SUPER_ADMIN)")
	public ModeletPoolingDetails getModeletPoolingDetails() throws SystemException {
		return bo.getModeletPoolingDetails();
	}
	
	@Override
	public void createPool(final CompletePoolDetails poolDetails) throws SystemException, BusinessException {
		bo.createPool(poolDetails);
	}
	
	@Override
	public void updatePool(final List<CompletePoolDetails> poolDetailList) throws SystemException, BusinessException {
		bo.updatePool(poolDetailList);
	}
	
	@Override
	public void deletePool(final String poolId) throws SystemException, BusinessException {
		bo.deletePool(poolId);
	}
	
	@Override
	public boolean isModeletPoolingInProgress() {
		return bo.isModeletPoolingInProgress();
	}
	
	@Override
	public void setModeletPoolingInProgress() {
		bo.setModeletPoolingInProgress();
	}

	@Override
	public void setModeletPoolingDone() {
		bo.setModeletPoolingDone();
	}
	
	@Override
	public List<CompletePoolDetails> searchPool(final String searchString) throws SystemException {
		return bo.searchPool(searchString);
	}

    @Override
    public List<String> switchModelet(final ModeletClientInfo modeletClientInfo, final Object status) {
        return bo.switchModelet(modeletClientInfo, status);
    }

	@Override
	public List<String> fetchModeletCommandResult(ModeletClientInfo modeletClientInfo, Object status) {
		return bo.fetchModeletCommandResult(modeletClientInfo, status);
	}

	@Override
	public ModeletRestartDetails getModeletRestartDetails() throws SystemException {		
		return bo.getModeletRestartDetails();
	}
	
	@Override
	public ModeletRestartDetails addModeletSetting(List<ModeletRestartInfo> modeletRestartInfoList) throws SystemException {		
		bo.addModeletSetting(modeletRestartInfoList);
		return bo.getModeletRestartDetails();
	
	}
	
	@Override
	public ModeletRestartDetails deleteModeletSetting(ModeletRestartInfo modeletRestartInfo) throws SystemException {		
		bo.deleteModeletSetting(modeletRestartInfo);
		return bo.getModeletRestartDetails();
		
	}
}