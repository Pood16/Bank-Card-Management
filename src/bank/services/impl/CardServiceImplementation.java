package bank.services.impl;

import bank.dao.CardDAO;
import bank.exception.DaoException;
import bank.exception.ServiceException;
import bank.model.*;
import bank.model.enums.CardType;
import bank.model.enums.Status;
import bank.services.CardService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class CardServiceImplementation implements CardService {

    private final CardDAO cardDAO;

    public CardServiceImplementation(CardDAO cardDAO) {
        this.cardDAO = cardDAO;
    }

    @Override
    public Card createCard(String clientId, CardType cardType, double limit, Double secondaryLimit) throws ServiceException {
        try {
            String cardNumber = generateCardNumber();
            LocalDate expirationDate = LocalDate.now().plusYears(3);
            Status status = Status.ACTIVE;

            Card card = switch (cardType) {
                case DEBIT -> new DebitCard(cardNumber, expirationDate, status, clientId, limit);
                case CREDIT -> new CreditCard(cardNumber, expirationDate, status, clientId, limit,
                        secondaryLimit != null ? secondaryLimit : 0.0);
                case PREPAID -> new PrepaidCard(cardNumber, expirationDate, status, clientId, limit);
            };

            return cardDAO.create(card);
        } catch (DaoException e) {
            throw new ServiceException("Failed to create card: " + e.getMessage());
        }
    }

    @Override
    public Card activateCard(String cardId) throws ServiceException {
        try {
            Optional<Card> optionalCard = cardDAO.findById(cardId);
            if (optionalCard.isEmpty()) {
                throw new ServiceException("Card not found");
            }

            Card card = optionalCard.get();
            card.activeCard();
            cardDAO.updateStatus(cardId, Status.ACTIVE);
            return card;
        } catch (DaoException e) {
            throw new ServiceException("Failed to activate card: " + e.getMessage());
        }
    }

    @Override
    public Card suspendCard(String cardId) throws ServiceException {
        try {
            Optional<Card> optionalCard = cardDAO.findById(cardId);
            if (optionalCard.isEmpty()) {
                throw new ServiceException("Card not found");
            }

            Card card = optionalCard.get();
            card.suspendCard();
            cardDAO.updateStatus(cardId, Status.SUSPENDED);
            return card;
        } catch (DaoException e) {
            throw new ServiceException("Failed to suspend card: " + e.getMessage());
        }
    }

    @Override
    public Card blockCard(String cardId) throws ServiceException {
        try {
            Optional<Card> optionalCard = cardDAO.findById(cardId);
            if (optionalCard.isEmpty()) {
                throw new ServiceException("Card not found");
            }

            Card card = optionalCard.get();
            card.blockCard();
            cardDAO.updateStatus(cardId, Status.BLOCKED);
            return card;
        } catch (DaoException e) {
            throw new ServiceException("Failed to block card: " + e.getMessage());
        }
    }

    @Override
    public Card renewCard(String cardId) throws ServiceException {
        try {
            Optional<Card> optionalCard = cardDAO.findById(cardId);
            if (optionalCard.isEmpty()) {
                throw new ServiceException("Card not found");
            }

            Card oldCard = optionalCard.get();
            String newCardNumber = generateCardNumber();
            LocalDate newExpirationDate = LocalDate.now().plusYears(3);

            Card newCard = switch (oldCard.getCardType()) {
                case DEBIT -> new DebitCard(newCardNumber, newExpirationDate, Status.ACTIVE,
                        oldCard.getClientId(), ((DebitCard) oldCard).getDailyLimit());
                case CREDIT -> new CreditCard(newCardNumber, newExpirationDate, Status.ACTIVE,
                        oldCard.getClientId(), ((CreditCard) oldCard).getMonthlyLimit(),
                        ((CreditCard) oldCard).getInterestRate());
                case PREPAID -> new PrepaidCard(newCardNumber, newExpirationDate, Status.ACTIVE,
                        oldCard.getClientId(), ((PrepaidCard) oldCard).getBalance());
            };

            return cardDAO.create(newCard);
        } catch (DaoException e) {
            throw new ServiceException("Failed to renew card: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyLimitBeforeOperation(String cardId, double amount) throws ServiceException {
        try {
            Optional<Card> optionalCard = cardDAO.findById(cardId);
            if (optionalCard.isEmpty()) {
                throw new ServiceException("Card not found");
            }

            Card card = optionalCard.get();

            if (card.getStatus() != Status.ACTIVE) {
                return false;
            }

            return card.isOperationAllowed(amount);
        } catch (DaoException e) {
            throw new ServiceException("Failed to verify limit: " + e.getMessage());
        }
    }

    @Override
    public List<Card> findCardsByClient(String clientId) throws ServiceException {
        try {
            return cardDAO.findByClientId(clientId);
        } catch (DaoException e) {
            throw new ServiceException("Failed to find cards: " + e.getMessage());
        }
    }

    @Override
    public Optional<Card> getCardDetails(String cardId) throws ServiceException {
        try {
            return cardDAO.findById(cardId);
        } catch (DaoException e) {
            throw new ServiceException("Failed to get card details: " + e.getMessage());
        }
    }

    @Override
    public List<Card> getCardsByStatus(Status status) throws ServiceException {
        try {
            return cardDAO.findByStatus(status);
        } catch (DaoException e) {
            throw new ServiceException("Failed to get cards by status: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteCard(String cardId) throws ServiceException {
        try {
            return cardDAO.delete(cardId);
        } catch (DaoException e) {
            throw new ServiceException("Failed to delete card: " + e.getMessage());
        }
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (i > 0) {
                cardNumber.append("-");
            }
            for (int j = 0; j < 4; j++) {
                cardNumber.append(random.nextInt(10));
            }
        }
        return cardNumber.toString();
    }
}

