/**
 * 
 */
package com.fa.dp.core.systemparam.delegate;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.systemparam.bo.SystemParameterBO;
import com.fa.dp.core.systemparam.domain.SystemParameter;
import com.fa.dp.core.systemparam.info.SystemParameterInfo;

/**
 *
 *
 */
@Named
public class SystemParameterDelegateImpl extends AbstractDelegate implements SystemParameterDelegate {

    @Inject
    private SystemParameterBO systemParameterBO;

    /*
     * (non-Javadoc)
     * 
     * @see com.fa.ra.client.core.systemparam.delegate.SystemParameterDelegate#save(com.fa.ra.client.core.systemparam.domain.
     * SystemParameter)
     */
    @Override
    public SystemParameterInfo save(SystemParameterInfo systemParameterInfo) throws SystemException {
        SystemParameter systemParameter = convert(systemParameterInfo, SystemParameter.class);
        return convert(systemParameterBO.save(systemParameter), SystemParameterInfo.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fa.ra.client.core.systemparam.delegate.SystemParameterDelegate#getAllSystemParamters()
     */
    @Override
    public List<SystemParameterInfo> getAllSystemParamters() throws SystemException {
        List<SystemParameter> systemParameters = systemParameterBO.getAllSystemParameters();
        return convertToList(systemParameters, SystemParameterInfo.class);
    }

    @Override
    public SystemParameterInfo getSystemParameterByKey(String key) throws SystemException {
        SystemParameter systemParameter = systemParameterBO.getSystemParameterByKey(key);
        return convert(systemParameter, SystemParameterInfo.class);
    }

}
