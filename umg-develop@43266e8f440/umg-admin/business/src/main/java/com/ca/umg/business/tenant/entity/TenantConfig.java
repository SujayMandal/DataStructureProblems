/**
 * 
 */
package com.ca.umg.business.tenant.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractAuditable;

/**
 * Holds tenant configuration details.
 * 
 * @author kamathan
 * @version 1.0
 */
@Entity
@Table(name = "TENANT_CONFIG")
public class TenantConfig extends AbstractAuditable {

    private static final long serialVersionUID = -8645945580006430818L;

    /**
     * holds the system key for the configuration
     */
    @ManyToOne
    @JoinColumn(name = "SYSTEM_KEY_ID")
    @Property
    private SystemKey systemKey;

    /**
     * holds the value of the tenant configuration
     */
    @Column(name = "CONFIG_VALUE")
    @Property
    private String value;

    /**
     * holds the tenant id for which the configuration belongs.
     */
    @ManyToOne
    @JoinColumn(name = "TENANT_ID", nullable = false)
    @Property
    private Tenant tenant;

    /**
     * Role of the tenant
     */
    @Column(name = "ROLE")
    @Property
    private String role;

    public SystemKey getSystemKey() {
        return systemKey;
    }

    public void setSystemKey(SystemKey systemKey) {
        this.systemKey = systemKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
