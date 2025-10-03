package bank.services;

import bank.exception.ServiceException;
import bank.model.CardOperation;
import bank.model.FraudAlert;
import bank.model.enums.AlertLevelType;

import java.util.List;

public interface FraudService {
    void analyzeOperationInRealTime(CardOperation operation) throws ServiceException;
    List<FraudAlert> getAlertsByCard(String cardId) throws ServiceException;
    List<FraudAlert> getCriticalAlerts() throws ServiceException;
    List<FraudAlert> getAllAlerts() throws ServiceException;
    FraudAlert generateAlert(String cardId, String description, AlertLevelType level) throws ServiceException;
}

