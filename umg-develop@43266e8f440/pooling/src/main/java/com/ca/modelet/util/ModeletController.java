package com.ca.modelet.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ModeletController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ModeletController.class);
	
	private static final int MODELET_STATUS = 0;
	private static final int MODELET_LOAD_NEW1 = 1;
	private static final int MODELET_LOAD_NEW2 = 2;
	private static final int MODELET_SELF_DESTRUCT = 3;

	private Socket clientSocket;

	private String hostname;
	private int port;
	private String username;
	private String idKeyLoc;
	private String password;

	/*public static void main(String[] args) throws Exception {

		try {
			ModeletController mctrller = new ModeletController();
			mctrller.hostname = args[0];
			mctrller.port = Integer.parseInt(args[1]);
			mctrller.username = args[2];
			mctrller.idKeyLoc = args[3];

			if (args.length > 4)
				mctrller.password = args[4];
			else
				mctrller.password = "";

			mctrller.sendControlMessage(MODELET_STATUS);
			Thread.sleep(1000);
			mctrller.sendControlMessage(MODELET_LOAD_NEW1);
			Thread.sleep(1000);
			mctrller.sendControlMessage(MODELET_STATUS);
			Thread.sleep(1000);
			mctrller.sendControlMessage(MODELET_LOAD_NEW2);
			Thread.sleep(1000);
			mctrller.sendControlMessage(MODELET_STATUS);
			Thread.sleep(1000);
			mctrller.sendControlMessage(MODELET_SELF_DESTRUCT);
			Thread.sleep(1000);
			try {
				mctrller.sendControlMessage(MODELET_STATUS);
			} catch (Exception e) {
				System.out.println(" Modelet STATUS request:");
				System.out.println(" ****  " + e.getMessage() + " --- Because modelet has been killed *******");
				System.out.println("============================================================");
			}
			Thread.sleep(1000);
			System.out.println("Attempting to restart modelet from ME2");
			mctrller.startNewModelet();
			System.out.println("Waiting 20 seconds for modelet to start up");
			System.out.println("============================================================");
			Thread.sleep(20000);
			mctrller.sendControlMessage(MODELET_STATUS);
			Thread.sleep(1000);
			mctrller.sendControlMessage(MODELET_LOAD_NEW1);
			Thread.sleep(1000);
			mctrller.sendControlMessage(MODELET_STATUS);
		} catch (Exception e) {
			LOGGER.error("Exception: ", e);
		}
	}*/

	private void sendControlMessage(int messageType) throws Exception {
		createConnection(hostname, port);

		String inputMsg = "";

		if (messageType == MODELET_STATUS) {
			inputMsg = "{  " + " \"headerInfo\":{  " + "	\"type\":\"CONTROL\" , " + "	\"msg\":\"STATUS\" " + " } "
					+ "}";
		} else if (messageType == MODELET_LOAD_NEW1) {
			inputMsg = "{  " + " \"headerInfo\":{  " + "	\"type\":\"CONTROL\" , " + "	\"msg\":\"LOAD\", "
					+ "	\"modelName\":\"Hubzu\" , " + "	\"modelLibraryVersionName\":\"version1\"  " + " } " + "}";
		} else if (messageType == MODELET_LOAD_NEW2) {
			inputMsg = "{  " + " \"headerInfo\":{  " + "	\"type\":\"CONTROL\" , " + "	\"msg\":\"LOAD\", "
					+ " 	\"modelName\":\"ECMA\" , " + "	\"modelLibraryVersionName\":\"version2\"  " + " }  " + "}";
		} else if (messageType == MODELET_SELF_DESTRUCT) {
			inputMsg = "{  " + " \"headerInfo\":{  " + "	\"type\":\"CONTROL\" , " + "	\"msg\":\"DESTRUCT\" "
					+ " } " + "}";
		}

		String inputData = new String(IOUtils.toByteArray(inputMsg));
		String strRet = sendData(inputData);

		ObjectMapper mapper = new ObjectMapper();
		ModelResponseInfo response = mapper.readValue(strRet, ModelResponseInfo.class);

		if (response != null && response.getPayload() != null) {
			List<LinkedHashMap<String, Object>> fiList = (List<LinkedHashMap<String, Object>>) response.getPayload();
			// first is fieldName "type"
			System.out.println("Modelet " + fiList.get(0).get("value") + " Request:");

			if (fiList.get(0).get("value").equals("STATUS")) {
				System.out.println("Model: " + fiList.get(1).get("value") + " Version:" + fiList.get(2).get("value"));
			} else if (fiList.get(0).get("value").equals("LOAD") || fiList.get(0).get("value").equals("DESTRUCT")) {
				System.out.println("Response: " + fiList.get(1).get("value"));
			}

		}
		System.out.println(strRet);
		System.out.println("============================================================");
		shutdownConnection();
	}

	private void startNewModelet() {

		SSHConnector sshConnector = new SSHConnector(hostname, username, password, idKeyLoc);
		sshConnector.connectToServer();

		String command = "nohup java -XX:MaxPermSize=256m -Xmx1536m -Dlogroot=" + port + "-Dloglevel=error -Dport="
				+ port + " -DserverType=SOCKET -DsanPath=/sanpath "
				+ "-Dworkspace=/opt/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector "
				+ "-DisThreadContextMapInheritable=true -jar /opt/ms-umg/modelet.one-jar.jar > " + port
				+ "_auto.log 2>&1 &";

		System.out.println(command);

		// String command = "sh /opt/ms-umg/modelet_start_script.sh";

		sshConnector.executeCommand(command);

		sshConnector.disconnectFromServer();

	}

	public void createConnection(String hostName, int port) throws Exception {
		try {
			clientSocket = new Socket();
			clientSocket.connect(new InetSocketAddress(hostName, port));
		} catch (IOException e) {
			throw e;
		}
	}

	public String sendData(final String input) throws Exception {
		writeToServer(input);
		String response = readFromServer();
		return response;
	}

	private void writeToServer(String input) throws Exception {
		try {
			OutputStream outToServer = clientSocket.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			out.writeInt(input.getBytes().length);
			out.write(input.getBytes());
			out.flush();
		} catch (Exception e) { // NOPMD
			throw e;
		}
	}

	private String readFromServer() throws Exception {
		String response = null;
		try {
			InputStream inFromServer = clientSocket.getInputStream();
			DataInputStream inStream = new DataInputStream(inFromServer);
			int length = inStream.readInt();
			byte[] respBytes = new byte[length];
			inStream.readFully(respBytes);
			response = new String(respBytes);
		} catch (Exception e) { // NOPMD
			throw e;
		}
		return response;
	}

	public void shutdownConnection() throws Exception {
		try {
			clientSocket.close();
		} catch (Exception e) {
			throw e;
		}
	}
}