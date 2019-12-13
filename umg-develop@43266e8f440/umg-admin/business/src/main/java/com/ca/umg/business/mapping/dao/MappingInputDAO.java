/**
 * 
 */
package com.ca.umg.business.mapping.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.entity.MappingInput;

/**
 * @author kamathan
 *
 */
public interface MappingInputDAO extends JpaRepository<MappingInput, String> {

    /**
     * Returns the input for the given mapping.
     * 
     * @param mapping
     * @return
     */
    MappingInput findByMapping(Mapping mapping);
}
