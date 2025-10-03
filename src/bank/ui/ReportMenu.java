package bank.ui;

import bank.exception.ServiceException;
import bank.model.Card;
import bank.services.ReportService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ReportMenu {

    private final Scanner scanner;
    private final ReportService reportService;

    public ReportMenu(Scanner scanner, ReportService reportService) {
        this.scanner = scanner;
        this.reportService = reportService;
    }

    public void display() {
        while (true) {
            System.out.println("\n===== REPORTS =====");
            System.out.println("1. Top 5 most used cards");
            System.out.println("2. Monthly statistics by operation type");
            System.out.println("3. Blocked cards report");
            System.out.println("4. Suspicious cards report");
            System.out.println("5. Client statistics");
            System.out.println("6. Back to main menu");
            System.out.print("Choose an option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> showTop5Cards();
                case 2 -> showMonthlyStatistics();
                case 3 -> showBlockedCards();
                case 4 -> showSuspiciousCards();
                case 5 -> showClientStatistics();
                case 6 -> { return; }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void showTop5Cards() {
        try {
            List<Card> topCards = reportService.getTop5MostUsedCards();
            if (topCards.isEmpty()) {
                System.out.println("No cards found.");
            } else {
                System.out.println("\n===== TOP 5 MOST USED CARDS =====");
                int rank = 1;
                for (Card card : topCards) {
                    System.out.println(rank + ". Card Number: " + card.getNumber());
                    System.out.println("   Card ID: " + card.getId());
                    System.out.println("   Type: " + card.getCardType());
                    System.out.println("   Status: " + card.getStatus());
                    System.out.println("--------------------");
                    rank++;
                }
            }
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showMonthlyStatistics() {
        try {
            System.out.print("Enter month (1-12): ");
            int month = getIntInput();
            System.out.print("Enter year (e.g., 2025): ");
            int year = getIntInput();

            if (month < 1 || month > 12) {
                System.out.println("Invalid month.");
                return;
            }

            Map<String, Integer> stats = reportService.getMonthlyStatisticsByType(month, year);
            System.out.println("\n===== MONTHLY STATISTICS FOR " + month + "/" + year + " =====");
            System.out.println("Purchases: " + stats.get("PURCHASE"));
            System.out.println("Withdrawals: " + stats.get("WITHDRAWAL"));
            System.out.println("Online Payments: " + stats.get("ONLINE_PAYMENT"));
            System.out.println("Total Operations: " + (stats.get("PURCHASE") + stats.get("WITHDRAWAL") + stats.get("ONLINE_PAYMENT")));
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showBlockedCards() {
        try {
            List<Card> blockedCards = reportService.getBlockedCards();
            if (blockedCards.isEmpty()) {
                System.out.println("No blocked cards found.");
            } else {
                System.out.println("\n===== BLOCKED CARDS REPORT =====");
                for (Card card : blockedCards) {
                    System.out.println("Card Number: " + card.getNumber());
                    System.out.println("Card ID: " + card.getId());
                    System.out.println("Type: " + card.getCardType());
                    System.out.println("Client ID: " + card.getClientId());
                    System.out.println("--------------------");
                }
            }
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showSuspiciousCards() {
        try {
            List<Card> suspiciousCards = reportService.getSuspiciousCards();
            if (suspiciousCards.isEmpty()) {
                System.out.println("No suspicious cards found.");
            } else {
                System.out.println("\n===== SUSPICIOUS CARDS REPORT =====");
                System.out.println("Cards with CRITICAL fraud alerts:");
                for (Card card : suspiciousCards) {
                    System.out.println("Card Number: " + card.getNumber());
                    System.out.println("Card ID: " + card.getId());
                    System.out.println("Type: " + card.getCardType());
                    System.out.println("Status: " + card.getStatus());
                    System.out.println("Client ID: " + card.getClientId());
                    System.out.println("--------------------");
                }
            }
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showClientStatistics() {
        try {
            System.out.print("Enter client ID: ");
            String clientId = scanner.nextLine();

            Map<String, Object> stats = reportService.getClientStatistics(clientId);
            System.out.println("\n===== CLIENT STATISTICS =====");
            System.out.println("Total Cards: " + stats.get("totalCards"));
            System.out.println("Total Operations: " + stats.get("totalOperations"));
            System.out.println("Total Amount: " + stats.get("totalAmount"));

            System.out.println("\nOperations per card:");
            @SuppressWarnings("unchecked")
            Map<String, Integer> opsPerCard = (Map<String, Integer>) stats.get("operationsPerCard");
            for (Map.Entry<String, Integer> entry : opsPerCard.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " operations");
            }

            System.out.println("\nAmount per card:");
            @SuppressWarnings("unchecked")
            Map<String, Double> amountPerCard = (Map<String, Double>) stats.get("amountPerCard");
            for (Map.Entry<String, Double> entry : amountPerCard.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

