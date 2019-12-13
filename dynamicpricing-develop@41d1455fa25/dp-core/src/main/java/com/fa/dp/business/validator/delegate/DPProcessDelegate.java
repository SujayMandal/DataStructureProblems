package com.fa.dp.business.validator.delegate;

import java.io.IOException;
import java.util.List;

import com.fa.dp.business.validation.input.info.DPFileProcessStatusInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.week0.entity.DynamicPricingFilePrcsStatus;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.core.exception.SystemException;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author yogeshku
 * 
 * Interface for validation and file upload service
 *
 */
public interface DPProcessDelegate {
	/**
	 * File validation operation
	 * 
	 * @param file
	 * @param generatedFileName
	 * @param errorMessages
	 * @return
	 * @throws SystemException
	 * @throws IOException
	 */
	DPProcessParamEntryInfo validateFile(MultipartFile file, String generatedFileName, List<String> errorMessages)
			throws SystemException, IOException;

	/**
	 * @param file
	 * @param generatedFileName
	 * 
	 * File upload operation
	 * @throws IOException
	 */
	void createFile(MultipartFile file, String generatedFileName) throws IOException;

	/**
	 * @param fileName
	 * @return
	 * 
	 * Generating new file name for system
	 */
	String generateFileName(String fileName);

	DPProcessWeekNParamEntryInfo validateWeeknFile(
            MultipartFile file,
            String generatedFileName,
            List<String> errorMessages)
			throws SystemException, IOException;
	
	void saveDpPrcsStatus(DPFileProcessStatusInfo dpFilePrcsStatusInfo);

	DynamicPricingFilePrcsStatus checkForPrcsStatus(String fileStatus);
	
	void saveFileEntriesInDB(DPProcessParamEntryInfo dpParamEntry);
	
	void saveWeekNFileEntriesInDB(DPProcessWeekNParamEntryInfo dpWeeknParamEntry);
}
