package com.fa.dp.core.encryption;

import org.jasypt.util.text.BasicTextEncryptor;

public class EncryptionUtil {

    public static String encryptToken(String authToken) {
        BasicTextEncryptor bte = new BasicTextEncryptor();
        bte.setPassword(EncryptionConstants.PASSWORD_FOR_ENCRYPTION);
        return bte.encrypt(authToken);
    }

    public static String decryptToken(String encryptedToken) {
        BasicTextEncryptor bte = new BasicTextEncryptor();
        bte.setPassword(EncryptionConstants.PASSWORD_FOR_ENCRYPTION);
        return bte.decrypt(encryptedToken);
    }

}
