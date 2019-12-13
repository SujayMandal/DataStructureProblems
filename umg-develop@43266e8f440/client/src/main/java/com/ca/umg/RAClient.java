package com.ca.umg;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import sun.security.x509.X509CertImpl;

import com.ca.exception.ExceptionCodes;
import com.ca.exception.RAClientException;

/**
 * Created by repvenk on 5/11/2016.
 */
public class RAClient implements IRAClient {

	private RestTemplate restTemplate = new RestTemplate();

	private String runtimeUrl;
	private String authToken;

	private int connectTimeout = 60 * 1000;
	private int readTimeout = 60 * 1000;

	private final String KEY_STORE_NAME = "RR-RA-KEY_STORE";
	private final String CERTIFICATE_ALIAS_NAME = "RR-RA-intcert";
	private final String PASSWORD = "$3cr3t";
	private final String certificateCommonName = "*.modeloncloud.com";

	private String proxyHostName;
	private int proxyPort;
	private String proxyUserName;
	private String proxyPassword;
	private volatile Map<String, Boolean> certificatesDownloaded = new HashMap<>();
	private ObjectMapper objectMapper = new ObjectMapper();
	private HttpClientBuilder clientBuilder = null;
	private RequestConfig clientConfig = null;

	private static final Logger LOGGER = LoggerFactory.getLogger(RAClient.class);

	public RAClient(String runtimeUrl, String authToken) {
		this.runtimeUrl = runtimeUrl;
		this.authToken = authToken;
	}

	public RAClient(String runtimeUrl, String authToken, Integer connectTimeout, Integer readTimeout) {
		this.runtimeUrl = runtimeUrl;
		this.authToken = authToken;
		this.connectTimeout = connectTimeout != null & connectTimeout > 0 ? connectTimeout * 1000 : 60 * 1000;
		this.readTimeout = readTimeout != null && readTimeout > 0 ? readTimeout * 1000 : 60 * 1000;
	}

	public RAClient(String runtimeUrl, String authToken, String proxyHostName, int proxyPort, String proxyUserName,
			String proxyPassword) {
		this.runtimeUrl = runtimeUrl;
		this.authToken = authToken;
		this.proxyHostName = proxyHostName;
		this.proxyPort = proxyPort;
		this.proxyUserName = proxyUserName;
		this.proxyPassword = proxyPassword;

	}

	public RAClient(String runtimeUrl, String authToken, int connectTimeout, int readTimeout, String proxyHostName,
			int proxyPort, String proxyUserName, String proxyPassword) {
		this.runtimeUrl = runtimeUrl;
		this.authToken = authToken;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.proxyHostName = proxyHostName;
		this.proxyPort = proxyPort;
		this.proxyUserName = proxyUserName;
		this.proxyPassword = proxyPassword;
	}

	private void downloadAndInstallCertificate() throws CertificateException, NoSuchAlgorithmException,
			KeyStoreException, IOException, KeyManagementException, UnrecoverableKeyException {
		FileOutputStream fileOutputStream;
		FileInputStream fileInputStream;
		// Download certificates start
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };

		Proxy proxy = null;
		HttpHost httpHost = null;
		// CredentialsProvider credentialsProvider = null;
		if (StringUtils.isNotEmpty(proxyHostName)) {
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHostName, proxyPort));
			LOGGER.debug("Proxy created with Host: {} and Port: {}", proxyHostName, proxyPort);
			httpHost = new HttpHost(proxyHostName, proxyPort, "http");
			LOGGER.debug("Http Host for client factory created successfully");
		}

		if (StringUtils.isNotEmpty(proxyUserName)) {
			Authenticator authenticator = new Authenticator() {

				public PasswordAuthentication getPasswordAuthentication() {
					return (new PasswordAuthentication(proxyUserName, proxyPassword.toCharArray()));
				}
			};
			Authenticator.setDefault(authenticator);
			LOGGER.debug("Authenticator created with user name {} and password {}", proxyUserName, "******");
			/*
			 * credentialsProvider = new BasicCredentialsProvider();
			 * credentialsProvider.setCredentials(new AuthScope(proxyHostName, proxyPort),
			 * new UsernamePasswordCredentials(proxyUserName, proxyPassword));
			 */
		}

		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String s, SSLSession sslSession) {
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		URL destinationURL = new URL(runtimeUrl);

		HttpsURLConnection conn = (HttpsURLConnection) (proxy == null ? destinationURL.openConnection()
				: destinationURL.openConnection(proxy));
		conn.connect();
		LOGGER.debug("Connection created to download certificates");
		ByteArrayInputStream arrayInputStream = null;
		Certificate[] certs = conn.getServerCertificates();
		String subjectDNName = null;
		for (Certificate cert : certs) {
			LOGGER.debug("Certificate type {}", cert.getType());
			LOGGER.debug("Certificate name {}", cert.getClass().getName());
		}
		for (Certificate cert : certs) {
			if (cert instanceof X509Certificate) {
				subjectDNName = ((X509CertImpl) certs[0]).getSubjectDN().getName();
				if (subjectDNName.contains(certificateCommonName)) {
					arrayInputStream = new ByteArrayInputStream(cert.getEncoded());
					LOGGER.debug("Certificate downloaded and converted to Input Stream");
					break;
				}

			}
		}
		// Download certificates end

		// store into keystore start
		char sep = File.separatorChar;
		KeyStore ks = KeyStore.getInstance("JKS");
		LOGGER.debug("Opened keystore");
		ks.load(null, null);
		BufferedInputStream bis = new BufferedInputStream(arrayInputStream);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		LOGGER.debug("Certificate factory instance is loaded");
		Certificate cert = null;
		while (bis.available() > 0) {
			cert = cf.generateCertificate(bis);
			ks.setCertificateEntry(CERTIFICATE_ALIAS_NAME, cert);
		}
		ks.setCertificateEntry(CERTIFICATE_ALIAS_NAME, cert);
		LOGGER.debug("Certificate added to keystore");
		fileOutputStream = new FileOutputStream(KEY_STORE_NAME);
		ks.store(fileOutputStream, PASSWORD.toCharArray());
		fileOutputStream.close();
		LOGGER.debug("Closed keystore");
		// store into keystore end

		File file = new File(KEY_STORE_NAME);
		fileInputStream = new FileInputStream(file);
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		try {
		trustStore.load(fileInputStream, PASSWORD.toCharArray());
		} finally {
			try {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		} catch (IOException e) {
			 LOGGER.error(e.getMessage());
		}
		}
		LOGGER.debug("Added to trust store");

		SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(trustStore, PASSWORD.toCharArray())
				.loadTrustMaterial(trustStore).build();
		LOGGER.debug("SSL context is created");
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
		LOGGER.debug("SSL connection factory is created");

		// CloseableHttpClient httpClient =
		// HttpClients.custom().setSSLSocketFactory(csf).setProxy(httpHost).setDefaultCredentialsProvider(credentialsProvider).setProxyAuthenticationStrategy(new
		// ProxyAuthenticationStrategy()).build();
		clientBuilder = HttpClients.custom().setSSLSocketFactory(csf);
		LOGGER.debug("HTTP Client Builder is created");
		if (httpHost != null) {
			clientBuilder.setProxy(httpHost);
			LOGGER.debug("Proxy is set to HTTP Client Builder");
		}
		// CloseableHttpClient httpClient =
		// HttpClients.custom().setSSLSocketFactory(csf).setProxy(httpHost).build();
		RequestConfig clientConfig = RequestConfig.custom().setConnectTimeout(connectTimeout)
				.setSocketTimeout(readTimeout).setConnectionRequestTimeout(readTimeout).build();
		this.clientConfig = clientConfig;
		clientBuilder.setDefaultRequestConfig(clientConfig);
		CloseableHttpClient httpClient = clientBuilder.build();
		LOGGER.debug("Http Client is created");

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		if (connectTimeout != 0) {
			requestFactory.setConnectTimeout(connectTimeout);
		}
		if (readTimeout != 0) {
			requestFactory.setReadTimeout(readTimeout);
		}

		requestFactory.setHttpClient(httpClient);
		file.delete();
		restTemplate.setRequestFactory(requestFactory);
	}

	private void setRequestFactoryForInvocation() throws RAClientException {
		if (runtimeUrl.startsWith("https")) {
			if (!certificatesDownloaded.containsKey(runtimeUrl)) {
				try {
					synchronized (certificatesDownloaded) {
						if (!certificatesDownloaded.containsKey(runtimeUrl)) {
							downloadAndInstallCertificate();
						}
					}
					certificatesDownloaded.put(runtimeUrl, Boolean.TRUE);
				} catch (CertificateException e) {
					LOGGER.error("Certificate Exception", e);
					throw new RAClientException(ExceptionCodes.RACLIENTEXECPTIONCODE3, e);
				} catch (NoSuchAlgorithmException e) {
					LOGGER.error("NoSuchAlgorithmException Exception", e);
					throw new RAClientException(ExceptionCodes.RACLIENTEXECPTIONCODE4, e);
				} catch (KeyStoreException e) {
					LOGGER.error("KeyStoreException Exception", e);
					throw new RAClientException(ExceptionCodes.RACLIENTEXECPTIONCODE5, e);
				} catch (IOException e) {
					LOGGER.error("IOException Exception", e);
					throw new RAClientException(ExceptionCodes.RACLIENTEXECPTIONCODE1, e);
				} catch (KeyManagementException e) {
					LOGGER.error("KeyManagementException Exception", e);
					throw new RAClientException(ExceptionCodes.RACLIENTEXECPTIONCODE6, e);
				} catch (UnrecoverableKeyException e) {
					LOGGER.error("UnrecoverableKeyException Exception", e);
					throw new RAClientException(ExceptionCodes.RACLIENTEXECPTIONCODE7, e);
				} catch (Exception e) {
					LOGGER.error("Exception", e);
					throw new RAClientException(ExceptionCodes.RACLIENTEXECPTIONCODE2, e);
				}
			}
		}
		/*
		 * else { if(restTemplate.getRequestFactory() == null) { CloseableHttpClient
		 * httpClient = HttpClients.createDefault();
		 * HttpComponentsClientHttpRequestFactory requestFactory = new
		 * HttpComponentsClientHttpRequestFactory();
		 * requestFactory.setHttpClient(httpClient);
		 * restTemplate.setRequestFactory(requestFactory); } }
		 */
	}

	private HttpHeaders generateHeaders(String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Basic " + getRuntimeCredentials());
		headers.add("authToken", authToken);
		return headers;
	}

	public byte[] execute(byte[] requestBytes) throws RAClientException {
		validate(runtimeUrl, authToken, requestBytes);
		LOGGER.debug("Validation successful");
		setRequestFactoryForInvocation();
		HttpHeaders headers = generateHeaders(authToken);
		LOGGER.debug("Headers generated");
		try {
			Map inputMap = objectMapper.readValue(requestBytes, Map.class);
			LOGGER.debug("Bytes to Map transformation done");
			final HttpEntity<Map> request = new HttpEntity<>(inputMap, headers);
			LOGGER.debug("Posting data to {}", runtimeUrl);
			ResponseEntity<String> response = restTemplate.postForEntity(runtimeUrl, request, String.class);
			LOGGER.debug("Response from {}", runtimeUrl);
			LOGGER.info("Response Data: \n" + response.getBody().toString());
			return response.getBody().getBytes();
		} catch (IOException e) {
			LOGGER.error("IOException Exception", e);
			throw new RAClientException(ExceptionCodes.RACLIENTEXECPTIONCODE1, e);
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			throw new RAClientException(ExceptionCodes.RACLIENTEXECPTIONCODE8, e);
		}
	}

	public Map execute(Map inputMap) throws RAClientException {
		validate(runtimeUrl, authToken, inputMap);
		LOGGER.debug("Validation successful");
		setRequestFactoryForInvocation();
		HttpHeaders headers = generateHeaders(authToken);
		LOGGER.debug("Headers generated");
		try {
			final HttpEntity<Map> request = new HttpEntity<>(inputMap, headers);
			LOGGER.debug("Posting data to {}", runtimeUrl);
			LOGGER.info("Request Data: \n" + inputMap.toString());
			ResponseEntity<String> response = restTemplate.postForEntity(runtimeUrl, request, String.class);
			LOGGER.debug("Response from {}", runtimeUrl);
			LOGGER.info("Response Data: \n" + response.getBody().toString());
			Map mapResponse = objectMapper.readValue(response.getBody().toString(), Map.class);
			return mapResponse;
		} catch (Exception e) {
			LOGGER.error("Exception", e);
			throw new RAClientException(ExceptionCodes.RACLIENTEXECPTIONCODE1, e);
		}
	}

	private String getRuntimeCredentials() {
		String username = "admin";
		String pwd = "admin";
		String plainCreds = username + ":" + pwd;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		return new String(base64CredsBytes);
	}

	private void validate(String runtimeUrl, String authToken, byte[] requestData) throws RAClientException {
		if (StringUtils.isBlank(runtimeUrl) || StringUtils.isBlank(authToken) || requestData == null) {
			throw new RAClientException("RA url, authToken and request data are mandatory");
		}
	}

	private void validate(String runtimeUrl, String authToken, Map requestData) throws RAClientException {
		if (StringUtils.isBlank(runtimeUrl) || StringUtils.isBlank(authToken) || requestData == null
				|| requestData.isEmpty()) {
			throw new RAClientException("RA url, authToken and request data are mandatory");
		}
	}

	public void setRuntimeUrl(String runtimeUrl) {
		this.runtimeUrl = runtimeUrl;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void setProxyHostName(String proxyHostName) {
		this.proxyHostName = proxyHostName;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public void setProxyUserName(String proxyUserName) {
		this.proxyUserName = proxyUserName;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout * 1000;
		if (restTemplate != null && restTemplate.getRequestFactory() instanceof HttpComponentsClientHttpRequestFactory
				&& clientConfig.getConnectTimeout() != this.connectTimeout) {
			RequestConfig clientConfig = RequestConfig.custom().setConnectTimeout(this.connectTimeout)
					.setSocketTimeout(this.readTimeout).setConnectionRequestTimeout(this.readTimeout).build();
			this.clientConfig = clientConfig;
			clientBuilder.setDefaultRequestConfig(clientConfig);
			CloseableHttpClient httpClient = clientBuilder.build();
			((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory()).setHttpClient(httpClient);
		} else if (restTemplate != null && restTemplate.getRequestFactory() instanceof SimpleClientHttpRequestFactory) {
			((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(connectTimeout);
		}
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout * 1000;
		if (restTemplate != null && restTemplate.getRequestFactory() instanceof HttpComponentsClientHttpRequestFactory
				&& clientConfig.getSocketTimeout() != this.readTimeout) {
			RequestConfig clientConfig = RequestConfig.custom().setConnectTimeout(this.connectTimeout)
					.setSocketTimeout(this.readTimeout).setConnectionRequestTimeout(this.readTimeout).build();
			clientBuilder.setDefaultRequestConfig(clientConfig);
			CloseableHttpClient httpClient = clientBuilder.build();
			((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory()).setHttpClient(httpClient);
		} else if (restTemplate != null && restTemplate.getRequestFactory() instanceof SimpleClientHttpRequestFactory) {
			((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(readTimeout);
		}
	}

	public static void main(String[] args) throws InterruptedException {

		if (args.length < 3) {
			System.out.println("arg[0] is runtime url, arg[1] is authtoken and arg[2] is input json path");
			System.exit(0);
		}

		String authToken = args[0];
		String invocationURL = args[1];
		String proxyHostName = System.getProperty("proxyHostName");
		String proxyPort = StringUtils.defaultString(System.getProperty("proxyPort"), "0");
		String proxyUserName = System.getProperty("proxyUserName");
		String proxyPassword = System.getProperty("proxyPassword");
		Integer connectTimeout = 60;
		Integer readTimeout = 60;

		RAClient raClient = new RAClient(invocationURL, authToken);
		raClient.setProxyHostName(proxyHostName);
		raClient.setProxyPort(Integer.parseInt(proxyPort));
		raClient.setProxyUserName(proxyUserName);
		raClient.setProxyPassword(proxyPassword);

		RAClient raClientWithTimeouts = new RAClient(invocationURL, authToken, connectTimeout, readTimeout);
		Path path = Paths.get(args[2]);
		try {
			byte[] data = Files.readAllBytes(path);
			raClient.execute(data);
			Thread.sleep(5000);
			raClientWithTimeouts.execute(data);
			Thread.sleep(5000);
		} catch (IOException e) {
			LOGGER.error("IOException : ", e);
		} catch (RAClientException e) {
			LOGGER.error("RAClientException : ", e);
		}
	}

}