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
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/login?logout"));
    }

    @Test
    void testLogoutWithoutCsrf() throws Exception {
        mvc.perform(post("/logout").with(csrf().useInvalidToken()))
                .andExpect(status().is(403));
    }

}
