package bank.ui;

import bank.exception.ServiceException;
import bank.model.Client;
import bank.services.ClientService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ClientMenu {

    private final Scanner scanner;
    private final ClientService clientService;

    public ClientMenu(Scanner scanner, ClientService clientService) {
        this.scanner = scanner;
        this.clientService = clientService;
    }

    public void display() {
        while (true) {
            System.out.println("\n===== CLIENT MANAGEMENT =====");
            System.out.println("1. Register new client");
            System.out.println("2. Update client info");
            System.out.println("3. Search client by ID");
            System.out.println("4. Search client by email");
            System.out.println("5. Search client by phone");
            System.out.println("6. List all clients");
            System.out.println("7. Delete client");
            System.out.println("8. Back to main menu");
            System.out.print("Choose an option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> registerClient();
                case 2 -> updateClient();
                case 3 -> searchClientById();
                case 4 -> searchClientByEmail();
                case 5 -> searchClientByPhone();
                case 6 -> listAllClients();
                case 7 -> deleteClient();
                case 8 -> { return; }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void registerClient() {
        try {
            System.out.print("Enter name: ");
            String name = scanner.nextLine();
            System.out.print("Enter email: ");
            String email = scanner.nextLine();
            System.out.print("Enter phone: ");
            String phone = scanner.nextLine();

            Client client = clientService.registerClient(name, email, phone);
            System.out.println("Client registered successfully!");
            System.out.println("Client ID: " + client.id());
            System.out.println("Name: " + client.name());
            System.out.println("Email: " + client.email());
            System.out.println("Phone: " + client.phone());
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateClient() {
        try {
            System.out.print("Enter client ID: ");
            String id = scanner.nextLine();
            System.out.print("Enter new name: ");
            String name = scanner.nextLine();
            System.out.print("Enter new email: ");
            String email = scanner.nextLine();
            System.out.print("Enter new phone: ");
            String phone = scanner.nextLine();

            Client client = clientService.updateClient(id, name, email, phone);
            System.out.println("Client updated successfully!");
            displayClient(client);
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void searchClientById() {
        try {
            System.out.print("Enter client ID: ");
            String id = scanner.nextLine();

            Optional<Client> optionalClient = clientService.findClientById(id);
            if (optionalClient.isPresent()) {
                displayClient(optionalClient.get());
            } else {
                System.out.println("Client not found.");
            }
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void searchClientByEmail() {
        try {
            System.out.print("Enter email: ");
            String email = scanner.nextLine();

            Optional<Client> optionalClient = clientService.searchByEmail(email);
            if (optionalClient.isPresent()) {
                displayClient(optionalClient.get());
            } else {
                System.out.println("Client not found.");
            }
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void searchClientByPhone() {
        try {
            System.out.print("Enter phone: ");
            String phone = scanner.nextLine();

            Optional<Client> optionalClient = clientService.searchByPhone(phone);
            if (optionalClient.isPresent()) {
                displayClient(optionalClient.get());
            } else {
                System.out.println("Client not found.");
            }
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listAllClients() {
        try {
            List<Client> clients = clientService.listAllClients();
            if (clients.isEmpty()) {
                System.out.println("No clients found.");
            } else {
                System.out.println("\n===== ALL CLIENTS =====");
                for (Client client : clients) {
                    displayClient(client);
                    System.out.println("--------------------");
                }
            }
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteClient() {
        try {
            System.out.print("Enter client ID to delete: ");
            String id = scanner.nextLine();
            System.out.print("Are you sure? (yes/no): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("yes")) {
                boolean deleted = clientService.deleteClient(id);
                if (deleted) {
                    System.out.println("Client deleted successfully!");
                } else {
                    System.out.println("Client not found.");
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (ServiceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void displayClient(Client client) {
        System.out.println("ID: " + client.id());
        System.out.println("Name: " + client.name());
        System.out.println("Email: " + client.email());
        System.out.println("Phone: " + client.phone());
    }

    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

