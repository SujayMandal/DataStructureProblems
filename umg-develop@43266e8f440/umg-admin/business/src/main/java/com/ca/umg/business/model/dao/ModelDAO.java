/*
 * ModelDAO.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.ca.umg.business.model.entity.Model;

/**
 * DOCUMENT ME!
 * 
 * @author $author$
 * @version $Revision$
 */
public interface ModelDAO extends JpaRepository<Model, String>, JpaSpecificationExecutor<Model> {
    /**
     * 
     * Returns the model information for the given model name.
     * 
     * @param name
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/

    List<Model> findByName(String name);

    /**
     * DOCUMENT ME!
     * 
     * @param umgName
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     **/
    Model findByUmgName(String umgName);

    /**
     * Get all model names.
     * 
     * @return model names.
     */
    @Query("select distinct(u.name) from #{#entityName} u")
    List<String> getAllModelNames();

}
