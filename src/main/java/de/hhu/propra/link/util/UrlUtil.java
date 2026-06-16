package de.hhu.propra.link.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Set;

public class UrlUtil {

    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");

    private UrlUtil() {
        // Utility class: prevent instantiation.
    }

    /**
     * Whether the value is an absolute {@code http}/{@code https} URL with a host.
     *
     * <p>Rejects every other scheme — notably {@code javascript:}, {@code data:} and {@code file:} —
     * so it can guard both link creation and the redirect itself, ensuring a stored short link can
     * never produce a non-web redirect even if its URL was tampered with outside the application.
     */
    public static boolean isHttpUrl(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();
            return scheme != null
                    && ALLOWED_SCHEMES.contains(scheme.toLowerCase(Locale.ROOT))
                    && uri.getHost() != null;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
