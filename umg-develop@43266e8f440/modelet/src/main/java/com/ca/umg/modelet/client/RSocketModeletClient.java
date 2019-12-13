
package com.ca.umg.modelet.client;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.client.ModeletClient;
import com.ca.umg.modelet.common.ExceptionResolver;
import com.ca.umg.modelet.constants.ErrorCodes;

@SuppressWarnings("PMD")
public class RSocketModeletClient implements ModeletClient {

	private static final long serialVersionUID = -8868590992027071816L;

	private static final Logger LOGGER = LoggerFactory.getLogger(RSocketModeletClient.class);

	private Socket clientSocket;
	private final String hostName;
	private final int port;

	private static final int THREE = 3;

	//MS:This is a throw away method not called from the main codebase, so skipping complexity check
	@SuppressWarnings({"PMD.SystemPrintln", "PMD.NPathComplexity"})
	public static void main(final String[] args) {
		try {
			RSocketModeletClient client = new RSocketModeletClient("localhost", 7901);
//			RSocketModeletClient client = new RSocketModeletClient("10.0.10.62", 7904);
//			RSocketModeletClient client = new RSocketModeletClient("10.0.20.31", 7901);
			client.createConnection();
			String inputData = null;

			if (args != null && args.length < 1)	{
				LOGGER.error("Correct arguments not provided");
				System.exit(1);
			}

			String path = args[0]; //"D:\\umg\\modelet_test\\r_new_test\\";
			String dataType = args[1]; //"primitive";
			String fileName = null;

			if (args.length == THREE)	{
				fileName = args[2]; //"integer";
			}

//			File file = new File(path + dataType + "\\R-DTTESTER-MODELCALL-" + dataType + (fileName!=null?"-"+fileName:"") + ".json");
			File file = new File("C:" + File.separator +"Users"+ File.separator +"reddnage"+ File.separator +"Desktop"+ File.separator +"ArrayOfObjectss"+ File.separator +"Array_Of_Objects_6.json");
			try (FileInputStream fis = new FileInputStream(file)) {
				inputData = new String(IOUtils.toByteArray(fis));
			} catch (IOException e) {
				LOGGER.error("Exception occured while reading file data", e);
			}


			String strRet = client.sendData(inputData);
			new File(path + dataType + File.separator + "response").mkdir();
			file = new File(path + dataType + File.separator + "response"+ File.separator +"R-DTTESTER-MODELRESPONSE-" + dataType + (fileName!=null?"-"+fileName:"") + ".json");

			try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
				bw.write(strRet);
				bw.flush();

			} catch (IOException e) {
				LOGGER.error("Exception occured while writing response", e);

			}

			System.out.println("-------------------------------------------------------------------------------");
			System.out.println(strRet);
			System.out.println("-------------------------------------------------------------------------------");

			client.shutdownConnection();
		} catch (SystemException | BusinessException e) {

			LOGGER.error("Exception occured.", e);
		}
	}

	public RSocketModeletClient(final String hostName, final int port) {
		this.hostName = hostName;
		this.port = port;
	}

	@Override
	public void createConnection() throws SystemException {
		try {
			//clientSocket = new Socket(hostName, port);
			clientSocket = new Socket();
			clientSocket.connect(new InetSocketAddress(hostName, port));
			//            LOGGER.info("Connection successful to host {} port {}", hostName, port);
		} catch (IOException e) {
			throw SystemException.newSystemException("", new Object[] { "" }, e);
		}
	}

	@Override
	public String sendData(final String input) throws SystemException, BusinessException {
		//        LOGGER.info("Sending data to Modelet");
        LOGGER.error("Sending data to Modelet");
        LOGGER.error("Input Length: " + input.getBytes().length);
        long startTime = System.currentTimeMillis();
		writeToServer(input);
		String response = readFromServer();
        LOGGER.error("Modelet Execution time: " + (System.currentTimeMillis() - startTime));
        LOGGER.error("Output Length: " + response.getBytes().length);
		ExceptionResolver.resolveException(response);
		//        LOGGER.info("Received response from Modelet {}", response);
		return response;
	}

	private void writeToServer(final String input) throws SystemException {
		try {
			OutputStream outToServer = clientSocket.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			out.writeInt(input.getBytes().length);
			out.write(input.getBytes());
			out.flush();
		} catch (IOException e) {        
			LOGGER.error("Exception occured while writing to server.", e);
        	SystemException.newSystemException(ErrorCodes.ME0030, new String[] { "writing"});   
		} catch (Exception e) { // NOPMD
			LOGGER.error("Exception occured while writing to server.", e);
			SystemException.newSystemException(ErrorCodes.ME0009, new String[] {"writing", e.getMessage()});
		}
	}

	private String readFromServer() throws SystemException {
		String response = null;
		try {
			InputStream inFromServer = clientSocket.getInputStream();
			DataInputStream inStream = new DataInputStream(inFromServer);
			int length = inStream.readInt();
			byte[] respBytes = new byte[length];
			inStream.readFully(respBytes);
			response = new String(respBytes);
		} catch (IOException se) {        
			LOGGER.error("Exception occured while response.", se);
        	SystemException.newSystemException(ErrorCodes.ME0030, new String[] { "reading"});   
		} catch (Exception e) { // NOPMD
			LOGGER.error("Exception occured while response.", e);
			SystemException.newSystemException(ErrorCodes.ME0009, new String[] { "reading", e.getMessage() });
		}
		return response;
	}

	@Override
	public void shutdownConnection() throws SystemException {
		try {
			clientSocket.close();
		} catch (IOException e) {
			LOGGER.error("Exception occured during shutdown.", e);
		}
	}

}