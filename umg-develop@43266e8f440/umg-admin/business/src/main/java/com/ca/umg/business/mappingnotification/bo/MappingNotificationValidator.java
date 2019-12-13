package com.ca.umg.business.mappingnotification.bo;

import com.ca.framework.core.constants.PoolConstants;
import com.ca.umg.business.mappingnotification.entity.MappingNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
@SuppressWarnings("PMD")
public class MappingNotificationValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(MappingNotificationValidator.class);

	private final static String EMAIL_REG_EXP = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

	private final static Pattern EMAIL_PATTERN = compile(EMAIL_REG_EXP);

	private final static  String SPLITS_PATTERN = "[;:,]";

	private final  static String NOT_VALID_EMAILID = "Email id invalid";

	private final  static String TO_ADDRESS_IS_EMPTY = "To email id is mandatory";

	private final  static String EVENT_NAME_IS_EMPTY = "Event Name Is Empty";



	public static List<String> validateCreateMapping(final MappingNotification md){
		LOGGER.info("Validating fileds for create mapping");
		final List<String> errorList = new ArrayList<String>();
		if(isStringEmptyOrNull(md.getToAddress())){
			errorList.add(TO_ADDRESS_IS_EMPTY);
		}

		if(errorList.isEmpty() && isStringEmptyOrNull(md.getNotificationEventId())){
			errorList.add(EVENT_NAME_IS_EMPTY);
		}

		if(errorList.isEmpty()) {
			validatedEmailId(md.getToAddress(), errorList);
		}

		if(errorList.isEmpty()  && !isStringEmptyOrNull( md.getCcAddress())){
			validatedEmailId(md.getCcAddress(), errorList);
		}

		return errorList;
	}

	private static void validatedEmailId(final String emailAddress, final List<String> errorList) {
		LOGGER.info("Validating email id" + emailAddress);
		final String[] splits =emailAddress.split(SPLITS_PATTERN);
		for (int i =0 ; i <splits.length ; i++){
			validateEmailId(errorList , splits[i]);
		}

	}

	public static List<String> validateUpdateMapping(final MappingNotification md) {
		final List<String> errorList = new ArrayList<String>();


		if(isStringEmptyOrNull(md.getToAddress())){
			errorList.add(TO_ADDRESS_IS_EMPTY);
		}


		if(errorList.isEmpty() ){
			validatedEmailId(md.getToAddress(), errorList);
		}

		if(errorList.isEmpty() && !isStringEmptyOrNull( md.getCcAddress())){
			validatedEmailId(md.getCcAddress(), errorList);
		}

		return errorList;
	}

	//move this to some util method

	private static void validateEmailId(final List<String> errorList , final String emailId){
		final Matcher m = EMAIL_PATTERN.matcher(emailId);

		if(!m.matches()){
			LOGGER.error(NOT_VALID_EMAILID+ emailId);
			errorList.add(NOT_VALID_EMAILID);
		}
	}

	private static boolean isStringEmptyOrNull(final String str){
		LOGGER.info("Validating Field for empty or null  " + str);
		boolean flag =  true;
		if( str != null && !str.isEmpty()){
			flag = false;
		}

		return flag;
	}


	public static void main(String dd[]){
		Set<String> s = new TreeSet<String>();
		String a = "aaaa_1.0";
		String a1 = "aaaa_1.2";
		String model = a.substring(0,a.indexOf(PoolConstants.MODEL_SEPERATOR));
		String model1 = a1.substring(0,a1.indexOf(PoolConstants.MODEL_SEPERATOR));
		s.add(model);
		s.add(model1);
		Iterator<String> i = s.iterator();
		while(i.hasNext()){
			System.out.println(i.next());
		}
		List<String> list = new ArrayList<String>();
		MappingNotificationValidator.validateEmailId(list, "anand.kumar@altisource.com");
	}
}
