package bank.services.impl;

import bank.dao.AlertDAO;
import bank.dao.OperationDAO;
import bank.exception.DaoException;
import bank.exception.ServiceException;
import bank.model.CardOperation;
import bank.model.FraudAlert;
import bank.model.enums.AlertLevelType;
import bank.services.FraudService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class FraudServiceImplementation implements FraudService {

    private final AlertDAO alertDAO;
    private final OperationDAO operationDAO;
    private static final double LARGE_AMOUNT_THRESHOLD = 5000.0;
    private static final int FREQUENT_OPS_TIME_WINDOW_MINUTES = 30;
    private static final int MAX_OPS_IN_TIME_WINDOW = 5;

    public FraudServiceImplementation(AlertDAO alertDAO, OperationDAO operationDAO) {
        this.alertDAO = alertDAO;
        this.operationDAO = operationDAO;
    }

    @Override
    public void analyzeOperationInRealTime(CardOperation operation) throws ServiceException {
        try {
            if (detectLargeAmount(operation)) {
                generateAlert(operation.cardId(),
                    "Large amount transaction: " + operation.amount() + " at " + operation.location(),
                    AlertLevelType.WARNING);
            }

            if (detectFrequentOperations(operation)) {
                generateAlert(operation.cardId(),
                    "Multiple operations in short time window",
                    AlertLevelType.CRITICAL);
            }

            if (detectMultipleLocations(operation)) {
                generateAlert(operation.cardId(),
                    "Operations in different locations detected: " + operation.location(),
                    AlertLevelType.CRITICAL);
            }

        } catch (Exception e) {
            throw new ServiceException("Failed to analyze operation: " + e.getMessage());
        }
    }

    private boolean detectLargeAmount(CardOperation operation) {
        return operation.amount() > LARGE_AMOUNT_THRESHOLD;
    }

    private boolean detectFrequentOperations(CardOperation operation) throws DaoException {
        LocalDateTime windowStart = operation.date().minus(FREQUENT_OPS_TIME_WINDOW_MINUTES, ChronoUnit.MINUTES);
        List<CardOperation> recentOps = operationDAO.findByCardIdAndDateRange(
            operation.cardId(),
            windowStart,
            operation.date()
        );
        return recentOps.size() >= MAX_OPS_IN_TIME_WINDOW;
    }

    private boolean detectMultipleLocations(CardOperation operation) throws DaoException {
        LocalDateTime windowStart = operation.date().minus(60, ChronoUnit.MINUTES);
        List<CardOperation> recentOps = operationDAO.findByCardIdAndDateRange(
            operation.cardId(),
            windowStart,
            operation.date()
        );

        Set<String> locations = recentOps.stream()
            .map(CardOperation::location)
            .collect(Collectors.toSet());

        return locations.size() > 1;
    }

    @Override
    public List<FraudAlert> getAlertsByCard(String cardId) throws ServiceException {
        try {
            return alertDAO.findByCardId(cardId);
        } catch (DaoException e) {
            throw new ServiceException("Failed to get alerts: " + e.getMessage());
        }
    }

    @Override
    public List<FraudAlert> getCriticalAlerts() throws ServiceException {
        try {
            return alertDAO.findCriticalAlerts();
        } catch (DaoException e) {
            throw new ServiceException("Failed to get critical alerts: " + e.getMessage());
        }
    }

    @Override
    public List<FraudAlert> getAllAlerts() throws ServiceException {
        try {
            return alertDAO.findAll();
        } catch (DaoException e) {
            throw new ServiceException("Failed to get all alerts: " + e.getMessage());
        }
    }

    @Override
    public FraudAlert generateAlert(String cardId, String description, AlertLevelType level) throws ServiceException {
        try {
            String alertId = UUID.randomUUID().toString();
            FraudAlert alert = new FraudAlert(alertId, description, level, cardId);
            return alertDAO.create(alert);
        } catch (DaoException e) {
            throw new ServiceException("Failed to generate alert: " + e.getMessage());
        }
    }
}

