/**
 * 
 */
package com.fa.dp.localization.config;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class MessageLocationConfig {

	@Value("${message.resource.path}")
	private String messageResourePath;

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.US);
		return slr;
	}

	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		String[] messageResourePaths = StringUtils.split(messageResourePath, ",");
		messageSource.setBasenames(messageResourePaths);
		messageSource.setCacheSeconds(3600); // refresh cache once per hour
		return messageSource;
	}

}
