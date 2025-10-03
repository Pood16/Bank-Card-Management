package bank.services;

import bank.exception.ServiceException;
import bank.model.Card;
import bank.model.CardOperation;

import java.util.List;
import java.util.Map;

public interface ReportService {
    List<Card> getTop5MostUsedCards() throws ServiceException;
    Map<String, Integer> getMonthlyStatisticsByType(int month, int year) throws ServiceException;
    List<Card> getBlockedCards() throws ServiceException;
    List<Card> getSuspiciousCards() throws ServiceException;
    Map<String, Object> getClientStatistics(String clientId) throws ServiceException;
}

