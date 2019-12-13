/**
 * 
 */
package com.fa.dp.business.command.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fa.dp.core.entityaudit.domain.AbstractAuditable;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity class to map to COMMAND_SEQUENCE table. This table holds the list of
 * commands and sequence of its execution.
 * 
 * @author mandasuj
 *
 */
@Entity
@Setter
@Getter
@Table(name = "COMMAND")
public class Command extends AbstractAuditable {

	private static final long serialVersionUID = -7256909336081995174L;

	@Column(name = "NAME")
	private String name;

	@Column(name = "EXECUTION_SEQUENCE")
	private int executionSequence;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "PROCESS")
	private String process;

	@Column(name = "ACTIVE")

	private boolean active;

}
