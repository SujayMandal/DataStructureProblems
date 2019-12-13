package com.ca.umg.business.encryption;

import javax.crypto.SecretKey;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.encryption.info.EncryptionDataInfo;

public interface EncryptionProvider {
    
    byte[] ENCRYPTION_IV = new byte[8];

    String TRIPLE_DES = "DESede/CBC/PKCS5Padding";

    String TRIPLE_DES_ALGORITHM = "DESede";

    EncryptionDataInfo encrypt(EncryptionDataInfo inputData) throws BusinessException, SystemException;
    
    void decrypt(EncryptionDataInfo inputData) throws BusinessException, SystemException;
    
    SecretKey generateKey() throws BusinessException;
    
}
