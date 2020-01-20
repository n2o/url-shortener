package de.hhu.propra.link.util;

import org.junit.jupiter.api.Test;

import static de.hhu.propra.link.util.StringUtil.isLowerCaseEnglishVowel;
import static de.hhu.propra.link.util.StringUtil.unvowelize;
import static org.assertj.core.api.Assertions.assertThat;

class StringUtilTest {

    @Test
    void testVowelLower() {
        char testChar = 'a';
        boolean result = isLowerCaseEnglishVowel(testChar);
        assertThat(result).isTrue();
    }

    @Test
    void testVowelUpper() {
        char testChar = 'A';
        boolean result = isLowerCaseEnglishVowel(testChar);
        assertThat(result).isFalse();
    }

    @Test
    void testVowelLowerWithAccent() {
        char testChar = 'á';
        boolean result = isLowerCaseEnglishVowel(testChar);
        assertThat(result).isFalse();
    }

    @Test
    void testVowelUpperWithAccent() {
        char testChar = 'Á';
        boolean result = isLowerCaseEnglishVowel(testChar);
        assertThat(result).isFalse();
    }

    @Test
    void testConsonantLower() {
        char testChar = 'b';
        boolean result = isLowerCaseEnglishVowel(testChar);
        assertThat(result).isFalse();
    }

    @Test
    void testConsonantUpper() {
        char testChar = 'B';
        boolean result = isLowerCaseEnglishVowel(testChar);
        assertThat(result).isFalse();
    }

    @Test
    void testConsonantLowerWithAccent() {
        char testChar = 'ý';
        boolean result = isLowerCaseEnglishVowel(testChar);
        assertThat(result).isFalse();
    }

    @Test
    void testConsonantUpperWithAccent() {
        char testChar = 'Ý';
        boolean result = isLowerCaseEnglishVowel(testChar);
        assertThat(result).isFalse();
    }

    @Test
    void testUnvowelizeLong() {
        String testString = "hallo.test.ýá.123";
        String result = unvowelize(testString);
        assertThat(result).isEqualTo("hlltstýá123");
    }
}
