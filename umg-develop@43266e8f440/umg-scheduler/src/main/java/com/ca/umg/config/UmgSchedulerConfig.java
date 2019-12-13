/**
 * 
 */
package com.ca.umg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.cache.registry.impl.DefaultCacheRegistry;

/**
 * @author kamathan
 *
 */
@Configuration
@EnableScheduling
@PropertySource("${application.properties}")
@ComponentScan(basePackages = { "com.ca.umg", "com.ca.framework.core.restclient", "com.ca.framework.core.util"}, excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.ca.umg.notification.*"))
public class UmgSchedulerConfig {

    @Bean(name = "cacheRegistry")
    public CacheRegistry getCacheRegistry() {
        return new DefaultCacheRegistry("hazelcast-config.xml");
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        String[] resources = { "classpath:locale/umg-scheduler-messages", "classpath:locale/framework-messages" };
        messageSource.setBasenames(resources);
        return messageSource;
    }

}
