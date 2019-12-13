package com.ca.umg.business.migration.audit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.MultiTenantEntity;
import com.ca.umg.business.version.entity.Version;

@Entity
@Table(name = "MIGRATION_LOG")
public class MigrationAudit extends MultiTenantEntity {

    private static final long serialVersionUID = 1080669030501322783L;

    @ManyToOne
    @JoinColumn(name = "VERSION_ID")
    @Property
    private Version version;

    @NotNull(message = "Type cannot be null.")
    @NotBlank(message = "Type cannot be blank.")
    @Column(name = "MIGRATION_TYPE")
    @Property
    private String type;

    @Column(name = "VERSION_DATA")
    @Lob
    private byte[] versionData;

    @NotNull(message = "Status cannot be null.")
    @NotBlank(message = "Status cannot be blank.")
    @Column(name = "STATUS")
    @Property
    private String status;

    @Column(name = "IMPORT_FILE_NAME")
    @Property
    private String importFileName;

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getVersionData() {
        return versionData;
    }

    public void setVersionData(byte[] versionData) {
        this.versionData = versionData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImportFileName() {
        return importFileName;
    }

    public void setImportFileName(final String importFileName) {
        this.importFileName = importFileName;
    }

}
