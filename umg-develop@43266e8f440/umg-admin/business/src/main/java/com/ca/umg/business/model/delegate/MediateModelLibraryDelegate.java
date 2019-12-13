package com.ca.umg.business.model.delegate;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.model.entity.MediateModelLibrary;
import com.ca.umg.business.model.info.MediateModelLibraryInfo;
import com.ca.umg.business.model.info.ModelExecutionEnvironmentInfo;

/**
 * This class used to delegate the requests for create,get or delete the Mediate model Libraries from DB
 * 
 * @author basanaga
 *
 */
public interface MediateModelLibraryDelegate {
    
    /**
     * This method used to validate mediate Library checksum
     * 
     * @param mediateModelLibraryInfo
     * @throws BusinessException
     * @throws SystemException
     */
    void validateMediateLibChecksum(MediateModelLibraryInfo mediateModelLibraryInfo) throws BusinessException, SystemException;
    
    /**
     * This method used to create mediate model library
     * 
     * @param mediateModelLibraryInfo
     * @throws BusinessException
     * @throws SystemException
     */
    public void createMediateModelLibrary(MediateModelLibraryInfo mediateModelLibraryInfo)
            throws BusinessException, SystemException;
    
    /**
     * This method used to get all the mediate model libraries
     * 
     * @param modelExecutionEnvironment
     * @return
     */
    List<MediateModelLibraryInfo> getAllMediateModelLibraries(ModelExecutionEnvironmentInfo modelExecutionEnvironment);

    /**
     * This method used to get mediate model library based on id
     * 
     * @param id
     * @return
     */
    MediateModelLibrary getMediateModelLibrray(String id);

    /**
     * This method used to get mediate model librray based on tarName and checksum
     * 
     * @param tarName
     * @param checksum
     */
    void deleteByNameAndchecksum(String tarName, String checksum);

    public MediateModelLibraryInfo setMediateModelLibrary(MediateModelLibraryInfo mediateModelLibraryInfo)
            throws BusinessException,
 SystemException;


}
