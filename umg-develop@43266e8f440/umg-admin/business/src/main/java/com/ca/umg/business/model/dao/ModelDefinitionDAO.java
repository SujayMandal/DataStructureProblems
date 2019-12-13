/*
 * ModelDefinitionDAO.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
/**
 * 
 */
package com.ca.umg.business.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelDefinition;

/**
 * @author kamathan
 *
 */
public interface ModelDefinitionDAO extends JpaRepository<ModelDefinition, String> {
    /**
     * DOCUMENT ME!
     *
     * @param model
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    ModelDefinition findByModel(Model model);
}
