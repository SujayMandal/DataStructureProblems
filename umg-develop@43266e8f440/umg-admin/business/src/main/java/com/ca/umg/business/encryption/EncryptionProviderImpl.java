package com.ca.umg.business.encryption;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.encryption.info.EncryptionDataInfo;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;

@Named
public class EncryptionProviderImpl implements EncryptionProvider {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionProviderImpl.class);

    @Override
    public EncryptionDataInfo encrypt(EncryptionDataInfo encryptionData) throws BusinessException, SystemException {
        SecretKey secretKey = null;
        try {
            secretKey = generateKey();
            byte[] encryptedData = cryptData(encryptionData.getDecryptedData(), secretKey, Cipher.ENCRYPT_MODE);
            encryptionData.setEncryptedData(encryptedData);
            encryptionData.setEncryptedKey(getRawKey(secretKey));
        } catch (InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException | IOException | 
                NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException  e) {
            LOGGER.error("Error occurred during encrypting data : " + e.getMessage());
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000088, new Object[] { "Decrypting" });
        }
        return encryptionData;
    }
    
    @Override
    public void decrypt(EncryptionDataInfo inputData) throws BusinessException, SystemException {
        try {
            byte[] decryptedData = cryptData(inputData.getEncryptedData(), getKeyFromBytes(inputData.getEncryptedKey()), Cipher.DECRYPT_MODE);
            inputData.setDecryptedData(decryptedData);
        } catch (NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | IOException e) {
            LOGGER.error("Error occurred during decrypting data : " + e.getMessage());
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000088, new Object[] { "Decrypting" });
        }
    }    
    
    private byte[] cryptData(byte[] data, SecretKey secretKey, int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
    InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, SystemException, IOException {
        Cipher cipher = Cipher.getInstance(TRIPLE_DES);
        final IvParameterSpec iv = new IvParameterSpec(ENCRYPTION_IV);
        cipher.init(mode, secretKey, iv);
        String usingMode = mode == 1 ? "Encrypt" : "Decrypt";
        LOGGER.info("Mode : " + usingMode + " **** Read Data Size **** : " + data.length);
        byte[] convertedData = cipher.doFinal(data);
        LOGGER.info("**** Converted Data Size **** : " + convertedData.length);
        return convertedData;
    }

    @Override
    public SecretKey generateKey() throws BusinessException {
        SecretKey key = null;
        try {
            KeyGenerator keygen = KeyGenerator.getInstance(TRIPLE_DES_ALGORITHM);
            key = keygen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error occurred during generating key : " + e.getMessage());
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000088,  new Object[] { "Generating Key For" });
        }
        return key;
    }
    
    public byte[] getRawKey(SecretKey key) throws BusinessException, UnsupportedEncodingException {
        byte[] rawkey = null;
        try {
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(TRIPLE_DES_ALGORITHM);
            DESedeKeySpec keyspec = (DESedeKeySpec) keyfactory.getKeySpec(key, DESedeKeySpec.class);
            rawkey = keyspec.getKey();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOGGER.error("Error occurred during key convertion : " + e.getMessage());
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000088, new Object[] { "Generating Key For" });
        }
        return rawkey;
    }

    public SecretKey getKeyFromBytes(byte[] key) throws BusinessException {
        SecretKey secretKey = null;
        try {
            DESedeKeySpec keyspec = new DESedeKeySpec(key);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(TRIPLE_DES_ALGORITHM);
            secretKey = keyfactory.generateSecret(keyspec);
        } catch (NoSuchAlgorithmException | InvalidKeyException |  InvalidKeySpecException e) {
            LOGGER.error("Error occurred during key convertion : " + e.getMessage());
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000088, new Object[] { "Generating Key For" });
        }
        return secretKey;
    }

}
