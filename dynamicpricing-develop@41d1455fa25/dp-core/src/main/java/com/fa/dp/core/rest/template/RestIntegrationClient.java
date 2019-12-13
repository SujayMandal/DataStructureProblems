package com.fa.dp.core.rest.template;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.systemparam.provider.SystemParameterProvider;
import com.fa.dp.core.systemparam.util.AppType;
import com.fa.dp.core.systemparam.util.SystemParameterConstant;
import com.fa.dp.core.util.RAClientConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Named
public class RestIntegrationClient {

	private static final String HTTPS = "https";

	private static final String HTTP = "http";

	private static final Logger LOGGER = LoggerFactory.getLogger(RestIntegrationClient.class);

	@Value("${CONNTIMEOUT}")
	private int connectTimeout;

	@Value("${READTIMEOUT}")
	private int readTimeout;

	@Value("${CERT_COMMON_NAME}")
	private String allowedCertificateDomains;

	private String[] allowedCertificates;

	private ObjectMapper objectMapper = new ObjectMapper();
	private RestTemplate restTemplate = new RestTemplate();

	@Value("${PROXY_URL}")
	private String proxyUrl;

	@Value("${PROXY_PORT_VALUE}")
	private int proxyPortValue;

	@Value("${PROXY_USR_NAME}")
	private String proxyUser;

	@Value("${PROXY_P$D}")
	private String proxyPwd;
	
	@Inject
	private SystemParameterProvider systemParameterProvider;
	
	@PostConstruct
	public void configureProxyAndInitializeRestTemplate() {
		this.connectTimeout = this.connectTimeout * 1000;
		this.readTimeout = this.readTimeout * 1000;
		MDC.put(RAClientConstants.APP_CODE, AppType.DPA.getAppCode());
		allowedCertificateDomains += ",";
		allowedCertificateDomains += systemParameterProvider.getSystemParamValue(SystemParameterConstant.APP_PARAM_CA_COMMON_NAME);
		MDC.remove(RAClientConstants.APP_CODE);
		allowedCertificates = Arrays.stream(allowedCertificateDomains.split(",")).map(item -> item.trim())
				.toArray(String[]::new);
		initializeRestTemplate();
	}

	public class AllHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String s, SSLSession sslSession) {
			return true;
		}
	}

	public class CustomTrustStrategy implements TrustStrategy {
		@Override
		public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
			return Arrays.stream(x509Certificates).anyMatch(cert -> StringUtils
					.containsAny(cert.getSubjectDN().getName(), StringUtils.join(allowedCertificates, ",")));
		}
	}

	private void initializeRestTemplate() {
		SSLContext sslContext = null;
		try {
			sslContext = new SSLContextBuilder().loadTrustMaterial(null, new CustomTrustStrategy()).build();
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("NoSuchAlgorithmException while initializing rest template : {}", e);
		} catch (KeyManagementException e) {
			LOGGER.error("KeyManagementException while initializing rest template : {}", e);
		} catch (KeyStoreException e) {
			LOGGER.error("KeyStoreException while initializing rest template : {}", e);
		}
		// SSLContexts.createDefault();
		SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext,
				new AllHostnameVerifier());
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register(HTTP, PlainConnectionSocketFactory.getSocketFactory()).register(HTTPS, sslSocketFactory)
				.build();
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
				socketFactoryRegistry);
		HttpHost httpHost = null;
		if (this.proxyUrl != null) {
			httpHost = new HttpHost(proxyUrl, proxyPortValue, HTTP);
		}
		RequestConfig clientConfig = RequestConfig.custom().setConnectTimeout(this.connectTimeout)
				.setSocketTimeout(this.readTimeout).setConnectionRequestTimeout(this.readTimeout).build();
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(clientConfig).setProxy(httpHost)
				// .setDefaultCredentialsProvider(credentialProvider)
				// .setSSLHostnameVerifier(new AllHostnameVerifier())
				.setConnectionManager(connectionManager)
				// .setSSLContext(sslContext)
				// .setSSLSocketFactory(new SSLSocketFactory(new CustomTrustStrategy(), new
				// AllHostnameVerifier()))
				// .setSSLSocketFactory()
				.build();
		HttpComponentsClientHttpRequestFactory httpClientRequestFactory = new HttpComponentsClientHttpRequestFactory();
		httpClientRequestFactory.setHttpClient(httpClient);
		this.restTemplate.setRequestFactory(httpClientRequestFactory);
	}

	private HttpHeaders generateHeaders(String authToken, String authUserName, String authPassword, MediaType appType) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(appType);

		if (StringUtils.isNotBlank(authUserName) && StringUtils.isNotBlank(authPassword)) {
			headers.add("Authorization", "Basic " + getRuntimeCredentials(authUserName, authPassword));
		}
		if (StringUtils.isNotBlank(authToken)) {
			headers.add("authToken", authToken);
		}
		return headers;
	}

	private String getRuntimeCredentials(String authUserName, String authPassword) {
		String plainCreds = authUserName + ":" + authPassword;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		return new String(base64CredsBytes);
	}
	
	public <I, R> R execute(String restURL, I input, String customAuthToken, String authUserName, String authPassword, MediaType appType, Class<R> responseType)
			throws SystemException {

		// TODO: validation
		LOGGER.debug("Validation successful");
		HttpHeaders headers = generateHeaders(customAuthToken, authUserName, authPassword, appType);
		LOGGER.debug("Headers generated");
		try {
			final HttpEntity<I> request = new HttpEntity<>(input, headers);
			LOGGER.debug("Posting data to {}", restURL);
			LOGGER.info("Request Data: \n" + objectMapper.writeValueAsString(input));
			LOGGER.debug("Response from {}", restURL);
			R restResponse = postForObject(restURL, request, responseType);
			LOGGER.debug("Response Data: \n" + objectMapper.writeValueAsString(restResponse));
			return restResponse;
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			throw new SystemException(CoreExceptionCodes.RACLNCOM001);
		}
	}

	private <R> R postForObject(String restURL, HttpEntity<?> request, Class<R> responseType) {
		R restResponse = null;
		restResponse = restTemplate.postForObject(restURL, request, responseType);
		//ResponseEntity<R> restResponse = null;
		//restResponse = restTemplate.postForEntity(restURL, request, responseType);
		return restResponse;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout, RestTemplate restTemplate) {
		this.connectTimeout = connectTimeout * 1000;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout, RestTemplate restTemplate) {
		this.readTimeout = readTimeout * 1000;
	}

}
