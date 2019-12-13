/**
 * 
 */
package com.ca.umg.file.event.info;

import java.io.Serializable;
import java.nio.file.Path;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.Property;

/**
 * @author kamathan
 *
 */
public class FileEvent implements Serializable {

    private static final long serialVersionUID = 2052216886192094612L;

    @Property
    private Path watchedDirectory;

    @Property
    private String tenantCode;

    @Property
    private String eventType;

    @Property
    private Path file;

    @Property
    private long lastModifiedTime;

    public FileEvent(String tenantCode, String eventType, Path watchedDirectory, Path file, long lastModifiedTime) {
        this.watchedDirectory = watchedDirectory;
        this.tenantCode = tenantCode;
        this.eventType = eventType;
        this.file = file;
        this.lastModifiedTime = lastModifiedTime;
    }

    public Path getWatchedDirectory() {
        return watchedDirectory;
    }

    public void setWatchedDirectory(Path watchedDirectory) {
        this.watchedDirectory = watchedDirectory;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Path getFile() {
        return file;
    }

    public void setFile(Path file) {
        this.file = file;
    }

    @Override
    public final int hashCode() {
        return Pojomatic.hashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return Pojomatic.equals(this, obj);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return Pojomatic.toString(this);
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

}
