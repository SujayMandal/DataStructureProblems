/**
 * 
 */
package com.ca.framework.core.restclient;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author kamathan
 * 
 */
@Configuration
@PropertySource("${httpConnectionPooling.properties}")
public class UmgHttpConfig {

    private static final String READ_TIMEOUT = "http.connection.timeout";

    private static final int DEFAULT_READ_TIMEOUT = 60 * 1000;

    @Resource
    private Environment env;

    @Inject
    @Named("umgHttpConnectionManager")
    private PoolingHttpClientConnectionManager poolingManager;

    /**
     * The HTTP connection factory.
     * 
     * @return {@link HttpComponentsClientHttpRequestFactory}
     */
    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(httpRequestFactory());
    }

    /**
     * The HTTP Client configured with connection pool setting.
     * 
     * @return {@link HttpClient}
     */
    @Bean
    public HttpClient httpClient() {
        int conTimeOut = 0;
        HttpClientBuilder httpClientBuilder = null;
        if (StringUtils.isNotBlank(env.getRequiredProperty(READ_TIMEOUT))) {
            conTimeOut = Integer.parseInt(env.getRequiredProperty(READ_TIMEOUT));
        }
        setPoolProperties();
        httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(poolingManager);

        RequestConfig config = RequestConfig.custom().setConnectTimeout(conTimeOut > 0 ? conTimeOut : DEFAULT_READ_TIMEOUT)
                .build();

        httpClientBuilder.setDefaultRequestConfig(config);
        return httpClientBuilder.build();
    }

    private void setPoolProperties() {
        int maxCon = 0;
        int perRoute = 0;
        String maxConnection = env.getRequiredProperty("http.max.connection");
        String maxConnectionRoute = env.getRequiredProperty("http.max.route");
        if (StringUtils.isNotBlank(maxConnection)) {
            maxCon = Integer.parseInt(maxConnection);
            poolingManager.setMaxTotal(maxCon);
        }
        if (StringUtils.isNotBlank(maxConnectionRoute)) {
            perRoute = Integer.parseInt(maxConnectionRoute);
            poolingManager.setDefaultMaxPerRoute(perRoute);
        }
    }

}
