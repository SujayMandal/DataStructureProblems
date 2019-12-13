/**
 * 
 */
package com.fa.dp.business.command.dao;

import java.util.List;

import com.fa.dp.business.command.entity.Command;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author mandasuj
 *
 */
public interface CommandDAO extends JpaRepository<Command, String> {

    /**
     * Returns list of execution commands for the given process
     * 
     * @param process
     * @return
     */
    public List<Command> findByProcessOrderByExecutionSequence(String process);
    
    @Query("SELECT c FROM Command c WHERE c.process = :process AND c.name = :name")
    public List<Command> findByProcess(@Param("process") String process, @Param("name") String name);

    List<Command> findByName(String filterName);
}
