package com.ca.umg.business.model.bo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.model.dao.MediateModelLibraryDAO;
import com.ca.umg.business.model.dao.ModelLibraryDAO;
import com.ca.umg.business.model.entity.MediateModelLibrary;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.version.dao.VersionContainerDAO;

/**
 * This class used to implements the requests for create,get or delete the Mediate model Libraries from DB
 * 
 * @author basanaga
 *
 */
@Named
public class MediateModelLibraryBOImpl implements MediateModelLibraryBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediateModelLibraryBOImpl.class);

    @Inject
    private MediateModelLibraryDAO mediateModelLibraryDAO;

    @Inject
    private ModelLibraryDAO modelLibraryDAO;
    @Inject
    private VersionContainerDAO versionContainerDAO;

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.model.bo.MediateModelLibraryBO#validateMediateLibraryBycheckSumAndtarName(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void validateMediateLibraryBycheckSumAndtarName(String checksum, String tarName)
            throws BusinessException, SystemException {
        try {
            MediateModelLibrary mediateModelLibrary = mediateModelLibraryDAO.findByChecksumAndTarName(checksum, tarName);

            if (mediateModelLibrary != null) {
                throw new BusinessException(BusinessExceptionCodes.BSE000138, new Object[] { mediateModelLibrary.getTarName(),
                        mediateModelLibrary.getCreatedBy(), AdminUtil.getDateFormatMillisForEst(mediateModelLibrary.getCreatedDate().getMillis(), null)});
            } else {
                List<ModelLibrary> modelLibraries = modelLibraryDAO
                        .findByJarNameContainingIgnoreCaseAndChecksumOrderByCreatedDateDesc(tarName, checksum);
                if (CollectionUtils.isNotEmpty(modelLibraries)) {
                    String versionNumber = versionContainerDAO.getVersionNumberForModelLibId(modelLibraries.get(0).getId());                                       
                    throw new BusinessException(BusinessExceptionCodes.BSE000139,
                            new Object[] { modelLibraries.get(0).getName(),versionNumber });
                }
            }
        } catch (DataAccessException ex) { // NOPMD
            LOGGER.error("Exception occurred while checking the Model Library for the file :" + tarName + ". Exception is :", ex);
            SystemException.newSystemException(BusinessExceptionCodes.BSE000137, new Object[] { ex.getMessage() });

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.model.bo.MediateModelLibraryBO#createMediateModelLibrary(com.ca.umg.business.model.entity.
     * MediateModelLibrary)
     */
    @Override
    public void createMediateModelLibrary(MediateModelLibrary mediateModelLibrary) throws SystemException {
        try {
            mediateModelLibraryDAO.save(mediateModelLibrary);
        } catch (DataAccessException ex) {// NOPMD
            LOGGER.error("Exception occurred while inserting Mediate the Model Library into DB. Exception is :", ex);
            SystemException.newSystemException(BusinessExceptionCodes.BSE000140, new Object[] { ex.getMessage() });

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.model.bo.MediateModelLibraryBO#getAllMediateModelLiobraries(com.ca.umg.business.execution.entity.
     * ModelExecutionEnvironment)
     */
    @Override
    public List<MediateModelLibrary> getAllMediateModelLiobraries(String modelExecEnvName) {
        return mediateModelLibraryDAO.findByModelExecEnvName(modelExecEnvName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.model.bo.MediateModelLibraryBO#getMediateModelLibrary(java.lang.String)
     */
    @Override
    public MediateModelLibrary getMediateModelLibrary(String id) {
        return mediateModelLibraryDAO.findOne(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.model.bo.MediateModelLibraryBO#deleteByNameAndchecksum(java.lang.String, java.lang.String)
     */
    @Override
    public void deleteByNameAndchecksum(String tarName, String checksum) {
        mediateModelLibraryDAO.deleteByNameAndchecksum(tarName, checksum);

    }

}
