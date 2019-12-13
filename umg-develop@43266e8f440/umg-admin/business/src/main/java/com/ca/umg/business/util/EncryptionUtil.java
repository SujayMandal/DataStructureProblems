package com.ca.umg.business.util;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.binary.BasicBinaryEncryptor;

import com.ca.framework.core.encryption.EncryptionConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;


/**
 * @author raghavek
 * TODO: Need to move this to framework soon.
 */
public final class EncryptionUtil {
    
    private EncryptionUtil() {
    }
    
    public static byte[] applyKeyEncryption(byte[] key) throws BusinessException {
        byte[] encryptedKey = null;
        try {
            BasicBinaryEncryptor keyEncryptor = new BasicBinaryEncryptor();
            keyEncryptor.setPassword(EncryptionConstants.ENC_KY);
            encryptedKey = keyEncryptor.encrypt(key);
        } catch (EncryptionOperationNotPossibleException excp) {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000088,  new Object[] { "Encrypting Key " });
        }
        return encryptedKey;
    }
    
    public static byte[] applyKeyDecryption(byte[] key) throws BusinessException {
        byte[] encryptedKey = null;
        try {
            BasicBinaryEncryptor keyDecryptor = new BasicBinaryEncryptor();
            keyDecryptor.setPassword(EncryptionConstants.ENC_KY);
            encryptedKey = keyDecryptor.decrypt(key);
        } catch (EncryptionOperationNotPossibleException excp) {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000088,  new Object[] { "Decrypting Key " });
        }
        return encryptedKey;
    }

}
