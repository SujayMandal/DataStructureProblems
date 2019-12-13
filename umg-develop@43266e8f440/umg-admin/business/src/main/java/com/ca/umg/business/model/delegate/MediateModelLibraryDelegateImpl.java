package com.ca.umg.business.model.delegate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.CheckSumUtil;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.execution.bo.ModelExecutionEnvironmentBO;
import com.ca.umg.business.execution.entity.ModelExecutionEnvironment;
import com.ca.umg.business.model.bo.MediateModelLibraryBO;
import com.ca.umg.business.model.entity.MediateModelLibrary;
import com.ca.umg.business.model.info.MediateModelLibraryInfo;
import com.ca.umg.business.model.info.ModelExecutionEnvironmentInfo;
import com.ca.umg.business.util.AdminUtil;

/**
 * This class used to delegate the requests for create,get or delete the Mediate
 * model Librraies from DB
 * 
 * @author basanaga
 *
 */
@Component
public class MediateModelLibraryDelegateImpl extends AbstractDelegate implements MediateModelLibraryDelegate {

	private static final Logger LOGGER = LoggerFactory.getLogger(MediateModelLibraryDelegateImpl.class);

	@Inject
	private MediateModelLibraryBO mediateModelLibraryBO;

	@Inject
	private ModelExecutionEnvironmentBO modelExecutionEnvironmentBO;

	@Inject
	private UmgFileProxy umgFileProxy;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ca.umg.business.model.delegate.MediateModelLibraryDelegate#
	 * validateMediateLibChecksum(com.ca.umg.business.model.info.
	 * MediateModelLibraryInfo)
	 */
	@Override
	public void validateMediateLibChecksum(MediateModelLibraryInfo mediateModelLibraryInfo)
			throws BusinessException, SystemException {
		mediateModelLibraryBO.validateMediateLibraryBycheckSumAndtarName(mediateModelLibraryInfo.getChecksum(),
				mediateModelLibraryInfo.getTarName());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ca.umg.business.model.delegate.MediateModelLibraryDelegate#
	 * createMediateModelLibrary(com.ca.umg.business.model.info.
	 * MediateModelLibraryInfo)
	 */
	@Override

	public MediateModelLibraryInfo setMediateModelLibrary(MediateModelLibraryInfo mediateModelLibraryInfo)
			throws BusinessException, SystemException {
		MediateModelLibrary mediateModelLibrary = convert(mediateModelLibraryInfo, MediateModelLibrary.class);
		String[] version = mediateModelLibraryInfo.getVersion().split(BusinessConstants.CHAR_HYPHEN);
		String filePath = AdminUtil
				.getSanBasePath(umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE)))
				+ File.separator + systemParameterProvider.getParameter(SystemConstants.FILE_UPLOAD_TEMP_PATH);
		File tempFile = null;
		FileInputStream fis = null;
		try {
			long startTime = System.currentTimeMillis();
			LOGGER.debug("Started calculating checksum for the file :" + mediateModelLibrary.getTarName());
			MessageDigest md = MessageDigest.getInstance(BusinessConstants.RSHA256);
			tempFile = new File(filePath + File.separator + mediateModelLibrary.getTarName());
			fis = new FileInputStream(tempFile);
			byte[] dataBytes = new byte[1024 * 2];
			int nread = 0;
			while ((nread = fis.read(dataBytes)) != -1) { // NOPMD
				md.update(dataBytes, 0, nread);
			}
			byte[] mdbytes = md.digest();
			// convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mdbytes.length; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));// NOPMD
			}

			LOGGER.debug("checksum value for the tar :" + mediateModelLibrary.getTarName() + " is :" + sb.toString()
					+ " and time taken to calculate is :" + (System.currentTimeMillis() - startTime));
			if (!CheckSumUtil.validateRCheckSum(sb.toString(), mediateModelLibrary.getChecksum())) {
				if (tempFile.exists()) {
					tempFile.delete();
				}
				throw new BusinessException(BusinessExceptionCodes.BSE000059, new Object[] {});
			}
			mediateModelLibrary.setChecksum(sb.toString());
			mediateModelLibrary.setEncodingType(BusinessConstants.RSHA256);

			mediateModelLibrary.setModelExecEnvName(
					modelExecutionEnvironmentBO.getModelExecutionEnvironment(version[0], version[1]).getName());

		} catch (IOException | NoSuchAlgorithmException e) {// NOPMD
			LOGGER.error("Error occurred while inserting MediateModelLibrary. Exception is :", e);
			if (tempFile != null && tempFile.exists()) {
				tempFile.delete();
			}
			BusinessException.newBusinessException(BusinessExceptionCodes.BSE000140, new Object[] { e.getMessage() });
		} finally {
			IOUtils.closeQuietly(fis);
		}
		return convert(mediateModelLibrary, MediateModelLibraryInfo.class);
	}

	@Override
	@Transactional
	public void createMediateModelLibrary(MediateModelLibraryInfo mediateModelLibraryInfo) throws SystemException {
		mediateModelLibraryBO.createMediateModelLibrary(convert(mediateModelLibraryInfo, MediateModelLibrary.class));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ca.umg.business.model.delegate.MediateModelLibraryDelegate#
	 * getAllMediateModelLibraries(com.ca.umg.business.execution.
	 * entity.ModelExecutionEnvironment)
	 */
	@Override
	public List<MediateModelLibraryInfo> getAllMediateModelLibraries(
			ModelExecutionEnvironmentInfo modelExecutionEnvironment) {
		List<MediateModelLibrary> mediateModelLibraries = mediateModelLibraryBO.getAllMediateModelLiobraries(
				convert(modelExecutionEnvironment, ModelExecutionEnvironment.class).getName());
		List<MediateModelLibraryInfo> mediateModelLibraryInfos = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(mediateModelLibraries)) {
			for (MediateModelLibrary mediateModelLibrary : mediateModelLibraries) {
				mediateModelLibraryInfos.add(getMediateModelLibraryInfo(mediateModelLibrary));
			}
		}
		return mediateModelLibraryInfos;
	}

	private MediateModelLibraryInfo getMediateModelLibraryInfo(MediateModelLibrary mediateModelLibrary) {
		MediateModelLibraryInfo mediateModelLibraryInfo = convert(mediateModelLibrary, MediateModelLibraryInfo.class);
		mediateModelLibraryInfo.setCreatedDateTime(
				AdminUtil.getDateFormatMillisForEst(mediateModelLibrary.getCreatedDate().getMillis(), null));
		return mediateModelLibraryInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ca.umg.business.model.delegate.MediateModelLibraryDelegate#
	 * getMediateModelLibrray(java.lang.String)
	 */
	@Override
	public MediateModelLibrary getMediateModelLibrray(String id) {
		return mediateModelLibraryBO.getMediateModelLibrary(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ca.umg.business.model.delegate.MediateModelLibraryDelegate#
	 * deleteByNameAndchecksum(java.lang.String, java.lang.String)
	 */
	@Override
	public void deleteByNameAndchecksum(String tarName, String checksum) {
		mediateModelLibraryBO.deleteByNameAndchecksum(tarName, checksum);

	}

}
