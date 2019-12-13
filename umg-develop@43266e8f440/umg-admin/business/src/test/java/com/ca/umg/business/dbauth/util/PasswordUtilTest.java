package com.ca.umg.business.dbauth.util;

import static com.ca.umg.business.dbauth.util.PasswordUtil.bothPasswordsMatched;
import static com.ca.umg.business.dbauth.util.PasswordUtil.doesPasswordHaveCapitalLetter;
import static com.ca.umg.business.dbauth.util.PasswordUtil.doesPasswordHaveNumber;
import static com.ca.umg.business.dbauth.util.PasswordUtil.doesPasswordHaveSmallLetter;
import static com.ca.umg.business.dbauth.util.PasswordUtil.doesPasswordHaveSpecialLetter;
import static com.ca.umg.business.dbauth.util.PasswordUtil.isNewPasswordSameAsCurrentPassword;
import static com.ca.umg.business.dbauth.util.PasswordUtil.isPasswordLengthLessThanEight;
import static com.ca.umg.business.dbauth.util.PasswordUtil.isPasswordMatchedWithEncoded;
import static com.ca.umg.business.dbauth.util.PasswordUtil.isPasswordValid;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtilTest {

	@Test
	public void testIsPasswordLengthLessThanEightNullPassword() {
		final String kyPsswd = null;
		final boolean value = isPasswordLengthLessThanEight(kyPsswd);
		assertTrue(value);
	}

	@Test
	public void testIsPasswordLengthLessThanEightLessThanEigth() {
		final String kyPsswd = "1234567";
		final boolean value = isPasswordLengthLessThanEight(kyPsswd);
		assertTrue(value);
	}

	@Test
	public void testIsPasswordLengthLessThanEightEqualToEight() {
		final String kyPsswd = "12345678";
		final boolean value = isPasswordLengthLessThanEight(kyPsswd);
		assertFalse(value);
	}

	@Test
	public void testIsPasswordLengthLessThanEightMoreThanEigth() {
		final String kyPsswd = "123456789";
		final boolean value = isPasswordLengthLessThanEight(kyPsswd);
		assertFalse(value);
	}

	@Test
	public void testDoesPasswordHaveCapitalLetterWithOneCapitalLetter() {
		assertTrue(doesPasswordHaveCapitalLetter("A23456789"));
		assertTrue(doesPasswordHaveCapitalLetter("B23456789"));
		assertTrue(doesPasswordHaveCapitalLetter("2345C6789"));
		assertTrue(doesPasswordHaveCapitalLetter("2345C67Z9"));
		assertTrue(doesPasswordHaveCapitalLetter("2345C678Y"));
	}

	@Test
	public void testDoesPasswordHaveCapitalLetterWithoutCapitalLetter() {
		assertFalse(doesPasswordHaveCapitalLetter("a23456789"));
		assertFalse(doesPasswordHaveCapitalLetter("23456s789"));
		assertFalse(doesPasswordHaveCapitalLetter("a23456789z"));
	}

	@Test
	public void testDoesPasswordHaveSmallLetterWithOneSmallLetter() {
		assertTrue(doesPasswordHaveSmallLetter("a23456789"));
		assertTrue(doesPasswordHaveSmallLetter("234a56789"));
		assertTrue(doesPasswordHaveSmallLetter("a23456b789"));
		assertTrue(doesPasswordHaveSmallLetter("a23456789"));
		assertTrue(doesPasswordHaveSmallLetter("a234567z89"));
		assertTrue(doesPasswordHaveSmallLetter("a23456789z"));
	}

	@Test
	public void testDoesPasswordHaveSmallLetterWithoutSmallLetter() {
		assertFalse(doesPasswordHaveSmallLetter("A23456789"));
		assertFalse(doesPasswordHaveSmallLetter("23456789"));
		assertFalse(doesPasswordHaveSmallLetter("@23456789"));
	}

	@Test
	public void testDoesPasswordHaveNumberWithNumber() {
		assertTrue(doesPasswordHaveNumber("1aa"));
		assertTrue(doesPasswordHaveNumber("a2a"));
		assertTrue(doesPasswordHaveNumber("aa3"));
		assertTrue(doesPasswordHaveNumber("0aa"));
		assertTrue(doesPasswordHaveNumber("a4a"));
		assertTrue(doesPasswordHaveNumber("aa5"));
		assertTrue(doesPasswordHaveNumber("6aa"));
		assertTrue(doesPasswordHaveNumber("a7a"));
		assertTrue(doesPasswordHaveNumber("aa8"));
		assertTrue(doesPasswordHaveNumber("aa9"));
	}

	@Test
	public void testDoesPasswordHaveSmallLetterWithoutNumber() {
		assertFalse(doesPasswordHaveNumber("ABCDEFGH"));
		assertFalse(doesPasswordHaveNumber("abc"));
		assertFalse(doesPasswordHaveNumber("@!#"));
	}

	@Test
	public void testDoesPasswordHaveSpecialLetterWithSpecialLetter() {
		assertTrue(doesPasswordHaveSpecialLetter("a23456789@"));
		assertTrue(doesPasswordHaveSpecialLetter("a234567!89"));
		assertTrue(doesPasswordHaveSpecialLetter("a23#456789"));
		assertTrue(doesPasswordHaveSpecialLetter("a$23456789"));
		assertTrue(doesPasswordHaveSpecialLetter("a2%3456789"));
	}

	@Test
	public void testDoesPasswordHaveSmallLetterWithoutSpecialLetter() {
		assertFalse(doesPasswordHaveSpecialLetter("ABCDEFGH"));
	}

	@Test
	public void testBothPasswordsMatchedSamePasswords() {
		assertTrue(bothPasswordsMatched("abc", "abc"));
		assertTrue(bothPasswordsMatched("123a", "123a"));
	}

	@Test
	public void testBothPasswordsMatchedNotSamePasswords() {
		assertFalse(bothPasswordsMatched("abc", "ABC"));
		assertFalse(bothPasswordsMatched("123a", "123b"));
		assertFalse(bothPasswordsMatched("ab", "@#$$"));
	}

	@Test
	public void testIsPasswordMatchedWithEncodedMatched() {
		final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		final String plainTextPassword = "Welcome1";
		final String encodedPassword = "$2a$10$lvPYaNadHDPuUMTfGkx34uwVgrIZWPsr6acx70S7ZB8pGJwIU5ffW";
		assertTrue(isPasswordMatchedWithEncoded(plainTextPassword, encodedPassword, encoder));
	}

	@Test
	public void testIsPasswordMatchedWithEncodedNotMatched() {
		final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		final String plainTextPassword = "Welcome";
		final String encodedPassword = "$2a$10$lvPYaNadHDPuUMTfGkx34uwVgrIZWPsr6acx70S7ZB8pGJwIU5ffW";
		assertFalse(isPasswordMatchedWithEncoded(plainTextPassword, encodedPassword, encoder));
	}

	@Test
	public void testIsNewPasswordSameAsCurrentPasswordSame() {
		final String newPassword = "Welcome1";
		final String currentPassword = "$2a$10$lvPYaNadHDPuUMTfGkx34uwVgrIZWPsr6acx70S7ZB8pGJwIU5ffW";
		final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		assertTrue(isNewPasswordSameAsCurrentPassword(newPassword, currentPassword, encoder));
	}

	@Test
	public void testIsNewPasswordSameAsCurrentPasswordNotSame() {
		final String newPassword = "Welcome";
		final String currentPassword = "$2a$10$lvPYaNadHDPuUMTfGkx34uwVgrIZWPsr6acx70S7ZB8pGJwIU5ffW";
		final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		assertFalse(isNewPasswordSameAsCurrentPassword(newPassword, currentPassword, encoder));
	}

	@Test
	public void testIsPasswordValidForValidPassword() {
		assertTrue(isPasswordValid("Abc1!FGH"));
		assertTrue(isPasswordValid("jkhAbc1!FGH"));
		assertTrue(isPasswordValid("AAAAAA1!BB"));
		assertTrue(isPasswordValid("wewesddv1!sd"));
	}

	@Test
	public void testIsPasswordValidForNotPassword() {
		assertFalse(isPasswordValid("Abc1!"));
		assertFalse(isPasswordValid("AAAAAA!BB"));
		assertFalse(isPasswordValid("AAAAAA1BB"));
	}

}
