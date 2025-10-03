package bank.model;

import bank.model.enums.CardType;
import bank.model.enums.Status;

import java.time.LocalDate;

public final class PrepaidCard extends Card {

    private double balance;

    public PrepaidCard(String number, LocalDate expirationDate, Status status, String clientId, double balance) {
        super(number, expirationDate, status, clientId);
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public CardType getCardType(){
        return CardType.PREPAID;
    }

    @Override
    public boolean isOperationAllowed(double amount){
        return amount <= balance;
    }

}
