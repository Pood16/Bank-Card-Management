package bank.model;

import bank.model.enums.CardType;
import bank.model.enums.Status;

import java.time.LocalDate;

public final class DebitCard extends Card {

    private final double dailyLimit;

    public DebitCard(String number, LocalDate expirationDate, Status status, String clientId, double dailyLimit) {
        super(number, expirationDate, status, clientId);
        this.dailyLimit = dailyLimit;
    }

    public double getDailyLimit() {
        return dailyLimit;
    }

    @Override
    public CardType getCardType(){
        return CardType.DEBIT;
    }

    @Override
    public boolean isOperationAllowed(double amount){
        return amount <= dailyLimit;
    }
}
