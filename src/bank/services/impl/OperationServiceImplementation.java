package bank.services.impl;

import bank.dao.CardDAO;
import bank.dao.OperationDAO;
import bank.exception.DaoException;
import bank.exception.ServiceException;
import bank.model.Card;
import bank.model.CardOperation;
import bank.model.enums.OperationType;
import bank.model.enums.Status;
import bank.services.OperationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class OperationServiceImplementation implements OperationService {

    private final OperationDAO operationDAO;
    private final CardDAO cardDAO;

    public OperationServiceImplementation(OperationDAO operationDAO, CardDAO cardDAO) {
        this.operationDAO = operationDAO;
        this.cardDAO = cardDAO;
    }

    @Override
    public CardOperation recordPurchase(String cardId, double amount, String location) throws ServiceException {
        return recordOperation(cardId, amount, location, OperationType.PURCHASE);
    }

    @Override
    public CardOperation recordWithdrawal(String cardId, double amount, String location) throws ServiceException {
        return recordOperation(cardId, amount, location, OperationType.WITHDRAWAL);
    }

    @Override
    public CardOperation recordOnlinePayment(String cardId, double amount, String location) throws ServiceException {
        return recordOperation(cardId, amount, location, OperationType.ONLINE_PAYMENT);
    }

    private CardOperation recordOperation(String cardId, double amount, String location, OperationType type) throws ServiceException {
        try {
            Optional<Card> optionalCard = cardDAO.findById(cardId);
            if (optionalCard.isEmpty()) {
                throw new ServiceException("Card not found");
            }

            Card card = optionalCard.get();

            if (card.getStatus() != Status.ACTIVE) {
                throw new ServiceException("Card is not active. Status: " + card.getStatus());
            }

            if (!card.isOperationAllowed(amount)) {
                throw new ServiceException("Operation amount exceeds card limit");
            }

            String operationId = UUID.randomUUID().toString();
            LocalDateTime operationDate = LocalDateTime.now();

            CardOperation operation = new CardOperation(operationId, operationDate, amount, type, location, cardId);
            return operationDAO.create(operation);

        } catch (DaoException e) {
            throw new ServiceException("Failed to record operation: " + e.getMessage());
        }
    }

    @Override
    public List<CardOperation> getOperationsByCard(String cardId) throws ServiceException {
        try {
            return operationDAO.findByCardId(cardId);
        } catch (DaoException e) {
            throw new ServiceException("Failed to get operations: " + e.getMessage());
        }
    }

    @Override
    public List<CardOperation> getOperationsByClient(String clientId) throws ServiceException {
        try {
            List<Card> clientCards = cardDAO.findByClientId(clientId);
            return clientCards.stream()
                    .flatMap(card -> {
                        try {
                            return operationDAO.findByCardId(card.getId()).stream();
                        } catch (DaoException e) {
                            return java.util.stream.Stream.empty();
                        }
                    })
                    .collect(Collectors.toList());
        } catch (DaoException e) {
            throw new ServiceException("Failed to get operations by client: " + e.getMessage());
        }
    }

    @Override
    public List<CardOperation> filterOperationsByType(String cardId, OperationType type) throws ServiceException {
        try {
            return operationDAO.findByCardIdAndType(cardId, type);
        } catch (DaoException e) {
            throw new ServiceException("Failed to filter operations by type: " + e.getMessage());
        }
    }

    @Override
    public List<CardOperation> filterOperationsByDateRange(String cardId, LocalDateTime startDate, LocalDateTime endDate) throws ServiceException {
        try {
            return operationDAO.findByCardIdAndDateRange(cardId, startDate, endDate);
        } catch (DaoException e) {
            throw new ServiceException("Failed to filter operations by date range: " + e.getMessage());
        }
    }

    @Override
    public Optional<CardOperation> getOperationById(String operationId) throws ServiceException {
        try {
            return operationDAO.findById(operationId);
        } catch (DaoException e) {
            throw new ServiceException("Failed to get operation: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteOperation(String operationId) throws ServiceException {
        try {
            return operationDAO.delete(operationId);
        } catch (DaoException e) {
            throw new ServiceException("Failed to delete operation: " + e.getMessage());
        }
    }
}

