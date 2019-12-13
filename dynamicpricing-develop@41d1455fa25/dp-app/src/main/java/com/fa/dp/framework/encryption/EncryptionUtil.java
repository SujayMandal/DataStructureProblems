package com.fa.dp.framework.encryption;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

	private static String saltedPassword;
	
	@Value("${ENCRYPTION_SALT}")
    public void setSalt(String encryptionSalt) {
		saltedPassword = encryptionSalt;
    }
	
	public static String encryptToken(String authToken) {
    	BasicTextEncryptor bte = new BasicTextEncryptor();
    	bte.setPassword(saltedPassword);
    	return bte.encrypt(authToken);
	}
    
    public static String decryptToken(String encryptedToken) {
    	BasicTextEncryptor bte = new BasicTextEncryptor();
    	bte.setPassword(saltedPassword);
    	return bte.decrypt(encryptedToken);
	}
}