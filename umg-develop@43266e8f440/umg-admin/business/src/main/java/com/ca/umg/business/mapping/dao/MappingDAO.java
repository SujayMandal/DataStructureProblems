package com.ca.umg.business.mapping.dao;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.model.entity.Model;

public interface MappingDAO extends JpaRepository<Mapping, String>, JpaSpecificationExecutor<Mapping> {

    /**
     * Returns the Mapping identified by the given name.
     * 
     * @param name
     * @return
     */
    Mapping findByName(String name);

    /**
     * Returns the list of mapping defined for the given model.
     * 
     * @param model
     * @return
     */
    List<Mapping> findByModel(Model model);

    /**
     * Get list of mapping names.
     * 
     * @param modelName
     * @return mapping names.
     */
    @Query("select u.name from #{#entityName} u where u.model.name = ?1")
    List<String> getListOfMappingNames(String modelName);

    /**
     * Get mappings based on model name
     * 
     * @param modelName
     * @return mappings
     */
    List<Mapping> findByModelName(String modelName);

    /**
     * gets the mapping by model name and having status as finalized
     * 
     * @param modelName
     * @param status
     * @return
     */
    List<Mapping> findByModelNameAndStatus(String modelName, String status);

    /**
     * Get list of mapping names.
     * 
     * @param modelName
     * @return mapping names.
     */
    @Query("select u.name from #{#entityName} u where u.model.id = ?1")
    List<String> getListOfMappingNamesById(String modelId);

    /**
     * This method will return list of Mappings details
     */
    @Query("select M.name,M.model.name,M.model.umgName from #{#entityName} M")
    List<String[]> findListOfMappingsForTidCopy();

    /**
     * gets the status of the mapping
     * 
     * @param tidName
     * @return
     */
    @Query("select M.status from #{#entityName} M where  M.name = ?1")
    String getMappingStatus(String tidName);

    @Override
    List<Mapping> findAll(Specification<Mapping> spec);

}
