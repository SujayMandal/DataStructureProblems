package com.ca.framework.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.util.InsertScriptCreator;

public class PasswordDecryptionUtil {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PasswordDecryptionUtil.class);

    static byte[] ENCRYPTION_IV = new byte[8];
    static String TRIPLE_DES_ALGORITHM = "DESede";
    static String TRIPLE_DES = "DESede/CBC/PKCS5Padding";

    public static void main(String[] args) throws InvalidKeySpecException, InvalidAlgorithmParameterException, IOException,
            NoSuchAlgorithmException, InvalidKeyException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(TRIPLE_DES_ALGORITHM);
        if (args.length < 2) {
            System.out.println("Args lenth should be 2");
            System.exit(0);
        }
        InputStream is = null;
        try {
            is = PasswordDecryptionUtil.class.getClassLoader().getResourceAsStream("secretKey/secretKey.txt");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int reads = is.read();
            while (reads != -1) {
                baos.write(reads);
                reads = is.read();
            }
            byte[] keyBytes = baos.toByteArray();
            SecretKey key = factory.generateSecret(new DESedeKeySpec(keyBytes));
            if ("encrypt".equals(args[0])) {
                String encryptedData = encryptData(args[1], key);
                System.out.println("encryptedData for the value \"" + args[1] + "\" is : " + encryptedData);
            } else if ("decrypt".equals(args[0])) {
                String decryptedData = decryptData(args[1], key);
                System.out.println("decryptedData for the value \"" + args[1] + "\" is : " + decryptedData);
            } else {
                System.out.println(" First argument value should be either decrypt or encrypt");

            }

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
        	LOGGER.error("Exception: {}",e);
            System.out.print("Exception while encrypt or decrypt :" + e);
        }finally {
			if(is !=null)
				is.close();
		}
    }

    private static String encryptData(String toEncryptStr, SecretKey key) throws IllegalBlockSizeException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {

        Cipher cipher = Cipher.getInstance(TRIPLE_DES);
        final IvParameterSpec iv = new IvParameterSpec(ENCRYPTION_IV);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        byte[] convertedData = cipher.doFinal(toEncryptStr.getBytes());
        return DatatypeConverter.printHexBinary(convertedData);

    }

    private static String decryptData(String dataToEncrypt, SecretKey key)
            throws IllegalBlockSizeException, BadPaddingException, IOException, NoSuchAlgorithmException, InvalidKeySpecException,
            InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException {

        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(TRIPLE_DES_ALGORITHM);
        DESedeKeySpec keyspec = (DESedeKeySpec) keyfactory.getKeySpec(key, DESedeKeySpec.class);
        byte[] rawkey = keyspec.getKey();
        DESedeKeySpec keyspec1 = new DESedeKeySpec(rawkey);
        SecretKeyFactory keyfactory1 = SecretKeyFactory.getInstance(TRIPLE_DES_ALGORITHM);
        SecretKey secretKey1 = keyfactory1.generateSecret(keyspec1);

        Cipher cipher = Cipher.getInstance(TRIPLE_DES);
        final IvParameterSpec iv = new IvParameterSpec(ENCRYPTION_IV);
        cipher.init(Cipher.DECRYPT_MODE, secretKey1, iv);

        byte[] convertedData = cipher.doFinal(DatatypeConverter.parseHexBinary(dataToEncrypt));
        return new String(convertedData);
    }

    public SecretKey getKeyFromBytes(byte[] key) throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKey secretKey = null;
        DESedeKeySpec keyspec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(TRIPLE_DES_ALGORITHM);
        secretKey = keyfactory.generateSecret(keyspec);

        return secretKey;
    }

    public byte[] getRawKey(SecretKey key)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] rawkey = null;

        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(TRIPLE_DES_ALGORITHM);
        DESedeKeySpec keyspec = (DESedeKeySpec) keyfactory.getKeySpec(key, DESedeKeySpec.class);
        rawkey = keyspec.getKey();

        return rawkey;
    }

}
