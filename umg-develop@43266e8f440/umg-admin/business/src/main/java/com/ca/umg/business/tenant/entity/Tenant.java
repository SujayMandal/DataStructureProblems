package com.ca.umg.business.tenant.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.pojomatic.annotations.PojomaticPolicy;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractAuditable;

/**
 * holds tenant information.
 * 
 * @author kamathan
 * @version 1.0
 *
 */
@Entity
@Table(name = "TENANT")
public class Tenant extends AbstractAuditable {
    private static final long serialVersionUID = -7183155545754589873L;

    /**
     * holds the tenant code
     */
    @Column(name = "CODE", nullable = false, unique = true)
    @Property
    private String code;

    /**
     * holds name of the tenant
     */
    @Column(name = "NAME", nullable = false, unique = true)
    @Property
    private String name;

    /**
     * holds the description of the tenant
     */
    @Column(name = "DESCRIPTION")
    @Property(policy = PojomaticPolicy.TO_STRING)
    private String description;

    /**
     * holds the tenant type
     */
    @Column(name = "TENANT_TYPE", nullable = false)
    @Property
    private String tenantType;

    /**
     * holds configuration of the tenant
     */
    @OneToMany(mappedBy = "tenant", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<TenantConfig> tenantConfigs;
    
    /**
     * holds the tenant token
     */
    @OneToMany(mappedBy = "tenant", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<AuthToken> authTokens;

    /**
     * holds the addresses of the tenant
     * 
     * @return
     */
    @OneToMany(mappedBy = "tenant", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Address> addresses;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public String getTenantType() {
        return tenantType;
    }

    public void setTenantType(String tenantType) {
        this.tenantType = tenantType;
    }

    public Set<TenantConfig> getTenantConfigs() {
        return tenantConfigs;
    }

    public void setTenantConfigs(Set<TenantConfig> tenantConfigs) {
        this.tenantConfigs = tenantConfigs;
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    public Set<AuthToken> getAuthTokens() {
        return authTokens;
    }

    public void setAuthTokens(Set<AuthToken> authTokens) {
        this.authTokens = authTokens;
    }

}
