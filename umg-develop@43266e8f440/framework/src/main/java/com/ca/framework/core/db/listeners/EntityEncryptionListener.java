package com.ca.framework.core.db.listeners;

import static org.springframework.util.ReflectionUtils.getField;
import static org.springframework.util.ReflectionUtils.makeAccessible;
import static org.springframework.util.ReflectionUtils.setField;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.jasypt.util.text.BasicTextEncryptor;

import com.ca.framework.core.db.annotation.Encrypt;

//@Configurable
public class EntityEncryptionListener {
	
    //@Value("$entity.field.encryption.password")
    // TODO : Hardcoding of password need to be removed. We need to add the aspectj-maven-plugin and enable @Configurable annotation
	private String encKy = "1233#7788THysdsds";
	
	// Currently encryption/decryption is supported only for string classes.
	private BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
	
	public EntityEncryptionListener() {
	    // TODO : Need to be moved to @PostConstruct
	    textEncryptor.setPassword(encKy);
    }
	
	@PostLoad
	@PostPersist
	@PostUpdate
	public void decrypt(Object entity) {
	    for (Field field : findFieldsWithAnnotation(entity.getClass(), Encrypt.class)) {
			makeAccessible(field);
			Object value = getField(field, entity);
			Object decryptedValue = decryptValue(value);
			setField(field, entity, decryptedValue);
		}
	}

	@PrePersist
	@PreUpdate
	public void encrypt(Object entity) {
	    for (Field field : findFieldsWithAnnotation(entity.getClass(), Encrypt.class)) {
			makeAccessible(field);
			Object value = getField(field, entity);
			Object encryptedValue = encryptValue(value);
			setField(field, entity, encryptedValue);
		}
	}
	
	protected Object encryptValue(Object value) {
		// Currently encryption/decryption is supported only for string classes.
		Class<? extends Object> valueClass = value.getClass();
		if (valueClass == String.class) {
			return textEncryptor.encrypt(value.toString());
		}
		return value;
	}

	protected Object decryptValue(Object value) {
		// Currently encryption/decryption is supported only for string classes.
		Class<? extends Object> valueClass = value.getClass();
		if (valueClass == String.class) {
			return textEncryptor.decrypt(value.toString());
		}
		return value;
	}
	
	private static <T extends Annotation> List<Field> findFieldsWithAnnotation(Class<?> type, Class<T> annotationType) {
	    // TODO : this should be cached to improve performance
		List<Field> fieldsWithAnnotation = new ArrayList<Field>();
		Class<?> currentType = type;
		while (!Object.class.equals(currentType)) {
			for (Field field : currentType.getDeclaredFields()) {
				T annotation = field.getAnnotation(annotationType);

				if (annotation != null) {
				    fieldsWithAnnotation.add(field);
				}
			}
			currentType = currentType.getSuperclass();
		}
		return fieldsWithAnnotation;
	}

	public String getKey() {
		return encKy;
	}

	public void setKey(String key) {
		this.encKy = key;
	}

  
}
