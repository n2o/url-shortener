package de.hhu.propra.link.services;

import de.hhu.propra.link.entities.Link;
import de.hhu.propra.link.repositories.LinkRepository;
import org.springframework.stereotype.Component;

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
     * @param url The URL that is to be shortened
     * @return the abbreviation for the long URL
     */
    private String autoAbbreviation(String url) {
        String schemelessUrl = removeProtocol(url);
        if (schemelessUrl.indexOf("www") == 0) {
            schemelessUrl = schemelessUrl.substring(3);
        }
        StringBuilder abbreviation = makeAbbreviationFromPath(schemelessUrl);
        return findNextFreeAbbreviation(abbreviation);
    }

    /**
     * Remove leading http(s?):// from URL.
     *
     * @param url current url
     * @return (possibly) stripped url
     */
    private String removeProtocol(String url) {
        int schemelessUrlindex = url.indexOf("://");
        if (schemelessUrlindex > 0) {
            return url.substring(schemelessUrlindex + 3);
        } else {
            return url;
        }
    }

    /**
     * Determine a suitable abbreviation taking the paths into consideration.
     * ex: "sub.example.com/path/to/index.html" -> "sbxmplpti"
     *
     * @param schemelessUrl part of URL
     * @return current abbreviation
     */
    private StringBuilder makeAbbreviationFromPath(String schemelessUrl) {
        String[] domainAndPaths = schemelessUrl.split("/");
        StringBuilder tmp_abbreviation = new StringBuilder(unvowelize(domainAndPaths[0]));
        for (int i = 1; i < domainAndPaths.length; i++) {
            tmp_abbreviation.append(domainAndPaths[i].charAt(0));
        }
        return tmp_abbreviation;
    }

    /**
     * Create another abbreviation if the preferred one already exists
     *
     * @param tmp_abbreviation current abbreviation
     * @return abbreviation with appended number for uniqueness
     */
    private String findNextFreeAbbreviation(StringBuilder tmp_abbreviation) {
        int i = 1;
        String abbrevation = tmp_abbreviation.toString();
        while (linkRepository.findById(abbrevation).isPresent()) {
            abbrevation = tmp_abbreviation.toString() + i;
            i += 1;
        }
        return abbrevation;
    }

    /**
     * @param domain The domain name where the vowels, dots and the TLD should be removed from
     * @return the String without any vowels, dots and the TLD
     */
    private String unvowelize(String domain) {
        StringBuilder unvowelized = new StringBuilder();
        for (int i = 0; i < domain.lastIndexOf('.'); i++) {
            if (!(isVowel(domain.charAt(i)) || domain.charAt(i) == '.')) {
                unvowelized.append(domain.charAt(i));
            }
        }
        return unvowelized.toString();
    }

    /**
     * Check if character is a vowel.
     *
     * @param letter The letter to check
     * @return true if letter is a vowel, false elsewise
     */
    private boolean isVowel(char letter) {
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

    public void createAbbreviation(Link link) {
        link.setAbbreviation(autoAbbreviation(link.getUrl()));
    }
}
