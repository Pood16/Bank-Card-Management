package bank.dao.impl;

import bank.config.DatabaseConnection;
import bank.dao.AlertDAO;
import bank.exception.DaoException;
import bank.model.FraudAlert;
import bank.model.enums.AlertLevelType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AlertDAOImplementation implements AlertDAO {

    private final DatabaseConnection dbconfig;

    public AlertDAOImplementation() {
        this.dbconfig = DatabaseConnection.getInstance();
    }

    @Override
    public FraudAlert create(FraudAlert alert) throws DaoException {
        String sql = "INSERT INTO alerts (id, card_id, description, level) VALUES (?, ?, ?, ?)";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, alert.id());
            preparedStatement.setString(2, alert.cardId());
            preparedStatement.setString(3, alert.description());
            preparedStatement.setString(4, mapAlertLevelToSQL(alert.level()));

            preparedStatement.executeUpdate();
            return alert;

        }catch(SQLException e) {
            throw new DaoException("Failed to create alert: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String id) throws DaoException {
        String sql = "DELETE FROM alerts WHERE id = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, id);
            int count = preparedStatement.executeUpdate();
            return count == 1;
        } catch(SQLException e){
            throw new DaoException("Failed to delete alert: " + e.getMessage());
        }
    }

    @Override
    public Optional<FraudAlert> findById(String id) throws DaoException {
        String sql = "SELECT * FROM alerts WHERE id = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, id);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()){
                FraudAlert alert = extractAlertFromResultSet(result);
                return Optional.of(alert);
            }
            return Optional.empty();
        } catch(SQLException e){
            throw new DaoException("Failed to retrieve alert: " + e.getMessage());
        }
    }

    @Override
    public List<FraudAlert> findByCardId(String cardId) throws DaoException {
        String sql = "SELECT * FROM alerts WHERE card_id = ? ORDER BY created_at DESC";
        List<FraudAlert> alerts = new ArrayList<>();
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, cardId);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()){
                FraudAlert alert = extractAlertFromResultSet(result);
                alerts.add(alert);
            }
            return alerts;
        } catch(SQLException e){
            throw new DaoException("Failed to retrieve alerts by card: " + e.getMessage());
        }
    }

    @Override
    public List<FraudAlert> findByLevel(AlertLevelType level) throws DaoException {
        String sql = "SELECT * FROM alerts WHERE level = ? ORDER BY created_at DESC";
        List<FraudAlert> alerts = new ArrayList<>();
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, mapAlertLevelToSQL(level));
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()){
                FraudAlert alert = extractAlertFromResultSet(result);
                alerts.add(alert);
            }
            return alerts;
        } catch(SQLException e){
            throw new DaoException("Failed to retrieve alerts by level: " + e.getMessage());
        }
    }

    @Override
    public List<FraudAlert> findCriticalAlerts() throws DaoException {
        return findByLevel(AlertLevelType.CRITICAL);
    }

    @Override
    public List<FraudAlert> findAll() throws DaoException {
        String sql = "SELECT * FROM alerts ORDER BY created_at DESC";
        List<FraudAlert> alerts = new ArrayList<>();
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()){
                FraudAlert alert = extractAlertFromResultSet(result);
                alerts.add(alert);
            }
            return alerts;
        } catch(SQLException e){
            throw new DaoException("Failed to retrieve all alerts: " + e.getMessage());
        }
    }

    private FraudAlert extractAlertFromResultSet(ResultSet result) throws SQLException {
        String id = result.getString("id");
        String cardId = result.getString("card_id");
        String description = result.getString("description");
        String levelStr = result.getString("level");
        AlertLevelType level = mapSQLToAlertLevel(levelStr);

        return new FraudAlert(id, description, level, cardId);
    }

    private String mapAlertLevelToSQL(AlertLevelType level) {
        return switch (level) {
            case INFO -> "INFO";
            case WARNING -> "AVERTISSEMENT";
            case CRITICAL -> "CRITIQUE";
        };
    }

    private AlertLevelType mapSQLToAlertLevel(String sqlLevel) {
        return switch (sqlLevel) {
            case "INFO" -> AlertLevelType.INFO;
            case "AVERTISSEMENT" -> AlertLevelType.WARNING;
            case "CRITIQUE" -> AlertLevelType.CRITICAL;
            default -> throw new IllegalArgumentException("Unknown alert level: " + sqlLevel);
        };
    }
}

