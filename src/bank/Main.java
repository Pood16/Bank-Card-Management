package bank;

import bank.ui.MainMenu;

public class Main {
    public static void main(String[] args) {
        System.out.println("===== Banking Card Management System =====");
        MainMenu mainMenu = new MainMenu();
        mainMenu.display();
    }
}
