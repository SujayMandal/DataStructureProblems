package com.ca.umg.business.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class EncryptionUtilTest {
    
    @Test
    public void testUtil() throws IOException {
        File file = new File ("./src/test/resources/umg_encrypted.key");
        FileOutputStream fos= null;
        FileInputStream newFis= null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File ("./src/test/resources/umg.key"));
            byte[] keyBytes = IOUtils.toByteArray(fis);
            byte[] encryptedKeyBytes = EncryptionUtil.applyKeyEncryption(keyBytes);
            fos = new FileOutputStream(file);
            fos.write(encryptedKeyBytes);
            newFis = new FileInputStream(file);
            byte[] encKeyBytes = IOUtils.toByteArray(newFis);
            byte[] decryptedKeyBytes = EncryptionUtil.applyKeyDecryption(encKeyBytes);
            assertTrue(Arrays.equals(keyBytes, decryptedKeyBytes));
        } catch (Exception e) {
            Assert.fail();
        } finally {
            FileUtils.forceDelete(file);
            if(fos != null) {
            	fos.flush();
            	fos.close();
            }
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(newFis);
      
        }
    }

}
