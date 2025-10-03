package bank.services;

import bank.exception.ServiceException;
import bank.model.CardOperation;
import bank.model.enums.OperationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OperationService {
    CardOperation recordPurchase(String cardId, double amount, String location) throws ServiceException;
    CardOperation recordWithdrawal(String cardId, double amount, String location) throws ServiceException;
    CardOperation recordOnlinePayment(String cardId, double amount, String location) throws ServiceException;
    List<CardOperation> getOperationsByCard(String cardId) throws ServiceException;
    List<CardOperation> getOperationsByClient(String clientId) throws ServiceException;
    List<CardOperation> filterOperationsByType(String cardId, OperationType type) throws ServiceException;
    List<CardOperation> filterOperationsByDateRange(String cardId, LocalDateTime startDate, LocalDateTime endDate) throws ServiceException;
    Optional<CardOperation> getOperationById(String operationId) throws ServiceException;
    boolean deleteOperation(String operationId) throws ServiceException;
}

