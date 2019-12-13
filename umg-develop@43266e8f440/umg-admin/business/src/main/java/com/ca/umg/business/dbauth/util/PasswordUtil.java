package com.ca.umg.business.dbauth.util;

import static java.util.regex.Pattern.compile;

import java.util.regex.Pattern;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class PasswordUtil {

	private static final int MIN_PASSWORD_LENGTH = 8;
	private static final Pattern REG_CAP_LETTER = compile("[A-Z]");
	private static final Pattern REG_SMALL_LETTER = compile("[a-z]");
	private static final Pattern REG_NUMERIC = compile("\\d+");
	private static final Pattern REG_SPE_CHAR = compile("[!@#$%]");

	private PasswordUtil() {

	}

	public static boolean isPasswordValid(final String password) {
		return !isPasswordLengthLessThanEight(password) && //
				(doesPasswordHaveCapitalLetter(password) || doesPasswordHaveSmallLetter(password)) && //
				doesPasswordHaveNumber(password) && //
				doesPasswordHaveSpecialLetter(password);
	}

	public static boolean isPasswordLengthLessThanEight(final String password) {
		boolean value = true;
		if (password != null) {
			final int passwordLength = password.trim().length();
			value = passwordLength < MIN_PASSWORD_LENGTH;
		}

		return value;
	}

	public static boolean doesPasswordHaveCapitalLetter(final String password) {
		boolean value = false;
		if (password != null) {
			value = REG_CAP_LETTER.matcher(password).find();
		}

		return value;
	}

	public static boolean doesPasswordHaveSmallLetter(final String password) {
		boolean value = false;
		if (password != null) {
			value = REG_SMALL_LETTER.matcher(password).find();
		}

		return value;
	}

	public static boolean doesPasswordHaveNumber(final String password) {
		boolean value = false;
		if (password != null) {
			value = REG_NUMERIC.matcher(password).find();
		}

		return value;
	}

	public static boolean doesPasswordHaveSpecialLetter(final String password) {
		boolean value = false;
		if (password != null) {
			value = REG_SPE_CHAR.matcher(password).find();
		}

		return value;
	}

	public static boolean bothPasswordsMatched(final String newPassword, final String confirmPassword) {
		return newPassword.equals(confirmPassword);
	}

	public static boolean isPasswordMatchedWithEncoded(final String plainTextPassword, final String encodedPassword, final BCryptPasswordEncoder encoder) {
		return encoder.matches(plainTextPassword, encodedPassword);
	}

	public static boolean isNewPasswordSameAsCurrentPassword(final String newPassword, final String currentPassword, final BCryptPasswordEncoder encoder) {
		return isPasswordMatchedWithEncoded(newPassword, currentPassword, encoder);
	}
}