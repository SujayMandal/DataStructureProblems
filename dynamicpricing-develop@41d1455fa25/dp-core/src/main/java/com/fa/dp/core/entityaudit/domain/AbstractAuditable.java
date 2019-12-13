package com.fa.dp.core.entityaudit.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.pojomatic.annotations.PojomaticPolicy;
import org.pojomatic.annotations.Property;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fa.dp.core.util.DateConversionUtil;

/**
 * Abstract base class for auditable entities. Stores the audit values in persistent fields. "Borrowed" from spring-data-jpa.
 *
 * ILR - Added in onPersist and onCreate. These will put in dates and users.
 *
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Setter
@Getter
public abstract class AbstractAuditable extends AbstractPersistable {

	private static final long serialVersionUID = -6615842299925446677L;

	@CreatedBy
	@Basic
	@Column(name = "CREATED_BY", updatable = false, nullable = false)
	private String createdBy;

	@CreatedDate
	@Basic
	@Column(name = "CREATED_ON", updatable = false, nullable = false)
	private Long createdDate;

	@LastModifiedBy
	@Basic
	@Column(name = "LAST_UPDATED_BY")
	private String lastModifiedBy;

	@LastModifiedDate
	@Basic
	@Column(name = "LAST_UPDATED_ON")
	private Long lastModifiedDate;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.data.domain.Auditable#getCreatedDate()
	 */
	@Property(policy = PojomaticPolicy.TO_STRING)
	public Long getCreatedDate() {
		return createdDate == null ? null : DateConversionUtil.getMillisFromUtcToEst(createdDate.longValue());
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
	 * @see org.springframework.data.domain.Auditable#getLastModifiedDate()
	 */
	public Long getLastModifiedDate() {
		return lastModifiedDate == null ? null : DateConversionUtil.getMillisFromUtcToEst(lastModifiedDate.longValue());
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
