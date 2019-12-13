package com.ca.umg.business.tenant.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractAuditable;

/**
 * Holds tenant authtoken details.
 * 
 * @author basanaga
 * @version 1.0
 */
@Entity
@Table(name = "AUTHTOKEN")

public class AuthToken extends AbstractAuditable {

    /**
     * dsefault serialVersionId
     */
    private static final long serialVersionUID = 1L;

    /**
     * holds authCode information
     */
    @Column(name = "AUTH_CODE", nullable = false)
    @Property
    private String authCode;


    /**
     * holds the tenant details for whom the current address belongs to.
     */
    @ManyToOne
    @JoinColumn(name = "TENANT_ID", nullable = false)
    @Property
    private Tenant tenant;

    /**
     * holds authcode active from information
     */
    @Column(name = "ACTIVE_FROM")
    @Property
    private Long activeFrom;


    /**
     * holds authcode active until information
     */
    @Column(name = "ACTIVE_UNTIL")
    @Property
    private Long activeUntil;

    /**
     * holds autoken status until information
     */
    @Column(name = "STATUS", nullable = false)
    @Property
    private String status;
    /**
     * holds authtoken comment information
     */
    @Column(name = "COMMENT")
    @Property
    private String comment;

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(Long activeFrom) {
        this.activeFrom = activeFrom;
    }

    public Long getActiveUntil() {
        return activeUntil;
    }

    public void setActiveUntil(Long activeUntil) {
        this.activeUntil = activeUntil;
    }

}
