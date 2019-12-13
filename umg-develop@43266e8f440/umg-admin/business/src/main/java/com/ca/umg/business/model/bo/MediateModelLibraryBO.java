package com.ca.umg.business.model.bo;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.model.entity.MediateModelLibrary;

/**
 * @author basanaga
 *
 */
public interface MediateModelLibraryBO {    
    
    /**
     * This method used to validate mediate Library checksum
     * 
     * @param checksum
     * @param tarName
     * @throws BusinessException
     * @throws SystemException
     */
    void validateMediateLibraryBycheckSumAndtarName(String checksum, String tarName) throws BusinessException, SystemException;

    /**
     * This method used to create mediate model library
     * 
     * @param mediateModelLibrary
     * @throws SystemException
     */
    void createMediateModelLibrary(MediateModelLibrary mediateModelLibrary) throws SystemException;
    
    /**
     * This method used to get all the mediate model libraries
     * 
     * @param modelExecutionEnvironment
     * @return
     */
    List<MediateModelLibrary> getAllMediateModelLiobraries(String modelExecEnvName);

    /**
     * This method used to get mediate model library based on id
     * 
     * @param id
     * @return
     */
    MediateModelLibrary getMediateModelLibrary(String id);

    /**
     * This method used to get mediate model librray based on tarName and checksum
     * 
     * @param tarName
     * @param checksum
     */
    void deleteByNameAndchecksum(String tarName, String checksum);

}
