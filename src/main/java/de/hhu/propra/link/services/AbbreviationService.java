package de.hhu.propra.link.services;

import de.hhu.propra.link.util.StringUtil;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class AbbreviationService {

    private static final String WWW_PREFIX = "www.";
    private static final Set<String> BLACKLIST = Set.of("admin");

    /**
     * Creates an abbreviation for the entered URL.
     *
     * @param urlString the URL that is to be shortened
     * @return the abbreviation for the long URL or Optional.empty() if no such abbreviation can be generated
     */
    public Optional<String> generateAbbreviation(String urlString) {
        urlString = urlString.toLowerCase(Locale.ROOT);

        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException ignored) {
            return Optional.empty();
        }

        String host = getStrippedHost(url);
        String[] path = getSplitPath(url);

        StringBuilder abbreviation = makeAbbreviation(host, path);
        if (abbreviation.length() == 0) {
            return Optional.empty();
        }

        String abbreviationString = StringUtil.slugify(abbreviation.toString());

        return Optional.of(abbreviationString);
    }

    /**
     * Remove unwanted subdomains (like "www") and the TLD part from the host part of the given URL.
     *
     * @param url the URL
     * @return the stripped host part
     */
    String getStrippedHost(URL url) {
        String host = url.getHost();

        if (host.startsWith(WWW_PREFIX)) {
            host = host.substring(WWW_PREFIX.length());
        }

        host = StringUtil.stripAfterLastDot(host);

        return host;
    }

    /**
     * Split the path part of the given URL into pieces.
     *
     * @param url the URL
     * @return the split path part
     */
    String[] getSplitPath(URL url) {
        String path = url.getPath();
        return Arrays.stream(path.split("/"))
                .filter(s -> !s.isEmpty())
                .map(StringUtil::stripAfterLastDot)
                .toArray(String[]::new);
    }

    /**
     * Determine a suitable abbreviation taking the paths into consideration.
     * ex: "https://www.sub.example.com/path/to/index.html" -> "sbxmplpti"
     *
     * @param host      stripped host part of the URL
     * @param pathParts split path parts of the URL
     * @return current abbreviation
     */
    StringBuilder makeAbbreviation(String host, String[] pathParts) {
        StringBuilder tmpAbbreviation = new StringBuilder(StringUtil.unvowelize(host));
        for (String path : pathParts) {
            tmpAbbreviation.append(path.charAt(0));
        }
        return tmpAbbreviation;
    }

    /**
     * Get the validity of the given abbreviation.
     *
     * @param abbreviation the abbreviation candidate
     * @return true if the abbreviation is invalid, false otherwise
     */
    public boolean isReserved(String abbreviation) {
        return BLACKLIST.contains(abbreviation);
    }
}
