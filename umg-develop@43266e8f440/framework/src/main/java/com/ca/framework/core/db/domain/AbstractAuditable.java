package com.ca.framework.core.db.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.pojomatic.annotations.PojomaticPolicy;
import org.pojomatic.annotations.Property;
import org.springframework.data.domain.Auditable;

import com.ca.framework.core.util.DateConversionUtil;

/**
 * Abstract base class for auditable entities. Stores the audit values in persistent fields. "Borrowed" from spring-data-jpa.
 * 
 * ILR - Added in onPersist and onCreate. These will put in dates and users.
 * 
 * @author Anish Devasia
 */
@MappedSuperclass
@Audited
public abstract class AbstractAuditable extends AbstractPersistable implements Auditable<String, String> {

    private static final long serialVersionUID = -6615842299925446677L;

    @Basic
    @Column(name = "CREATED_BY", updatable = false, nullable = false)
    private String createdBy;

    @Basic
    @Column(name = "CREATED_ON", updatable = false, nullable = false)
    private Long createdDate;

    @Basic
    @Column(name = "LAST_UPDATED_BY")
    private String lastModifiedBy;

    @Basic
    @Column(name = "LAST_UPDATED_ON")
    private Long lastModifiedDate;

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.domain.Auditable#getCreatedBy()
     */
    @Property(policy = PojomaticPolicy.TO_STRING)
    public String getCreatedBy() {
        return createdBy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.domain.Auditable#setCreatedBy(java.lang.Object)
     */
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.domain.Auditable#getCreatedDate()
     */
    @Property(policy = PojomaticPolicy.TO_STRING)
    public DateTime getCreatedDate() {
        return createdDate == null ? null : new DateTime(createdDate.longValue());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.domain.Auditable#setCreatedDate(org.joda.time .DateTime)
     */
    public void setCreatedDate(final DateTime createdDate) {
        this.createdDate = DateConversionUtil.convertToUtcForAuditable(createdDate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.domain.Auditable#getLastModifiedBy()
     */
    @Property(policy = PojomaticPolicy.TO_STRING)
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.domain.Auditable#setLastModifiedBy(java.lang .Object)
     */
    public void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.domain.Auditable#getLastModifiedDate()
     */
    @Property(policy = PojomaticPolicy.TO_STRING)
    public DateTime getLastModifiedDate() {
        return lastModifiedDate == null ? null : new DateTime(lastModifiedDate.longValue());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.domain.Auditable#setLastModifiedDate(org.joda .time.DateTime)
     */
    public void setLastModifiedDate(final DateTime lastModifiedDate) {
        this.lastModifiedDate = DateConversionUtil.convertToUtcForAuditable(lastModifiedDate);
    }

}
