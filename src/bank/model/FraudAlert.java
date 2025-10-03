package bank.model;

import bank.model.enums.AlertLevelType;

public record FraudAlert(String id, String description, AlertLevelType level, String cardId) {}
