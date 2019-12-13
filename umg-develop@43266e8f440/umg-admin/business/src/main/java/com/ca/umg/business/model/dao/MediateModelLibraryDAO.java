package com.ca.umg.business.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ca.umg.business.model.entity.MediateModelLibrary;

/**
 * This is JPA Repository for the entity MediateModelLibrary
 * 
 * @author basanaga
 *
 */
public interface MediateModelLibraryDAO extends JpaRepository<MediateModelLibrary, String>{
    
    MediateModelLibrary findByChecksumAndTarName(String checksum, String tarName);

    List<MediateModelLibrary> findByModelExecEnvName(String modelExecEnvName);

    @Modifying
    @Query("DELETE FROM MediateModelLibrary tempLib where tempLib.tarName = ?1 AND tempLib.checksum = ?2 ")
    @Transactional
    void deleteByNameAndchecksum(String tarName, String checksum);
   
}
