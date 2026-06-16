package de.hhu.propra.link.controllers;

import de.hhu.propra.link.entities.Link;
import de.hhu.propra.link.services.LinkService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
public class LinkController {
    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "index";
    }

    @PostMapping("/")
    public String newLink(@ModelAttribute("link") @Valid Link link, BindingResult bindingResult,
                          Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return renderError(model, "The link or abbreviation is invalid. Try another one.");
        }

        if (link.getAbbreviation().isEmpty() && !linkService.createAbbreviation(link)) {
            return renderError(model, "The link could not be shortened automatically. Supply an abbreviation.");
        }

        if (linkService.findById(link.getAbbreviation()).isPresent()) {
            return renderError(model, "The short link already exists. Try another one.");
        }

        linkService.save(link);
        redirectAttributes.addFlashAttribute("success", "Successfully added a new short link!");
        return "redirect:/";
    }

    /**
     * Re-render the index form with an error banner, keeping the values the user submitted.
     */
    private String renderError(Model model, String message) {
        model.addAttribute("error", message);
        return "index";
    }

    @GetMapping("/{abbreviation}")
    public String redirectUrl(@PathVariable String abbreviation) {
        Optional<Link> link = linkService.findById(abbreviation);
        String url = link.map(Link::getUrl)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "short link does not exist"));
        return "redirect:" + url;
    }

    @PostMapping("/{abbreviation}/delete")
    public String deleteLink(@PathVariable String abbreviation, RedirectAttributes redirectAttributes) {
        Optional<Link> link = linkService.findById(abbreviation);
        if (link.isPresent()) {
            linkService.delete(link.get());
            redirectAttributes.addFlashAttribute("success", "Successfully deleted short link");
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Short link could not be deleted, because it was not found in the database");
        }
        return "redirect:/";
    }

    @ModelAttribute("maxAbbreviationLength")
    int getMaxAbbreviationLength() {
        return Link.MAX_ABBREVIATION_LENGTH;
    }

    @ModelAttribute("links")
    Iterable<Link> getLinks() {
        return linkService.allLinks();
    }

    @ModelAttribute("link")
    Link link() {
        return new Link();
    }
}
