package bank.config;

import com.sun.security.jgss.GSSUtil;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {


    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/card_db", "root", "");
        }catch(SQLException e){
            System.out.println("Failed to connect to database due to: " + e.getMessage());
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()){
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/card_db", "root", "");
        }
        return connection;
    }


}
