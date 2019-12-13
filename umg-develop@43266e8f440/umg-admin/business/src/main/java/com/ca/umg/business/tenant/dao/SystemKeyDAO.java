/**
 * 
 */
package com.ca.umg.business.tenant.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ca.umg.business.tenant.entity.SystemKey;

/**
 * Repository for {@link SystemKey} entity.
 * 
 * @author kamathan
 * @version 1.0
 */
public interface SystemKeyDAO extends JpaRepository<SystemKey, String> {

    /**
     * Returns the system Key details for the given key.
     * 
     * @param key
     * @return
     */
    SystemKey findByKey(String key);

    /**
     * Returns the system keys of given type.
     * 
     * @param type
     * @return
     */
    List<SystemKey> findByType(String type);
    
    
    /**
     * Returns the system key of given key and type.
     * @param key
     * @param type
     * @return
     */
    SystemKey findByKeyAndType(String key,String type);
    
    /**
     * returns the list of system key for the type 
     * @param type
     * @return
     */
    @Query("select u.key from #{#entityName} u where u.type = ?1")
    List<String> findByKeyType(String type);
}
