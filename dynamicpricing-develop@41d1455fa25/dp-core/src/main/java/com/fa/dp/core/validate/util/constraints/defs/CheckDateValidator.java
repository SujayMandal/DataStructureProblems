package com.fa.dp.core.validate.util.constraints.defs;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.fa.dp.core.util.RAClientConstants;
import com.fa.dp.core.validate.util.constraints.CheckDateFormat;

public class CheckDateValidator implements ConstraintValidator<CheckDateFormat, String> {

	private String pattern;
	
	private String regexp;

	@Override
	public void initialize(CheckDateFormat constraintAnnotation) {
		this.pattern = constraintAnnotation.pattern();
		this.regexp = constraintAnnotation.regexp();
	}

	@Override
	public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
		boolean status = false; 
		if (object != null) {
			try {
				new SimpleDateFormat(pattern).parse(object);
				status = true;
				if(regexp != null && !RAClientConstants.CHAR_EMPTY.equals(regexp)) {
					Pattern p = Pattern.compile(regexp);
					Matcher matcher = p.matcher(object);
					status = matcher.matches();
				}
			} catch (Exception e) {
				status = false;
			}
		}
		
		return status;
	}
}