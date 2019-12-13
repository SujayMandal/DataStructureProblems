package com.ca.umg.business.transaction.util;

import static com.ca.umg.business.constants.BusinessConstants.EXTN_XLS;
import static com.ca.umg.business.constants.BusinessConstants.NUMBER_ONE;

import java.io.IOException;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.transaction.info.TransactionFilter;
import com.ca.umg.business.util.AdminUtil;

/**
 * @author basanaga
 * 
 */
public final class TransactionUtil {

	public static final String QUOTE = "'";
	public static final String CLOSING_BRACE = ")";
	private static final String AND_OPERTAOR = " AND ";
	private static final String OR_OPERTAOR = " OR ";
    private static final String ERROR = "Error";
	private static final String ANY = "Any";

	private TransactionUtil() {
		throw new UnsupportedOperationException();
	}

	/**
	 * This method used for adding Data to ZIpOutputStream
	 * 
	 * @param fileName
	 * @param data
	 * @param zos
	 * @throws IOException
	 * @throws SystemException
	 */
	public static void addToZipFile(final String fileName, final byte[] data, final ZipOutputStream zos) throws IOException, SystemException {

		try {
            if (fileName.endsWith(BusinessConstants.EXTN_JSON)) {
                zos.putNextEntry(new ZipEntry(fileName));
            } else {
                zos.putNextEntry(new ZipEntry(fileName + BusinessConstants.EXTN_TXT));
            }
		} catch (IOException io) {
			putEntry(fileName, zos, NUMBER_ONE);
		}
		zos.write(ConversionUtil.convertToFormattedJsonStringByteArray(data));
		zos.closeEntry();
	}

	/**
	 * This method used for adding content to the Zip Entry If the same filename is adding to the ZipEntry, The filename modified
	 * to filename-1 increment by 1 .txt file
	 * 
	 * @param fileName
	 * @param zos
	 * @param version
	 * @throws IOException
	 */
	private static void putEntry(final String fileName, final ZipOutputStream zos,final int version) throws IOException {
		try {
			zos.putNextEntry(new ZipEntry(fileName + BusinessConstants.HYPHEN + version + BusinessConstants.EXTN_TXT));
		} catch (IOException ie) {
			putEntry(fileName, zos, version + 1);
		}


	}
	
	 public static void addToZipFileForBatch(final String fileName, final byte[] data, final ZipOutputStream zos) throws IOException, SystemException {

			try {
				zos.putNextEntry(new ZipEntry(fileName));
			} catch (IOException io) {
				putEntryForBatch(fileName, zos, NUMBER_ONE);
			}
			zos.write(ConversionUtil.convertToFormattedJsonStringByteArray(data));
			if(zos != null){
			zos.closeEntry();
			}
		}
	    
	    private static void putEntryForBatch(final String fileName, final ZipOutputStream zos,final int version) throws IOException {
			try {
				String fileNameWithoutExtn = StringUtils.substringBeforeLast(fileName, BusinessConstants.DOT);

				zos.putNextEntry(new ZipEntry(fileNameWithoutExtn + BusinessConstants.HYPHEN + version + EXTN_XLS));
			} catch (IOException ie) {
				putEntry(fileName, zos, version + 1);
			}
		}

	/**
	 * This method is used to generate the file name
	 * 
	 * @param txnInfo
	 * @param type
	 * @return
	 */
	public static String getFileName(final String clientTxnId, final Long runAsofDate, final String type) {
		StringBuilder fileNamePreFix = new StringBuilder();
		fileNamePreFix.append(clientTxnId).append(BusinessConstants.HYPHEN)
		.append(AdminUtil.getDateFormatMillis(runAsofDate, BusinessConstants.TXN_DOWNLOAD_DATE))
		.append(BusinessConstants.HYPHEN).append(type);

		return fileNamePreFix.toString();

	}

    /**
     * This method is used to generate the file name for Error
     * 
     * @param txnInfo
     * @param type
     * @return
     */
    public static String getErrorFileName(final String clientTxnId, final Long runAsofDate, final String type) {
        StringBuilder fileNamePreFix = new StringBuilder();
        fileNamePreFix.append(clientTxnId).append(BusinessConstants.HYPHEN)
                .append(AdminUtil.getDateFormatMillis(runAsofDate, BusinessConstants.TXN_DOWNLOAD_DATE))
                .append(BusinessConstants.HYPHEN).append(ERROR)
                .append(BusinessConstants.HYPHEN).append(type);

        return fileNamePreFix.toString();

    }

	/**
	 * This method prepares the search criteria to get the Transactions : UMG-1519
	 * 
	 * @param transactionFilter
	 * @return
	 */
	//method not used anywhere
	public static StringBuffer getSearchCriteria(final TransactionFilter transactionFilter,final SystemParameterProvider systemParameterProvider) {
		StringBuffer serachCriteria = new StringBuffer();
		checkLibraryAndVersion(transactionFilter, serachCriteria);
		checkRunAsOfDate(transactionFilter, serachCriteria);

		if (StringUtils.isNotBlank(transactionFilter.getClientTransactionID())) {
			serachCriteria.append(AND_OPERTAOR).append("LOWER(TXN.CLIENT_TRANSACTION_ID) like '")
			.append(AdminUtil.getLikePattern(transactionFilter.getClientTransactionID())).append(QUOTE);
		}
		if (transactionFilter.getMajorVersion() != null) {
			serachCriteria.append(AND_OPERTAOR).append("TXN.MAJOR_VERSION = ").append(transactionFilter.getMajorVersion());
		}
		if (transactionFilter.getMinorVersion() != null) {
			serachCriteria.append(AND_OPERTAOR).append("TXN.MINOR_VERSION = ").append(transactionFilter.getMinorVersion());
		}
		//TODO commented this as method is not used anywhere for umg-4200 
		//need to change according to new filter object if this method is used
		//serachCriteria.append(AND_OPERTAOR).append("TXN.IS_TEST = ").append(transactionFilter.isTestTxn());
		String errorCodePattern = getErrorCodePattern(transactionFilter,systemParameterProvider);
		if (StringUtils.isNotBlank(errorCodePattern) && !StringUtils.equals(ANY, errorCodePattern)) {
			serachCriteria.append(AND_OPERTAOR).append("LOWER(TXN.ERROR_CODE) LIKE '")
			.append(AdminUtil.getLikePattern(errorCodePattern.toLowerCase(Locale.getDefault())))
			.append(QUOTE);
		}
		String errorDescription = transactionFilter.getErrorDescription();
		if (StringUtils.isNoneBlank(errorDescription) && !StringUtils.equals(ANY, errorDescription)) {
			serachCriteria.append(AND_OPERTAOR).append("(LOWER(TXN.ERROR_DESCRIPTION) LIKE '")
			.append(AdminUtil.getLikePattern(errorDescription.toLowerCase(Locale.getDefault()))).append(QUOTE)
			.append(OR_OPERTAOR).append("LOWER(TXN.ERROR_CODE) LIKE '")
			.append(AdminUtil.getLikePattern(errorDescription.toLowerCase(Locale.getDefault()))).append(QUOTE)
			.append(CLOSING_BRACE);

		}

		return serachCriteria;
	}

	/**
	 * This method adds the run as of date to the serach criteria if start date and end date are not null : UMG-1519
	 * 
	 * @param transactionFilter
	 * @param serachCriteria
	 */
	private static void checkRunAsOfDate(final TransactionFilter transactionFilter, final StringBuffer serachCriteria) {
		Long startDate = transactionFilter.getRunAsOfDateFrom();
		Long endDate = transactionFilter.getRunAsOfDateTo();

		if (startDate != null && endDate != null && endDate > startDate) {
			serachCriteria.append(AND_OPERTAOR).append("(TXN.RUN_AS_OF_DATE BETWEEN ").append(startDate).append(AND_OPERTAOR)
			.append(endDate).append(CLOSING_BRACE);
		}
		// The default implementation is that the To_Date is calculated
		// for the current DateTime and not for future DateTime.
		else if (endDate == null && startDate != null && startDate < System.currentTimeMillis()) {
			serachCriteria.append(AND_OPERTAOR).append("(TXN.RUN_AS_OF_DATE BETWEEN ").append(startDate).append(AND_OPERTAOR)
			.append(System.currentTimeMillis()).append(CLOSING_BRACE);
		}
	}

	/**
	 * This method adds the Library name and version name to the search criteria if these are not null or empty : UMG-1519
	 * 
	 * @param transactionFilter
	 * @param serachCriteria
	 */
	private static void checkLibraryAndVersion(final TransactionFilter transactionFilter, final StringBuffer serachCriteria) {
		if (StringUtils.isNotBlank(transactionFilter.getLibraryName())
				&& !StringUtils.endsWithIgnoreCase(ANY, transactionFilter.getLibraryName())) {
			serachCriteria.append(AND_OPERTAOR).append("TXN.LIBRARY_NAME = '").append(transactionFilter.getLibraryName())
			.append(QUOTE);

		}
		if (StringUtils.isNotBlank(transactionFilter.getTenantModelName())
				&& !StringUtils.equalsIgnoreCase(ANY, transactionFilter.getTenantModelName())) {
			serachCriteria.append(AND_OPERTAOR).append("TXN.VERSION_NAME = '").append(transactionFilter.getTenantModelName())
			.append(QUOTE);

		}
	}

	/**
	 * This method used to error code pattern from the properties for the error type : UMG-1519
	 * 
	 * @param transactionFilter
	 * @return
	 */
	public static String getErrorCodePattern(final TransactionFilter transactionFilter,final SystemParameterProvider systemParameterProvider) {
		/**
		 * Search Error code pattern
		 */
		String errorCodePattern = StringUtils.EMPTY;
		if (StringUtils.isNotBlank(transactionFilter.getErrorType())) {
			if (transactionFilter.getErrorType().equalsIgnoreCase("validation")) {
				errorCodePattern = systemParameterProvider.getParameter(SystemConstants.VALIDATION_ERROR_CODE_PATTERN);
			} else if (transactionFilter.getErrorType().equalsIgnoreCase("systemException")) {
				errorCodePattern = systemParameterProvider.getParameter(SystemConstants.SYSTEM_EXCEPTION_ERROR_CODE_PATTERN);
			}
			if (transactionFilter.getErrorType().equalsIgnoreCase("modelException")) {
				errorCodePattern = systemParameterProvider.getParameter(SystemConstants.MODEL_EXCEPTION_ERROR_CODE_PATTERN);
			}
		}
		return errorCodePattern;
	}

	public static void addWorkbookToZipFile(final String fileName, final byte[] data, final ZipOutputStream zos) throws IOException, SystemException {

		try {
			zos.putNextEntry(new ZipEntry(fileName + EXTN_XLS));
		} catch (IOException io) {
			putEntry(fileName, zos, NUMBER_ONE);
		}
		zos.write(data);
		if(zos != null){
		zos.closeEntry();
		}
	}
}
