package dev.parikh.contact;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootApplication
@Controller
public class ContactApplication {

    private final ContactRepository repository;

    public ContactApplication(ContactRepository repository) {
        this.repository = repository;
    }

    public static void main(String[] args) {
        SpringApplication.run(ContactApplication.class, args);
    }

    @GetMapping("/")
    String index() {
        return "redirect:/contacts";
    }

    @GetMapping("/contacts")
    String contacts(@RequestParam("q") Optional<String> search, Model model,
                    HttpServletRequest request) {
        List<Contact> contacts;

        if (search.isEmpty()) {
            contacts = repository.findAll();
        } else {
            contacts = repository.findAll();
        }

        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);

        if (flashMap != null) {
            Contact newContact = (Contact) flashMap.get("newContact");
            if (newContact != null) {
                model.addAttribute("newContact", newContact);
            }

            String message = (String) flashMap.get("message");
            if (message != null) {
                model.addAttribute("message", message);
            }
        }

        model.addAttribute("contacts", contacts);
        return "index";
    }

    @GetMapping("/contacts/{id}")
    String showContact(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Contact> contact = repository.findById(id);

        if (contact.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Contact ID %d not found".formatted(id));
            return "redirect:/contacts";
        }

        model.addAttribute("contact", contact.get());

        return "show";
    }

    @GetMapping("/contacts/{id}/edit")
    String editContactGet(@PathVariable Long id, Model model,
                          RedirectAttributes redirectAttributes) {
        Optional<Contact> contact = repository.findById(id);

        if (contact.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Contact ID %d not found".formatted(id));
            return "redirect:/contacts";
        }

        model.addAttribute("contact", contact.get());

        return "edit";
    }

    @PostMapping("/contacts/{id}/edit")
    String editContact(@PathVariable Long id,
                       @RequestParam("first_name") String firstName,
                       @RequestParam("last_name") String lastName,
                       @RequestParam("phone") String phone,
                       @RequestParam("email") String email) {
        repository.findById(id).map(contact -> {
            contact.setFirst(firstName);
            contact.setLast(lastName);
            contact.setPhone(phone);
            contact.setEmail(email);
            return repository.save(contact);
        }).orElseGet(() -> {
            var contact = Contact.builder().first(firstName).last(lastName).phone(phone)
                    .email(email).build();
            contact.setId(id);
            return repository.save(contact);
        });

        return "redirect:/contacts/" + id;
    }

    @PostMapping("/contacts/{id}/delete")
    String deleteContact(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        repository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Deleted Contact!");
        return "redirect:/contacts";
    }

    @GetMapping("/contacts/new")
    String newContactGet(Model model) {
        model.addAttribute("contact", new Contact());
        return "new";
    }

    @PostMapping("/contacts/new")
    String newContact(@RequestParam("first_name") String firstName,
                      @RequestParam("last_name") String lastName,
                      @RequestParam("phone") String phone,
                      @RequestParam("email") String email,
                      RedirectAttributes redirectAttributes) {
        var contact = Contact.builder().first(firstName).last(lastName).phone(phone)
                .email(email).build();
        repository.save(contact);

        redirectAttributes.addFlashAttribute("message",
                "Created new contact %s %s!".formatted(contact.getFirst(), contact.getLast()));

        return "redirect:/contacts";
    }
}
