package bank.ui;

import bank.dao.impl.*;
import bank.exception.ServiceException;
import bank.services.*;
import bank.services.impl.*;

import java.util.Scanner;

public class MainMenu {

    private final Scanner scanner;
    private final ClientService clientService;
    private final CardService cardService;
    private final OperationService operationService;
    private final FraudService fraudService;
    private final ReportService reportService;

    public MainMenu() {
        this.scanner = new Scanner(System.in);

        ClientdaoImplementation clientDAO = new ClientdaoImplementation();
        CarddaoImplementation cardDAO = new CarddaoImplementation();
        OperationDAOImplementation operationDAO = new OperationDAOImplementation();
        AlertDAOImplementation alertDAO = new AlertDAOImplementation();

        this.clientService = new ClientServiceImplementation(clientDAO);
        this.cardService = new CardServiceImplementation(cardDAO);
        this.operationService = new OperationServiceImplementation(operationDAO, cardDAO);
        this.fraudService = new FraudServiceImplementation(alertDAO, operationDAO);
        this.reportService = new ReportServiceImplementation(cardDAO, operationDAO, alertDAO);
    }

    public void display() {
        while (true) {
            System.out.println("\n========== BANKING CARD MANAGEMENT SYSTEM ==========");
            System.out.println("1. Client Management");
            System.out.println("2. Card Management");
            System.out.println("3. Operations");
            System.out.println("4. Fraud Detection");
            System.out.println("5. Reports");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> new ClientMenu(scanner, clientService).display();
                case 2 -> new CardMenu(scanner, cardService, clientService).display();
                case 3 -> new OperationMenu(scanner, operationService, cardService, fraudService).display();
                case 4 -> new FraudMenu(scanner, fraudService).display();
                case 5 -> new ReportMenu(scanner, reportService).display();
                case 6 -> {
                    System.out.println("Exiting... Goodbye!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
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

