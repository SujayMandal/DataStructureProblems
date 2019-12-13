package com.ca.umg.business.version.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.pojomatic.annotations.PojomaticPolicy;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.MultiTenantEntity;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.model.entity.ModelLibrary;

@Entity
@Table(name = "UMG_VERSION")
@Audited
@SuppressWarnings("PMD.TooManyFields")
public class Version extends MultiTenantEntity {

    private static final long serialVersionUID = 7675184327204106681L;

    /**
     * tenant model name.
     */
    @Property
    @NotNull(message = "Version name cannot be null.")
    @Column(name = "NAME")
    private String name;

    /**
     * tenant model description.
     */
    @Property(policy = PojomaticPolicy.TO_STRING)
    @NotNull(message = "Description cannot be null.")
    @Column(name = "DESCRIPTION")
    private String description;

    @Property
    @NotNull(message = "Major version cannot be null.")
    @Column(name = "MAJOR_VERSION")
    private Integer majorVersion;

    @Property
    @NotNull(message = "Minor version cannot be null.")
    @Column(name = "MINOR_VERSION")
    private Integer minorVersion;

    @Property
    @NotNull(message = "Mapping cannot be null.")
    @ManyToOne
    @JoinColumn(name = "MAPPING_ID")
    private Mapping mapping;

    @Property
    @NotNull(message = "Model library cannot be null.")
    @ManyToOne
    @JoinColumn(name = "MODEL_LIBRARY_ID")
    private ModelLibrary modelLibrary;

    @Property
    @NotNull(message = "Staus cannot be null.")
    @Column(name = "STATUS")
    private String status;

    @Property
    @NotNull(message = "Version description cannot be null.")
    @Column(name = "VERSION_DESCRIPTION")
    private String versionDescription;

    @Property(policy = PojomaticPolicy.TO_STRING)
    @Column(name = "PUBLISHED_ON")
    private Long publishedOn;

    @Property(policy = PojomaticPolicy.TO_STRING)
    @Column(name = "PUBLISHED_BY")
    private String publishedBy;

    @Property(policy = PojomaticPolicy.TO_STRING)
    @Column(name = "DEACTIVATED_ON")
    private Long deactivatedOn;

    @Property(policy = PojomaticPolicy.TO_STRING)
    @Column(name = "DEACTIVATED_BY")
    private String deactivatedBy;
    
    @Property(policy = PojomaticPolicy.TO_STRING)
    @NotNull(message = "Model Type cannot be null.")
    @Column(name = "MODEL_TYPE")
    private String modelType;

    @Property(policy = PojomaticPolicy.TO_STRING)
    @Column(name = "REQUESTED_ON")
    private Long requestedOn;

    @Property(policy = PojomaticPolicy.TO_STRING)
    @Column(name = "REQUESTED_BY")
    private String requestedBy;
    
    @Column(name = "EMAIL_APPRROVAL")
    private int emailApproval;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(Integer majorVersion) {
        this.majorVersion = majorVersion;
    }

    public Integer getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(Integer minorVersion) {
        this.minorVersion = minorVersion;
    }

    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

    public ModelLibrary getModelLibrary() {
        return modelLibrary;
    }

    public void setModelLibrary(ModelLibrary modelLibrary) {
        this.modelLibrary = modelLibrary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

    public DateTime getPublishedOn() {
        return publishedOn == null ? null : new DateTime(publishedOn.longValue());
    }

    public void setPublishedOn(DateTime publishedOn) {
        this.publishedOn = publishedOn == null ? null : publishedOn.getMillis();
    }

    public String getPublishedBy() {
        return publishedBy;
    }

    public void setPublishedBy(String publishedBy) {
        this.publishedBy = publishedBy;
    }

    public DateTime getDeactivatedOn() {
        return deactivatedOn == null ? null : new DateTime(deactivatedOn.longValue());
    }

    public void setDeactivatedOn(DateTime deactivatedOn) {
        this.deactivatedOn = deactivatedOn == null ? null : deactivatedOn.getMillis();
    }

    public String getDeactivatedBy() {
        return deactivatedBy;
    }

    public void setDeactivatedBy(String deactivatedBy) {
        this.deactivatedBy = deactivatedBy;
    }

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

    public DateTime getRequestedOn() {
        return requestedOn == null ? null : new DateTime(requestedOn.longValue());
    }

    public void setRequestedOn(final DateTime requestedOn) {
        this.requestedOn = requestedOn == null ? null : requestedOn.getMillis();
    }

	public String getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}

	public int getEmailApproval() {
		return emailApproval;
	}

	public void setEmailApproval(int emailApproval) {
		this.emailApproval = emailApproval;
	}			
}
