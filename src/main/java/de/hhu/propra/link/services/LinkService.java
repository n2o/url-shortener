package de.hhu.propra.link.services;

import de.hhu.propra.link.entities.Link;
import de.hhu.propra.link.repositories.LinkRepository;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

@Component
public class LinkService {
    private final LinkRepository linkRepository;

    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    /**
     * Creates a not yet used viable abbreviation for the entered URL.
     *
     * @param urlString the URL that is to be shortened
     * @return the abbreviation for the long URL or Optional.empty() if no such abbreviation can be generated
     */
    Optional<String> autoAbbreviation(String urlString) {
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

        return Optional.of(findNextFreeAbbreviation(abbreviation));
    }

    /**
     * Remove unwanted subdomains (like "www") and the TLD part from the host part of the given URL.
     *
     * @param url the URL
     * @return the stripped host part
     */
    String getStrippedHost(URL url) {
        String host = url.getHost();

        String www = "www.";
        if (host.startsWith(www)) {
            host = host.substring(www.length());
        }

        host = stripAfterLastDot(host);

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
                .map(this::stripAfterLastDot)
                .toArray(String[]::new);
    }

    /**
     * Strip everything after the last dot, if that dot is not first character.
     *
     * @param s the string
     * @return the stripped string
     */
    String stripAfterLastDot(String s) {
        int lastDot = s.lastIndexOf('.');
        if (lastDot > 0) {
            s = s.substring(0, lastDot);
        }
        return s;
    }

    /**
     * Determine a suitable abbreviation taking the paths into consideration.
     * ex: "sub.example.com/path/to/index.html" -> "sbxmplpti"
     *
     * @param host      stripped host part of the URL
     * @param pathParts split path parts of the URL
     * @return current abbreviation
     */
    StringBuilder makeAbbreviation(String host, String[] pathParts) {
        StringBuilder tmp_abbreviation = new StringBuilder(unvowelize(host));
        for (String path : pathParts) {
            tmp_abbreviation.append(path.charAt(0));
        }
        return tmp_abbreviation;
    }

    /**
     * Create another abbreviation if the preferred one already exists
     *
     * @param tmp_abbreviation current abbreviation
     * @return abbreviation with appended number for uniqueness
     */
    String findNextFreeAbbreviation(StringBuilder tmp_abbreviation) {
        int i = 1;
        String abbrevation = tmp_abbreviation.toString();
        while (linkRepository.findById(abbrevation).isPresent()) {
            abbrevation = tmp_abbreviation.toString() + i;
            i++;
        }
        return abbrevation;
    }

    /**
     * Strip the string of vowels and dots.
     *
     * @param s the string
     * @return the string without vowels and dots
     */
    String unvowelize(String s) {
        StringBuilder unvowelized = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (!isLowerCaseEnglishVowel(c) && c != '.') {
                unvowelized.append(c);
            }
        }
        return unvowelized.toString();
    }

    /**
     * Check if character is a lower case vowel.
     *
     * @param letter The letter to check
     * @return true if letter is a vowel and lower case, false otherwise
     */
    boolean isLowerCaseEnglishVowel(char letter) {
        switch (letter) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
                return true;
            default:
                return false;
        }
    }

    public Iterable<Link> allLinks() {
        return linkRepository.findAll();
    }

    public Optional<Link> findById(String abbreviation) {
        return linkRepository.findById(abbreviation);
    }

    public void delete(Link link) {
        linkRepository.delete(link);
    }

    public void save(Link link) {
        linkRepository.save(link);
    }

    /**
     * Generates an abbreviation for the given link, setting it directly.
     *
     * @param link the link to be abbreviated
     * @return true on successful operation, false otherwise
     */
    public boolean createAbbreviation(Link link) {
        Optional<String> abbreviation = autoAbbreviation(link.getUrl());
        abbreviation.ifPresent(link::setAbbreviation);
        return abbreviation.isPresent();
    }
}
