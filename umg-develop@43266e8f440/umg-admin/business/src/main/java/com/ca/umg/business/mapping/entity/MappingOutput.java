/**
 * 
 */
package com.ca.umg.business.mapping.entity;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.MultiTenantEntity;

/**
 * @author kamathan
 *
 */
@Entity
@Table(name = "MAPPING_OUTPUT")
@Audited
public class MappingOutput extends MultiTenantEntity {

    private static final long serialVersionUID = 6449916508234215164L;

    @NotNull(message = "Mapping cannot be null.")
    @OneToOne
    @JoinColumn(name = "MAPPING_ID")
    @Property
    private Mapping mapping;

    @NotNull(message = "Tenant Interface Definition cannot be null.")
    @Column(name = "TENANT_INTERFACE_DEFINITION")
    @Lob
    private byte[] tenantInterfaceDefn;

    @NotNull(message = "Mapping data cannot be null.")
    @Column(name = "MAPPING_DATA")
    @Lob
    private byte[] mappingData;

    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

    public void setMappingData(byte[] mappingData) {
        this.mappingData = mappingData;
    }

    public byte[] getMappingData() {
        return mappingData != null ? Arrays.copyOf(mappingData, mappingData.length) : null;
    }

    public void setTenantInterfaceDefn(byte[] tenantInterfaceDefn) {
        if (tenantInterfaceDefn != null) {
            this.tenantInterfaceDefn = Arrays.copyOf(tenantInterfaceDefn, tenantInterfaceDefn.length);
        }
    }

    public byte[] getTenantInterfaceDefn() {
        return tenantInterfaceDefn != null ? Arrays.copyOf(tenantInterfaceDefn, tenantInterfaceDefn.length) : null;
    }

}
