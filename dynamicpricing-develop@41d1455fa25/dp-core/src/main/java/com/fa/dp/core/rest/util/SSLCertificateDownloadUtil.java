package com.fa.dp.core.rest.util;

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
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.rest.exception.codes.RAIntegrationErrorCodes;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import sun.security.x509.X509CertImpl;

public class SSLCertificateDownloadUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(SSLCertificateDownloadUtil.class);

	private static final String PASSWORD = "PASSWORD";

	public static void setupCertificates(String url, RestTemplate restTemplate, String certificateCommonName,
			String keyStoreName, String certificateAliasName, int connectionTimeout, int readTimout, String proxyUrl,
			int proxyPortValue, String proxyUser, String proxyPwd) {
		if (StringUtils.startsWith(url, "https:")) {
			try {
				downloadAndInstallCertificate(url, restTemplate, certificateCommonName, keyStoreName,
						certificateAliasName, connectionTimeout, readTimout, proxyUrl, proxyPortValue, proxyUser,
						proxyPwd);
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

			if (restTemplate.getRequestFactory() instanceof SimpleClientHttpRequestFactory) {
				((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory())
						.setConnectTimeout(connectionTimeout * 1000);
				((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(readTimout * 1000);
			} else if (restTemplate.getRequestFactory() instanceof HttpComponentsClientHttpRequestFactory) {
				((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory())
						.setConnectTimeout(connectionTimeout * 1000);
				((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory())
						.setReadTimeout(readTimout * 1000);
			}
		} else {
			((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory())
					.setConnectTimeout(connectionTimeout * 1000);
			((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(readTimout * 1000);
		}
	}

	public static void validateUrl(String url, String module) throws SystemException {
		if (StringUtils.isBlank(url)) {
			SystemException.newSystemException(RAIntegrationErrorCodes.RAINT001, new Object[] { module });
		}
	}

	private static void downloadAndInstallCertificate(String runtimeUrl, RestTemplate restTemplate,
			String certificateCommonName, String keyStoreName, String certificateAliasName, int connectionTimeout,
			int readTimout, String proxyUrl, int proxyPortValue, String proxyUser, String proxyPwd)
			throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException,
			KeyManagementException {
		FileOutputStream fileOutputStream = null;
		FileInputStream fileInputStream = null;
		// Download certificates start
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };
		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HostnameVerifier allHostsValid = (hostname, session) -> true;
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

		Proxy proxy = null;
		HttpHost httpHost = null;
		// CredentialsProvider credentialsProvider = null;
		if (StringUtils.isNotEmpty(proxyUrl)) {
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl, proxyPortValue));
			LOGGER.debug("Proxy created with Host: {} and Port: {}", proxyUrl, proxyPortValue);
			httpHost = new HttpHost(proxyUrl, proxyPortValue, "http");
			LOGGER.debug("Http Host for client factory created successfully");
		}

		if (StringUtils.isNotEmpty(proxyUser)) {
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
			if (cert instanceof X509Certificate) {
				subjectDNName = ((X509CertImpl) certs[0]).getSubjectDN().getName();
				if (subjectDNName.contains(certificateCommonName)) {
					arrayInputStream = new ByteArrayInputStream(cert.getEncoded());
					break;
				}
			}
		}
		// Download certificates end

		// store into keystore start
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(null, null);
		BufferedInputStream bis = new BufferedInputStream(arrayInputStream);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		Certificate cert = null;
		while (bis.available() > 0) {
			cert = cf.generateCertificate(bis);
			ks.setCertificateEntry(certificateAliasName, cert);
		}
		ks.setCertificateEntry(certificateAliasName, cert);
		File file = null;
		try {
			fileOutputStream = new FileOutputStream(keyStoreName);
			ks.store(fileOutputStream, PASSWORD.toCharArray());
			// store into keystore end

			file = new File(keyStoreName);
			fileInputStream = new FileInputStream(file);
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(fileInputStream, PASSWORD.toCharArray());
		} finally {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		}

		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(file, PASSWORD.toCharArray())
				.build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

		HttpClientBuilder clientBuilder = HttpClients.custom().setSSLSocketFactory(csf);

		LOGGER.debug("HTTP Client Builder is created");
		if (httpHost != null) {
			clientBuilder.setProxy(httpHost);
			LOGGER.debug("Proxy is set to HTTP Client Builder");
		}
		// CloseableHttpClient httpClient =
		// HttpClients.custom().setSSLSocketFactory(csf).setProxy(httpHost).build();
		RequestConfig clientConfig = RequestConfig.custom().setConnectTimeout(connectionTimeout)
				.setSocketTimeout(readTimout).setConnectionRequestTimeout(readTimout).build();
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
}
