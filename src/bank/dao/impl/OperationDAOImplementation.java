package bank.dao.impl;

import bank.config.DatabaseConnection;
import bank.dao.OperationDAO;
import bank.exception.DaoException;
import bank.model.CardOperation;
import bank.model.enums.OperationType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OperationDAOImplementation implements OperationDAO {

    private final DatabaseConnection dbconfig;

    public OperationDAOImplementation() {
        this.dbconfig = DatabaseConnection.getInstance();
    }

    @Override
    public CardOperation create(CardOperation operation) throws DaoException {
        String sql = "INSERT INTO operations (id, card_id, operation_date, amount, type, location) VALUES (?, ?, ?, ?, ?, ?)";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, operation.id());
            preparedStatement.setString(2, operation.cardId());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(operation.date()));
            preparedStatement.setDouble(4, operation.amount());
            preparedStatement.setString(5, mapOperationTypeToSQL(operation.type()));
            preparedStatement.setString(6, operation.location());

            preparedStatement.executeUpdate();
            return operation;

        }catch(SQLException e) {
            throw new DaoException("Failed to create operation: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String id) throws DaoException {
        String sql = "DELETE FROM operations WHERE id = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, id);
            int count = preparedStatement.executeUpdate();
            return count == 1;
        } catch(SQLException e){
            throw new DaoException("Failed to delete operation: " + e.getMessage());
        }
    }

    @Override
    public Optional<CardOperation> findById(String id) throws DaoException {
        String sql = "SELECT * FROM operations WHERE id = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, id);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()){
                CardOperation operation = extractOperationFromResultSet(result);
                return Optional.of(operation);
            }
            return Optional.empty();
        } catch(SQLException e){
            throw new DaoException("Failed to retrieve operation: " + e.getMessage());
        }
    }

    @Override
    public List<CardOperation> findByCardId(String cardId) throws DaoException {
        String sql = "SELECT * FROM operations WHERE card_id = ? ORDER BY operation_date DESC";
        List<CardOperation> operations = new ArrayList<>();
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, cardId);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()){
                CardOperation operation = extractOperationFromResultSet(result);
                operations.add(operation);
            }
            return operations;
        } catch(SQLException e){
            throw new DaoException("Failed to retrieve operations by card: " + e.getMessage());
        }
    }

    @Override
    public List<CardOperation> findByType(OperationType type) throws DaoException {
        String sql = "SELECT * FROM operations WHERE type = ? ORDER BY operation_date DESC";
        List<CardOperation> operations = new ArrayList<>();
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, mapOperationTypeToSQL(type));
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()){
                CardOperation operation = extractOperationFromResultSet(result);
                operations.add(operation);
            }
            return operations;
        } catch(SQLException e){
            throw new DaoException("Failed to retrieve operations by type: " + e.getMessage());
        }
    }

    @Override
    public List<CardOperation> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws DaoException {
        String sql = "SELECT * FROM operations WHERE operation_date BETWEEN ? AND ? ORDER BY operation_date DESC";
        List<CardOperation> operations = new ArrayList<>();
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setTimestamp(1, Timestamp.valueOf(startDate));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()){
                CardOperation operation = extractOperationFromResultSet(result);
                operations.add(operation);
            }
            return operations;
        } catch(SQLException e){
            throw new DaoException("Failed to retrieve operations by date range: " + e.getMessage());
        }
    }

    @Override
    public List<CardOperation> findByCardIdAndDateRange(String cardId, LocalDateTime startDate, LocalDateTime endDate) throws DaoException {
        String sql = "SELECT * FROM operations WHERE card_id = ? AND operation_date BETWEEN ? AND ? ORDER BY operation_date DESC";
        List<CardOperation> operations = new ArrayList<>();
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, cardId);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(startDate));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(endDate));
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()){
                CardOperation operation = extractOperationFromResultSet(result);
                operations.add(operation);
            }
            return operations;
        } catch(SQLException e){
            throw new DaoException("Failed to retrieve operations by card and date range: " + e.getMessage());
        }
    }

    @Override
    public List<CardOperation> findByCardIdAndType(String cardId, OperationType type) throws DaoException {
        String sql = "SELECT * FROM operations WHERE card_id = ? AND type = ? ORDER BY operation_date DESC";
        List<CardOperation> operations = new ArrayList<>();
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, cardId);
            preparedStatement.setString(2, mapOperationTypeToSQL(type));
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()){
                CardOperation operation = extractOperationFromResultSet(result);
                operations.add(operation);
            }
            return operations;
        } catch(SQLException e){
            throw new DaoException("Failed to retrieve operations by card and type: " + e.getMessage());
        }
    }

    @Override
    public List<CardOperation> findAll() throws DaoException {
        String sql = "SELECT * FROM operations ORDER BY operation_date DESC";
        List<CardOperation> operations = new ArrayList<>();
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()){
                CardOperation operation = extractOperationFromResultSet(result);
                operations.add(operation);
            }
            return operations;
        } catch(SQLException e){
            throw new DaoException("Failed to retrieve all operations: " + e.getMessage());
        }
    }

    private CardOperation extractOperationFromResultSet(ResultSet result) throws SQLException {
        String id = result.getString("id");
        String cardId = result.getString("card_id");
        Timestamp timestamp = result.getTimestamp("operation_date");
        LocalDateTime date = timestamp.toLocalDateTime();
        double amount = result.getDouble("amount");
        String typeStr = result.getString("type");
        OperationType type = mapSQLToOperationType(typeStr);
        String location = result.getString("location");

        return new CardOperation(id, date, amount, type, location, cardId);
    }

    private String mapOperationTypeToSQL(OperationType type) {
        return switch (type) {
            case PURCHASE -> "ACHAT";
            case WITHDRAWAL -> "RETRAIT";
            case ONLINE_PAYMENT -> "PAIEMENTENLIGNE";
        };
    }

    private OperationType mapSQLToOperationType(String sqlType) {
        return switch (sqlType) {
            case "ACHAT" -> OperationType.PURCHASE;
            case "RETRAIT" -> OperationType.WITHDRAWAL;
            case "PAIEMENTENLIGNE" -> OperationType.ONLINE_PAYMENT;
            default -> throw new IllegalArgumentException("Unknown operation type: " + sqlType);
        };
    }
}

