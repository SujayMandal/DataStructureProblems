package com.ca.modelet.client;

import java.io.Serializable;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;

public interface ModeletClient extends Serializable {

    void createConnection() throws SystemException;

    String sendData(String input) throws SystemException, BusinessException;

    void shutdownConnection() throws SystemException;

}
