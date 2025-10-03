package bank.dao.impl;

import bank.config.DatabaseConnection;
import bank.dao.ClientDAO;
import bank.exception.DaoException;
import bank.model.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientdaoImplementation implements ClientDAO {

    private DatabaseConnection dbconfig;

    public ClientdaoImplementation() {
            this.dbconfig = DatabaseConnection.getInstance();
    }

    @Override
    public Client save(Client client) throws DaoException{
        String sql = "INSERT INTO clients (id, name, email, phone) VALUES (?, ?, ?, ?)";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, client.id());
            preparedStatement.setString(2, client.name());
            preparedStatement.setString(3, client.email());
            preparedStatement.setString(4, client.phone());

            preparedStatement.executeUpdate();
            return client;

        }catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public boolean update(Client client) throws DaoException {
        String sql = "UPDATE clients SET name = ?, email = ?, phone = ? WHERE id = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, client.name());
            preparedStatement.setString(2, client.email());
            preparedStatement.setString(3, client.phone());
            preparedStatement.setString(4, client.id());

            int count = preparedStatement.executeUpdate();
            return count == 1;
        }catch(SQLException e){
            throw new DaoException("Failed to update the client: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String client_id) throws DaoException {
        String sql = "DELETE FROM clients WHERE id = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, client_id);
            int count = preparedStatement.executeUpdate();
            return count == 1;
        }catch(SQLException e){
            throw new DaoException("Failed to delete the client");
        }
    }

    @Override
    public Optional<Client> findById(String id) throws DaoException {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            return getClient(id, preparedStatement);
        }catch(SQLException e) {
            throw new DaoException("Failed to retrieve the client information.");
        }
    }

    @Override
    public Optional<Client> findByName(String name) throws DaoException {
        String sql = "SELECT * FROM clients WHERE name = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            return getClient(name, preparedStatement);
        }catch(SQLException e) {
            throw new DaoException("Failed to retrieve client information.");
        }
    }

    @Override
    public Optional<Client> findByEmail(String email) throws DaoException {
        String sql = "SELECT * FROM clients WHERE email = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            return getClient(email, preparedStatement);
        }catch(SQLException e) {
            throw new DaoException("Failed to retrieve client information.");
        }
    }

    @Override
    public Optional<Client> findByPhone(String phone) throws DaoException {
        String sql = "SELECT * FROM clients WHERE phone = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            return getClient(phone, preparedStatement);
        }catch(SQLException e) {
            throw new DaoException("Failed to retrieve client information.");
        }
    }

    @Override
    public List<Client> findAll() throws DaoException {
        List<Client> clients = new ArrayList<>();
        try(Connection connection = dbconfig.getConnection()) {
            String sql = "SELECT * FROM clients";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet results = preparedStatement.executeQuery();
            while(results.next()) {
                Client client = new Client(
                        results.getString("id"),
                        results.getString("name"),
                        results.getString("email"),
                        results.getString("phone")
                );
                clients.add(client);
            }
            return clients;
        }catch(SQLException e) {
            throw new DaoException("Failed to retrieve clients list.");
        }
    }

    private Optional<Client> getClient(String key, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, key);
        ResultSet result = preparedStatement.executeQuery();
        if (result.next()) {
            Client client = new Client(result.getString("id"),result.getString("name"), result.getString("email"), result.getString("phone"));
            return Optional.of(client);
        } else {
            return Optional.empty();
        }
    }
}
