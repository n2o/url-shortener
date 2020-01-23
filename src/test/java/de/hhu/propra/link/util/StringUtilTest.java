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
        char testChar = '\u00e1'; // á
        boolean result = isLowerCaseEnglishVowel(testChar);
        assertThat(result).isFalse();
    }

    @Test
    void testVowelUpperWithAccent() {
        char testChar = '\u00c1'; // Á
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
        char testChar = '\u00fd'; // ý
        boolean result = isLowerCaseEnglishVowel(testChar);
        assertThat(result).isFalse();
    }

    @Test
    void testConsonantUpperWithAccent() {
        char testChar = '\u00dd'; // Ý
        boolean result = isLowerCaseEnglishVowel(testChar);
        assertThat(result).isFalse();
    }

    @Test
    void testUnvowelizeLong() {
        String testString = "hallo.test.\u00fd\u00e1.123"; // hallo.test.ýá.123
        String result = unvowelize(testString);
        assertThat(result).isEqualTo("hlltst\u00fd\u00e1123"); // hlltstýá123
    }
}
