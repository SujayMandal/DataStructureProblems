/**
 * 
 */
package com.ca.umg.business.version.command.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.pojomatic.annotations.PojomaticPolicy;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractAuditable;

/**
 * Entity class to map to COMMAND_SEQUENCE table. This table holds the list of commands and sequence of its execution.
 * 
 * @author kamathan
 *
 */
@Entity
@Table(name = "COMMAND")
public class Command extends AbstractAuditable {

    private static final long serialVersionUID = -5399644115131825823L;

    @Property
    @Column(name = "NAME")
    private String name;

    @Property(policy = PojomaticPolicy.TO_STRING)
    @Column(name = "EXECUTION_SEQUENCE")
    private int executionSequence;

    @Property(policy = PojomaticPolicy.TO_STRING)
    @Column(name = "DESCRIPTION")
    private String description;

    @Property
    @Column(name = "PROCESS")
    private String process;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExecutionSequence() {
        return executionSequence;
    }

    public void setExecutionSequence(int commandSequence) {
        this.executionSequence = commandSequence;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

}
