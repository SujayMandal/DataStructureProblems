/**
 * 
 */
package com.ca.umg.business.tenant;

import java.util.Set;

import com.ca.umg.business.BaseTest;
import com.ca.umg.business.tenant.entity.Address;
import com.ca.umg.business.tenant.entity.SystemKey;
import com.ca.umg.business.tenant.entity.Tenant;
import com.ca.umg.business.tenant.entity.TenantConfig;
import com.ca.framework.core.info.tenant.AddressInfo;
import com.ca.framework.core.info.tenant.SystemKeyInfo;
import com.ca.framework.core.info.tenant.TenantConfigInfo;
import com.ca.framework.core.info.tenant.TenantInfo;

/**
 * @author kamathan
 *
 */
public abstract class AbstractTenantTest extends BaseTest {

    public Address createAddress(String address1, String address2, String city, String state, String country, String zip,
            Tenant tenant) {
        Address address = new Address();
        address.setAddress1(address1);
        address.setAddress2(address2);
        address.setCity(city);
        address.setState(state);
        address.setCountry(country);
        address.setZip(zip);
        address.setTenant(tenant);
        return getAddressDAO().save(address);
    }

    public AddressInfo buildAddressInfo(String address1, String address2, String city, String state, String country, String zip) {
        AddressInfo address = new AddressInfo();
        address.setAddress1(address1);
        address.setAddress2(address2);
        address.setCity(city);
        address.setState(state);
        address.setCountry(country);
        address.setZip(zip);
        return address;
    }

    public SystemKeyInfo buildSystemKeyInfo(String key, String type) {
        SystemKeyInfo systemKey = new SystemKeyInfo();
        systemKey.setKey(key);
        systemKey.setType(type);
        return systemKey;
    }

    protected Tenant createTenant(String code, String name, String tenantType, String description) {
        Tenant tenant = getTenantDAO().findByName(name);
        if (tenant == null) {
            tenant = new Tenant();
            tenant.setCode(code);
            tenant.setName(name);
            tenant.setTenantType(tenantType);
            tenant.setDescription(description);
            tenant = getTenantDAO().save(tenant);
        }
        return tenant;
    }

    protected TenantInfo buildTenantInfo(String name, String desrcription, String tenantCode, String tenantType,
            Set<AddressInfo> addressInfos, Set<TenantConfigInfo> tenantConfigInfos) {
        TenantInfo tenantInfo = new TenantInfo();
        tenantInfo.setName(name);
        tenantInfo.setDescription(desrcription);
        tenantInfo.setCode(tenantCode);
        tenantInfo.setTenantType(tenantType);
        tenantInfo.setAddresses(addressInfos);
        tenantInfo.setTenantConfigs(tenantConfigInfos);
        return tenantInfo;
    }

    protected TenantConfigInfo buildTenantConfigInfo(SystemKeyInfo systemKeyInfo, String value, TenantInfo tenantInfo) {
        TenantConfigInfo tenantConfig = new TenantConfigInfo();
        tenantConfig.setSystemKey(systemKeyInfo);
        tenantConfig.setValue(value);
        tenantConfig.setTenantInfo(tenantInfo);
        return tenantConfig;
    }

    protected TenantConfig createTenantConfig(Tenant tenant, SystemKey systemKey, String value) {
        TenantConfig tenantConfig = new TenantConfig();
        tenantConfig.setTenant(tenant);
        tenantConfig.setSystemKey(systemKey);
        tenantConfig.setValue(value);
        return getTenantConfigDAO().save(tenantConfig);
    }

}
