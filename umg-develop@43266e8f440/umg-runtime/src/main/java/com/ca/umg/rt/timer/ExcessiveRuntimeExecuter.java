package com.ca.umg.rt.timer;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterConstants;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.modelet.ModeletClientInfo;
import com.ca.modelet.common.ServerType;
import com.ca.pool.manager.ModeletManager;
import com.ca.pool.model.ExecutionLanguage;
import com.ca.pool.model.TransactionCriteria;
import com.ca.pool.util.TransactionsDetailMap;
import com.ca.umg.notification.model.NotificationHeaders;
import com.ca.umg.notification.notify.NotificationTriggerDelegate;
import com.ca.umg.notification.util.NotificationUtil;
import com.ca.umg.rt.util.container.StaticDataContainer;

/**
 * @author basanaga This class used to check for excessive runtime calls
 */
@Named
public class ExcessiveRuntimeExecuter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcessiveRuntimeExecuter.class);
    public static final int NUMBER_THREE = 3;
    public static final String REGEX_HYPHEN = "[-]";

    @Inject
    private NotificationTriggerDelegate notificationTriggerDelegate;

    @Inject
    private CacheRegistry cacheRegistry;

    @Inject
    private TransactionsDetailMap localCacheMap;

    @Inject
    private StaticDataContainer staticDataContainer;
    
    @Inject
    private ModeletManager modeletManager;

    @PostConstruct
    public void init() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                try {
                    new ExcessiveRuntimeThread().call();
                } catch (Exception e) { // NOPMD
                    LOGGER.error("Exception while sending mail : Exception is", e);

                }

            }

        }, 120, 10, TimeUnit.SECONDS);
    }

    /**
     * @author basanaga This class used to check for excessive runtime transactions and send mail if it exceeds system setup time
     *
     */
    class ExcessiveRuntimeThread implements Callable<Void> {
        @Override
        public Void call() {
            Map<String, TransactionCriteria> excessiveRuntimeMap = localCacheMap.getExcesiveRuntimeMap();
            Set<String> entrySet = excessiveRuntimeMap.keySet();
            String systemExcessiveRuntime = (String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER)
                    .get(SystemParameterConstants.MODELET_EXEC_TIME_LIMIT);

            for (String entry : entrySet) {
            	boolean sentMail = false;
                try {
                    String[] keyString = entry.split(REGEX_HYPHEN);

                    long sendTime = Long.valueOf(keyString[2]);// NOPMD
                    long delay = Long.valueOf(systemExcessiveRuntime) * 1000;
                    if (System.currentTimeMillis() - sendTime >= delay) {
                        TransactionCriteria transactionCriteria = excessiveRuntimeMap.get(entry);
                        if (transactionCriteria != null) {
                            LOGGER.error("UMG Transaction :" + transactionCriteria.getUmgTransactionId() + " has taken more than "
                                    + systemExcessiveRuntime + "seconds");
                            NotificationHeaders headers = new NotificationHeaders();
                            headers.setModelName(transactionCriteria.getModelName());
                            headers.setModelVersion(transactionCriteria.getModelVersion());
                            headers.setTenantName(
                                    staticDataContainer.getTenantMap().get(transactionCriteria.getTenantCode()).getName());
                            headers.setTenantCode(transactionCriteria.getTenantCode());
                            headers.setTransactionId(transactionCriteria.getUmgTransactionId());
                            headers.setModelStartTime(NotificationUtil.getDateFormatMillisForEst(sendTime, null));
                            headers.setClienttransactionId(transactionCriteria.getClientTransactionId());
                            headers.setExcessiveRuntime(systemExcessiveRuntime);
                            headers.setEnvironment(transactionCriteria.getExecutionLanguage() + "-"
                                    + transactionCriteria.getExecutionLanguageVersion());
                            headers.setModeletHost(keyString[0]);
                            headers.setPort(keyString[1]);
                            StringBuffer poolName = new StringBuffer();
                            for (int i = 3; i < keyString.length; i++) {
                                poolName = poolName.append("-").append(keyString[i]);
                            }
                            headers.setPoolName(poolName.toString().replaceFirst("-", StringUtils.EMPTY));
                            LOGGER.error("Sending mail for transaction :" + transactionCriteria.getUmgTransactionId());
                            notificationTriggerDelegate.notifyExcessModelExecTime(headers, false);
                            sentMail = true;
                            LOGGER.error("Successfully sent mail for txn :" + transactionCriteria.getUmgTransactionId());
                            stopModelet(entry);
                            removeExcessiveRuntimeKey(entry, excessiveRuntimeMap);
                        }
                    }
                } catch (SystemException | BusinessException e) {
                    LOGGER.error("Exception while sending excessive runtime mail.Exception is : " + e.getLocalizedMessage());
                    if (!sentMail) {
                        try  {
                        	stopModelet(entry);                    	
                        } catch(SystemException se) {
                            LOGGER.error("Exception while stopping modelet. Exception is : ", se);
                        }
                    }
                    removeExcessiveRuntimeKey(entry, excessiveRuntimeMap);
                } catch (Exception e) {// NOPMD
                    LOGGER.error("Exception while sending excessive runtime mail.Exception is : ", e);
                    if (!sentMail) {
                        try  {
                        	stopModelet(entry);                    	
                        } catch (SystemException se) {
                            LOGGER.error("Exception while stopping modelet. Exception is : ", se);

                        }
                    }
                    removeExcessiveRuntimeKey(entry, excessiveRuntimeMap);
                }

            }
            return null;

        }
    }

    private ModeletClientInfo createModeletClientInfo(final String key) {
        final String[] spilts = key.split(REGEX_HYPHEN);
        final ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
        modeletClientInfo.setHost(spilts[0]);
        modeletClientInfo.setPort(Integer.valueOf(spilts[1]));
        modeletClientInfo.setExecutionLanguage(ExecutionLanguage.R.getValue());
        modeletClientInfo.setServerType(ServerType.SOCKET.toString());
        return modeletClientInfo;
    }
    
    private void stopModelet(final String key) throws SystemException {
        Map<String, TransactionCriteria> excessiveRuntimeMap = localCacheMap.getExcesiveRuntimeMap();
        ModeletClientInfo modeletClientInfo = createModeletClientInfo(key);
        localCacheMap.getTxnTimeOutMap().put(key, Boolean.TRUE);
        LOGGER.error(
                "Started stopping modelet of host :" + modeletClientInfo.getHost() + " and port :" + modeletClientInfo.getPort());
        modeletManager.stopModelet(modeletClientInfo);
        LOGGER.error("Stopped modelet of host :" + modeletClientInfo.getHost() + " and port :" + modeletClientInfo.getPort()
                + " Sucessfully");
        removeExcessiveRuntimeKey(key, excessiveRuntimeMap);
    }

    private void removeExcessiveRuntimeKey(final String key, Map<String, TransactionCriteria> excessiveRuntimeMap) {
        if (excessiveRuntimeMap.containsKey(key)) {
            LOGGER.error("removing the key from map SystemParameterConstants.MODELET_EXEC_TIME_LIMIT :" + key);
            excessiveRuntimeMap.remove(key);
            LOGGER.error("Removed Successfully");
        }
    }
}
