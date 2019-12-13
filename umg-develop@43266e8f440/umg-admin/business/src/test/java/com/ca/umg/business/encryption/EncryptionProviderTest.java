package com.ca.umg.business.encryption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.encryption.info.EncryptionDataInfo;

public class EncryptionProviderTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionProviderTest.class);

    @InjectMocks
    private EncryptionProviderImpl encryptionProvider;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testEncryption() {
        try {
            EncryptionDataInfo encryptionData = new EncryptionDataInfo();
            encryptionData.setDecryptedData("ABCD".getBytes());
            encryptionProvider.encrypt(encryptionData);
            assertNotNull(encryptionData.getEncryptedData());
            assertNotNull(encryptionData.getEncryptedKey());
            assertEquals(8, encryptionData.getEncryptedData().length);
        } catch (BusinessException | SystemException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
    
    @Test
    public void testDecryption() {
        try {
            EncryptionDataInfo encryptionData = readEncryptedDataAndKey();
            encryptionProvider.decrypt(encryptionData);
            assertNotNull(encryptionData.getEncryptedData());
            assertNotNull(encryptionData.getEncryptedKey());
            assertNotNull(encryptionData.getDecryptedData());
            assertEquals("ABCD", new String(encryptionData.getDecryptedData()));
        } catch (BusinessException | SystemException | UnsupportedEncodingException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    private EncryptionDataInfo readEncryptedDataAndKey() throws UnsupportedEncodingException {
        EncryptionDataInfo encryptionData = new EncryptionDataInfo();
        byte[] encryptedData = readFileData("./src/test/resources/encryption/encryptedTxt.txt");
        byte[] encryptedKey = readFileData("./src/test/resources/encryption/key.txt");
        encryptionData.setEncryptedData(encryptedData);
        encryptionData.setEncryptedKey(encryptedKey);
        return encryptionData;
    }
    
    @Test
    public void encryptZipFile() {
        byte[] zipData = readFileData("./src/test/resources/NewVersion.zip");
        try {
            EncryptionDataInfo encryptionData = new EncryptionDataInfo();
            encryptionData.setDecryptedData(zipData);
            encryptionProvider.encrypt(encryptionData);
            assertNotNull(encryptionData.getEncryptedData());
            assertNotNull(encryptionData.getEncryptedKey());
            assertEquals(703472, encryptionData.getEncryptedData().length);
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            Assert.fail();
        }
    }
    
    @Test
    public void decryptZipFile() {
        byte[] zipData = readFileData("./src/test/resources/encryption/EncryptedZip.zip");
        byte[] encryptedKey = readFileData("./src/test/resources/encryption/EncryptedKey.key");
        try {
            EncryptionDataInfo encryptionData = new EncryptionDataInfo();
            encryptionData.setEncryptedData(zipData);
            encryptionData.setEncryptedKey(encryptedKey);
            encryptionProvider.decrypt(encryptionData);
            assertNotNull(encryptionData.getDecryptedData());
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            Assert.fail();
        }
    }
    
    private byte[] readFileData(String fileName) {
        byte[] data = null;
        try {
            data = FileUtils.readFileToByteArray(new File(fileName));
        } catch (IOException e) {
            LOGGER.error("IOException: ", e);
        }
        return data;
    }
    
}
