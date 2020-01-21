package de.hhu.propra.link.services;

import de.hhu.propra.link.entities.Link;
import de.hhu.propra.link.repositories.LinkRepository;
import de.hhu.propra.link.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LinkService {

    private final AbbreviationService abbreviationService;
    private final LinkRepository linkRepository;

    public LinkService(AbbreviationService abbreviationService, LinkRepository linkRepository) {
        this.abbreviationService = abbreviationService;
        this.linkRepository = linkRepository;
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
     * Generate a new abbreviation for the given link, setting it directly.
     *
     * @param link the link to be abbreviated
     * @return true on successful operation, false otherwise
     */
    public boolean createAbbreviation(Link link) {
        Optional<String> abbreviation = abbreviationService.generateAbbreviation(link.getUrl());
        return abbreviation
                .map(this::findNextFreeAbbreviation)
                .map(abbr -> {
                    link.setAbbreviation(abbr);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Create the abbreviation of the {@link Link#MAX_ABBREVIATION_LENGTH maximum length}, generating a new one if the preferred one already exists.
     *
     * @param abbreviationCandidate current abbreviation candidate
     * @return the candidate as is or - when necessary - the candidate with an appended number for uniqueness
     */
    String findNextFreeAbbreviation(String abbreviationCandidate) {
        int i = 0;
        String abbreviation = StringUtil.truncate(abbreviationCandidate, Link.MAX_ABBREVIATION_LENGTH);
        while (abbreviationService.isReserved(abbreviation) || linkRepository.findById(abbreviation).isPresent()) {
            String numberString = String.valueOf(++i);
            abbreviation = StringUtil.truncate(abbreviationCandidate, Link.MAX_ABBREVIATION_LENGTH - numberString.length()) + numberString;
        }
        return abbreviation;
    }
}
