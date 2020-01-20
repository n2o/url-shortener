package de.hhu.propra.link.controllers;

import de.hhu.propra.link.services.LinkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class LinkControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    LinkService linkService;

    @Test
    void testIndex() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    void testNewLink() throws Exception {
        mvc
                .perform(
                        post("/")
                                .param("abbreviation", "abc")
                                .param("url", "http://www.abc.de")
                )
                .andExpect(status().is3xxRedirection());
    }
}
