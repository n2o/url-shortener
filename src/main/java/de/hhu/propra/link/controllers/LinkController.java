package de.hhu.propra.link.controllers;

import de.hhu.propra.link.entities.Link;
import de.hhu.propra.link.repositories.LinkRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.annotation.SessionScope;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@SessionScope
public class LinkController {
    private final LinkRepository linkRepository;
    private String errorMessage;
    private String successMessage;
    private Link currentLink = new Link();

    public LinkController(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("links", linkRepository.findAll());
        model.addAttribute("link", currentLink);
        model.addAttribute("error", errorMessage);
        model.addAttribute("success", successMessage);
        return "index";
    }

    @PostMapping("/")
    public String newLink(@ModelAttribute @Valid Link link, BindingResult bindingResult) {
        this.currentLink = link;

        if (bindingResult.hasErrors()) {
            return "index";
        }

        if (linkRepository.findById(link.getAbbreviation()).isEmpty()) {
            linkRepository.save(link);
            setMessages(null, "Successfully added a new short link!");
            this.currentLink = new Link();
        } else {
            setMessages("The short link already exists. Try another one.", null);
        }
        return "redirect:/";
    }

    @GetMapping("/{abbreviation}")
    public String redirectUrl(@PathVariable String abbreviation) {
        setMessages(null, null);
        Optional<Link> link = linkRepository.findById(abbreviation);
        return link.map(value -> "redirect:" + value.getUrl()).orElse("redirect:/");
    }

    @PostMapping("/{abbreviation}/delete")
    public String deleteLink(@PathVariable String abbreviation) {
        Optional<Link> link = linkRepository.findById(abbreviation);
        if (link.isPresent()) {
            linkRepository.delete(link.get());
            setMessages(null, "Successfully deleted short link");
        } else {
            setMessages("Short link could not be deleted, because it was not found in the database", null);
        }
        return "redirect:/";
    }

    /**
     * Set Error and Success Messages for the frontend
     *
     * @param errorMessage   Describe error
     * @param successMessage Send a joyful message to the user
     */
    private void setMessages(String errorMessage, String successMessage) {
        this.errorMessage = errorMessage;
        this.successMessage = successMessage;
    }
}
