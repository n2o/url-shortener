package de.hhu.propra.link.controllers;

import de.hhu.propra.link.entities.Link;
import de.hhu.propra.link.security.SecurityConfiguration;
import de.hhu.propra.link.services.AbbreviationService;
import de.hhu.propra.link.services.LinkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import(SecurityConfiguration.class)
@TestPropertySource(properties = "SHORTY_ADMIN_PASSWORD=1234")
class LinkControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    LinkService linkService;

    @MockitoBean
    AbbreviationService abbreviationService;

    @Test
    void testIndex() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminAuthorized() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void testNewLinkAnonymousWithoutCsrf() throws Exception {
        mvc.perform(
                post("/")
                        .param("abbreviation", "abc")
                        .param("url", "http://www.abc.de")
        ).andExpect(status().is4xxClientError());
    }


    @Test
    void testNewLinkAnonymousWithCsrfIsRejected() throws Exception {
        mvc.perform(
                post("/")
                        .param("abbreviation", "abc")
                        .param("url", "http://www.abc.de")
                        .with(csrf())
        ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testNewLinkAuthorizedWithoutCsrf() throws Exception {
        mvc.perform(
                post("/")
                        .param("abbreviation", "abc")
                        .param("url", "http://www.abc.de")
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testNewLinkAuthorizedWithCsrf() throws Exception {
        mvc.perform(
                post("/")
                        .param("abbreviation", "abc")
                        .param("url", "http://www.abc.de")
                        .with(csrf())
        ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testNewLinkSuccessUsesFlashAttribute() throws Exception {
        mvc.perform(
                post("/")
                        .param("abbreviation", "abc")
                        .param("url", "http://www.abc.de")
                        .with(csrf())
        ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("success", "Successfully added a new short link!"));
    }

    @Test
    void testIndexHasNoStickyMessages() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("success", "error"));
    }

    @Test
    void testLoginPage() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void testValidLogin() throws Exception {
        mvc.perform(post("/login")
                .param("username", "admin")
                .param("password", "1234")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testInvalidLogin() throws Exception {
        mvc.perform(post("/login")
                .param("username", "admin")
                .param("password", "")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/login?error"));
    }

    @Test
    void testLoginwithoutCsrf() throws Exception {
        mvc.perform(post("/login")
                .with(csrf().useInvalidToken()))
                .andExpect(status().is(403));
    }


    @Test
    void testLogoutWithCsrf() throws Exception {
        mvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testLogoutWithoutCsrf() throws Exception {
        mvc.perform(post("/logout").with(csrf().useInvalidToken()))
                .andExpect(status().is(403));
    }

    @Test
    void testRedirectToExistingLink() throws Exception {
        when(linkService.findById("abc")).thenReturn(Optional.of(link("abc", "https://example.com")));

        mvc.perform(get("/abc"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://example.com"));
    }

    @Test
    void testRedirectUnknownLinkReturnsNotFound() throws Exception {
        when(linkService.findById("missing")).thenReturn(Optional.empty());

        mvc.perform(get("/missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRedirectRejectsStoredNonHttpUrl() throws Exception {
        // Defense in depth: a stored link whose URL is not http/https (e.g. tampered in Redis)
        // must never produce a javascript:/data:/file: redirect.
        when(linkService.findById("evil")).thenReturn(Optional.of(link("evil", "javascript:alert(1)")));

        mvc.perform(get("/evil"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testNewLinkRejectsNonHttpScheme() throws Exception {
        mvc.perform(
                post("/")
                        .param("abbreviation", "abc")
                        .param("url", "javascript:alert(1)")
                        .with(csrf())
        ).andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("error"));

        verify(linkService, never()).save(any());
    }

    @Test
    void testDeleteAnonymousWithoutCsrfIsForbidden() throws Exception {
        mvc.perform(post("/abc/delete"))
                .andExpect(status().is4xxClientError());

        verify(linkService, never()).delete(any());
    }

    @Test
    void testDeleteAnonymousWithCsrfRedirectsToLogin() throws Exception {
        mvc.perform(post("/abc/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(linkService, never()).delete(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteAdminWithoutCsrfIsForbidden() throws Exception {
        mvc.perform(post("/abc/delete"))
                .andExpect(status().is(403));

        verify(linkService, never()).delete(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteAdminWithCsrfDeletesExistingLink() throws Exception {
        Link existing = link("abc", "https://example.com");
        when(linkService.findById("abc")).thenReturn(Optional.of(existing));

        mvc.perform(post("/abc/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("success", "Successfully deleted short link"));

        verify(linkService).delete(existing);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteAdminMissingLinkReportsError() throws Exception {
        when(linkService.findById("missing")).thenReturn(Optional.empty());

        mvc.perform(post("/missing/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("error"));

        verify(linkService, never()).delete(any());
    }

    private static Link link(String abbreviation, String url) {
        Link link = new Link();
        link.setAbbreviation(abbreviation);
        link.setUrl(url);
        return link;
    }

}
