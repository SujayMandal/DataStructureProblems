package com.ca.modelet.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

public class SocketModeletClient implements ModeletClient {

    private static final long serialVersionUID = -8868590992027071816L;

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketModeletClient.class);

    private Socket clientSocket;
    private final String hostName;
    private final int port;

    public SocketModeletClient(final String hostName, final int port) {
        this.hostName = hostName;
        this.port = port;
    }

   /* public static void main(String[] args) {

        SocketModeletClient socketModeletClient = new SocketModeletClient("localhost", 7905);
        FileInputStream fis = null;
        try {
            socketModeletClient.createConnection();

            // C:\\Users\\kamathan\\Desktop\\sample_excel_model_input.txt
            File file = new File("D:\\sanpath\\sanPath1\\localhost\\modelLibrary\\excel\\exce-22-Nov-2015-156-25-52\\input.txt");
            fis = new FileInputStream(file);

            String input = new String(IOUtils.toByteArray(fis));
            String output = socketModeletClient.sendData(input);
            System.out.println("###### Excel model output #######");
            System.out.println(output);
            System.out.println("###### Excel model output #######");
            // file = new File(
            // "D:\\sanpath\\sanPath1\\localhost\\modelLibrary\\Simple Excel model v2.0\\Simple Excel model v2.0-
            // 1-JUL-2016-05-00-00\\output.txt");
            // FileWriter fileWriter = new FileWriter(file);
            // ObjectMapper mapper = new ObjectMapper();
            // Object json = mapper.readValue(output, Object.class);
            // fileWriter.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
            socketModeletClient.shutdownConnection();
        } catch (Exception e) {
        	LOGGER.debug("Error : ",e);
            //e.printStackTrace();
        } finally {
        	IOUtils.closeQuietly(fis);
        }

    }*/

    public void createConnection() throws SystemException {
        try {
            // clientSocket = new Socket(hostName, port);
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(hostName, port));
            LOGGER.info("Connection successful to host {} port {}", hostName, port);
        } catch (IOException e) {
            LOGGER.error(String.format("An error occurred while creating connection. Host name %s, Port %d", hostName, port), e);
            throw SystemException.newSystemException("ME0010", new Object[] { e.getMessage(), hostName, port }, e);
        }
    }

    public String sendData(final String input) throws SystemException, BusinessException {
        LOGGER.error("Sending data to Modelet");
        LOGGER.debug("Input Length: " + input.getBytes().length);
        long startTime = System.currentTimeMillis();
        writeToServer(input);
        String response = readFromServer();
        LOGGER.error("Modelet Execution time: " + (System.currentTimeMillis() - startTime));
        LOGGER.debug("Output Length: " + response.getBytes().length);
        com.ca.modelet.common.ExceptionResolver.resolveException(response);
        if(LOGGER.isErrorEnabled()){
            LOGGER.error("Received response from Modelet ");
        }else{
            LOGGER.info("Received response from Modelet {}", response);
        }


        return response;
    }

    private void writeToServer(String input) throws SystemException {
        try {
            OutputStream outToServer = clientSocket.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeInt(input.getBytes().length);
            out.write(input.getBytes());
            out.flush();
        } catch (IOException ie) {      
            LOGGER.error("Exception occured while writing to server.", ie);
        	SystemException.newSystemException("ME0030", new String[] { "writing"});   
        } catch (Exception e) { // NOPMD
            LOGGER.error("Exception occured while writing to server.", e);
            SystemException.newSystemException("ME0009", new String[] { "writing", e.getMessage() });
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
        } catch (IOException ie) {          
            LOGGER.error("Exception occured while reading from server.", ie);        	
        	SystemException.newSystemException("ME0030", new String[] { "reading" });
        } catch (Exception e) { // NOPMD
            LOGGER.error("Exception occured while reading from server .", e);
            SystemException.newSystemException("ME0009", new String[] { "reading", e.getMessage() });
        }
        return response;
    }

    public void shutdownConnection() throws SystemException {
        try {
            clientSocket.close();
        } catch (IOException e) {
            LOGGER.error("Exception occured during shutdown.", e);
        }
    }
}