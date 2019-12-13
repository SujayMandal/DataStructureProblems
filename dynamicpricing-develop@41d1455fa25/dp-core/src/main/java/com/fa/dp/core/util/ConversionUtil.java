package com.fa.dp.core.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fa.dp.core.exception.SystemException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConversionUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConversionUtil.class);

	private ConversionUtil() {

	}

	/**
	 * Converts the given json string to the object to given class.
	 * 
	 * @param jsonString
	 * @param clazz
	 * @return
	 * @throws SystemException
	 */
	public static <T> T convertJson(String jsonString, Class<T> clazz) throws SystemException {
		ObjectMapper objectMapper = new ObjectMapper();
		T resultObject = null;
		try {
			resultObject = objectMapper.readValue(jsonString, clazz);
		} catch (IOException e) {
			LOGGER.error("An error occurred while converting json {} to object {}.", jsonString, clazz);
			SystemException.newSystemException(RAClientConstants.CHAR_EMPTY,
					new Object[] { String.format("An error occurred while converting json %s.", jsonString) });
		}
		return resultObject;
	}

	/**
	 * This method would convert any object to JSON String
	 * 
	 * @param data
	 * @return
	 * @throws SystemException
	 */
	public static <T> String convertToJsonString(T data) throws SystemException {
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonStr = null;
		try {
			if (data != null) {
				jsonStr = objectMapper.writeValueAsString(data);
			}
		} catch (IOException e) {
			SystemException.newSystemException(RAClientConstants.CHAR_EMPTY, new Object[] {
					String.format("An error occurred while converting %s to json string.", data.getClass()) });
		}
		return jsonStr;
	}

	public static String convertDate(Date date, String targetFormat) {
		String formattedDate = null;
		if (date != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(targetFormat);
			formattedDate = dateFormat.format(date);
		}
		return formattedDate;
	}

}
