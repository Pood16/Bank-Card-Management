package bank.ui;

import bank.exception.ServiceException;
import bank.model.FraudAlert;
import bank.services.FraudService;

import java.util.List;
import java.util.Scanner;

public class FraudMenu {

    private final Scanner scanner;
    private final FraudService fraudService;

    public FraudMenu(Scanner scanner, FraudService fraudService) {
        this.scanner = scanner;
        this.fraudService = fraudService;
    }

    public void display() {
        while (true) {
            System.out.println("\n===== FRAUD DETECTION =====");
            System.out.println("1. View alerts by card");
            System.out.println("2. View all critical alerts");
            System.out.println("3. View all alerts");
            System.out.println("4. Back to main menu");
            System.out.print("Choose an option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> viewAlertsByCard();
                case 2 -> viewCriticalAlerts();
                case 3 -> viewAllAlerts();
                case 4 -> { return; }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void viewAlertsByCard() {
        try {
            System.out.print("Enter card ID: ");
            String cardId = scanner.nextLine();

            List<FraudAlert> alerts = fraudService.getAlertsByCard(cardId);
            displayAlerts(alerts);
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewCriticalAlerts() {
        try {
            List<FraudAlert> alerts = fraudService.getCriticalAlerts();
            System.out.println("\n===== CRITICAL ALERTS =====");
            displayAlerts(alerts);
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewAllAlerts() {
        try {
            List<FraudAlert> alerts = fraudService.getAllAlerts();
            displayAlerts(alerts);
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void displayAlerts(List<FraudAlert> alerts) {
        if (alerts.isEmpty()) {
            System.out.println("No alerts found.");
        } else {
            System.out.println("\n===== FRAUD ALERTS =====");
            for (FraudAlert alert : alerts) {
                displayAlert(alert);
                System.out.println("--------------------");
            }
        }
    }

    private void displayAlert(FraudAlert alert) {
        System.out.println("Alert ID: " + alert.id());
        System.out.println("Card ID: " + alert.cardId());
        System.out.println("Level: " + alert.level());
        System.out.println("Description: " + alert.description());
    }

    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

