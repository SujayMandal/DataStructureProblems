/**
 * 
 */
package com.fa.dp.business.test.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "TEST_TABLE")
public class TestEntity extends AbstractAuditable {

	private static final long serialVersionUID = -8233966970487443346L;
	@Column(name = "TEST_NAME")
	private String name;

}
