package de.hhu.propra.link.services;

import de.hhu.propra.link.entities.Link;
import de.hhu.propra.link.repositories.LinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LinkServiceTest {

    AbbreviationService abbreviationServiceMock = mock(AbbreviationService.class);
    LinkRepository linkRepositoryMock = mock(LinkRepository.class);
    LinkService linkService;

    @BeforeEach
    void setup() {
        this.abbreviationServiceMock = mock(AbbreviationService.class);
        this.linkRepositoryMock = mock(LinkRepository.class);
        this.linkService = new LinkService(abbreviationServiceMock, linkRepositoryMock);
    }

    @Test
    void testGenerationFailureReturnsFalse() {
        Link newLink = new Link();
        newLink.setUrl("not a url");
        when(abbreviationServiceMock.generateAbbreviation("not a url")).thenReturn(empty());

        boolean result = linkService.createAbbreviation(newLink);

        assertThat(result).isFalse();
        assertThat(newLink.getAbbreviation()).isNull();
    }

    @Test
    void testFindNextFreeAbbreviationTruncatesToMaxLength() {
        String candidate = "a".repeat(40);

        String result = linkService.findNextFreeAbbreviation(candidate);

        assertThat(result).isEqualTo("a".repeat(Link.MAX_ABBREVIATION_LENGTH));
    }

    @Test
    void testFindNextFreeAbbreviationAppendsNumberWhenReserved() {
        when(abbreviationServiceMock.isReserved("admin")).thenReturn(true);
        when(abbreviationServiceMock.isReserved("admin1")).thenReturn(false);

        String result = linkService.findNextFreeAbbreviation("admin");

        assertThat(result).isEqualTo("admin1");
    }

    @Test
    void testFindNextFreeAbbreviationIncrementsOnRepeatedCollisions() {
        when(linkRepositoryMock.findById("abc")).thenReturn(of(new Link()));
        when(linkRepositoryMock.findById("abc1")).thenReturn(of(new Link()));
        when(linkRepositoryMock.findById("abc2")).thenReturn(empty());

        String result = linkService.findNextFreeAbbreviation("abc");

        assertThat(result).isEqualTo("abc2");
    }

    @Test
    void testFindNextFreeAbbreviationTruncatesToFitAppendedNumber() {
        // A 32-char candidate that collides must leave room for the suffix: 31 chars + "1".
        String candidate = "b".repeat(Link.MAX_ABBREVIATION_LENGTH);
        when(linkRepositoryMock.findById(candidate)).thenReturn(of(new Link()));

        String result = linkService.findNextFreeAbbreviation(candidate);

        assertThat(result)
                .hasSize(Link.MAX_ABBREVIATION_LENGTH)
                .isEqualTo("b".repeat(Link.MAX_ABBREVIATION_LENGTH - 1) + "1");
    }

    @Test
    void testAutoAbbreviationAlreadyPresent() {
        String url = "https://www.sub.example.com/path/to/index.html";
        String abbreviation = "sbxmplpti";
        String altAbbreviation = "sbxmplpti1";

        Link newLink = new Link();
        newLink.setUrl(url);
        Link presentLink = new Link();
        presentLink.setUrl(url);
        presentLink.setAbbreviation(abbreviation);
        when(abbreviationServiceMock.generateAbbreviation(url)).thenReturn(of(abbreviation));
        when(linkRepositoryMock.findById(abbreviation)).thenReturn(of(presentLink));
        when(linkRepositoryMock.findById(altAbbreviation)).thenReturn(empty());

        boolean result = linkService.createAbbreviation(newLink);

        assertThat(result).isTrue();
        assertThat(newLink).extracting(Link::getAbbreviation).isEqualTo(altAbbreviation);
    }
}
