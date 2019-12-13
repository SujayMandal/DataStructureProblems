/**
 *
 */
package com.fa.dp.core.rest;

import com.fa.dp.business.command.dao.CommandDAO;
import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.command.info.CommandProcess;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.bo.DPProcessParamsBO;
import com.fa.dp.business.filter.bo.DPProcessWeekNParamsBO;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.util.TransactionStatus;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.business.validator.dao.DPWeekNIntgAuditDao;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.entity.DynamicPricingIntgAudit;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.entity.DPWeekNIntgAudit;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.encryption.EncryptionUtil;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.model.delegate.ModelDetailDelegate;
import com.fa.dp.core.model.info.ModelDetailInfo;
import com.fa.dp.core.rest.exception.codes.RAIntegrationErrorCodes;
import com.fa.dp.core.rest.info.RestResponseForApi;
import com.fa.dp.core.rest.info.TenantIODefinition;
import com.fa.dp.core.rest.info.TransactionWrapperForApi;
import com.fa.dp.core.rest.template.RestIntegrationClient;
import com.fa.dp.core.systemparam.provider.SystemParameterProvider;
import com.fa.dp.core.systemparam.util.SystemParameterConstant;
import com.fa.dp.core.tenant.info.TenantInfo;
import com.fa.dp.core.transaction.delegate.TransactionDelegate;
import com.fa.dp.core.transaction.domain.Transaction;
import com.fa.dp.core.transaction.info.TransactionInfo;
import com.fa.dp.core.util.ConversionUtil;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.KeyValue;
import com.fa.dp.core.util.RAClientConstants;
import com.fa.dp.core.util.RAClientUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import sun.security.x509.X509CertImpl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 *
 */
@Named
public class RAClientImpl extends AbstractDelegate implements RAClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(RAClientImpl.class);

	@Inject
	private SystemParameterProvider systemParameterProvider;

	@Inject
	@Named("raRestTemplate")
	private RestTemplate restTemplate;

	@Inject
	private RestIntegrationClient restIntegrationClient;

	@Value("${CERT_COMMON_NAME}")
	private String certificateCommonName;

	private final String KEY_STORE_NAME = "RA_CLIENT-RA-KEY_STORE";

	private final String CERTIFICATE_ALIAS_NAME = "RA_CLIENT-RA-intcert";

	@Value("${PASSWORD_FOR_CERTIFICATE}")
	private String pwd;

	@Value("${CONNTIMEOUT}")
	private int connectionTimeout;

	@Value("${READTIMEOUT}")
	private int readTimout;

	@Value("${PROXY_URL}")
	private String proxyUrl;

	@Value("${PROXY_PORT_VALUE}")
	private int proxyPortValue;

	@Value("${PROXY_USR_NAME}")
	private String proxyUser;

	@Value("${PROXY_P$D}")
	private String proxyPwd;

	private String PASSWORD = "PASSWORD";

	private static final String RA_TXN_URL = "api/v1.0/search/";

	private static final String RA_DEFN_URL = "version/getTenantIODefinition/";

	private static final String[] RA_CREDENTIAL_ARR = {"admin", "admin"};

	@Inject
	private CacheManager cacheManager;

	@Inject
	private ModelDetailDelegate modelDetailDelegate;

	@Inject
	private TransactionDelegate transactionDelegate;

	@Inject
	private DPWeekNIntgAuditDao dpWeekNIntgAuditDao;

	@Inject
	private DPProcessWeekNParamsBO dpProcessWeekNParamsBO;

	@Inject
	private CommandDAO commandDAO;

	@Inject
	private DPFileProcessBO dpFileProcessBO;

	@Inject
	private DPProcessParamsBO dpProcessParamsBO;

	private ObjectMapper mapper = new ObjectMapper();

	private ExecutorService executorService = null;

	private HttpClientBuilder clientBuilder;

	private RequestConfig clientConfig;

	@PostConstruct
	public void init() {
		// download https certificates to the server for admin and runtime.
		// Since host names are common for both admin and runtime
		// we are downloading it only once. If host name changes for admin and
		// runtime we need to download them separately.
		String raRuntimeUrl = systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_RA_ADMIN_BASE_URL);

		setupRACertificates(raRuntimeUrl);
		executorService = Executors.newCachedThreadPool();
	}

	private void setupRACertificates(String url) {
		if(StringUtils.startsWith(url, "https:")) {
			try {
				downloadAndInstallCertificate(url);
			} catch (CertificateException e) {
				LOGGER.error("Certificate Exception", e);
			} catch (NoSuchAlgorithmException e) {
				LOGGER.error("Algorithm Definition Certificate Exception", e);
			} catch (KeyStoreException e) {
				LOGGER.error("Unable to add certificate into keystore", e);
			} catch (IOException e) {
				LOGGER.error("IOException while adding certificate to keystore", e);
			} catch (KeyManagementException e) {
				LOGGER.error("Key management exception", e);
			}

			if(restTemplate.getRequestFactory() instanceof SimpleClientHttpRequestFactory) {
				((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(connectionTimeout * 1000);
				((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(readTimout * 1000);
			} else if(restTemplate.getRequestFactory() instanceof HttpComponentsClientHttpRequestFactory) {
				((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(connectionTimeout * 1000);
				((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(readTimout * 1000);
			}
		} else {
			((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(connectionTimeout * 1000);
			((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(readTimout * 1000);
		}
	}

	private void downloadAndInstallCertificate(String runtimeUrl)
			throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
		FileOutputStream fileOutputStream = null;
		FileInputStream fileInputStream = null;
		// Download certificates start
		TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		}};
		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HostnameVerifier allHostsValid = (hostname, session) -> true;
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

		Proxy proxy = null;
		HttpHost httpHost = null;
		// CredentialsProvider credentialsProvider = null;
		if(StringUtils.isNotEmpty(proxyUrl)) {
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl, proxyPortValue));
			LOGGER.debug("Proxy created with Host: {} and Port: {}", proxyUrl, proxyPortValue);
			httpHost = new HttpHost(proxyUrl, proxyPortValue, "http");
			LOGGER.debug("Http Host for client factory created successfully");
		}

		if(StringUtils.isNotEmpty(proxyUser)) {
			Authenticator authenticator = new Authenticator() {

				public PasswordAuthentication getPasswordAuthentication() {
					return (new PasswordAuthentication(proxyUser, proxyPwd.toCharArray()));
				}
			};
			Authenticator.setDefault(authenticator);
			LOGGER.debug("Authenticator created with user name {} and password {}", proxyUser, "******");
			/*
			 * credentialsProvider = new BasicCredentialsProvider();
			 * credentialsProvider.setCredentials(new AuthScope(proxyHostName, proxyPort),
			 * new UsernamePasswordCredentials(proxyUserName, proxyPassword));
			 */
		}

		URL destinationURL = new URL(runtimeUrl);
		HttpsURLConnection conn = (HttpsURLConnection) destinationURL.openConnection(proxy);
		conn.connect();
		ByteArrayInputStream arrayInputStream = null;
		Certificate[] certs = conn.getServerCertificates();
		String subjectDNName = null;
		for (Certificate cert : certs) {
			if(cert instanceof X509Certificate) {
				subjectDNName = ((X509CertImpl) certs[0]).getSubjectDN().getName();
				if(subjectDNName.contains(certificateCommonName)) {
					arrayInputStream = new ByteArrayInputStream(cert.getEncoded());
					break;
				}
			}
		}
		// Download certificates end

		// store into keystore start
		char sep = File.separatorChar;
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(null, null);
		BufferedInputStream bis = new BufferedInputStream(arrayInputStream);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		Certificate cert = null;
		while (bis.available() > 0) {
			cert = cf.generateCertificate(bis);
			ks.setCertificateEntry(CERTIFICATE_ALIAS_NAME, cert);
		}
		ks.setCertificateEntry(CERTIFICATE_ALIAS_NAME, cert);
		File file = null;
		try {
			fileOutputStream = new FileOutputStream(KEY_STORE_NAME);
			ks.store(fileOutputStream, PASSWORD.toCharArray());
			// store into keystore end

			file = new File(KEY_STORE_NAME);
			fileInputStream = new FileInputStream(file);
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(fileInputStream, PASSWORD.toCharArray());
		} finally {
			if(fileInputStream != null) {
				fileInputStream.close();
			}
			if(fileOutputStream != null) {
				fileOutputStream.close();
			}
		}

		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(file, PASSWORD.toCharArray()).build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

		clientBuilder = HttpClients.custom().setSSLSocketFactory(csf);

		LOGGER.debug("HTTP Client Builder is created");
		if(httpHost != null) {
			clientBuilder.setProxy(httpHost);
			LOGGER.debug("Proxy is set to HTTP Client Builder");
		}
		// CloseableHttpClient httpClient =
		// HttpClients.custom().setSSLSocketFactory(csf).setProxy(httpHost).build();
		RequestConfig clientConfig = RequestConfig.custom().setConnectTimeout(connectionTimeout).setSocketTimeout(readTimout)
				.setConnectionRequestTimeout(readTimout).build();
		this.clientConfig = clientConfig;
		clientBuilder.setDefaultRequestConfig(clientConfig);
		CloseableHttpClient httpClient = clientBuilder.build();
		LOGGER.debug("Http Client is created");

		// CloseableHttpClient httpClient =
		// HttpClients.custom().setSSLSocketFactory(csf).build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		file.delete();
		restTemplate.setRequestFactory(requestFactory);
	}

	@PreDestroy
	public void destroy() throws Exception {
		((CloseableHttpClient) ((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory()).getHttpClient()).close();
	}

	@Override
	public List<Transaction> fetchRATransaction(String modelName, String modelVersion, String status, String transactionId, String fromDate,
			String toDate) throws SystemException {
		List<Transaction> transactions = null;
		try {
			String raAdminUrl = systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_RA_ADMIN_URL);

			validateUrl(raAdminUrl, SystemParameterConstant.SYS_PARAM_RA_ADMIN_URL);

			KeyValue<String, String> tenantInfo = getTenantInformationForModel(modelName, modelVersion);

			HttpHeaders headers = new HttpHeaders();
			headers.add("AuthToken", tenantInfo.getValue());
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity entity = new HttpEntity<>(headers);

			String baseUrl = (StringUtils.endsWith(raAdminUrl, RAClientConstants.CHAR_FORWARD_SLASH) ?
					raAdminUrl :
					raAdminUrl + RAClientConstants.CHAR_FORWARD_SLASH) + RA_TXN_URL + RAClientConstants.CHAR_FORWARD_SLASH + modelName;

			UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl).queryParam("transactionId", transactionId)
					.queryParam("runAsOfDateFrom", RAClientUtil
							.getDateFormatEpoch(RAClientUtil.convertTimeToMills(fromDate, RAClientConstants.RA_CLIENT_DATE_FORMAT),
									RAClientConstants.RA_UTC_DATE_FORMAT)).queryParam("runAsOfDateTo", RAClientUtil
							.getDateFormatEpoch(RAClientUtil.convertTimeToMills(toDate, RAClientConstants.RA_CLIENT_DATE_FORMAT),
									RAClientConstants.RA_UTC_DATE_FORMAT)).queryParam("status", status);

			HttpEntity<RestResponseForApi> response = restTemplate
					.exchange(uriComponentsBuilder.build().toUri(), HttpMethod.GET, entity, RestResponseForApi.class);
			RestResponseForApi<TransactionWrapperForApi> restResponseForApi = response.getBody();

			if(!restResponseForApi.isError()) {
				Map responseMap = (Map) restResponseForApi.getResponse();
				int totalCount = (int) responseMap.get("totalCount");
				List<Map<String, Object>> transactionsList = (List<Map<String, Object>>) responseMap.get("transactions");
				transactions = buildFromRATransactions(transactionsList);
			}
		} catch (HttpClientErrorException ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
		} catch (ResourceAccessException | HttpStatusCodeException ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
		} catch (Exception ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
		} finally {
			LOGGER.error("RA Execution time : {}");
		}

		return transactions;
	}

	private void validateUrl(String url, String module) throws SystemException {
		if(StringUtils.isBlank(url)) {
			SystemException.newSystemException(RAIntegrationErrorCodes.RAINT001, new Object[] {module});
		}
	}

	private KeyValue<String, String> getTenantInformationForModel(String modelName, String modelVersion) {
		ModelDetailInfo modelDetailInfo = getModelInfo(modelName, modelVersion);
		LOGGER.info("getTenantInformationForModel() -> modelDetailInfo" + modelDetailInfo);
		String tenantCode = modelDetailInfo.getTenant().getCode();
		String authToken = tenantCode + RAClientConstants.CHAR_DOT + cacheManager.getTenantAuthCode(tenantCode);
		return new KeyValue<String, String>(tenantCode, authToken);
	}

	private KeyValue<String, String> getTenantInformationForModel(String modelName) throws SystemException {
		ModelDetailInfo modelDetailInfo = getModelInfo(modelName);
		String tenantCode = modelDetailInfo.getTenant().getCode();
		String authToken = tenantCode + RAClientConstants.CHAR_DOT + cacheManager.getTenantAuthCode(tenantCode);
		return new KeyValue<String, String>(tenantCode, authToken);
	}

	private ModelDetailInfo getModelInfo(String modelName, String modelVersion) {
		int majorVersion = Integer.parseInt(StringUtils.substringBefore(modelVersion, RAClientConstants.CHAR_DOT));
		String minorVersion = StringUtils.substringAfter(modelVersion, RAClientConstants.CHAR_DOT);

		String modelKey = new StringBuffer(StringUtils.lowerCase(modelName)).append(RAClientConstants.CHAR_HYPHEN).append(majorVersion)
				.append(RAClientConstants.CHAR_HYPHEN).append(minorVersion).toString();
		ModelDetailInfo modelDetailInfo = cacheManager.getModelDetailVersions(modelKey);
		LOGGER.info("getModelInfo() -> modelDetailInfo : " + modelDetailInfo);
		LOGGER.info("getModelInfo() -> modelKey" + modelKey);
		return modelDetailInfo;
	}

	private ModelDetailInfo getModelInfo(String modelName) throws SystemException {

		// ModelDetailInfo modelDetailInfo =
		// cacheManager.getModelDetailVersions(modelName);
		List<ModelDetailInfo> modelDetailInfos = modelDetailDelegate.getAllModelDetails();

		ModelDetailInfo modelinfo = new ModelDetailInfo();

		for (ModelDetailInfo model : modelDetailInfos) {

			if(model.getName().equalsIgnoreCase(modelName)) {

				TenantInfo tenantInfo = new TenantInfo();
				modelinfo.setMajorVersion(model.getMajorVersion());
				modelinfo.setMajorVersion(model.getMajorVersion());
				modelinfo.setName(model.getName());
				String code = model.getTenant().getCode();
				tenantInfo.setCode(code);
				modelinfo.setTenant(tenantInfo);
			}
		}

		return modelinfo;
	}

	private List<Transaction> buildFromRATransactions(List<Map<String, Object>> transactionsList) {
		List<Transaction> transactions = new ArrayList<Transaction>();

		if(CollectionUtils.isNotEmpty(transactionsList)) {
			for (Map<String, Object> transactionMap : transactionsList) {
				Transaction transaction = new Transaction();
				transaction.setClientTransactionId((String) transactionMap.get("clientTransactionID"));
				transaction.setTransactionDate(
						RAClientUtil.convertTimeToMills((String) transactionMap.get("runAsOfDateTime"), RAClientConstants.RA_UTC_DATE_FORMAT));
				transaction.setUser((String) transactionMap.get("createdBy"));
				transaction.setMajorVersion((Integer) transactionMap.get("majorVersion"));
				String minorVersion = null;
				if(null != transactionMap.get("minorVersion")) {
					minorVersion = String.valueOf(transactionMap.get("minorVersion"));
				}
				transaction.setMinorVersion(minorVersion);
				transaction.setModelName((String) transactionMap.get("versionName"));
				transaction.setStatus((String) transactionMap.get("status"));
				transactions.add(transaction);
			}
		}
		return transactions;
	}

	private HttpHeaders buildHttpRequestHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Basic " + getRuntimeCredentials());
		return headers;
	}

	private String getRuntimeCredentials() {
		String plainCreds = RA_CREDENTIAL_ARR[0] + ":" + RA_CREDENTIAL_ARR[1];
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		return new String(base64CredsBytes);
	}

	@Override
	public TenantIODefinition getModelDefinition(String modelName, String modelVersion) throws SystemException {
		TenantIODefinition tenantIODefinition = null;
		KeyValue<String, String> tenantInfo = null;
		try {
			String raAdminUrl = systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_RA_ADMIN_BASE_URL);

			validateUrl(raAdminUrl, SystemParameterConstant.SYS_PARAM_RA_ADMIN_BASE_URL);

			tenantInfo = getTenantInformationForModel(modelName, modelVersion);

			HttpHeaders headers = new HttpHeaders();
			headers.add("AuthToken", tenantInfo.getValue());
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity entity = new HttpEntity<>(headers);

			StringBuffer url = new StringBuffer(raAdminUrl);
			if(!StringUtils.endsWith(raAdminUrl, RAClientConstants.CHAR_FORWARD_SLASH)) {
				url.append(RAClientConstants.CHAR_FORWARD_SLASH);
			}
			url.append(RA_DEFN_URL).append(modelName).append(RAClientConstants.CHAR_FORWARD_SLASH)
					.append(StringUtils.substringBefore(modelVersion, RAClientConstants.CHAR_DOT)).append(RAClientConstants.CHAR_FORWARD_SLASH)
					.append(StringUtils.substringAfter(modelVersion, RAClientConstants.CHAR_DOT));

			HttpEntity<String> responseEntity = restTemplate
					.exchange(UriComponentsBuilder.fromHttpUrl(url.toString()).build().toUri(), HttpMethod.GET, entity, String.class);

			/*JSONObject responseObj = (new JSONObject(responseEntity.getBody())).getJSONObject("response");*/

			tenantIODefinition = new TenantIODefinition();

			/*tenantIODefinition
					.setTenantInputs(sortModelDefinition(responseObj.getJSONArray("tenantInputs")).toString());
			tenantIODefinition
					.setTenantOutputs(sortModelDefinition(responseObj.getJSONArray("tenantOutputs")).toString());*/

		} catch (HttpClientErrorException ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
		} catch (ResourceAccessException | HttpStatusCodeException ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
		} catch (Exception ex) {
			LOGGER.error("Error occurred ywhile invoking runtime request", ex);
		} finally {
			LOGGER.error("RA Execution time : {}");
		}

		return tenantIODefinition;
	}

	@Override
	public Map executeWeek0DPAModel(String tenantCode, String modelName, String modelVersion, String authToken, Map<String, Object> raRequest,
			DPProcessParamInfo info) throws SystemException {
		LOGGER.debug("Execute RA model --> start");
		Map response = null;
		try {
			String raRuntimeUrl = systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_RA_RUNTIME_URL);
			LOGGER.info("ra runtime url : " + raRuntimeUrl);
			LOGGER.info("ra model name : " + tenantCode);
			LOGGER.info("ra model version : " + modelVersion);
			validateUrl(raRuntimeUrl, SystemParameterConstant.SYS_PARAM_RA_RUNTIME_URL);
			LOGGER.info("ra tenantInfo authToken : " + authToken);

			response = restIntegrationClient
					.execute(raRuntimeUrl, raRequest, tenantCode + RAClientConstants.CHAR_DOT + EncryptionUtil.decryptToken(authToken),
							RA_CREDENTIAL_ARR[0], RA_CREDENTIAL_ARR[1], MediaType.APPLICATION_JSON, Map.class);

			String transactionID = null;

			LOGGER.info("RA Response  : " + response);

			if(null != response && response.containsKey("header")) {
				Map<String, Object> headerResponse = (Map<String, Object>) response.get("header");

				if(null != headerResponse && headerResponse.containsKey("umgTransactionId")) {
					transactionID = (String) headerResponse.get("umgTransactionId");
				}
			}

			logTransaction(raRequest, modelName, modelVersion, "0", transactionID);
		} catch (HttpClientErrorException ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
			markWeek0PropertyAsFailedInRa(info, ex);
			logTransaction(raRequest, modelName, modelVersion, CoreExceptionCodes.RACLNCOM001, null);
			throw new SystemException(CoreExceptionCodes.RACLNCOM001, new Object[] {}, ex);
		} catch (ResourceAccessException | HttpStatusCodeException ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
			markWeek0PropertyAsFailedInRa(info, ex);
			logTransaction(raRequest, modelName, modelVersion, CoreExceptionCodes.RACLNCOM001, null);
			throw new SystemException(CoreExceptionCodes.RACLNCOM001, new Object[] {}, ex);
		} catch (Exception ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
			markWeek0PropertyAsFailedInRa(info, ex);
			logTransaction(raRequest, modelName, modelVersion, CoreExceptionCodes.RACLNCOM001, null);
			throw new SystemException(CoreExceptionCodes.RACLNCOM001, new Object[] {}, ex);
		} finally {
			LOGGER.debug("Execute RA model --> end");
		}
		return response;
	}

	@Override
	public Map executeWeekNDPAModel(String tenantCode, String modelName, String modelVersion, String authToken, Map<String, Object> raRequest,
			DPProcessWeekNParamInfo dpProcessWeekNParamInfo) {
		LOGGER.debug("Execute RA model --> start");
		Map response = null;
		long startTime = System.currentTimeMillis();
		try {
			String raRuntimeUrl = systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_RA_RUNTIME_URL);
			LOGGER.info("ra runtime url : " + raRuntimeUrl);
			LOGGER.info("ra model name : " + tenantCode);
			LOGGER.info("ra model version : " + modelVersion);
			validateUrl(raRuntimeUrl, SystemParameterConstant.SYS_PARAM_RA_RUNTIME_URL);
			LOGGER.info("ra tenantInfo authToken : " + authToken);

			response = restIntegrationClient
					.execute(raRuntimeUrl, raRequest, tenantCode + RAClientConstants.CHAR_DOT + EncryptionUtil.decryptToken(authToken),
							RA_CREDENTIAL_ARR[0], RA_CREDENTIAL_ARR[1], MediaType.APPLICATION_JSON, Map.class);

			String transactionID = null;

			LOGGER.info("RA Response  : " + response);

			if(null != response && response.containsKey("header")) {
				Map<String, Object> headerResponse = (Map<String, Object>) response.get("header");

				if(null != headerResponse && headerResponse.containsKey("umgTransactionId")) {
					transactionID = (String) headerResponse.get("umgTransactionId");
				}
			}

			logTransaction(raRequest, modelName, modelVersion, "0", transactionID);
		} catch (HttpClientErrorException ex) {
			LOGGER.error("Error occurred while invoking runtime request for weekn HttpClientErrorException.");
			markWeeknPropertyAsFailedInRa(dpProcessWeekNParamInfo, startTime);
			logTransaction(raRequest, modelName, modelVersion, CoreExceptionCodes.RACLNCOM001, null);
		} catch (ResourceAccessException | HttpStatusCodeException ex) {
			LOGGER.error("Error occurred while invoking runtime request for weekn ResourceAccessException or HttpStatusCodeException.");
			markWeeknPropertyAsFailedInRa(dpProcessWeekNParamInfo, startTime);
			logTransaction(raRequest, modelName, modelVersion, CoreExceptionCodes.RACLNCOM001, null);
		} catch (Exception ex) {
			LOGGER.error("Error occurred while invoking runtime for weekn request.");
			markWeeknPropertyAsFailedInRa(dpProcessWeekNParamInfo, startTime);
			logTransaction(raRequest, modelName, modelVersion, CoreExceptionCodes.RACLNCOM001, null);
		} finally {
			LOGGER.debug("Execute RA model --> end");
		}
		return response;
	}

	@Override
	public Map executeSopWeekNDPAModel(String tenantCode, String modelName, String modelVersion, String authToken, Map<String, Object> raRequest)
			throws SystemException {
		LOGGER.debug("Execute RA model --> start");
		Map response = null;
		long startTime = System.currentTimeMillis();
		try {
			String raRuntimeUrl = systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_RA_RUNTIME_URL);
			LOGGER.info("ra runtime url : " + raRuntimeUrl);
			LOGGER.info("ra model name : " + tenantCode);
			LOGGER.info("ra model version : " + modelVersion);
			validateUrl(raRuntimeUrl, SystemParameterConstant.SYS_PARAM_RA_RUNTIME_URL);
			LOGGER.info("ra tenantInfo authToken : " + authToken);

			response = restIntegrationClient
					.execute(raRuntimeUrl, raRequest, tenantCode + RAClientConstants.CHAR_DOT + EncryptionUtil.decryptToken(authToken),
							RA_CREDENTIAL_ARR[0], RA_CREDENTIAL_ARR[1], MediaType.APPLICATION_JSON, Map.class);

			String transactionID = null;

			LOGGER.info("RA Response  : " + response);

			if(null != response && response.containsKey("header")) {
				Map<String, Object> headerResponse = (Map<String, Object>) response.get("header");

				if(null != headerResponse && headerResponse.containsKey("umgTransactionId")) {
					transactionID = (String) headerResponse.get("umgTransactionId");
				}
			}
			logTransaction(raRequest, modelName, modelVersion, "0", transactionID);
		} catch (HttpClientErrorException ex) {
			LOGGER.error("Error occurred while invoking runtime request for weekn HttpClientErrorException.");
			logTransaction(raRequest, modelName, modelVersion, CoreExceptionCodes.RACLNCOM001, null);
			SystemException.newSystemException(CoreExceptionCodes.RACLNCOM001);
		} catch (ResourceAccessException | HttpStatusCodeException ex) {
			LOGGER.error("Error occurred while invoking runtime request for weekn ResourceAccessException or HttpStatusCodeException.");
			logTransaction(raRequest, modelName, modelVersion, CoreExceptionCodes.RACLNCOM001, null);
			SystemException.newSystemException(CoreExceptionCodes.RACLNCOM001);
		} catch (Exception ex) {
			LOGGER.error("Error occurred while invoking runtime for weekn request.");
			logTransaction(raRequest, modelName, modelVersion, CoreExceptionCodes.RACLNCOM001, null);
			SystemException.newSystemException(CoreExceptionCodes.RACLNCOM001);
		} finally {
			LOGGER.debug("Execute RA model --> end");
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.fa.ra.client.core.rest.RAClient#executeModel(java.util.Map)
	 */
	@Override
	public Map executeModel(String modelName, String modelVersion, Map<String, Object> raRequest) throws SystemException {
		LOGGER.debug("Execute RA model --> start");
		Map response = null;
		KeyValue<String, String> tenantInfo = null;
		try {
			String raRuntimeUrl = systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_RA_RUNTIME_URL);
			LOGGER.info("ra runtime url : " + raRuntimeUrl);
			LOGGER.info("ra model name : " + modelName);
			LOGGER.info("ra model version : " + modelVersion);
			validateUrl(raRuntimeUrl, SystemParameterConstant.SYS_PARAM_RA_RUNTIME_URL);
			tenantInfo = getTenantInformationForModel(modelName, modelVersion);
			LOGGER.info("ra tenantInfo : " + tenantInfo);
			LOGGER.info("ra tenantInfo authToken : " + tenantInfo.getValue());
			HttpHeaders headers = buildHttpRequestHeader();
			headers.add("authToken", tenantInfo.getValue());
			final HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(raRequest, headers);
			ResponseEntity<Map> responseEntity = restTemplate.postForEntity(raRuntimeUrl, request, Map.class);
			if(responseEntity != null) {
				response = responseEntity.getBody();
			}
			logTransaction(raRequest, modelName, modelVersion, "0", null);
		} catch (HttpClientErrorException ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
			logTransaction(raRequest, modelName, modelVersion, CoreExceptionCodes.RACLNCOM001, null);
			throw new SystemException(CoreExceptionCodes.RACLNCOM001, new Object[] {});
		} catch (ResourceAccessException | HttpStatusCodeException ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
			logTransaction(raRequest, modelName, modelVersion, CoreExceptionCodes.RACLNCOM001, null);
			throw new SystemException(CoreExceptionCodes.RACLNCOM001, new Object[] {});
		} catch (Exception ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
			logTransaction(raRequest, modelName, modelVersion, CoreExceptionCodes.RACLNCOM001, null);
			throw new SystemException(CoreExceptionCodes.RACLNCOM001, new Object[] {});
		} finally {
			LOGGER.debug("Execute RA model --> end");
		}
		return response;
	}

	private void logTransaction(final Map request, final String modelName, final String modelVersion, final String errorCode, String transactionID) {
		executorService.submit(() -> saveTransactionDetails(request, modelName, modelVersion, errorCode, transactionID));
	}

	private void markWeeknPropertyAsFailedInRa(DPProcessWeekNParamInfo info, long startTime) {
		LOGGER.error("marking the weekn asset as ra failed : ");
		String process = CommandProcess.WEEKN_NRZ.getCommmandProcess();
		List<Command> command = commandDAO.findByProcess(process, DPAConstants.RA_FAIL_FILTER);
		CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
		info.setCommand(commandInfo);
		info.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
		info.setEligible(DPProcessFilterParams.ELIGIBLE.getValue());
		info.setAssignment(DPProcessFilterParams.ASSIGNMENT_ERROR.getValue());
		info.setExclusionReason(DPProcessFilterParams.RA_FAIL_EXCLUSION.getValue());
		dpProcessWeekNParamsBO.saveDPProcessWeekNParamInfo(info);

		CompletableFuture.runAsync(() -> {
			DPWeekNIntgAudit dpWeekNIntgAudit = new DPWeekNIntgAudit();
			dpWeekNIntgAudit.setEventType(IntegrationType.RA_INTEGRATION.getIntegrationType());
			dpWeekNIntgAudit.setStatus(TransactionStatus.FAIL.getTranStatus());
			dpWeekNIntgAudit.setStartTime(startTime);
			dpWeekNIntgAudit.setErrorDescription("Failed during RA Call");
			DPProcessWeekNParam dpProcessParam = new DPProcessWeekNParam();
			dpProcessParam.setId(info.getId());
			dpWeekNIntgAudit.setDpProcessWeekNParam(dpProcessParam);
			dpWeekNIntgAudit.setEndTime(System.currentTimeMillis());
			dpWeekNIntgAuditDao.save(dpWeekNIntgAudit);
		});
	}

	private void markWeek0PropertyAsFailedInRa(DPProcessParamInfo info, Exception e) throws SystemException {
		String errorDetail = dpFileProcessBO
				.saveDPProcessErrorDetail(info.getId(), IntegrationType.RA_INTEGRATION.getIntegrationType(), info.getErrorDetail(), e);
		info.setErrorDetail(errorDetail);
		info.setNotes(DPProcessParamAttributes.NOTES_RA.getValue());
		info.setWeek0Price(new BigDecimal(info.getListPrice()));
		info.setAssignment(DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue());
		info.setAssignmentDate(DateConversionUtil.getCurrentUTCTime().getMillis());
		String process = null;
		if(DPProcessParamAttributes.OCN.getValue().equals(info.getClassification()))
			process = CommandProcess.WEEK0_OCN.getCommmandProcess();
		else if(DPProcessParamAttributes.PHH.getValue().equals(info.getClassification()))
			process = CommandProcess.WEEK0_PHH.getCommmandProcess();
		else if(DPProcessParamAttributes.NRZ.getValue().equals(info.getClassification()))
			process = CommandProcess.WEEK0_NRZ.getCommmandProcess();
		List<Command> command = commandDAO.findByProcess(process, DPAConstants.RA_FILTER);
		CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
		info.setCommand(commandInfo);
		dpProcessParamsBO.saveDPProcessParamInfo(info);

		DynamicPricingIntgAudit dpIntgAudit = new DynamicPricingIntgAudit();
		DPProcessParam dpProcessParam = new DPProcessParam();
		dpProcessParam.setId(info.getId());
		dpIntgAudit.setDpProcessParam(dpProcessParam);
		dpIntgAudit.setStartTime(BigInteger.valueOf(DateConversionUtil.getMillisFromUtcToEst(System.currentTimeMillis())));
		Long endTime = DateConversionUtil.getMillisFromUtcToEst(System.currentTimeMillis());
		dpIntgAudit.setEndTime(BigInteger.valueOf(endTime));
		dpIntgAudit.setEventType(IntegrationType.RA_INTEGRATION.getIntegrationType());
		dpIntgAudit.setStatus(TransactionStatus.FAIL.getTranStatus());
		if(info.getErrorDetail() != null) {
			Map<String, String> errorMap = ConversionUtil.convertJson(info.getErrorDetail(), Map.class);
			dpIntgAudit.setErrorDescription(errorMap.get(IntegrationType.RA_INTEGRATION.getIntegrationType()));
		}
		dpFileProcessBO.saveDPProcessIntgAudit(dpIntgAudit);

	}

	private void saveTransactionDetails(Map request, String modelName, String modelVersion, String errorCode, String transactionID) {
		LOGGER.debug("saveTransactionDetails --> start");
		Map<String, Object> header = (Map<String, Object>) request.get("header");
		TransactionInfo transaction = new TransactionInfo();
		transaction.setModelName((String) header.get("modelName"));

		LOGGER.debug("saveTransactionDetails --> getModelInfo");
		ModelDetailInfo modelDetailInfo = getModelInfo(modelName, modelVersion);

		LOGGER.debug("saveTransactionDetails --> setting transaction param start");
		transaction.setMajorVersion(modelDetailInfo.getMajorVersion());
		transaction.setMinorVersion(modelDetailInfo.getMinorVersion());
		transaction.setClientTransactionId((String) header.get("transactionId"));
		transaction.setTransactionDate((RAClientUtil.utcDateStringToLong(
				(header != null && header.get("date") != null) ? (String) header.get("date") : (new LocalDateTime()).toString())));
		transaction.setUser(SecurityContextHolder.getContext() != null ?
				(SecurityContextHolder.getContext().getAuthentication() != null ?
						SecurityContextHolder.getContext().getAuthentication().getName() :
						"SYSTEM") :
				"SYSTEM");
		transaction.setTenantCode(modelDetailInfo.getTenant().getCode());
		transaction.setStatus(errorCode != null ? errorCode : "Failed");
		transaction.setRaTransactionId(transactionID);
		LOGGER.debug("saveTransactionDetails --> setting transaction param end ");
		try {
			transaction.setTenantInput(mapper.writeValueAsBytes(request));
			transaction.setTenantOutput(mapper.writeValueAsBytes(RAClientConstants.CHAR_EMPTY));
			transactionDelegate.saveTransaction(transaction);
		} catch (JsonProcessingException e) {
			LOGGER.error("An error occurred while parsing request.", e);
		} catch (SystemException e) {
			LOGGER.error("An error occurred while saving transaction.", e);
		}
		LOGGER.debug("saveTransactionDetails --> ends");
	}

	@Override
	public List<Transaction> fetchRATransaction(String modelName, String transactionType, String fromDate, String endDate) {
		List<Transaction> transactions = null;
		try {

			String raAdminUrl = systemParameterProvider.getSystemParamValue(SystemParameterConstant.SYS_PARAM_RA_ADMIN_URL);

			validateUrl(raAdminUrl, SystemParameterConstant.SYS_PARAM_RA_ADMIN_URL);

			KeyValue<String, String> tenantInfo = getTenantInformationForModel(modelName);

			HttpHeaders headers = new HttpHeaders();
			headers.add("AuthToken", tenantInfo.getValue());
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity entity = new HttpEntity<>(headers);

			String baseUrl = (StringUtils.endsWith(raAdminUrl, RAClientConstants.CHAR_FORWARD_SLASH) ?
					raAdminUrl :
					raAdminUrl + RAClientConstants.CHAR_FORWARD_SLASH) + RA_TXN_URL + RAClientConstants.CHAR_FORWARD_SLASH + modelName;

			UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl).queryParam("runAsOfDateFrom", RAClientUtil
					.getDateFormatEpoch(RAClientUtil.convertTimeToMills(fromDate, RAClientConstants.RA_CLIENT_DATE_FORMAT),
							RAClientConstants.RA_UTC_DATE_FORMAT)).queryParam("runAsOfDateTo", RAClientUtil
					.getDateFormatEpoch(RAClientUtil.convertTimeToMills(endDate, RAClientConstants.RA_CLIENT_DATE_FORMAT),
							RAClientConstants.RA_UTC_DATE_FORMAT));

			HttpEntity<RestResponseForApi> response = restTemplate
					.exchange(uriComponentsBuilder.build().toUri(), HttpMethod.GET, entity, RestResponseForApi.class);
			RestResponseForApi<TransactionWrapperForApi> restResponseForApi = response.getBody();

			if(!restResponseForApi.isError()) {
				Map responseMap = (Map) restResponseForApi.getResponse();
				int totalCount = (int) responseMap.get("totalCount");
				List<Map<String, Object>> transactionsList = (List<Map<String, Object>>) responseMap.get("transactions");
				transactions = buildFromRATransactions(transactionsList);
			}
		} catch (HttpClientErrorException ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
		} catch (ResourceAccessException | HttpStatusCodeException ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
		} catch (Exception ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
		} finally {
			LOGGER.error("RA Execution time : {}");
		}

		return transactions;
	}

	/*private JSONArray sortModelDefinition(JSONArray list) {
		int len, i;
		JSONArray tempList = new JSONArray();
		try {
			if (list != null) {

				len = list.length();

				i = 0;
				while (i < len) {
					if (list.getJSONObject(i).isNull("children")) {
						tempList.put(list.getJSONObject(i));
						list.remove(i);
						len--;
						continue;
					} else if ((!list.getJSONObject(i).isNull("children"))
							&& list.getJSONObject(i).get("children") instanceof JSONArray) {
						list.getJSONObject(i).put("children",
								sortModelDefinition(list.getJSONObject(i).getJSONArray("children")));

					}
					i++;
				}

				if (tempList.length() > 0) {
					for (int j = 0; j < list.length(); j++)
						tempList.put(list.get(j));
				} else {
					tempList = list;
				}

			}
		} catch (JSONException ex) {
			LOGGER.error("Error occurred sorting I/O definition", ex);
		} catch (Exception ex) {
			LOGGER.error("Error occurred sorting I/O definition", ex);
		} finally {
			LOGGER.error("sorting I/O definition::: end ");
		}
		return tempList;
	}*/
}