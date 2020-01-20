package de.hhu.propra.link.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        assertThat(result).isEqualTo("sbxmplpti");
    }
}
