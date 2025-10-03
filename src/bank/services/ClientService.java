package bank.services;

import bank.exception.ServiceException;
import bank.model.Client;
import java.util.List;
import java.util.Optional;

public interface ClientService {
    Client registerClient(String name, String email, String phone) throws ServiceException;
    Client updateClient(String id, String name, String email, String phone) throws ServiceException;
    boolean deleteClient(String clientId) throws ServiceException;
    Optional<Client> findClientById(String clientId) throws ServiceException;
    Optional<Client> searchByEmail(String email) throws ServiceException;
    Optional<Client> searchByPhone(String phone) throws ServiceException;
    List<Client> listAllClients() throws ServiceException;
}
