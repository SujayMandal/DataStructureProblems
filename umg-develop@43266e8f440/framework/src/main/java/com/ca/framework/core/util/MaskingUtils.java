package com.ca.framework.core.util;


public final class MaskingUtils {

    private static final char CHAR_REPRESENTING_NO_MASK = '#';

    private static final char DEFAULT_MASK_CHAR = 'x';

    private MaskingUtils() {
    }

    /**
     * Utility method for masking a text with mask character. <br>
     * This method uses a default mask, character : 'x'. <br>
     * Characters which should not be masked needs to be represented by : '#' <br>
     * 
     * ex: mask("1234-1234-1234-1234", "xxxx-xxxx-xxxx-####") -> "xxxx-xxxx-xxxx-1234" <br> 
     * mask("1234-1234-1234-1234", "##xx-xxxx-xxxx-xx##") -> "12xx-xxxx-xxxx-xx34" <br>
     * 
     * @param text to be masked
     * @param regex for masking
     * @return masked text
     * @throws IllegalStateException if text length and regex length does not match
     */
    public static String mask(String text, String regex) {
        return mask(text, regex, DEFAULT_MASK_CHAR);
    }

    /**
     * Utility method for masking a text with mask character. <br>
     * Characters which should not be masked needs to be represented by : '#' <br>
     * 
     * ex: mask("1234-1234-1234-1234", "****-****-****-####", '*') -> "****-****-****-1234" <br>
     * mask("1234-1234-1234-1234", "##**-****-****-**##", '*') -> "12**-****-****-**34" <br>
     * 
     * @param text to be masked
     * @param regex for masking
     * @param mask character
     * @return masked text
     * @throws IllegalStateException if text length and regex length does not match
     */
    public static String mask(String text, String regex, char mask) {
        if (text.length() != regex.length()) {
            throw new IllegalStateException("text length and regex length should be equal");
        }
        StringBuilder maskedValue = new StringBuilder();
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            if (c == CHAR_REPRESENTING_NO_MASK) {
                maskedValue.append(text.charAt(i));
            } else if (c == DEFAULT_MASK_CHAR) {
                maskedValue.append(c);
            } else {
                maskedValue.append(c);
            }
        }
        return maskedValue.toString();
    }
}
