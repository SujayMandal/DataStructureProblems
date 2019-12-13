package com.ca.umg.modelet.listener;

import com.ca.umg.modelet.InitializeModelet;
import com.ca.umg.modelet.transport.cache.Registry;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by repvenk on 6/22/2016.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class ClientRegistrationListener implements LifecycleListener {

    private final Registry registry;
    private final InitializeModelet initializer;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientRegistrationListener.class);
    private final ExecutorService service = Executors.newFixedThreadPool(1);

    public ClientRegistrationListener(Registry registry, InitializeModelet initializer) {
        this.registry = registry;
        this.initializer = initializer;
    }

    @Override
    public void stateChanged(LifecycleEvent lifecycleEvent) {
        LOGGER.info("State: " + lifecycleEvent.toString());
        if(lifecycleEvent.getState().equals(LifecycleEvent.LifecycleState.CLIENT_CONNECTED)) {
            LOGGER.info("Client state changed to " + LifecycleEvent.LifecycleState.CLIENT_CONNECTED);
            service.submit(new ClientConnectedCallable(registry));
        }
        else if(lifecycleEvent.getState().equals(LifecycleEvent.LifecycleState.CLIENT_DISCONNECTED)) {
            LOGGER.error("Client state changed to " + LifecycleEvent.LifecycleState.CLIENT_DISCONNECTED);
        }
        else if(lifecycleEvent.getState().equals(LifecycleEvent.LifecycleState.SHUTTING_DOWN)) {
            LOGGER.error("Client state changed to " + LifecycleEvent.LifecycleState.SHUTTING_DOWN);
            shutdown();
        }
    }

    private void shutdown() {
        LOGGER.error("Unable to connect to Hazelcast cluster. Modelet shutdown initiated.");
        initializer.shutDownHazelcastClient();
        System.exit(0);
    }

    private class ClientConnectedCallable implements Callable<Void> {

        private final Registry registry;

        public ClientConnectedCallable(Registry registry) {
            this.registry = registry;
        }

        @Override
        public Void call() throws Exception { //NOPMD
            registry.reRegister();
            return null;
        }
    }

}
