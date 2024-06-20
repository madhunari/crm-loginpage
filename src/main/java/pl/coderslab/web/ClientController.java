package pl.coderslab.web;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pl.coderslab.entity.Client;
import pl.coderslab.entity.Contract;
import pl.coderslab.entity.Event;
import pl.coderslab.repository.EventRepository;
import pl.coderslab.service.ClientService;
import pl.coderslab.service.ContractService;
import pl.coderslab.service.UserService;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;
    private final UserService userService;
    private final ContractService contractService;
    private final EventRepository eventRepository;

    @Autowired
    public ClientController(ClientService clientService, UserService userService, ContractService contractService, EventRepository eventRepository) {
        this.clientService = clientService;
        this.userService = userService;
        this.contractService = contractService;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/add")
    public String addClient(Model model) {
        Client client = new Client();
        model.addAttribute("client", client);

        List<String> statusList = clientService.getStatusList();
        model.addAttribute("statusList", statusList);

        return "client/addClient";
    }

    @PostMapping("/add")
    public String registerClient(@Valid @ModelAttribute("client") Client client, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<String> statusList = clientService.getStatusList();
            model.addAttribute("statusList", statusList);
            return "client/addClient";
        } else {
            clientService.saveClientWithLoggedUser(client);
            return "redirect:/client/details/" + client.getId();
        }
    }

    @GetMapping("/edit/{id}")
    public String editClient(@PathVariable Long id, Model model) {
        Optional<Client> clientOpt = clientService.findById(id);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            model.addAttribute("client", client);

            List<String> statusList = clientService.getStatusList();
            model.addAttribute("statusList", statusList);

            return "client/editClient";
        } else {
            return "client/error";
        }
    }

    @PostMapping("/edit/{id}")
    public String saveClient(@Valid @ModelAttribute("client") Client client, BindingResult bindingResult,
                             @PathVariable Long id, Model model) {
        if (bindingResult.hasErrors()) {
            List<String> statusList = clientService.getStatusList();
            model.addAttribute("statusList", statusList);
            return "client/editClient";
        } else {
            client.setId(id);
            client.setUser(userService.findByEmail(client.getUser().getEmail()));
            clientService.saveClient(client);
            return "redirect:/client/details/" + id;
        }
    }

    @GetMapping("/details/{id}")
    public String clientDetails(@PathVariable Long id, Model model) {
        Optional<Client> clientOpt = clientService.findById(id);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            model.addAttribute("client", client);

            List<Contract> contractList = contractService.findByClient(client);
            model.addAttribute("contractList", contractList);

            List<Event> eventList = eventRepository.findByClient(client);
            model.addAttribute("eventList", eventList);

            return "client/clientDetails";
        } else {
            return "client/error";
        }
    }

    @GetMapping("/addSimilar/{id}")
    public String addSimilarClient(@PathVariable Long id, Model model) {
        Optional<Client> clientOpt = clientService.findById(id);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            model.addAttribute("client", client);

            List<String> statusList = clientService.getStatusList();
            model.addAttribute("statusList", statusList);

            return "client/addSimilarClient";
        } else {
            return "client/error";
        }
    }
}
