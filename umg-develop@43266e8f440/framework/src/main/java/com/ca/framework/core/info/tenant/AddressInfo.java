/**
 * 
 */
package com.ca.framework.core.info.tenant;

import com.ca.framework.core.info.BaseInfo;

/**
 * 
 * @author kamathan
 * @version 1.0
 */
public class AddressInfo extends BaseInfo {

    private static final long serialVersionUID = 8844651319321771830L;

    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String zip;

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress2() {
        return address2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getZip() {
        return zip;
    }

}
