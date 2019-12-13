package com.ca.umg.business.pooling.bo;

import java.util.List;

import com.ca.framework.core.entity.ModeletRestartInfo;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.ModeletClientInfo;
import com.ca.umg.business.pooling.info.ModeletRestartDetails;
import com.ca.umg.business.pooling.model.CompletePoolDetails;
import com.ca.umg.business.pooling.model.ModeletPoolingDetails;

public interface ModeletPoolingBO {

    public List<CompletePoolDetails> getAllPoolDetails() throws SystemException;

	List<ModeletClientInfo> fetchAllModeletClients() throws SystemException;
	
	public ModeletPoolingDetails getModeletPoolingDetails() throws SystemException;
	
	public void createPool(final CompletePoolDetails poolDetails) throws SystemException, BusinessException;
	
	public void updatePool(final List<CompletePoolDetails> poolDetailList) throws SystemException, BusinessException;
	
	public void deletePool(final String poolId) throws SystemException, BusinessException;
	
	public boolean isModeletPoolingInProgress();
	
	public void setModeletPoolingInProgress();

	public void setModeletPoolingDone();	
	
	public List<CompletePoolDetails> searchPool(final String searchString) throws SystemException;

    public List<String> switchModelet(final ModeletClientInfo modeletClientInfo, final Object status);

	List<String> fetchModeletCommandResult(final ModeletClientInfo modeletClientInfo, final Object status);
    
    public ModeletRestartDetails getModeletRestartDetails() throws SystemException;
    
    public void deleteModeletSetting(ModeletRestartInfo modeletRestartInfo) throws SystemException;
    
    public void addModeletSetting(List<ModeletRestartInfo> modeletRestartInfoList) throws SystemException;
}
