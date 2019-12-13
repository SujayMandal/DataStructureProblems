/**
 * 
 */
package com.ca.umg.business.systemparam.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ca.umg.business.systemparam.entity.SystemParameter;

/**
 * Repository for {@link SystemParameter} entity.
 * 
 * @author mahantat
 * @version 1.0
 */
public interface SystemParameterDAO extends JpaRepository<SystemParameter, String> {

    /**
     * Returns the system Key details for the given key.
     * 
     * @param key
     * @return
     */
	SystemParameter findBySysKey(String key);

    /**
     * Returns the system keys of given type.
     * 
     * @param type
     * @return
     */
    List<SystemParameter> findBySysValue(String value);
    
    
    /**
     * Returns the system key of given key and type.
     * @param key
     * @param type
     * @return
     */
    List<SystemParameter> findAll();
    
  
}
