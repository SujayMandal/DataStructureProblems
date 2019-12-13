/**
 * 
 */
package com.ca.umg.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.umg.exception.UmgSchedulerExceptionCodes;
import com.ca.umg.file.event.FileEventBus;
import com.ca.umg.file.event.info.FileEvent;
import com.ca.umg.util.UmgSchedulerConstants;

/**
 * @author kamathan
 *
 */
@Named
public class UmgFilePoller {

    private static final String PATH_MATCHER_EXPRESSION = "glob:";

    private static final Logger LOGGER = LoggerFactory.getLogger(UmgFilePoller.class);

    @Inject
    private FileEventBus<FileEvent> eventBus;

    /**
     * Holds the directory pattern to be registered for polling
     */
    @Value("${bulk.folder.pattern}")
    private String targetFolderPattern;

    /**
     * Holds the san base root directory.
     */
    @Value("${sanBase}")
    private String sanBase;

    @Value("${request.file.etxn.pattern}")
    private String fileExtnPattern;
    
    @Value("${lookup.rate}")
    private String pollingInterval;
    
    /**
     * this flag will be set to true in properties file if we want the poller for (lookupFiles)
     * and false if we want the apache poller
     */
    @Value("${enable.lookup}")
    private Boolean lookupEnabled;
    
    @Inject
    private CacheRegistry cacheRegistry;
    
    //private WatchService watcherService;
    
    private FileAlterationMonitor monitor = null;
    
    private FileAlterationListener listener = null;
    
    private Map<String,FileAlterationObserver> monitorPathMap = new HashMap<String,FileAlterationObserver>(); 

    /**
     * Holds the watcher key and tenant code
     */
    //private Map<WatchKey, String> watchKeyTenantMap = new HashMap<WatchKey, String>();

    @PostConstruct
    public void init() throws SystemException {

        if (StringUtils.isBlank(sanBase) || !Files.exists(Paths.get(sanBase), LinkOption.NOFOLLOW_LINKS)) {
            throw new FatalBeanException(String.format("San base not defined/available."));
        }

        this.targetFolderPattern = this.sanBase + File.separator + this.targetFolderPattern;

        // if windows file system multiple escape characters to handle the path matcher
        if (StringUtils.equals(File.separator, UmgSchedulerConstants.FILE_ESC_SEPARATOR)) {
            this.sanBase = StringUtils.replaceEach(sanBase, new String[] { UmgSchedulerConstants.DOUBLE_ESC_FILE_SEPARATOR },
                    new String[] { UmgSchedulerConstants.FILE_ESC_SEPARATOR });
            this.sanBase = StringUtils.replaceEach(sanBase, new String[] { UmgSchedulerConstants.FILE_ESC_SEPARATOR },
                    new String[] { UmgSchedulerConstants.DOUBLE_ESC_FILE_SEPARATOR });
            this.targetFolderPattern = StringUtils.replaceEach(targetFolderPattern,
                    new String[] { UmgSchedulerConstants.DOUBLE_ESC_FILE_SEPARATOR },
                    new String[] { UmgSchedulerConstants.FILE_ESC_SEPARATOR });
            this.targetFolderPattern = StringUtils.replaceEach(targetFolderPattern,
                    new String[] { UmgSchedulerConstants.FILE_ESC_SEPARATOR },
                    new String[] { UmgSchedulerConstants.DOUBLE_ESC_FILE_SEPARATOR });
        }

        // process existing files in the bulk/input folder
        processExistingFiles();
        if (!lookupEnabled) {
            initializeListener();
            poll();
        }
    }

    /*
     * Processes already existing files in the san folder, as WatcherService doesn't trigger any event for these files.
     */
    private void processExistingFiles() {
        Path path = Paths.get(sanBase);
        final FileSystem fileSystem = FileSystems.getDefault();
        String pattern = targetFolderPattern;
        if (StringUtils.equals(File.separator, UmgSchedulerConstants.FILE_ESC_SEPARATOR)) {
            pattern = pattern + UmgSchedulerConstants.DOUBLE_ESC_FILE_SEPARATOR + fileExtnPattern;
        } else {
            pattern = pattern + UmgSchedulerConstants.CHAR_SLASH + fileExtnPattern;
        }

        final PathMatcher pathMatcher = fileSystem.getPathMatcher(PATH_MATCHER_EXPRESSION + pattern);

        try {
            // scan san base for existing files in bulk/input folder of all tenants and post it to event bus
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (pathMatcher.matches(file)) {
                        LOGGER.info("Matched file path {} while initial startup ",file.toFile().getAbsolutePath());
                        String tenantCode = determineTenantCode(file.toFile().getAbsolutePath());
                        if (isBulkEnabledForTenant(tenantCode)) {
                            LOGGER.info("Retrieved file {} from tenant folder {}.", file.getFileName(), tenantCode);
                            FileEvent fileEvent = new FileEvent(tenantCode, StandardWatchEventKinds.ENTRY_CREATE.name(), file,
                                    file.getFileName(), Files.getLastModifiedTime(file, LinkOption.NOFOLLOW_LINKS).toMillis());
                            eventBus.add(fileEvent);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            LOGGER.error("An error occurred while processing files in sanbase.");
        }
    }

    @Scheduled(fixedDelayString = "${lookup.rate}")
    public void lookupFiles() {
        if (lookupEnabled) {
            LOGGER.error("Started looking up for files.");
            processExistingFiles();
        }
    }
    
    /**
     * checks if the bulk is enabled for tenant
     * @param tenantCode
     * @return
     */
    private Boolean isBulkEnabledForTenant (String tenantCode) {
        LOGGER.info("Checking bulk is enabled for tenant : "+tenantCode);
        Boolean bulkEnabled = Boolean.FALSE;
        if (cacheRegistry != null) {
            Map<String,TenantInfo> tenantInfoMap = cacheRegistry.getMap(FrameworkConstant.TENANT_MAP);
            if (MapUtils.isNotEmpty(tenantInfoMap) && StringUtils.isNotBlank(tenantCode)  
                    && tenantInfoMap.get(tenantCode) != null
                    && tenantInfoMap.get(tenantCode).getTenantConfigsMap() != null) {
                if (Boolean.valueOf(tenantInfoMap.get(tenantCode).getTenantConfigsMap().get("BULK_ENABLED"))) {
                    LOGGER.info("Bulk is enabled for tenant : "+tenantCode);
                    bulkEnabled = Boolean.TRUE;
                } else {
                    LOGGER.info("Bulk is not enabled for tenant : "+tenantCode);
                }
            } else {
                LOGGER.info("tenantInfoMap has some null value for tenant : "+tenantCode);
            }
        } else {
            LOGGER.info("CacheRegistry is null : ");
        }
        return bulkEnabled;
    }
    
    /*
     * Determines the tenant code of from the given san directory
     */
    private String determineTenantCode(String directory) {
        String tenantCode = null;
        if (StringUtils.equalsIgnoreCase(File.separator, UmgSchedulerConstants.FILE_ESC_SEPARATOR)) {
            // determine the tenant code in a windows system
            String tempSanBase = StringUtils.replaceEach(sanBase,
                    new String[] { UmgSchedulerConstants.DOUBLE_ESC_FILE_SEPARATOR },
                    new String[] { UmgSchedulerConstants.FILE_ESC_SEPARATOR });
            tenantCode = StringUtils.substringBetween(directory, tempSanBase + File.separator, File.separator);
        } else {
            tenantCode = StringUtils.substringBetween(directory, sanBase + File.separator, File.separator);
        }
        return tenantCode;
    }
    
    /**
     * Starts the monitoring for all the added folders
     * @throws SystemException
     */
    private void startPoller() throws SystemException {
        try {
            monitor.start();
            LOGGER.error("Started Monitoring for files.");
        } catch (Exception e) {
            LOGGER.error("An error occurred while initilaizing monitor for polling.", e);
            SystemException.newSystemException(UmgSchedulerExceptionCodes.USC0000003, new Object[] {});
        }
    }
    
    
    /**
     * gets the bulk folder for each tenant and adds it for observation/monitoring
     * @throws SystemException
     */
    private void poll() throws SystemException {
    	if (monitor == null && StringUtils.isNotBlank(pollingInterval)) {
            monitor = new FileAlterationMonitor(Long.parseLong(pollingInterval));
    	}
    	LOGGER.info("Fetching TENANT_MAP from cacheRegistry.");
        Map<String,TenantInfo> tenantInfoMap = cacheRegistry.getMap(FrameworkConstant.TENANT_MAP);
        if (MapUtils.isNotEmpty(tenantInfoMap)) {
            Set<String> tenantSet = tenantInfoMap.keySet();
        	for (String tenantCode : tenantSet) {  
                if (tenantInfoMap.get(tenantCode) != null 
                        && tenantInfoMap.get(tenantCode).getTenantConfigsMap() != null 
                        && Boolean.valueOf(tenantInfoMap.get(tenantCode).getTenantConfigsMap().get("BULK_ENABLED"))) {
            		enableTenantBulkPoll(tenantCode);
                } else {
                	disableTenantBulkPoll(tenantCode);
                }
            }
		}
        startPoller();
    }
    
	/**
	 * gets the path till the bulk folder 
	 * @param bulkInput
	 * @param tntDirName
	 * @return
	 */
	private String createSanBulkFolderPath(String bulkInput, String tntDirName) {
		String sanBulkFolder = null;
		if (StringUtils.equals(File.separator, UmgSchedulerConstants.FILE_ESC_SEPARATOR)) {
		    sanBulkFolder = sanBase + UmgSchedulerConstants.DOUBLE_ESC_FILE_SEPARATOR + 
		    		tntDirName + bulkInput;
		    sanBulkFolder = StringUtils.substringBeforeLast(sanBulkFolder, UmgSchedulerConstants.FILE_ESC_SEPARATOR);
		} else {
		    sanBulkFolder = sanBase + UmgSchedulerConstants.CHAR_SLASH + 
		    		tntDirName + bulkInput;
		    sanBulkFolder = StringUtils.substringBeforeLast(sanBulkFolder, UmgSchedulerConstants.CHAR_SLASH);
		}
		return sanBulkFolder;
	}
    
    /**
     * gets the bulk folder for new tenant and adds it for observation/monitoring if not present already
     * @throws SystemException
     */
    public void enableTenantBulkPoll(String tenantCode) throws SystemException {
        LOGGER.info("Enabling bulk poll for tenant : "+tenantCode);
        String bulkInput = StringUtils.substringAfter(targetFolderPattern, "*");
        String sanBulkInput = createSanBulkFolderPath(bulkInput, tenantCode);
        if (sanBulkInput != null && Files.exists(Paths.get(sanBulkInput), LinkOption.NOFOLLOW_LINKS)) {
        	if(!monitorPathMap.containsKey(sanBulkInput)) {
        		addPathToMonitor (sanBulkInput);
        	}
        }
    }
    
    /**
     * gets the bulk folder for tenant and removes it for observation/monitoring if present 
     * @throws SystemException
     */
    public void disableTenantBulkPoll(String tenantCode) throws SystemException {
        LOGGER.info("Disabling bulk poll for tenant : "+tenantCode);
        String bulkInput = StringUtils.substringAfter(targetFolderPattern, "*");
        String sanBulkInput = createSanBulkFolderPath(bulkInput, tenantCode);
        if (sanBulkInput != null && Files.exists(Paths.get(sanBulkInput), LinkOption.NOFOLLOW_LINKS)) {
        	if(monitorPathMap.containsKey(sanBulkInput)) {
        		monitor.removeObserver(monitorPathMap.get(sanBulkInput));
        		monitorPathMap.remove(sanBulkInput);
        	}
        }
    }
    
    /**
     * Adds the folder for monitoring
     * @param pathToMonitor
     */
    private void addPathToMonitor (String pathToMonitor) {
        FileAlterationObserver observer = null;
        if (StringUtils.isNotBlank(pathToMonitor)) {
            observer = new FileAlterationObserver(pathToMonitor);
            observer.addListener(listener);
            monitorPathMap.put(pathToMonitor, observer);
            LOGGER.info("Adding the folder : {} for monitoring ",pathToMonitor);
            monitor.addObserver(observer);
        }
    }
    
    /**
     * Initializes the listener for file create and file modify events 
     */
    private void initializeListener () {
        final FileSystem fileSystem = FileSystems.getDefault();
        String pattern = targetFolderPattern;
        if (StringUtils.equals(File.separator, UmgSchedulerConstants.FILE_ESC_SEPARATOR)) {
            pattern = pattern + UmgSchedulerConstants.DOUBLE_ESC_FILE_SEPARATOR + fileExtnPattern;
        } else {
            pattern = pattern + UmgSchedulerConstants.CHAR_SLASH + fileExtnPattern;
        }

        final PathMatcher pathMatcher = fileSystem.getPathMatcher(PATH_MATCHER_EXPRESSION + pattern);
        listener = new FileAlterationListenerAdaptor() {
            // Is triggered when a file is created in the monitored folder
            @Override
            public void onFileCreate(File file) {
                try {
                    // "file" is the reference to the newly created file
                    String tenantCode = determineTenantCode(file.getAbsolutePath());
                    Path path = Paths.get(file.getAbsolutePath());
                    if (pathMatcher.matches(path)) {
                        LOGGER.info("Received create trigger for file {} from tenant folder {}.", file.getAbsolutePath(), tenantCode);
                        FileEvent fileEvent = new FileEvent(tenantCode, StandardWatchEventKinds.ENTRY_CREATE.name(), path,
                                path.getFileName(), Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS).toMillis());
                        eventBus.add(fileEvent);
                    }
                } catch (IOException e) {
                    LOGGER.error("Error occured in file create event for file name : {}",file.getName(),e);
                }
            }
            
            @Override
            public void onFileDelete(File file) {
                LOGGER.info("Received delete trigger for file {}.", file.getAbsolutePath());
            }
    
            @Override
            public void onFileChange(File file) {
                super.onFileChange(file);
                try {
                    String tenantCode = determineTenantCode(file.getAbsolutePath());
                    Path path = Paths.get(file.getAbsolutePath());
                    if (pathMatcher.matches(path)) {
                        LOGGER.info("Received modify trigger for file {} from tenant folder {}.", file.getAbsolutePath(), tenantCode);
                        FileEvent fileEvent;
                        fileEvent = new FileEvent(tenantCode, StandardWatchEventKinds.ENTRY_CREATE.name(), path,
                                path.getFileName(), Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS).toMillis());
                        eventBus.add(fileEvent);
                    }
                } catch (IOException e) {
                    LOGGER.error("Error occured in file change event for file name : {}",file.getName(),e);
                }
            }
        };
    }
    
    
    /**
     * Starts polling on folders registered for bulk model request.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    /*public void poll() throws SystemException {
        WatchKey watchKey = null;
        try {
            if (watcherService != null) {
                for (;;) {
                    watchKey = watcherService.take();
                    // wait for the file event
                    List<WatchEvent<?>> folderPollEvents = watchKey.pollEvents();
                    for (WatchEvent<?> watchEvent : folderPollEvents) {
                        LOGGER.info("Received event {} on directory {} File name {}.", watchEvent.kind().name(),
                                watchKey.watchable(), watchEvent.context());

                        if (watchEvent.kind() == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }
                        // add the file to the event bus
                        eventBus.add(new FileEvent(watchKeyTenantMap.get(watchKey), watchEvent.kind().name(),
                                (Path) watchKey.watchable(), (Path) watchEvent.context()));
                    }

                    if (!watchKey.reset()) {
                        break;
                    }
                }
                watchKey.cancel();
            }
        } catch (InterruptedException e) {
            LOGGER.error("An error occurred while initilaizing polling on folder.", e);
            SystemException.newSystemException(UmgSchedulerExceptionCodes.USC0000003, new Object[] {});
        }
    }*/

    /**
     * Registers directories that matches the given target folder pattern and registers the same for File create and Delete
     * events.
     * 
     * @throws SystemException
     */
    /*public void registerDirectoriesForPolling() throws SystemException {
        LOGGER.debug("Started registering folders for file change events");
        try {
            final FileSystem fileSystem = FileSystems.getDefault();
            watcherService = fileSystem.newWatchService();

            final PathMatcher pathMatcher = fileSystem.getPathMatcher(PATH_MATCHER_EXPRESSION + targetFolderPattern);

            // scan all tenants and register bulk/input folder and register with watcher service
            final SimpleFileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (pathMatcher.matches(dir)) {
                        WatchKey watchKey = dir.register(watcherService, StandardWatchEventKinds.ENTRY_CREATE,
                                StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE,
                                StandardWatchEventKinds.OVERFLOW);
                        LOGGER.info("Registered directory {} for polling with watch key {}.", dir, watchKey);
                        watchKeyTenantMap.put(watchKey, determineTenantCode(dir.toString()));
                    }
                    return FileVisitResult.CONTINUE;
                }
            };
            Files.walkFileTree(new File(sanBase).toPath(), fileVisitor);
        } catch (IOException e) {
            LOGGER.error("An error occurred while registering directory {} for polling.", sanBase);
            SystemException.newSystemException(UmgSchedulerExceptionCodes.USC0000001, new Object[] { sanBase });
        }
        LOGGER.debug("Completed registration of folders for file change events");
    }*/
}
