package com.ca.umg.business.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ca.umg.business.model.entity.ModelImplementationType;

public interface ModelImplementationTypeDAO extends JpaRepository<ModelImplementationType, String> {

    /**
     * 
     * Returns implementation data for the input implementation
     * 
     * @param implementation
     * @return
     */
    public ModelImplementationType findByImplementation(String implementation);

}
