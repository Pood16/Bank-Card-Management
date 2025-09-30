package bank.dao;

import bank.exception.DaoException;
import bank.model.Client;

import java.util.Optional;

public interface ClientDAO {
    Client save(String client_id, String name, String email, String phone) throws DaoException;
    boolean delete(String client_id) throws DaoException;
    Optional<Client> findById(String client_id) throws DaoException;
    Optional<Client> findByName(String name) throws DaoException;
    Optional<Client> findByEmail(String email) throws DaoException;
}
