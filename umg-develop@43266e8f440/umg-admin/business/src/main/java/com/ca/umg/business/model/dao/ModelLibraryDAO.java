/**
 * 
 */
package com.ca.umg.business.model.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ca.umg.business.model.entity.ModelLibrary;

/**
 * @author kamathan
 * 
 */
public interface ModelLibraryDAO extends JpaRepository<ModelLibrary, String>, JpaSpecificationExecutor<ModelLibrary> {

    ModelLibrary findByUmgName(String umgName);

    /**
     * Get all library names.
     * 
     * @return library names.
     */
    @Query("select distinct(u.name) from #{#entityName} u")
    List<String> getAllLibraryNames();

    /**
     * Get derived model library names.
     * 
     * @param libraryName
     * @return list if derivedModelLibraryNames.
     */
    @Query("select u.umgName from #{#entityName} u where u.name = ?1")
    List<String> getListOfDerivedModelLibraryNames(String libraryName);

    /**
     * Get Model Library values.
     * 
     * @param name
     * @return
     */
    List<ModelLibrary> findByName(String name);

    List<ModelLibrary> findByJarNameContainingIgnoreCaseAndChecksumOrderByCreatedDateDesc(String jarName, String checkSum);

    List<ModelLibrary> findByChecksum(String checksum);

    @Override
    Page<ModelLibrary> findAll(Specification<ModelLibrary> spec, Pageable pageable);

    @Query("SELECT M FROM ModelLibrary M where M.name = :modelLibName")
    Page<ModelLibrary> findAllModelLibByName(@Param("modelLibName") String modelLibName, Pageable pageRequest);

}
