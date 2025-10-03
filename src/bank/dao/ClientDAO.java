package bank.dao;

import bank.exception.DaoException;
import bank.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientDAO {
    Client save(Client client) throws DaoException;
    boolean update(Client client) throws DaoException;
    boolean delete(String id) throws DaoException;
    Optional<Client> findById(String id) throws DaoException;
    Optional<Client> findByName(String name) throws DaoException;
    Optional<Client> findByEmail(String email) throws DaoException;
    Optional<Client> findByPhone(String phone) throws DaoException;
    List<Client> findAll() throws DaoException;
}
