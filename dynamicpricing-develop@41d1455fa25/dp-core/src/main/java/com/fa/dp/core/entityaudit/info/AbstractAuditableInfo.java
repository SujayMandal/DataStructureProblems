/**
 * 
 */
package com.fa.dp.core.entityaudit.info;

import com.fa.dp.core.util.DateConversionUtil;

import org.joda.time.DateTime;
import org.pojomatic.annotations.PojomaticPolicy;
import org.pojomatic.annotations.Property;

import lombok.Getter;
import lombok.Setter;


public class AbstractAuditableInfo {

	private String id;

	private String createdBy;

	private Long createdDate;

	private String lastModifiedBy;

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
