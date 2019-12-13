package com.ca.framework.core.db.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;
import org.hibernate.envers.Audited;
import org.pojomatic.annotations.PojomaticPolicy;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;
import com.ca.framework.core.requestcontext.RequestContext;

/**
 * Base class for all Entities which are having tenant discriminatory column.
 * 
 * @author Anish Devasia
 * 
 */
@FilterDefs({ @FilterDef(name = "system_tenantFilter", parameters = { @ParamDef(name = "tenantFilterString", type = "string") }) })
@Filters({ @Filter(name = "system_tenantFilter", condition = "(TENANT_ID = :tenantFilterString)") })
@MappedSuperclass
@Audited
public class MultiTenantEntity extends AbstractAuditable {

    private static final long serialVersionUID = -3661924001030744357L;

    @Column(name = "TENANT_ID", nullable = true, insertable = true, updatable = true, length = 36)
    @Property(policy = PojomaticPolicy.TO_STRING)
    private String tenantId;

    @PrePersist
    public void onPersist() {
        if (tenantId == null && RequestContext.getRequestContext().getTenantCode() != null) {
            tenantId = RequestContext.getRequestContext().getTenantCode();
        }
    }

    @PostLoad
    @PreUpdate
    @PreRemove
    public void onEntityModification() throws Exception {
        if (tenantId != null && RequestContext.getRequestContext().getTenantCode() != null
                && !StringUtils.equals(RequestContext.getRequestContext().getTenantCode(), tenantId)) {
            throw new SystemException(FrameworkExceptionCodes.FSE0000200, 
                    new Object[]{tenantId,RequestContext.getRequestContext().getTenantCode()});
        }
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
