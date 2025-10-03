package bank.ui;

import bank.exception.ServiceException;
import bank.model.CardOperation;
import bank.model.enums.OperationType;
import bank.services.CardService;
import bank.services.FraudService;
import bank.services.OperationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class OperationMenu {

    private final Scanner scanner;
    private final OperationService operationService;
    private final CardService cardService;
    private final FraudService fraudService;

    public OperationMenu(Scanner scanner, OperationService operationService, CardService cardService, FraudService fraudService) {
        this.scanner = scanner;
        this.operationService = operationService;
        this.cardService = cardService;
        this.fraudService = fraudService;
    }

    public void display() {
        while (true) {
            System.out.println("\n===== OPERATIONS MANAGEMENT =====");
            System.out.println("1. Record purchase");
            System.out.println("2. Record withdrawal");
            System.out.println("3. Record online payment");
            System.out.println("4. View operations by card");
            System.out.println("5. View operations by client");
            System.out.println("6. Filter operations by type");
            System.out.println("7. Filter operations by date range");
            System.out.println("8. Back to main menu");
            System.out.print("Choose an option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> recordPurchase();
                case 2 -> recordWithdrawal();
                case 3 -> recordOnlinePayment();
                case 4 -> viewOperationsByCard();
                case 5 -> viewOperationsByClient();
                case 6 -> filterByType();
                case 7 -> filterByDateRange();
                case 8 -> { return; }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void recordPurchase() {
        try {
            System.out.print("Enter card ID: ");
            String cardId = scanner.nextLine();

            if (cardService.getCardDetails(cardId).isEmpty()) {
                System.out.println("Card not found.");
                return;
            }

            System.out.print("Enter amount: ");
            double amount = getDoubleInput();
            System.out.print("Enter location: ");
            String location = scanner.nextLine();

            CardOperation operation = operationService.recordPurchase(cardId, amount, location);
            System.out.println("Purchase recorded successfully!");
            displayOperation(operation);

            fraudService.analyzeOperationInRealTime(operation);
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void recordWithdrawal() {
        try {
            System.out.print("Enter card ID: ");
            String cardId = scanner.nextLine();

            if (cardService.getCardDetails(cardId).isEmpty()) {
                System.out.println("Card not found.");
                return;
            }

            System.out.print("Enter amount: ");
            double amount = getDoubleInput();
            System.out.print("Enter location: ");
            String location = scanner.nextLine();

            CardOperation operation = operationService.recordWithdrawal(cardId, amount, location);
            System.out.println("Withdrawal recorded successfully!");
            displayOperation(operation);

            fraudService.analyzeOperationInRealTime(operation);
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void recordOnlinePayment() {
        try {
            System.out.print("Enter card ID: ");
            String cardId = scanner.nextLine();

            if (cardService.getCardDetails(cardId).isEmpty()) {
                System.out.println("Card not found.");
                return;
            }

            System.out.print("Enter amount: ");
            double amount = getDoubleInput();
            System.out.print("Enter location/merchant: ");
            String location = scanner.nextLine();

            CardOperation operation = operationService.recordOnlinePayment(cardId, amount, location);
            System.out.println("Online payment recorded successfully!");
            displayOperation(operation);

            fraudService.analyzeOperationInRealTime(operation);
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewOperationsByCard() {
        try {
            System.out.print("Enter card ID: ");
            String cardId = scanner.nextLine();

            List<CardOperation> operations = operationService.getOperationsByCard(cardId);
            displayOperations(operations);
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewOperationsByClient() {
        try {
            System.out.print("Enter client ID: ");
            String clientId = scanner.nextLine();

            List<CardOperation> operations = operationService.getOperationsByClient(clientId);
            displayOperations(operations);
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void filterByType() {
        try {
            System.out.print("Enter card ID: ");
            String cardId = scanner.nextLine();

            System.out.println("Select operation type:");
            System.out.println("1. Purchase");
            System.out.println("2. Withdrawal");
            System.out.println("3. Online Payment");
            System.out.print("Choice: ");
            int typeChoice = getIntInput();

            OperationType type = switch (typeChoice) {
                case 1 -> OperationType.PURCHASE;
                case 2 -> OperationType.WITHDRAWAL;
                case 3 -> OperationType.ONLINE_PAYMENT;
                default -> null;
            };

            if (type == null) {
                System.out.println("Invalid type.");
                return;
            }

            List<CardOperation> operations = operationService.filterOperationsByType(cardId, type);
            displayOperations(operations);
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void filterByDateRange() {
        try {
            System.out.print("Enter card ID: ");
            String cardId = scanner.nextLine();

            System.out.print("Enter start date (yyyy-MM-dd HH:mm): ");
            String startStr = scanner.nextLine();
            System.out.print("Enter end date (yyyy-MM-dd HH:mm): ");
            String endStr = scanner.nextLine();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime startDate = LocalDateTime.parse(startStr, formatter);
            LocalDateTime endDate = LocalDateTime.parse(endStr, formatter);

            List<CardOperation> operations = operationService.filterOperationsByDateRange(cardId, startDate, endDate);
            displayOperations(operations);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void displayOperations(List<CardOperation> operations) {
        if (operations.isEmpty()) {
            System.out.println("No operations found.");
        } else {
            System.out.println("\n===== OPERATIONS =====");
            for (CardOperation operation : operations) {
                displayOperation(operation);
                System.out.println("--------------------");
            }
        }
    }

    private void displayOperation(CardOperation operation) {
        System.out.println("Operation ID: " + operation.id());
        System.out.println("Date: " + operation.date());
        System.out.println("Amount: " + operation.amount());
        System.out.println("Type: " + operation.type());
        System.out.println("Location: " + operation.location());
        System.out.println("Card ID: " + operation.cardId());
    }

    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private double getDoubleInput() {
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}

