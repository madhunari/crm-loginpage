package pl.coderslab.api;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.coderslab.entity.Client;
import pl.coderslab.service.ClientService;
import pl.coderslab.service.UserService;

@RestController
@RequestMapping("/clients")
public class ClientResource {

	private final ClientService clientService;
	private final UserService userService;

	@Autowired
	public ClientResource(ClientService clientService, UserService userService) {
		this.clientService = clientService;
		this.userService = userService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<Client> getClient(@PathVariable Long id) {
		Optional<Client> client = clientService.findById(id);
		return client.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping
	public ResponseEntity<List<Client>> getClients() {
		List<Client> clients = clientService.findAll();
		return ResponseEntity.ok(clients);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		clientService.deleteClient(id);
		return ResponseEntity.accepted().build();
	}

	@PostMapping("/{userEmail}")
	public ResponseEntity<Long> createClient(@PathVariable String userEmail, @Valid @RequestBody Client client) {
		client.setUser(userService.findByEmail(userEmail));
		Long id = clientService.saveClient(client);
		return ResponseEntity.ok(id);
	}

	@PutMapping("/{userEmail}")
	public ResponseEntity<Void> updateUser(@PathVariable String userEmail, @Valid @RequestBody Client client) {
		client.setUser(userService.findByEmail(userEmail));
		clientService.saveClient(client);
		return ResponseEntity.accepted().build();
	}
}
