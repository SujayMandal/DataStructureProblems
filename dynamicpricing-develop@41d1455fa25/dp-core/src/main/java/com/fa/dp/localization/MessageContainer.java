/**
 * 
 */
package com.fa.dp.localization;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Named
public class MessageContainer {

	private static MessageSource messageSource;

	@Inject
	private ApplicationContext applicationContext;

	@PostConstruct
	public void init() {
		messageSource = applicationContext.getBean(MessageSource.class);
	}

	public static String getMessage(String id, Object... arguments) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(id, arguments, locale);
	}
}
