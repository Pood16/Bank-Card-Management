package bank.ui;

import bank.exception.ServiceException;
import bank.model.Card;
import bank.model.CreditCard;
import bank.model.DebitCard;
import bank.model.PrepaidCard;
import bank.model.enums.CardType;
import bank.model.enums.Status;
import bank.services.CardService;
import bank.services.ClientService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class CardMenu {

    private final Scanner scanner;
    private final CardService cardService;
    private final ClientService clientService;

    public CardMenu(Scanner scanner, CardService cardService, ClientService clientService) {
        this.scanner = scanner;
        this.cardService = cardService;
        this.clientService = clientService;
    }

    public void display() {
        while (true) {
            System.out.println("\n===== CARD MANAGEMENT =====");
            System.out.println("1. Create new card");
            System.out.println("2. Activate card");
            System.out.println("3. Suspend card");
            System.out.println("4. Block card");
            System.out.println("5. Renew card");
            System.out.println("6. View card details");
            System.out.println("7. List cards by client");
            System.out.println("8. List cards by status");
            System.out.println("9. Delete card");
            System.out.println("10. Back to main menu");
            System.out.print("Choose an option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> createCard();
                case 2 -> activateCard();
                case 3 -> suspendCard();
                case 4 -> blockCard();
                case 5 -> renewCard();
                case 6 -> viewCardDetails();
                case 7 -> listCardsByClient();
                case 8 -> listCardsByStatus();
                case 9 -> deleteCard();
                case 10 -> { return; }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void createCard() {
        try {
            System.out.print("Enter client ID: ");
            String clientId = scanner.nextLine();

            if (clientService.findClientById(clientId).isEmpty()) {
                System.out.println("Client not found.");
                return;
            }

            System.out.println("Select card type:");
            System.out.println("1. Debit Card");
            System.out.println("2. Credit Card");
            System.out.println("3. Prepaid Card");
            System.out.print("Choice: ");
            int typeChoice = getIntInput();

            CardType cardType = switch (typeChoice) {
                case 1 -> CardType.DEBIT;
                case 2 -> CardType.CREDIT;
                case 3 -> CardType.PREPAID;
                default -> null;
            };

            if (cardType == null) {
                System.out.println("Invalid card type.");
                return;
            }

            double limit;
            Double secondaryLimit = null;

            if (cardType == CardType.DEBIT) {
                System.out.print("Enter daily limit: ");
                limit = getDoubleInput();
            } else if (cardType == CardType.CREDIT) {
                System.out.print("Enter monthly limit: ");
                limit = getDoubleInput();
                System.out.print("Enter interest rate (e.g., 0.195 for 19.5%): ");
                secondaryLimit = getDoubleInput();
            } else {
                System.out.print("Enter initial balance: ");
                limit = getDoubleInput();
            }

            Card card = cardService.createCard(clientId, cardType, limit, secondaryLimit);
            System.out.println("Card created successfully!");
            displayCard(card);
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void activateCard() {
        try {
            System.out.print("Enter card ID: ");
            String cardId = scanner.nextLine();

            Card card = cardService.activateCard(cardId);
            System.out.println("Card activated successfully!");
            displayCard(card);
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void suspendCard() {
        try {
            System.out.print("Enter card ID: ");
            String cardId = scanner.nextLine();

            Card card = cardService.suspendCard(cardId);
            System.out.println("Card suspended successfully!");
            displayCard(card);
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void blockCard() {
        try {
            System.out.print("Enter card ID: ");
            String cardId = scanner.nextLine();

            Card card = cardService.blockCard(cardId);
            System.out.println("Card blocked successfully!");
            displayCard(card);
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void renewCard() {
        try {
            System.out.print("Enter card ID to renew: ");
            String cardId = scanner.nextLine();

            Card newCard = cardService.renewCard(cardId);
            System.out.println("Card renewed successfully!");
            displayCard(newCard);
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewCardDetails() {
        try {
            System.out.print("Enter card ID: ");
            String cardId = scanner.nextLine();

            Optional<Card> optionalCard = cardService.getCardDetails(cardId);
            if (optionalCard.isPresent()) {
                displayCard(optionalCard.get());
            } else {
                System.out.println("Card not found.");
            }
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listCardsByClient() {
        try {
            System.out.print("Enter client ID: ");
            String clientId = scanner.nextLine();

            List<Card> cards = cardService.findCardsByClient(clientId);
            if (cards.isEmpty()) {
                System.out.println("No cards found for this client.");
            } else {
                System.out.println("\n===== CLIENT CARDS =====");
                for (Card card : cards) {
                    displayCard(card);
                    System.out.println("--------------------");
                }
            }
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listCardsByStatus() {
        try {
            System.out.println("Select status:");
            System.out.println("1. ACTIVE");
            System.out.println("2. SUSPENDED");
            System.out.println("3. BLOCKED");
            System.out.print("Choice: ");
            int statusChoice = getIntInput();

            Status status = switch (statusChoice) {
                case 1 -> Status.ACTIVE;
                case 2 -> Status.SUSPENDED;
                case 3 -> Status.BLOCKED;
                default -> null;
            };

            if (status == null) {
                System.out.println("Invalid status.");
                return;
            }

            List<Card> cards = cardService.getCardsByStatus(status);
            if (cards.isEmpty()) {
                System.out.println("No cards found with status: " + status);
            } else {
                System.out.println("\n===== CARDS WITH STATUS: " + status + " =====");
                for (Card card : cards) {
                    displayCard(card);
                    System.out.println("--------------------");
                }
            }
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteCard() {
        try {
            System.out.print("Enter card ID to delete: ");
            String cardId = scanner.nextLine();
            System.out.print("Are you sure? (yes/no): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("yes")) {
                boolean deleted = cardService.deleteCard(cardId);
                if (deleted) {
                    System.out.println("Card deleted successfully!");
                } else {
                    System.out.println("Card not found.");
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void displayCard(Card card) {
        System.out.println("Card ID: " + card.getId());
        System.out.println("Number: " + card.getNumber());
        System.out.println("Type: " + card.getCardType());
        System.out.println("Expiration: " + card.getExpirationDate());
        System.out.println("Status: " + card.getStatus());
        System.out.println("Client ID: " + card.getClientId());

        if (card instanceof DebitCard) {
            System.out.println("Daily Limit: " + ((DebitCard) card).getDailyLimit());
        } else if (card instanceof CreditCard) {
            System.out.println("Monthly Limit: " + ((CreditCard) card).getMonthlyLimit());
            System.out.println("Interest Rate: " + ((CreditCard) card).getInterestRate());
        } else if (card instanceof PrepaidCard) {
            System.out.println("Balance: " + ((PrepaidCard) card).getBalance());
        }
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

