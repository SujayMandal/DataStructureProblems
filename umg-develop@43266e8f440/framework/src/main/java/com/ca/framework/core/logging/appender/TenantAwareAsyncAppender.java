/**
 * 
 */
package com.ca.framework.core.logging.appender;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.util.Booleans;
import org.slf4j.MDC;

/**
 * @author kamathan
 * 
 */
@Plugin(name = "TenantAwareAsync", category = "Core", elementType = "appender", printObject = true)
public final class TenantAwareAsyncAppender extends AbstractAppender {

    private static final int DEFAULT_QUEUE_SIZE = 128;
    private static final String SHUTDOWN = "Shutdown";

    private final BlockingQueue<Serializable> queue;
    private final int queueSize;
    private final boolean blocking;
    private final Configuration config;
    private final AppenderRef[] appenderRefs;
    private final String errorRef;
    private final boolean includeLocation;
    private AppenderControl errorAppender;
    private AsyncThread thread;
    private static final AtomicLong THREAD_SEQUENCE = new AtomicLong(1);
    private static ThreadLocal<Boolean> isAppenderThread = new ThreadLocal<Boolean>();

    private TenantAwareAsyncAppender(final String name, final Filter filter, final AppenderRef[] appenderRefs,
            final String errorRef, final int queueSize, final boolean blocking, final boolean ignoreExceptions,
            final Configuration config, final boolean includeLocation) {
        super(name, filter, null, ignoreExceptions);
        this.queue = new ArrayBlockingQueue<Serializable>(queueSize);
        this.queueSize = queueSize;
        this.blocking = blocking;
        this.config = config;
        this.appenderRefs = appenderRefs != null ? appenderRefs.clone() : null;
        this.errorRef = errorRef;
        this.includeLocation = includeLocation;
    }

    @Override
    public void start() {
        final Map<String, Appender> map = config.getAppenders();
        final List<AppenderControl> appenders = new ArrayList<AppenderControl>();
        for (final AppenderRef appenderRef : appenderRefs) {
            if (map.containsKey(appenderRef.getRef())) {
                appenders
                        .add(new AppenderControl(map.get(appenderRef.getRef()), appenderRef.getLevel(), appenderRef.getFilter()));
            } else {
                LOGGER.error("No appender named {} was configured", appenderRef);
            }
        }
        if (errorRef != null) {
            if (map.containsKey(errorRef)) {
                errorAppender = new AppenderControl(map.get(errorRef), null, null);
            } else {
                LOGGER.error("Unable to set up error Appender. No appender named {} was configured", errorRef);
            }
        }
        if (appenders.size() > 0) {
            thread = new AsyncThread(appenders, queue);
            thread.setName("AsyncAppender-" + getName());
        } else if (errorRef == null) {
            throw new ConfigurationException("No appenders are available for AsyncAppender " + getName());
        }

        thread.start();
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        thread.shutdown();
        try {
            thread.join();
        } catch (final InterruptedException ex) {
            LOGGER.warn("Interrupted while stopping AsyncAppender {}", getName());
        }
    }

    /**
     * Actual writing occurs here.
     * <p/>
     * 
     * @param evt
     *            The LogEvent.
     */
    public void append(final LogEvent evt) {
        if (!isStarted()) {
            throw new IllegalStateException("AsyncAppender " + getName() + " is not active");
        }
        if (!(evt instanceof Log4jLogEvent)) {
            // only know how to Serialize Log4jLogEvents
            return;
        }
        Log4jLogEvent event = (Log4jLogEvent) evt;
        boolean appendSuccessful = false;
        if (blocking) {
            appendSuccessful = appendEvents(event);
        } else {
            appendSuccessful = queue.offer(Log4jLogEvent.serialize(event, includeLocation));
            if (!appendSuccessful) {
                error("Appender " + getName() + " is unable to write primary appenders. queue is full");
            }
        }
        if (!appendSuccessful && errorAppender != null) {
            errorAppender.callAppender(event);
        }
    }

    private boolean appendEvents(Log4jLogEvent event) {
        boolean appendSuccessful = false;
        if (isAppenderThread.get() == Boolean.TRUE && queue.remainingCapacity() == 0) {
            // LOG4J2-485: avoid deadlock that would result from trying
            // to add to a full queue from appender thread
            // queue is definitely not empty!
            event.setEndOfBatch(false);
            appendSuccessful  = thread.callAppenders(event);
        } else {
            try {
                // wait for free slots in the queue
                queue.put(Log4jLogEvent.serialize(event, includeLocation));
                appendSuccessful = true;
            } catch (final InterruptedException e) {
                LOGGER.warn("Interrupted while waiting for a free slot in the AsyncAppender LogEvent-queue {}", getName());
            }
        }
        return appendSuccessful;
    }

    /**
     * Create an AsyncAppender.
     * 
     * @param appenderRefs
     *            The Appenders to reference.
     * @param errorRef
     *            An optional Appender to write to if the queue is full or other errors occur.
     * @param blocking
     *            True if the Appender should wait when the queue is full. The default is true.
     * @param size
     *            The size of the event queue. The default is 128.
     * @param name
     *            The name of the Appender.
     * @param includeLocation
     *            whether to include location information. The default is false.
     * @param filter
     *            The Filter or null.
     * @param config
     *            The Configuration.
     * @param ignore
     *            If {@code "true"} (default) exceptions encountered when appending events are logged; otherwise they are
     *            propagated to the caller.
     * @return The AsyncAppender.
     */
    @PluginFactory
    public static TenantAwareAsyncAppender createAppender(@PluginElement("AppenderRef") final AppenderRef[] appenderRefs,
            @PluginAttribute("errorRef") @PluginAliases("error-ref") final String errorRef,
            @PluginAttribute("blocking") final String blocking, @PluginAttribute("bufferSize") final String size,
            @PluginAttribute("name") final String name, @PluginAttribute("includeLocation") final String includeLocation,
            @PluginElement("Filter") final Filter filter, @PluginConfiguration final Configuration config,
            @PluginAttribute("ignoreExceptions") final String ignore) {
        if (name == null) {
            LOGGER.error("No name provided for AsyncAppender");
            return null;
        }
        if (appenderRefs == null) {
            LOGGER.error("No appender references provided to AsyncAppender {}", name);
        }

        final boolean isBlocking = Booleans.parseBoolean(blocking, true);
        final int queueSize = AbstractAppender.parseInt(size, DEFAULT_QUEUE_SIZE);
        final boolean isIncludeLocation = Boolean.parseBoolean(includeLocation);
        final boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);

        return new TenantAwareAsyncAppender(name, filter, appenderRefs, errorRef, queueSize, isBlocking, ignoreExceptions,
                config, isIncludeLocation);
    }

    /**
     * Thread that calls the Appenders.
     */
    private class AsyncThread extends Thread {

        private volatile boolean shutdown = false;
        private final List<AppenderControl> appenders;
        private final BlockingQueue<Serializable> queue;

        public AsyncThread(final List<AppenderControl> appenders, final BlockingQueue<Serializable> queue) {
            this.appenders = appenders;
            this.queue = queue;
            setDaemon(true);
            setName("AsyncAppenderThread" + THREAD_SEQUENCE.getAndIncrement());
        }

        @Override
        public void run() {
            // LOG4J2-485
            isAppenderThread.set(Boolean.TRUE);
            while (!shutdown) {
                Serializable s;
                try {
                    s = queue.take();
                    if (s instanceof String && SHUTDOWN.equals(s.toString())) {
                        shutdown = true;
                        continue;
                    }
                } catch (final InterruptedException ex) {
                    // No good reason for this.
                    continue;
                }
                // clear MDC and set new MDC values
                MDC.clear();
                final Log4jLogEvent event = Log4jLogEvent.deserialize(s);
                if (MapUtils.isNotEmpty(event.getContextMap())) {
                    MDC.setContextMap(event.getContextMap());
                }
                event.setEndOfBatch(queue.isEmpty());
                boolean success = callAppenders(event);
                if (!success && errorAppender != null) {
                    try {
                        errorAppender.callAppender(event);
                    } catch (final Exception ex) {
                        // Silently accept the error.
                        LOGGER.trace("Exception occurred while invoking error appender.");
                    } finally {
                        MDC.clear();
                    }
                }
            }
            processRemainingItemsInQueue();
        }

        private void processRemainingItemsInQueue() {
            // Process any remaining items in the queue.
            while (!queue.isEmpty()) {
                try {
                    final Serializable s = queue.take();
                    if (s instanceof Log4jLogEvent) {
                        final Log4jLogEvent event = Log4jLogEvent.deserialize(s);
                        event.setEndOfBatch(queue.isEmpty());
                        callAppenders(event);
                    }
                } catch (final InterruptedException ex) {
                    // May have been interrupted to shut down.
                    LOGGER.warn("Interrupted while stopping TenantAwareAsyncAppender {}", getName());
                }
            }
        }

        /**
         * Calls {@link AppenderControl#callAppender(LogEvent) callAppender} on all registered {@code AppenderControl} objects,
         * and returns {@code true} if at least one appender call was successful, {@code false} otherwise. Any exceptions are
         * silently ignored.
         * 
         * @param event
         *            the event to forward to the registered appenders
         * @return {@code true} if at least one appender call succeeded, {@code false} otherwise
         */
        boolean callAppenders(final Log4jLogEvent event) {
            boolean success = false;
            for (final AppenderControl control : appenders) {
                try {
                    control.callAppender(event);
                    success = true;
                } catch (final Exception ex) {
                    // If no appender is successful the error appender will get it.
                    LOGGER.warn("Error while invoking appender {}", getName());
                }
            }
            return success;
        }

        public void shutdown() {
            shutdown = true;
            if (queue.isEmpty()) {
                queue.offer(SHUTDOWN);
            }
        }

    }

    /**
     * Returns the names of the appenders that this asyncAppender delegates to as an array of Strings.
     * 
     * @return the names of the sink appenders
     */
    public String[] getAppenderRefStrings() {
        final String[] result = new String[appenderRefs.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = appenderRefs[i].getRef();
        }
        return result;
    }

    /**
     * Returns {@code true} if this AsyncAppender will take a snapshot of the stack with every log event to determine the class
     * and method where the logging call was made.
     * 
     * @return {@code true} if location is included with every event, {@code false} otherwise
     */
    public boolean isIncludeLocation() {
        return includeLocation;
    }

    /**
     * Returns {@code true} if this AsyncAppender will block when the queue is full, or {@code false} if events are dropped when
     * the queue is full.
     * 
     * @return whether this AsyncAppender will block or drop events when the queue is full.
     */
    public boolean isBlocking() {
        return blocking;
    }

    /**
     * Returns the name of the appender that any errors are logged to or {@code null}.
     * 
     * @return the name of the appender that any errors are logged to or {@code null}
     */
    public String getErrorRef() {
        return errorRef;
    }

    public int getQueueCapacity() {
        return queueSize;
    }

    public int getQueueRemainingCapacity() {
        return queue.remainingCapacity();
    }

}
