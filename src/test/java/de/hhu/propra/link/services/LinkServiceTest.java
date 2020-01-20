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
