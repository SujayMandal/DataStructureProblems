/**
 * 
 */
package com.ca.umg.business.mapping.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.entity.MappingOutput;

/**
 * @author kamathan
 *
 */
public interface MappingOutputDAO extends JpaRepository<MappingOutput, String> {

    /**
     * Returns the output for the given mapping.
     * 
     * @param mapping
     * @return
     */
    MappingOutput findByMapping(Mapping mapping);

    /**
     * Returns the output for the given mapping name
     * 
     * @param name
     * @return
     */
    MappingOutput findByMappingName(String name);

}
