/**
 * 
 */
package com.ca.framework.core.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;

/**
 * @author elumalas
 * 
 */
public final class CheckSumUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckSumUtil.class);

    private CheckSumUtil() {
    }

    public static String getCheckSumValue(byte[] byteArray, String algorithm) {

        String checksumVal = null;
        switch (algorithm.toUpperCase(Locale.getDefault())) {
        case "SHA256":
            checksumVal = DigestUtils.sha256Hex(byteArray);
            break;
        case "MD5":
            checksumVal = DigestUtils.md5Hex(byteArray);
            break;
        case "SHA512":
            checksumVal = DigestUtils.sha512Hex(byteArray);
            break;
        case "SHA384":
            checksumVal = DigestUtils.sha384Hex(byteArray);
            break;
        case "SHA1":
            checksumVal = DigestUtils.sha1Hex(byteArray);
            break;
        default:
            checksumVal = DigestUtils.sha256Hex(byteArray);
            break;
        }
        return checksumVal;
    }

    public static boolean validateCheckSumBySHA256(byte[] modelLibJar, final String checksum) {
        boolean isValid = false;
        final String checkSumVal = DigestUtils.sha256Hex(modelLibJar);
        LOGGER.info("Expected SHA256 checksum for the model library jar file is {}", checkSumVal);
        if (StringUtils.isNotEmpty(checksum) && StringUtils.isNotEmpty(checkSumVal) && checkSumVal.equals(checksum)) {
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }

    public static boolean validateCheckSumByMD5(byte[] modelLibJar, final String checksum) {
        boolean isValid = false;
        final String checkSumVal = DigestUtils.md5Hex(modelLibJar);
        LOGGER.info("Expected MD5 checksum for the model library jar file is {}", checkSumVal);
        if (StringUtils.isNotEmpty(checksum) && StringUtils.isNotEmpty(checkSumVal) && checkSumVal.equals(checksum)) {
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }

    public static boolean validateCheckSumBySHA512(byte[] modelLibJar, final String checksum) {
        boolean isValid = false;
        final String checkSumVal = DigestUtils.sha512Hex(modelLibJar);
        LOGGER.info("Expected SHA512 checksum for the model library jar file is {}", checkSumVal);
        if (StringUtils.isNotEmpty(checksum) && StringUtils.isNotEmpty(checkSumVal) && checkSumVal.equals(checksum)) {
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }

    public static boolean validateCheckSumBySHA384(byte[] modelLibJar, final String checksum) {
        boolean isValid = false;
        final String checkSumVal = DigestUtils.sha384Hex(modelLibJar);
        LOGGER.info("Expected SHA384 checksum for the model library jar file is {}", checkSumVal);
        if (StringUtils.isNotEmpty(checksum) && StringUtils.isNotEmpty(checkSumVal) && checkSumVal.equals(checksum)) {
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }

    public static boolean validateCheckSumBySHA1(byte[] modelLibJar, final String checksum) {
        boolean isValid = false;
        final String checkSumVal = DigestUtils.sha1Hex(modelLibJar);
        LOGGER.info("Expected SHA1 checksum for the model library jar file is {}", checkSumVal);
        if (StringUtils.isNotEmpty(checksum) && StringUtils.isNotEmpty(checkSumVal) && checkSumVal.equals(checksum)) {
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }
    
    public static boolean validateRCheckSum(final String calculatedChecksum, final String checksum)
            throws NoSuchAlgorithmException, IOException {
        boolean isValid = false;
        LOGGER.info("Expected SHA-256 checksum for the model library jar file is {}", calculatedChecksum);
        if (StringUtils.isNotEmpty(checksum) && StringUtils.isNotEmpty(calculatedChecksum)
                && calculatedChecksum.equals(checksum)) {
            isValid = true;
        }
        return isValid;
    }
    
    public static byte[] readZipFile(String zipName) throws IOException {
        byte[] zipArray = null;
        FileInputStream zfis = null;
        try {
            zfis = new FileInputStream(zipName);
            zipArray = IOUtils.toByteArray(zfis);
            LOGGER.info("CheckSumUtil :: Read the file "+zipName+"Successfully");
        }
        finally {
            IOUtils.closeQuietly(zfis);
        }
        return zipArray;
    }
}