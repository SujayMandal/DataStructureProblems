/**
 * 
 */
package com.ca.umg.business.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.model.entity.ModelLibraryExecPackageMapping;

/**
 * @author kamathan
 *
 */
public interface ModelLibraryExecPackageMappingDAO extends JpaRepository<ModelLibraryExecPackageMapping, String> {

    @Modifying
    @Query("DELETE FROM ModelLibraryExecPackageMapping MLEPM where MLEPM.modelLibrary = ?1")
    @Transactional
    void deleteByModelLibrary(ModelLibrary modelLibrary);



}
