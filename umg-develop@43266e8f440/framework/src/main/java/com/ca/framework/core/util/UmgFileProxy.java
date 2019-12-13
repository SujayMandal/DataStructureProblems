package com.ca.framework.core.util;

import java.io.File;
import java.lang.Thread.State;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.FileProxyBean;
import com.ca.framework.core.systemparameter.SystemParameterProvider;

/**
 * @author basanaga
 * 
 * 
 */
@Named
public class UmgFileProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmgFileProxy.class);
    //private static final String UMG_FILE_PATH = "UMG_FILE_PATH";
    private static final String SAN_BASE = "sanBase";
    private static final int WAIT_TIME = 1500;

    @Inject
    private CacheRegistry cacheRegistry;

    public String getSanPath(String sanBasePath) throws SystemException {
        //long start = System.currentTimeMillis();
        String sanBase = (String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER).get(SAN_BASE);
        /*if (sanBase != null) {
            if (!checkSanBaseexist(sanBase)) {
                sanBase = null;
                start = System.currentTimeMillis();
                sanBase = getWorkingSanBase(sanBasePath);
                LOGGER.info("getMap 2: " + (System.currentTimeMillis() - start));
            }
        } else {
            start = System.currentTimeMillis();
            sanBase = getWorkingSanBase(sanBasePath);
            LOGGER.info("getMap 3: " + (System.currentTimeMillis() - start));
        }

        if (sanBase == null) {
            LOGGER.error("San bases {} are not defined or network file storage is down -", new Object[] { sanBasePath });
            throw new SystemException(FrameworkExceptionCodes.FSE0000201, new Object[] {});
        }*/
        return sanBase;
    }

    private Boolean checkSanBaseexist(String sanBase) {
        FileProxyBean proxyBean = new FileProxyBean();
        proxyBean.setSanexist(Boolean.FALSE);
        proxyBean.setSanBase(sanBase);
        try {
            FileExistanceThread thread = new FileExistanceThread(proxyBean);
            thread.start();
            Boolean isWaited = Boolean.FALSE;
            while (!thread.getState().equals(State.NEW) && !isWaited && !thread.getState().equals(State.TERMINATED)) {
                Thread.sleep(WAIT_TIME);
                isWaited = Boolean.TRUE;
            }
            if (thread.isAlive()) {
                thread.interrupt();
                LOGGER.error("Thread Interrupted while checking for mount path availability. after waiting " + WAIT_TIME
                        + " millis");
            }

        } catch (InterruptedException e) {
            LOGGER.error("Execution exception while interrupting thread. ", e);
        }
        return proxyBean.isSanexist();

    }

    /*private String getWorkingSanBase(String sanBasePath) {
        String sanBase = null;
        String sanBaseArr[] = sanBasePath.split(",");
        for (String sanBasefromDb : sanBaseArr) {
            if (checkSanBaseexist(sanBasefromDb.trim())) {
                sanBase = sanBasefromDb;
                cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER).put(UMG_FILE_PATH, sanBase);
                break;
            }
        }
        return sanBase;
    }*/

    class FileExistanceThread extends Thread {
        private FileProxyBean fileProxybean;

        public FileExistanceThread(FileProxyBean fileProxyBean) {
            this.fileProxybean = fileProxyBean;

        }

        public void run() {
            File sanFile = new File(fileProxybean.getSanBase());
            if (sanFile.exists()) {              
                fileProxybean.setSanexist(Boolean.TRUE);
            }

        }

    }

}
