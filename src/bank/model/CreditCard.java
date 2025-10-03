package bank.model;

import bank.model.enums.CardType;
import bank.model.enums.Status;

import java.time.LocalDate;

public final class CreditCard extends Card {

    private final double monthlyLimit;
    private final double interestRate;

    public CreditCard(String number, LocalDate expirationDate, Status status, String clientId, double monthlyLimit, double interestRate) {
        super(number, expirationDate, status, clientId);
        this.interestRate = interestRate;
        this.monthlyLimit = monthlyLimit;
    }

    public double getMonthlyLimit() {
        return monthlyLimit;
    }

    public double getInterestRate() {
        return interestRate;
    }

    @Override
    public CardType getCardType(){
        return CardType.CREDIT;
    }

    @Override
    public boolean isOperationAllowed(double amount){
        return amount <= monthlyLimit;
    }


}
