package com.ca.umg.business.transaction.migrate.listener;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.umg.business.transaction.migrate.execution.StopMigrateTransaction;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class MigrateTransactionListener implements MessageListener<Object> {

    private CacheRegistry cacheRegistry;

    private ITopic<Object> topic;

    private StopMigrateTransaction stopMigrateTransaction;

    public static final String MIGRATE_TRANSACTION = "MIGRATE_TRANSACTION";

    public void init() {
        topic = cacheRegistry.getTopic(MIGRATE_TRANSACTION);
        topic.addMessageListener(this);
    }

    @Override
    public void onMessage(Message<Object> message) {
    	final Boolean stopMigration = (Boolean) message.getMessageObject();
        if (!message.getPublishingMember().localMember()) {
        	stopMigrateTransaction.setStopMigration(stopMigration);
        }
    }

    public CacheRegistry getCacheRegistry() {
        return cacheRegistry;
    }

    public void setCacheRegistry(CacheRegistry cacheRegistry) {
        this.cacheRegistry = cacheRegistry;
    }

    public ITopic<Object> getTopic() {
        return topic;
    }

    public void setTopic(ITopic<Object> topic) {
        this.topic = topic;
    }

	public StopMigrateTransaction getStopMigrateTransaction() {
		return stopMigrateTransaction;
	}

	public void setStopMigrateTransaction(
			StopMigrateTransaction stopMigrateTransaction) {
		this.stopMigrateTransaction = stopMigrateTransaction;
	}

}
