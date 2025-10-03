package bank.dao;

import bank.exception.DaoException;
import bank.model.Card;
import bank.model.enums.Status;

import java.util.List;
import java.util.Optional;

public interface CardDAO {

    Card create(Card card) throws DaoException;
    boolean delete(String id) throws DaoException;
    Optional<Card> findById(String id) throws DaoException;
    List<Card> findByClientId(String id) throws DaoException;
    Optional<Card> findByNumber(String number) throws DaoException;
    List<Card> findByStatus(Status status) throws DaoException;
    List<Card> findAll() throws DaoException;
    boolean updateStatus(String id, Status newStatus) throws DaoException;
    boolean update(Card card) throws DaoException;
}
