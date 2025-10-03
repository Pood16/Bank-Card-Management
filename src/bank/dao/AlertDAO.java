package bank.dao;

import bank.exception.DaoException;
import bank.model.FraudAlert;
import bank.model.enums.AlertLevelType;

import java.util.List;
import java.util.Optional;

public interface AlertDAO {
    FraudAlert create(FraudAlert alert) throws DaoException;
    boolean delete(String id) throws DaoException;
    Optional<FraudAlert> findById(String id) throws DaoException;
    List<FraudAlert> findByCardId(String cardId) throws DaoException;
    List<FraudAlert> findByLevel(AlertLevelType level) throws DaoException;
    List<FraudAlert> findCriticalAlerts() throws DaoException;
    List<FraudAlert> findAll() throws DaoException;
}

