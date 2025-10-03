package bank.services.impl;

import bank.dao.AlertDAO;
import bank.dao.CardDAO;
import bank.dao.OperationDAO;
import bank.exception.DaoException;
import bank.exception.ServiceException;
import bank.model.Card;
import bank.model.CardOperation;
import bank.model.FraudAlert;
import bank.model.enums.AlertLevelType;
import bank.model.enums.Status;
import bank.services.ReportService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ReportServiceImplementation implements ReportService {

    private final CardDAO cardDAO;
    private final OperationDAO operationDAO;
    private final AlertDAO alertDAO;

    public ReportServiceImplementation(CardDAO cardDAO, OperationDAO operationDAO, AlertDAO alertDAO) {
        this.cardDAO = cardDAO;
        this.operationDAO = operationDAO;
        this.alertDAO = alertDAO;
    }

    @Override
    public List<Card> getTop5MostUsedCards() throws ServiceException {
        try {
            List<Card> allCards = cardDAO.findAll();

            return allCards.stream()
                .sorted((card1, card2) -> {
                    try {
                        int ops1 = operationDAO.findByCardId(card1.getId()).size();
                        int ops2 = operationDAO.findByCardId(card2.getId()).size();
                        return Integer.compare(ops2, ops1);
                    } catch (DaoException e) {
                        return 0;
                    }
                })
                .limit(5)
                .collect(Collectors.toList());
        } catch (DaoException e) {
            throw new ServiceException("Failed to get top used cards: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Integer> getMonthlyStatisticsByType(int month, int year) throws ServiceException {
        try {
            LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

            List<CardOperation> operations = operationDAO.findByDateRange(startDate, endDate);

            Map<String, Integer> stats = new HashMap<>();
            stats.put("PURCHASE", 0);
            stats.put("WITHDRAWAL", 0);
            stats.put("ONLINE_PAYMENT", 0);

            for (CardOperation op : operations) {
                String type = op.type().name();
                stats.put(type, stats.get(type) + 1);
            }

            return stats;
        } catch (DaoException e) {
            throw new ServiceException("Failed to get monthly statistics: " + e.getMessage());
        }
    }

    @Override
    public List<Card> getBlockedCards() throws ServiceException {
        try {
            return cardDAO.findByStatus(Status.BLOCKED);
        } catch (DaoException e) {
            throw new ServiceException("Failed to get blocked cards: " + e.getMessage());
        }
    }

    @Override
    public List<Card> getSuspiciousCards() throws ServiceException {
        try {
            List<FraudAlert> criticalAlerts = alertDAO.findByLevel(AlertLevelType.CRITICAL);

            Set<String> suspiciousCardIds = criticalAlerts.stream()
                .map(FraudAlert::cardId)
                .collect(Collectors.toSet());

            List<Card> allCards = cardDAO.findAll();

            return allCards.stream()
                .filter(card -> suspiciousCardIds.contains(card.getId()))
                .collect(Collectors.toList());
        } catch (DaoException e) {
            throw new ServiceException("Failed to get suspicious cards: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getClientStatistics(String clientId) throws ServiceException {
        try {
            List<Card> clientCards = cardDAO.findByClientId(clientId);
            Map<String, Object> stats = new HashMap<>();

            stats.put("totalCards", clientCards.size());

            int totalOperations = 0;
            double totalAmount = 0.0;
            Map<String, Integer> operationsPerCard = new HashMap<>();
            Map<String, Double> amountPerCard = new HashMap<>();

            for (Card card : clientCards) {
                List<CardOperation> operations = operationDAO.findByCardId(card.getId());
                int opsCount = operations.size();
                double cardAmount = operations.stream()
                    .mapToDouble(CardOperation::amount)
                    .sum();

                totalOperations += opsCount;
                totalAmount += cardAmount;

                operationsPerCard.put(card.getNumber(), opsCount);
                amountPerCard.put(card.getNumber(), cardAmount);
            }

            stats.put("totalOperations", totalOperations);
            stats.put("totalAmount", totalAmount);
            stats.put("operationsPerCard", operationsPerCard);
            stats.put("amountPerCard", amountPerCard);

            return stats;
        } catch (DaoException e) {
            throw new ServiceException("Failed to get client statistics: " + e.getMessage());
        }
    }
}

