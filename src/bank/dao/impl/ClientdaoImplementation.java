package bank.dao.impl;

import bank.config.DatabaseConfig;
import bank.dao.ClientDAO;
import bank.exception.DaoException;
import bank.model.Client;

import java.sql.*;
import java.util.Optional;

public class ClientdaoImplementation implements ClientDAO {

    private DatabaseConfig dbconfig;

    public ClientdaoImplementation(DatabaseConfig dbconfig) {
        this.dbconfig = dbconfig;
    }


    @Override
    public Client save(String client_id, String name, String email, String phone) throws DaoException{
        String sql = "INSERT INTO client (client_id, name, email, phone) VALUES (?, ?, ?, ?)";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, client_id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, phone);

            preparedStatement.executeUpdate();

            return new Client(client_id, name, email, phone);

        }catch (SQLException e) {
            throw new DaoException("Failed to insert the client due to: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String client_id) throws DaoException {
        String sql = "DELETE FROM client WHERE client_id = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, client_id);
            preparedStatement.executeUpdate();
            return true;
        }catch(SQLException e){
            throw new DaoException("Failed to delete the client. " + e.getMessage());
        }
    }

    @Override
    public Optional<Client> findById(String id) throws DaoException {
        String sql = "SELECT * FROM client WHERE client_id = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            return getClient(id, preparedStatement);
        }catch(SQLException e) {
            throw new DaoException("Failed to retrieve the client information. " + getClass());
        }
    }

    @Override
    public Optional<Client> findByName(String name) throws DaoException {
        String sql = "SELECT * FROM client WHERE name = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            return getClient(name, preparedStatement);
        }catch(SQLException e) {
            throw new DaoException("Failed to retrieve client. " + e.getMessage());
        }
    }

    @Override
    public Optional<Client> findByEmail(String email) throws DaoException {
        String sql = "SELECT * FROM client WHERE email = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            return getClient(email, preparedStatement);
        }catch(SQLException e) {
            throw new DaoException("Failed to retrieve the client information" + e.getMessage());
        }
    }

    private Optional<Client> getClient(String key, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, key);
        ResultSet result = preparedStatement.executeQuery();
        if (result.next()) {
            Client client = new Client(result.getString("client_id"),result.getString("name"), result.getString("email"), result.getString("phone"));
            return Optional.of(client);
        } else {
            return Optional.empty();
        }
    }
}
