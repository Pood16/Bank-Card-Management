package bank.services;

import bank.exception.ServiceException;
import bank.model.Card;
import bank.model.enums.CardType;
import bank.model.enums.Status;

import java.util.List;
import java.util.Optional;

public interface CardService {
    Card createCard(String clientId, CardType cardType, double limit, Double secondaryLimit) throws ServiceException;
    Card activateCard(String cardId) throws ServiceException;
    Card suspendCard(String cardId) throws ServiceException;
    Card blockCard(String cardId) throws ServiceException;
    Card renewCard(String cardId) throws ServiceException;
    boolean verifyLimitBeforeOperation(String cardId, double amount) throws ServiceException;
    List<Card> findCardsByClient(String clientId) throws ServiceException;
    Optional<Card> getCardDetails(String cardId) throws ServiceException;
    List<Card> getCardsByStatus(Status status) throws ServiceException;
    boolean deleteCard(String cardId) throws ServiceException;
}

