/**
 * 
 */
package com.fa.dp.core.systemparam.bo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.systemparam.dao.SystemParameterDao;
import com.fa.dp.core.systemparam.domain.SystemParameter;

/**
 *
 *
 */
@Named
public class SystemParameterBOImpl implements SystemParameterBO {

    @Inject
    private SystemParameterDao systemParameterDao;
    
    /*
     * (non-Javadoc)
     * 
     * @see com.fa.ra.client.core.systemparam.bo.SystemParameterBO#save(com.fa.ra.client.core.systemparam.domain.SystemParameter)
     */
    @Override
    public SystemParameter save(SystemParameter systemParameter) throws SystemException {
        SystemParameter updateSystemParamer = systemParameterDao.findByKey(systemParameter.getKey());
        if (updateSystemParamer == null) {
            SystemException.newSystemException(CoreExceptionCodes.SYSPAR001, new Object[] { systemParameter.getKey() });
        }else{
        	updateSystemParamer.setValue(systemParameter.getValue());
        	updateSystemParamer = systemParameterDao.save(updateSystemParamer);
        }
        return updateSystemParamer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fa.ra.client.core.systemparam.bo.SystemParameterBO#getAllSystemParameters()
     */
    @Override
    public List<SystemParameter> getAllSystemParameters() throws SystemException {
        return systemParameterDao.findAll();
    }

    @Override
    public SystemParameter getSystemParameterByKey(String key) throws SystemException {
        return systemParameterDao.findByKey(key);
    }

}
