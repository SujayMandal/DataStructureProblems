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
@Table(name = "MAPPING_INPUT")
@Audited
public class MappingInput extends MultiTenantEntity {

	private static final long serialVersionUID = 6449916508234215164L;

	@NotNull(message = "Mapping cannot be null.")
	@OneToOne
	@JoinColumn(name = "MAPPING_ID")
	@Property
	private Mapping mapping;

	@NotNull(message = "Mapping data cannot be null.")
	@Column(name = "MAPPING_DATA")
	@Lob
	private byte[] mappingData;

	@NotNull(message = "Tenant Interface Definition cannot be null.")
	@Column(name = "TENANT_INTERFACE_DEFINITION")
	@Lob
	private byte[] tenantInterfaceDefn;

	@Column(name = "TENANT_INTF_SYS_DEFINITION")
	@Lob
	private byte[] tenantInterfaceSysDefn;

	public Mapping getMapping() {
		return mapping;
	}

	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}

	public byte[] getMappingData() {
		return mappingData != null ? Arrays.copyOf(mappingData,
				mappingData.length) : null;
	}

	public void setMappingData(byte[] mappingData) {
		this.mappingData = mappingData;
	}

	public byte[] getTenantInterfaceDefn() {
		return tenantInterfaceDefn != null ? Arrays.copyOf(tenantInterfaceDefn,
				tenantInterfaceDefn.length) : null;
	}

	public void setTenantInterfaceDefn(byte[] tenantInterfaceDefn) {
		if (tenantInterfaceDefn != null) {
			this.tenantInterfaceDefn = Arrays.copyOf(tenantInterfaceDefn,
					tenantInterfaceDefn.length);
		}
	}

	public byte[] getTenantInterfaceSysDefn() {
		return tenantInterfaceSysDefn != null ? Arrays.copyOf(
				tenantInterfaceSysDefn, tenantInterfaceSysDefn.length) : null;
	}

	public void setTenantInterfaceSysDefn(byte[] tenantInterfaceSysDefn) {
		this.tenantInterfaceSysDefn = Arrays.copyOf(tenantInterfaceSysDefn,
				tenantInterfaceSysDefn.length);
	}

}
