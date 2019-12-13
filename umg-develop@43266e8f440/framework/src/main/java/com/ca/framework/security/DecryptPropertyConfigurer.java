package com.ca.framework.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Enumeration;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * @author basanaga
 * 
 *         This class used to decrypt password
 */
public class DecryptPropertyConfigurer extends PropertyPlaceholderConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecryptPropertyConfigurer.class);
    byte[] ENCRYPTION_IV = new byte[8];
    String TRIPLE_DES_ALGORITHM = "DESede";
    String TRIPLE_DES = "DESede/CBC/PKCS5Padding";

    @Override
    protected void convertProperties(Properties props) {
        super.convertProperties(props);
        LOGGER.info("Reading properties Started");
        try {
            Enumeration<?> propertyNames = props.propertyNames();
            while (propertyNames.hasMoreElements()) {
                String propertyName = (String) propertyNames.nextElement();
                String propertyValue = props.getProperty(propertyName);
                if (propertyName.contains("password") && StringUtils.isNoneBlank(propertyValue)) {
                    LOGGER.info("Decrypting value for the propertyName :" + propertyName + " started");
                    props.setProperty(propertyName, decrypt(propertyValue, IOUtils.toByteArray(
                            DecryptPropertyConfigurer.class.getClassLoader().getResourceAsStream("secretKey/secretKey.txt"))));
                    LOGGER.info("Decrypting value for the propertyName :" + propertyName + " ended");
                }
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
                | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException | IOException e) {
            LOGGER.error("Exception occured while decryptiing", e);
        }
        LOGGER.info("Reading properties Completed");
    }

    private String decrypt(String propertyValue, byte[] desKey)
            throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {

        SecretKeyFactory firstLevelFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey firstlevelKey = firstLevelFactory.generateSecret(new DESedeKeySpec(desKey));

        SecretKeyFactory secondLevelFactory = SecretKeyFactory.getInstance(TRIPLE_DES_ALGORITHM);
        DESedeKeySpec keyspec = (DESedeKeySpec) secondLevelFactory.getKeySpec(firstlevelKey, DESedeKeySpec.class);
        byte[] rawkey = keyspec.getKey();

        DESedeKeySpec keyspec_rawkey = new DESedeKeySpec(rawkey);
        SecretKeyFactory thirdLevelFactory = SecretKeyFactory.getInstance(TRIPLE_DES_ALGORITHM);
        SecretKey thirdlevelKey = thirdLevelFactory.generateSecret(keyspec_rawkey);
        Cipher cipher = Cipher.getInstance(TRIPLE_DES);
        LOGGER.info("Cipher created ");
        final IvParameterSpec iv = new IvParameterSpec(ENCRYPTION_IV);
        cipher.init(Cipher.DECRYPT_MODE, thirdlevelKey, iv);
        byte[] convertedData = cipher.doFinal(DatatypeConverter.parseHexBinary(propertyValue));
        LOGGER.info("*** Decryption Success ***");
        return new String(convertedData);

    }

}