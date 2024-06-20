package pl.coderslab.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import pl.coderslab.AuthenticationFacade;
import pl.coderslab.entity.Client;
import pl.coderslab.entity.User;
import pl.coderslab.repository.AddressRepository;
import pl.coderslab.repository.ClientRepository;
import pl.coderslab.repository.ContactPersonRepository;

@Service
public class ClientServiceImpl implements ClientService {

    private final AuthenticationFacade authenticationFacade;
    private final ClientRepository clientRepository;
    private final ContactPersonRepository contactPersonRepository;
    private final AddressRepository addressRepository;

    public ClientServiceImpl(AuthenticationFacade authenticationFacade,
                             ClientRepository clientRepository,
                             ContactPersonRepository contactPersonRepository,
                             AddressRepository addressRepository) {
        this.authenticationFacade = authenticationFacade;
        this.clientRepository = clientRepository;
        this.contactPersonRepository = contactPersonRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    @Override
    public Client findByName(String name) {
        return clientRepository.findByNameIgnoreCase(name);
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public List<String> getStatusList() {
        return Arrays.asList("lead", "client", "lost");
    }

    @Override
    public Long saveClientWithLoggedUser(Client client) {
        User user = authenticationFacade.getAuthenticatedUser();
        client.setUser(user);
        client.setCreated(LocalDateTime.now());
        clientRepository.save(client);
        return client.getId();
    }

    @Override
    public Long saveClient(Client client) {
        client.setCreated(LocalDateTime.now());
        clientRepository.save(client);
        return client.getId();
    }

    @Override
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    @Override
    public List<Client> findByUser(User user) {
        return clientRepository.findByUser(user);
    }
}
