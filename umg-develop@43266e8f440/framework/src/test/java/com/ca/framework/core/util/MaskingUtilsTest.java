package com.ca.framework.core.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class MaskingUtilsTest {

    @Test
    public void maskCreditCardNumbers() {
        assertThat(MaskingUtils.mask("1234-1234-1234-1234", "xxxx-xxxx-xxxx-####"), is("xxxx-xxxx-xxxx-1234"));
        assertThat(MaskingUtils.mask("1234-1234-1234-1234", "##xx-xxxx-xxxx-xx##"), is("12xx-xxxx-xxxx-xx34"));
    }

    @Test
    public void maskCreditCardNumbersOverrideMask() {
        assertThat(MaskingUtils.mask("1234-1234-1234-1234", "****-****-****-####", '*'), is("****-****-****-1234"));
        assertThat(MaskingUtils.mask("1234-1234-1234-1234", "##**-****-****-**##", '*'), is("12**-****-****-**34"));
    }

    @Test(expected = IllegalStateException.class)
    public void whenLenghtDoesNotMatch() {
        assertThat(MaskingUtils.mask("1234-1234-1234-1234", "****-****-****-###"), is("****-****-****-1234"));
    }

    @Test(expected = IllegalStateException.class)
    public void whenLenghtDoesNotMatchAndOverriddenMaskChar() {
        assertThat(MaskingUtils.mask("1234-1234-1234-1234", "##**-****-****-**#", '*'), is("12**-****-****-**34"));
    }
}
