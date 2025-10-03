package bank.model;


import bank.model.enums.CardType;
import bank.model.enums.Status;

import java.time.LocalDate;
import java.util.UUID;



public sealed abstract class Card permits DebitCard, CreditCard, PrepaidCard{

    private final String id;
    private final String number;
    private final LocalDate expirationDate;
    private Status status;
    private final String clientId;


    protected Card(String number, LocalDate expirationDate, Status status, String clientId) {
        this.id = UUID.randomUUID().toString();
        this.number = number;
        this.expirationDate = expirationDate;
        this.status = status;
        this.clientId = clientId;
    }

    public String getId() {
        return id;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public String getNumber() {
        return number;
    }

    public Status getStatus() {
        return status;
    }

    public String getClientId() {
        return clientId;
    }

    public void activeCard() {
        this.status = Status.ACTIVE;
    }

    public void blockCard() {
        this.status = Status.BLOCKED;
    }

    public void suspendCard() {
        this.status = Status.SUSPENDED;
    }

    public abstract CardType getCardType();

    public abstract boolean isOperationAllowed(double amount);

    @Override
    public String toString() {
        return "Card information: id= " + id + " | number= " + number + " | Card type: " + getCardType() + " | expirationDate= " + expirationDate + " | status= " + status + " | clientId= " + clientId;
    }
}
