package com.ca.umg.modelet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.cache.registry.impl.DefaultCacheRegistry;

@Configuration
@ComponentScan(basePackages = { "com.altisource.modelet", "com.ca" }, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.CUSTOM, value = RegexFilter.class) })
public class ModeletConfig {

    @Bean(name = "modeletCacheRegistry")
    public CacheRegistry getCacheRegistry() {
        return new DefaultCacheRegistry("hazelcast-config.xml", Boolean.TRUE);
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        String[] resources = { "classpath:locale/error-messages", "classpath:locale/framework-messages" };
        messageSource.setBasenames(resources);
        return messageSource;
    }

}
