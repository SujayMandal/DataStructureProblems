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
 * Holds the addresses of the different Tenant entities.
 * 
 * @author kamathan
 * @version 1.0
 */
@Entity
@Table(name = "ADDRESS")
public class Address extends AbstractAuditable {

    private static final long serialVersionUID = 3268229454686645556L;

    /**
     * holds address 1 information
     */
    @Column(name = "ADDRESS_1", nullable = false)
    @Property
    private String address1;

    /**
     * holds address 2 information
     */
    @Column(name = "ADDRESS_2")
    @Property
    private String address2;

    /**
     * holds city information
     */
    @Column(name = "CITY", nullable = false)
    @Property
    private String city;

    /**
     * holds state information
     */
    @Column(name = "STATE", nullable = false)
    @Property
    private String state;

    /**
     * holds the country information
     */
    @Column(name = "COUNTRY", nullable = false)
    @Property
    private String country;

    /**
     * holds the zip code information
     */
    @Column(name = "ZIP", nullable = false)
    @Property
    private String zip;

    /**
     * holds the tenant details for whom the current address belongs to.
     */
    @ManyToOne
    @JoinColumn(name = "TENANT_ID", nullable = false)
    @Property
    private Tenant tenant;

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
