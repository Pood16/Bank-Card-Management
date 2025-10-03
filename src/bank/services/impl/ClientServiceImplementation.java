package bank.services.impl;
import bank.dao.ClientDAO;
import bank.exception.DaoException;
import bank.exception.ServiceException;
import bank.model.Client;
import bank.services.ClientService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class ClientServiceImplementation implements ClientService {

    private final ClientDAO clientDAO;

    public ClientServiceImplementation(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    @Override
    public Client registerClient(String name, String email, String phone) throws ServiceException {
        try{
            Optional<Client> optionalClient = clientDAO.findByEmail(email);
            if (optionalClient.isPresent()){
                throw new ServiceException("Email already in use");
            }

            String id = UUID.randomUUID().toString();
            Client newClient = new Client(id, name, email, phone);
            return clientDAO.save(newClient);
        }catch(DaoException e) {
            throw new ServiceException("Service error. " + e.getMessage());
        }
    }

    @Override
    public Client updateClient(String id, String name, String email, String phone) throws ServiceException {
        try {
            Optional<Client> existingClient = clientDAO.findById(id);
            if (existingClient.isEmpty()) {
                throw new ServiceException("Client not found");
            }

            Client updatedClient = new Client(id, name, email, phone);
            clientDAO.update(updatedClient);
            return updatedClient;
        } catch (DaoException e) {
            throw new ServiceException("Service error. " + e.getMessage());
        }
    }

    @Override
    public boolean deleteClient(String clientId) throws ServiceException {
        try {
            Optional<Client> optionalClient = clientDAO.findById(clientId);
            if (optionalClient.isPresent()) {
                return clientDAO.delete(clientId);
            }
            return false;
        }catch(DaoException e) {
            throw new ServiceException("Service error. " + e.getMessage());
        }
    }

    @Override
    public Optional<Client> findClientById(String clientId) throws ServiceException{
        try{
            return clientDAO.findById(clientId);
        }catch(DaoException e){
            throw new ServiceException("Service error. " +  e.getMessage());
        }
    }

    @Override
    public Optional<Client> searchByEmail(String email) throws ServiceException {
        try {
            return clientDAO.findByEmail(email);
        } catch (DaoException e) {
            throw new ServiceException("Service error. " + e.getMessage());
        }
    }

    @Override
    public Optional<Client> searchByPhone(String phone) throws ServiceException {
        try {
            return clientDAO.findByPhone(phone);
        } catch (DaoException e) {
            throw new ServiceException("Service error. " + e.getMessage());
        }
    }

    @Override
    public List<Client> listAllClients() throws ServiceException {
        try {
            return clientDAO.findAll();
        } catch (DaoException e) {
            throw new ServiceException("Service error. " + e.getMessage());
        }
    }
}
