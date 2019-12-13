/**
 * 
 */
package com.ca.umg.business.version.command.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ca.umg.business.version.command.entity.Command;

/**
 * @author kamathan
 *
 */
public interface CommandDAO extends JpaRepository<Command, String> {

    /**
     * Returns list of execution commands for the given process
     * 
     * @param process
     * @param sort
     * @return
     */
    public List<Command> findByProcess(String process, Sort sort);
}
