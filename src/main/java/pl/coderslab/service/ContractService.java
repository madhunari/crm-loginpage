package pl.coderslab.service;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.coderslab.AuthenticationFacade;
import pl.coderslab.entity.Client;
import pl.coderslab.entity.Contract;
import pl.coderslab.entity.User;
import pl.coderslab.repository.ContractRepository;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final UserService userService;
    private final PdfService pdfService;
    private final AuthenticationFacade authenticationFacade;

    @Autowired
    public ContractService(ContractRepository contractRepository, UserService userService, PdfService pdfService,
            AuthenticationFacade authenticationFacade) {
        this.contractRepository = contractRepository;
        this.userService = userService;
        this.pdfService = pdfService;
        this.authenticationFacade = authenticationFacade;
    }

    public void print(Contract contract) throws FileNotFoundException {
        pdfService.printContract(contract);
    }

    public boolean sendToSupervisor(Contract contract, User user) {
        User supervisor = user.getSupervisor();
        while (supervisor != null) {
            if (contract.getValue() <= userService.getMaxContractValue(supervisor)) {
                contract.setAcceptedBy(supervisor);
                contractRepository.save(contract); // Corrected to use repository method
                return true;
            }
            supervisor = supervisor.getSupervisor();
        }
        return false;
    }

    public void acceptContract(Contract contract) {
        User user = authenticationFacade.getAuthenticatedUser();
        if (contract.getAcceptedBy() != null && contract.getAcceptedBy().equals(user)) {
            contract.setAccepted(true);
            this.save(contract);
        }
    }

    public Optional<Contract> findById(Long id) {
        return contractRepository.findById(id);
    }

    public void save(Contract contract) {
        contractRepository.save(contract);
    }

    public List<Contract> findByClient(Client client) {
        return contractRepository.findByClient(client);
    }

    public List<Contract> findByAcceptedBy(User user) {
        return contractRepository.findByAcceptedBy(user);
    }
}
