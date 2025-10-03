package bank.model;

import bank.model.enums.OperationType;

import java.time.LocalDateTime;

public record CardOperation(String id, LocalDateTime date, double amount, OperationType type, String location, String cardId) {}
