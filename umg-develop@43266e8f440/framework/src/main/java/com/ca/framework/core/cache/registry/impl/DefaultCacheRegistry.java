/**
 * 
 */
package com.ca.framework.core.cache.registry.impl;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.PoolConstants;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.locks.Lock;

/**
 * @author kamathan
 *
 */
public class DefaultCacheRegistry implements CacheRegistry {

    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultCacheRegistry.class);

    private IQueue<Object> distributedQueue;

    private HazelcastInstance hazelCastInstance;

    public static final String CACHE_NAME = "CACHE";

    public static final String DISTRIBUTED_QUEUE_NAME = "DISTRIBUTED_QUEUE";

    private String configFile = "hazelcast-config.xml";

    public static final String CAHE_INSTANCE_NAME = "UMG_DEFAULT_INSTANCE";

    private static final Object lock = new Object();

    private static final Object lockForHazelcastInstance = new Object();

    private Config config = null;

    private ClientConfig clientConfig = null;
    
    private static final int BACKUP_COUNT = 1;

    private boolean client = Boolean.FALSE;

    public DefaultCacheRegistry(final String configFile) {
        super();
        this.configFile = configFile;
    }

    public DefaultCacheRegistry(final String configFile, boolean client) {
        super();
        this.configFile = configFile;
        this.client = client;
    }

    @PostConstruct
    private void init() {
        if(!client) {
            if (StringUtils.isNotBlank(System.getProperty("hazelcast.config"))) {
                Properties properties = new Properties();
                properties.setProperty("hazelcast.config", System.getProperty("hazelcast.config"));

                XmlConfigBuilder builder = new XmlConfigBuilder();
                builder.setProperties(properties);
                config = builder.build();
            } else {
                config = new ClasspathXmlConfig(configFile);
            }
            //config.addListenerConfig(new ListenerConfig("com.ca.me2.listener.ClientRegistrationListener"));
            //config.addListenerConfig(new ListenerConfig(new ClientRegistrationListener()));
            config.setInstanceName(CAHE_INSTANCE_NAME);
            //config.setProperty("hazelcast.client.max.no.heartbeat.seconds", "10");
            //config.setProperty( "hazelcast.logging.type", "slf4j" );
            hazelCastInstance = Hazelcast.getOrCreateHazelcastInstance(config);
            /*Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        Collection<Client> clients = hazelCastInstance.getClientService().getConnectedClients();
                        for (Client client : clients) {
                            System.out.println(client);
                        }
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Thread t = new Thread(runnable);
            t.start();*/
        }
        else {
            try {
                if (StringUtils.isNotBlank(System.getProperty("hazelcast.config"))) {
                    Properties properties = new Properties();
                    properties.setProperty("hazelcast.config", System.getProperty("hazelcast.config"));
                    clientConfig = new XmlClientConfigBuilder(properties.getProperty("hazelcast.config")).build();
                    //clientConfig = new XmlClientConfigBuilder(configFile).build();
                    //clientConfig.setProperty("hazelcast.client.heartbeat.interval", "10000");
                    //clientConfig.setProperty( "hazelcast.logging.type", "slf4j" );
                    hazelCastInstance = HazelcastClient.newHazelcastClient(clientConfig);
                }
            } catch (FileNotFoundException e) {
            	LOGGER.error("FileNotFoundException: {}",e);
            } catch (IOException e) {
            	LOGGER.error("IOException: {}",e);
            }
        }
    }

    @PreDestroy
    public void cleanup() {
        hazelCastInstance.shutdown();
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(final String configFile) {
        this.configFile = configFile;
    }

    @Override
    public IQueue<Object> getDistributedQueue() {
        if(distributedQueue == null) {
            if(client) {
                distributedQueue = hazelCastInstance.getQueue(DISTRIBUTED_QUEUE_NAME);
            }
            else {
                distributedQueue = getDistributedQueueFromMember();
            }
        }
        return distributedQueue;
    }

    public IQueue<Object> getDistributedQueueFromMember() {
        if (distributedQueue == null) {
            synchronized (lock) {
                if (distributedQueue == null) {
                    try {
                        distributedQueue = hazelCastInstance.getQueue(DISTRIBUTED_QUEUE_NAME);
                    } catch (HazelcastInstanceNotActiveException exception) {
                        LOGGER.error("HAZELCAST instance down. Going for recreation");
                        restartInstance();
                        distributedQueue = hazelCastInstance.getQueue(DISTRIBUTED_QUEUE_NAME);
                    }
                }
            }
        }
        
        config.getQueueConfig(DISTRIBUTED_QUEUE_NAME).setBackupCount(BACKUP_COUNT);
        
        return distributedQueue;
    }

    @Override
    public <K, V> IMap<K, V> getMap(final String name) {
        IMap<K, V> iMap;
        if(client) {
            iMap = hazelCastInstance.getMap(name);
        }
        else {
            iMap = getMapFromMember(name);
        }
        return iMap;
    }

    public <K, V> IMap<K, V> getMapFromMember(final String name) {
        IMap<K, V> iMap = null;
        try {
            iMap = hazelCastInstance.getMap(name);
        } catch (HazelcastInstanceNotActiveException exception) {
            LOGGER.error("HAZELCAST instance down. Going for recreation");
            restartInstance();
            iMap = hazelCastInstance.getMap(name);
        }
        
        config.getMapConfig(name).setBackupCount(BACKUP_COUNT);
        
        return iMap;
    }

    @Override
    public ITopic<Object> getTopic(final String name) {
        ITopic<Object> iTopic = null;
        try {
            iTopic = hazelCastInstance.getTopic(name);
        } catch (HazelcastInstanceNotActiveException exception) {
            LOGGER.error("HAZELCAST instance down. Going for recreation");
            restartInstance();
            iTopic = hazelCastInstance.getTopic(name);
        }
        
        return iTopic;
    }

    @Override
    public Integer getMemberPort() {
        InetSocketAddress addr = (InetSocketAddress) hazelCastInstance.getLocalEndpoint().getSocketAddress();
        return addr.getPort();
    }

    @Override
    public String getMemberAddress() {
        InetSocketAddress addr = (InetSocketAddress) hazelCastInstance.getLocalEndpoint().getSocketAddress();
        return addr.getAddress().getHostAddress();
    }

    private void restartInstance() {
        LOGGER.error("Cache instance to be restarted");
        if (!hazelCastInstance.getLifecycleService().isRunning()) {
            hazelCastInstance.getLifecycleService().terminate();
            init();
        }
    }

    @Override
    public Lock getDistributedLock(final String key) {
        return hazelCastInstance.getLock(key);
    }

    @Override
    public IList<Object> getList(final String name) {
        IList<Object> iList;
        if(client) {
            iList = hazelCastInstance.getList(name);
        }
        else {
            iList = getListFromMember(name);
        }
        return iList;
    }


    public IList<Object> getListFromMember(final String name) {
        IList<Object> iList = null;
        try {
            iList = hazelCastInstance.getList(name);
        } catch (HazelcastInstanceNotActiveException exception) {
            LOGGER.error("HAZELCAST instance down. Going for recreation");
            restartInstance();
            iList = hazelCastInstance.getList(name);
        }
        
        config.getListConfig(name).setBackupCount(BACKUP_COUNT);

        return iList;
    }

    @Override
    public ISet<Object> getSet(final String name) {
        ISet<Object> iSet;
        if(client) {
            iSet = hazelCastInstance.getSet(name);
        }
        else {
            iSet = getSetFromMember(name);
        }
        return iSet;
    }

    public ISet<Object> getSetFromMember(final String name) {
        ISet<Object> iSet;
        try {
            iSet = hazelCastInstance.getSet(name);
        } catch (HazelcastInstanceNotActiveException exception) {
            LOGGER.error("HAZELCAST instance down. Going for recreation");
            restartInstance();
            iSet = hazelCastInstance.getSet(name);
        }
        
        config.getSetConfig(name).setBackupCount(BACKUP_COUNT);

        return iSet;
    }

    @Override
    public IQueue<Object> getDistributedPoolQueue(final String name) {
        final IQueue<Object> poolQueue;
        if(client) {
            poolQueue = getDistributedPoolQueueFromClient(name);
        }
        else {
            poolQueue = getDistributedPoolQueueFromMember(name);
        }
        return poolQueue;
    }

    public IQueue<Object> getDistributedPoolQueueFromClient(final String name) {
        final String poolName = getPoolName(name);
        final IQueue<Object> poolQueue = hazelCastInstance.getQueue(poolName);
        return poolQueue;
    }

    public IQueue<Object> getDistributedPoolQueueFromMember(final String name) {
        final String poolName = getPoolName(name);
        final IQueue<Object> poolQueue = hazelCastInstance.getQueue(poolName);
        
        config.getQueueConfig(name).setBackupCount(BACKUP_COUNT);
        
        return poolQueue;
    }

    private String getPoolName(final String name) {
        String poolName = name;
        if (name == null) {
            poolName = PoolConstants.DEFAULT_POOL;
        }

        return poolName;
    }
    
    
    @Override
    public HazelcastInstance getHazelcastInstance() {
    	if (hazelCastInstance == null) {
    		synchronized (lockForHazelcastInstance) {
    			if (hazelCastInstance == null) {
                        init();
                    }
    			}
			}
    	
    	return hazelCastInstance;
    }
}