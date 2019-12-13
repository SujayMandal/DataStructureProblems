package com.fa.dp.core.entityaudit.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.proxy.HibernateProxy;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.PojomaticPolicy;
import org.pojomatic.annotations.Property;
import org.springframework.data.domain.Persistable;

/**
 * Abstract base class for entities. Allows parameterization of id type, chooses auto-generation and implements
 * {@link #equals(Object)} and {@link #hashCode()} based on that id. "Borrowed" from spring-data-jpa.
 *
 */
@MappedSuperclass
@Setter
@Getter
public abstract class AbstractPersistable implements java.io.Serializable, Persistable<String> {

	private static final long serialVersionUID = 2535090450811888936L;

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "ID")
	@Property(policy = PojomaticPolicy.TO_STRING)
	private String id;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.data.domain.Persistable#isNew()
	 */
	public boolean isNew() {

		return null == getId();
	}

}
