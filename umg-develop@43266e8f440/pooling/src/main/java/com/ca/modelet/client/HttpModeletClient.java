package com.ca.modelet.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.common.ExceptionResolver;

public class HttpModeletClient implements ModeletClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpModeletClient.class);

	private static final long serialVersionUID = -4473490510204518557L;

	private HttpURLConnection conn;

	private final String host;

	private final int port;

	private final String contextPath;

	public HttpModeletClient(final String host, final int port, final String contextPath) {
		this.host = host;
		this.port = port;
		this.contextPath = contextPath;
	}

	public static void main(String[] args) {
		HttpModeletClient client;
		try {
			client = new HttpModeletClient("localhost", 8000, "");
			String response = client.sendData("");
			LOGGER.info("Response received from server {}", response);
		} catch (SystemException e) {
			LOGGER.error("Exception occured.", e);
		} catch (BusinessException exp) {
			LOGGER.error("Exception occured.", exp);
		}
	}

	public String sendData(String input) throws SystemException, BusinessException {
		String response = null;
		DataOutputStream out = null;
		DataInputStream dataInputStream = null;
		try {
			LOGGER.info("Sending data to Modelet");
			out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(input);
			dataInputStream = new DataInputStream(conn.getInputStream());
			byte[] bytes = new byte[conn.getContentLength()];
			dataInputStream.readFully(bytes);
			response = new String(bytes);
			ExceptionResolver.resolveException(response);
			LOGGER.info("Received data from Modelet");
		} catch (MalformedURLException e) {
			throw new SystemException("", new String[] { "" }, e);
		} catch (IOException e) {
			throw new SystemException("", new String[] { "" }, e);
		} finally {
			closeResources(out, dataInputStream);
		}
		return response;
	}

	private void closeResources(DataOutputStream out, DataInputStream dataInputStream) {
		try {
			if (out != null) {
				out.flush();
				out.close();
			}
			if (dataInputStream != null) {
				dataInputStream.close();
			}
		} catch (Exception e) {
			LOGGER.error("Exception while closing the Stream :" +e.getMessage());
		}
	}

	public void shutdownClient() {
		getConn().disconnect();
	}

	public void createConnection() throws SystemException {
		URL url = null;
		try {
			url = new URL("http://" + host + ":" + port + contextPath);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			getConn().connect();
			LOGGER.info("Connection successful to url {}", this.conn.getURL());
		} catch (IOException e) {
			throw new SystemException("", new String[] { "" }, e);
		}
	}

	public void shutdownConnection() {
		getConn().disconnect();
	}

	private HttpURLConnection getConn() {
		return conn;
	}

}
