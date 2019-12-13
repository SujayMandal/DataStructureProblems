package com.ca.umg.modelet;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.common.SystemInfo;
import com.ca.umg.modelet.runtime.factory.RuntimeFactory;
import com.ca.umg.modelet.transport.cache.Registry;
import com.ca.umg.modelet.transport.factory.ModeletServerFactory;

@Named
public class InitializeModelet {

    @Inject
    private SystemInfo systemInfo;

    @Inject
    private ModeletServerFactory serverFactory;

    @Inject
    private RuntimeFactory runtimeFactory;

    @Inject
    private Registry registry;

    public void initializeServer() throws SystemException {
        serverFactory.initializeServer(systemInfo.getServerType(), systemInfo.getPort());
    }

    public void initializeRuntime() throws SystemException {
        // MS: Changed method name to initialize all runtimes
        runtimeFactory.initializeRuntimes();
    }

    public void initializeModels() throws SystemException {
        runtimeFactory.loadModels();
    }

    public void destroyServer() throws SystemException {
        serverFactory.destroyServer(systemInfo.getServerType());
    }

    public void destroyRuntime() throws SystemException {
        // MS: Changed method name to destroy all runtimes
        runtimeFactory.destroyRuntimes();
    }

    public void registerModelet() throws SystemException {
        registry.register();
    }

    public void unregisterModelet() throws SystemException {
        registry.unregister();
    }

    public void shutDownHazelcastClient() {
        registry.shutdownHazelcastClient();
    }

    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(final SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    public ModeletServerFactory getServerFactory() {
        return serverFactory;
    }

    public void setServerFactory(final ModeletServerFactory serverFactory) {
        this.serverFactory = serverFactory;
    }

    public RuntimeFactory getRuntimeFactory() {
        return runtimeFactory;
    }

    public void setRuntimeFactory(final RuntimeFactory runtimeFactory) {
        this.runtimeFactory = runtimeFactory;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(final Registry registry) {
        this.registry = registry;
    }

}
