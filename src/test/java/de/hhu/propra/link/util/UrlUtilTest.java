package de.hhu.propra.link.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static de.hhu.propra.link.util.UrlUtil.isHttpUrl;
import static org.assertj.core.api.Assertions.assertThat;

class UrlUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "http://example.com",
            "https://example.com",
            "https://www.sub.example.com/path/to/index.html?q=1#frag",
            "HTTPS://EXAMPLE.COM",
            "http://localhost:8080/x"
    })
    void acceptsHttpAndHttpsUrls(String url) {
        assertThat(isHttpUrl(url)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "javascript:alert(1)",
            "data:text/html,<script>alert(1)</script>",
            "file:///etc/passwd",
            "ftp://example.com/file",
            "mailto:foo@example.com",
            "example.com",          // no scheme
            "https://",             // no host
            "not a url"             // malformed
    })
    void rejectsNonHttpSchemesAndMalformedUrls(String url) {
        assertThat(isHttpUrl(url)).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsNullBlankAndEmpty(String url) {
        assertThat(isHttpUrl(url)).isFalse();
    }
}
