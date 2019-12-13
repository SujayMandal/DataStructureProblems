package com.ca.umg.rt.web.request;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.constants.PoolConstants;
import com.ca.umg.rt.util.MessageVariables;

public class ResettableStreamHttpServletRequest extends HttpServletRequestWrapper {
    private byte[] rawData;
    private final HttpServletRequest request;
    private final ResettableServletInputStream servletStream;
    private static final Logger LOGGER = LoggerFactory.getLogger(ResettableStreamHttpServletRequest.class);

    public ResettableStreamHttpServletRequest(HttpServletRequest request) {
        super(request);
        this.request = request;
        this.servletStream = new ResettableServletInputStream();
        try {
            rawData = IOUtils.toByteArray(this.request.getReader());
        } catch (IOException e) {
            LOGGER.error("Exception occured parsing request stream", e);
        }
        /*
         * try { long start = System.currentTimeMillis(); BufferedReader bufferedReader = new BufferedReader( new
         * FileReader(System.getProperty("sanpath") + File.separator + "TenantInputSample.txt")); rawData =
         * IOUtils.toByteArray(bufferedReader); LOGGER.error("TenantInputSample.txt read time: " + (System.currentTimeMillis() -
         * start)); bufferedReader.close(); } catch (IOException e) { e.printStackTrace(); }
         */
    }

    public void resetInputStream() throws IOException {
        if (rawData == null) {
            rawData = IOUtils.toByteArray(this.request.getReader());
            servletStream.stream = new ByteArrayInputStream(rawData);
        } else {
            servletStream.stream = new ByteArrayInputStream(rawData);
        }

        if (this.request.getAttribute("FILE_NAME") == null) {        
        		this.request.setAttribute(PoolConstants.CHANNEL, MessageVariables.ChannelType.HTTP.getChannel());
        }
    }

    
    public void resetInputStreamForErrorResponse() throws IOException {
        if (rawData == null) {
            rawData = IOUtils.toByteArray(this.request.getReader());
            servletStream.stream = new ByteArrayInputStream(rawData);
        } else {
            servletStream.stream = new ByteArrayInputStream(rawData);
        }
    }
    public void resetInputStream(byte[] bytesOfFile, String fileName, String batchId) throws IOException {
        if (bytesOfFile != null) {
            rawData = bytesOfFile;
            servletStream.stream = new ByteArrayInputStream(rawData);
            this.request.setAttribute("FILE_NAME", fileName);
            this.request.setAttribute("BATCH_ID", batchId);
            this.request.setAttribute("CHANNEL", MessageVariables.ChannelType.FILE.getChannel());
        } else {
            resetInputStream();
        }
    }
    
    public void resetInputStream(String batchId, String timeStamp) throws IOException {
        if (rawData == null) {
            rawData = IOUtils.toByteArray(this.request.getReader());
            servletStream.stream = new ByteArrayInputStream(rawData);
        } else {
            servletStream.stream = new ByteArrayInputStream(rawData);
        }
        this.request.setAttribute("BATCH_ID", batchId);
        this.request.setAttribute("IO_TIMESTAMP", timeStamp);
    }



    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (rawData == null) {
            rawData = IOUtils.toByteArray(this.request.getReader());
            servletStream.stream = new ByteArrayInputStream(rawData);
        }

        return servletStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (rawData == null) {
            rawData = IOUtils.toByteArray(this.request.getReader());
            servletStream.stream = new ByteArrayInputStream(rawData);
        }

        return new BufferedReader(new InputStreamReader(servletStream));
    }

    private class ResettableServletInputStream extends ServletInputStream {
        private InputStream stream;

        @Override
        public int read() throws IOException {
            return stream.read();
        }

        @Override
        public boolean isFinished() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isReady() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void setReadListener(ReadListener arg0) {
            // TODO Auto-generated method stub

        }
    }
}
