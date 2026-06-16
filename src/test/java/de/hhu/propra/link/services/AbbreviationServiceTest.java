package de.hhu.propra.link.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AbbreviationServiceTest {

    AbbreviationService abbreviationService;

    @BeforeEach
    void setup() {
        this.abbreviationService = new AbbreviationService();
    }

    @Test
    void testAbbreviationLong() {
        String urlString = "https://www.sub.example.com/path/to/index.html";
        Optional<String> result = abbreviationService.generateAbbreviation(urlString);
        assertThat(result).contains("sbxmplpti");
    }

    @Test
    void testAbbreviationStripsWwwAndTld() {
        // www. prefix and .com are stripped, then vowels removed: github -> gthb
        Optional<String> result = abbreviationService.generateAbbreviation("https://www.github.com");
        assertThat(result).contains("gthb");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "not a valid url",          // illegal URI syntax
            "javascript:alert(1)",      // no URL protocol handler
            "ftp",                      // no scheme/host
            ""                          // empty
    })
    void testMalformedUrlYieldsEmpty(String url) {
        assertThat(abbreviationService.generateAbbreviation(url)).isEmpty();
    }

    @Test
    void testAllVowelHostWithoutPathYieldsEmpty() {
        // "aeiou" reduces to "" after vowel removal and there is no path to fall back on
        assertThat(abbreviationService.generateAbbreviation("http://aeiou.io")).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "login", "logout"})
    void testReservedWordsAreReserved(String word) {
        assertThat(abbreviationService.isReserved(word)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"foo", "bar", "gthb", "admins"})
    void testUnreservedWordsAreNotReserved(String word) {
        assertThat(abbreviationService.isReserved(word)).isFalse();
    }
}
