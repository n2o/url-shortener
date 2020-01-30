package de.hhu.propra.link.controllers;

import de.hhu.propra.link.services.AbbreviationService;
import de.hhu.propra.link.services.LinkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class LinkControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    LinkService linkService;

    @MockBean
    AbbreviationService abbreviationService;

    @Test
    void testIndex() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void testAdminAnonymous() throws Exception {
        mvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAdminAuthorized() throws Exception {
        mvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin"));
    }

    @Test
    void testNewLinkAnonymousWithoutCsrf() throws Exception {
        mvc.perform(
                post("/admin")
                        .param("abbreviation", "abc")
                        .param("url", "http://www.abc.de")
        ).andExpect(status().is4xxClientError());
    }

    @Test
    void testNewLinkAnonymousWithCsrf() throws Exception {
        mvc.perform(
                post("/admin")
                        .param("abbreviation", "abc")
                        .param("url", "http://www.abc.de")
                        .with(csrf())
        ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testNewLinkAuthorizedWithoutCsrf() throws Exception {
        mvc.perform(
                post("/admin")
                        .param("abbreviation", "abc")
                        .param("url", "http://www.abc.de")
        ).andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testNewLinkAuthorizedWithCsrf() throws Exception {
        mvc.perform(
                post("/admin")
                        .param("abbreviation", "abc")
                        .param("url", "http://www.abc.de")
                        .with(csrf())
        ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }
}
