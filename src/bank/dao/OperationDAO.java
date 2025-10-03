package bank.dao;

import bank.exception.DaoException;
import bank.model.CardOperation;
import bank.model.enums.OperationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OperationDAO {
    CardOperation create(CardOperation operation) throws DaoException;
    boolean delete(String id) throws DaoException;
    Optional<CardOperation> findById(String id) throws DaoException;
    List<CardOperation> findByCardId(String cardId) throws DaoException;
    List<CardOperation> findByType(OperationType type) throws DaoException;
    List<CardOperation> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws DaoException;
    List<CardOperation> findByCardIdAndDateRange(String cardId, LocalDateTime startDate, LocalDateTime endDate) throws DaoException;
    List<CardOperation> findByCardIdAndType(String cardId, OperationType type) throws DaoException;
    List<CardOperation> findAll() throws DaoException;
}

