package bank.dao.impl;

import bank.config.DatabaseConnection;
import bank.dao.CardDAO;
import bank.exception.DaoException;
import bank.model.Card;
import bank.model.CreditCard;
import bank.model.DebitCard;
import bank.model.PrepaidCard;
import bank.model.enums.Status;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CarddaoImplementation implements CardDAO {

    private final DatabaseConnection dbconfig;

    public CarddaoImplementation() {
        this.dbconfig = DatabaseConnection.getInstance();
    }

    @Override
    public Card create(Card card) throws DaoException {
        String sql = switch (card.getCardType()) {
            case DEBIT ->
                    "INSERT INTO cards (id, number, card_type, expiration_date, status, client_id, daily_limit) VALUES (?, ?, ?, ?, ?, ?, ?)";
            case CREDIT ->
                    "INSERT INTO cards (id, number, card_type, expiration_date, status, client_id, monthly_limit, interest_rate) VALUES (?, ?, ?,?, ?, ?, ?, ?)";
            case PREPAID ->
                    "INSERT INTO cards (id, number, card_type, expiration_date, status, client_id, available_balance) VALUES (?, ?, ?, ?, ?, ?, ?)";
            default -> throw new IllegalArgumentException("Invalid card type");
        };

        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, card.getId());
            preparedStatement.setString(2, card.getNumber());
            preparedStatement.setString(3, card.getCardType().name());
            preparedStatement.setDate(4, Date.valueOf(card.getExpirationDate()));
            preparedStatement.setString(5, card.getStatus().name());
            preparedStatement.setString(6, card.getClientId());

            switch (card.getCardType()) {
                case DEBIT:
                    preparedStatement.setDouble(7, ((DebitCard) card).getDailyLimit());
                    break;
                case CREDIT:
                    preparedStatement.setDouble(7, ((CreditCard) card).getMonthlyLimit());
                    preparedStatement.setDouble(8, ((CreditCard) card).getInterestRate());
                    break;
                case PREPAID:
                    preparedStatement.setDouble(7, ((PrepaidCard) card).getBalance());
                    break;
            }

            preparedStatement.executeUpdate();
            return card;

        }catch(SQLException e) {
            throw new DaoException("Failed to create the card." + e.getMessage());
        }
    }

    @Override
    public boolean delete(String id) throws DaoException{
        String sql = "DELETE FROM cards WHERE id = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, id);
            int count = preparedStatement.executeUpdate();
            return count == 1;
        } catch(SQLException e){
            throw new DaoException("Failed to retrieve card information: " + e.getMessage());
        }
    }

    @Override
    public boolean update(Card card) throws DaoException {
        String sql = switch (card.getCardType()) {
            case DEBIT -> "UPDATE cards SET number = ?, expiration_date = ?, status = ?, daily_limit = ? WHERE id = ?";
            case CREDIT -> "UPDATE cards SET number = ?, expiration_date = ?, status = ?, monthly_limit = ?, interest_rate = ? WHERE id = ?";
            case PREPAID -> "UPDATE cards SET number = ?, expiration_date = ?, status = ?, available_balance = ? WHERE id = ?";
        };

        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, card.getNumber());
            preparedStatement.setDate(2, Date.valueOf(card.getExpirationDate()));
            preparedStatement.setString(3, card.getStatus().name());

            switch (card.getCardType()) {
                case DEBIT:
                    preparedStatement.setDouble(4, ((DebitCard) card).getDailyLimit());
                    preparedStatement.setString(5, card.getId());
                    break;
                case CREDIT:
                    preparedStatement.setDouble(4, ((CreditCard) card).getMonthlyLimit());
                    preparedStatement.setDouble(5, ((CreditCard) card).getInterestRate());
                    preparedStatement.setString(6, card.getId());
                    break;
                case PREPAID:
                    preparedStatement.setDouble(4, ((PrepaidCard) card).getBalance());
                    preparedStatement.setString(5, card.getId());
                    break;
            }
            int count = preparedStatement.executeUpdate();
            return count == 1;
        }catch(SQLException e) {
            throw new DaoException("Failed to update the card: " + e.getMessage());
        }
    }

    @Override
    public boolean updateStatus(String id, Status newStatus) throws DaoException{
        String sql = "UPDATE cards SET status = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, id);
            int count = preparedStatement.executeUpdate();
            return count == 1;
        } catch(SQLException e){
            throw new DaoException("Failed to updat ethe card status.");
        }
    }

    @Override
    public Optional<Card> findById(String id) throws DaoException {
        String sql = "SELECT * FROM cards WHERE id = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, id);
            ResultSet result = preparedStatement.executeQuery();

            if (result.next()){
                Card card = extractCardFromResultSet(result);
                return Optional.of(card);
            }else{
                return Optional.empty();
            }
        } catch(SQLException e){
            throw new DaoException("Failed to retrieve card information: " + e.getMessage());
        }
    }

    @Override
    public List<Card> findByClientId(String id) throws DaoException {
        String sql = "SELECT * FROM cards WHERE client_id = ?";
        List<Card> cards = new ArrayList<>();
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, id);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()){
                Card card = extractCardFromResultSet(result);
                cards.add(card);
            }
            return cards;
        } catch(SQLException e){
            throw new DaoException("Failed to retrieve card information: " + e.getMessage());
        }
    }

    @Override
    public Optional<Card> findByNumber(String number) throws DaoException {
        String sql = "SELECT * FROM cards WHERE number = ?";
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, number);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()){
                Card card = extractCardFromResultSet(result);
                return Optional.of(card);
            }
            return Optional.empty();
        } catch(SQLException e){
            throw new DaoException("Failed to retrieve card information: " + e.getMessage());
        }
    }

    @Override
    public List<Card> findByStatus(Status status) throws DaoException {
        String sql = "SELECT * FROM cards WHERE status = ?";
        List<Card> cards = new ArrayList<>();
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setString(1, status.name());
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()){
                Card card = extractCardFromResultSet(result);
                cards.add(card);
            }
            return cards;
        } catch(SQLException e){
            throw new DaoException("Failed to retrieve cards by status: " + e.getMessage());
        }
    }

    @Override
    public List<Card> findAll() throws DaoException {
        String sql = "SELECT * FROM cards";
        List<Card> cards = new ArrayList<>();
        try(Connection connection = dbconfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()){
                Card card = extractCardFromResultSet(result);
                cards.add(card);
            }
            return cards;
        } catch(SQLException e){
            throw new DaoException("Failed to retrieve all cards: " + e.getMessage());
        }
    }

    private Card extractCardFromResultSet(ResultSet result) throws SQLException {
        String id = result.getString("id");
        String cardType = result.getString("card_type");
        String number = result.getString("number");
        Date expirationDate = result.getDate("expiration_date");
        String status = result.getString("status");
        String clientId = result.getString("client_id");

        Card card = null;
        switch(cardType){
            case "DEBIT":
                double dailyLimit = result.getDouble("daily_limit");
                card = new DebitCard(number, expirationDate.toLocalDate(), Status.valueOf(status), clientId, dailyLimit);
                break;
            case "CREDIT":
                double monthlyLimit = result.getDouble("monthly_limit");
                double interestRate = result.getDouble("interest_rate");
                card = new CreditCard(number, expirationDate.toLocalDate(), Status.valueOf(status), clientId, monthlyLimit, interestRate);
                break;
            case "PREPAID":
                double availableBalance = result.getDouble("available_balance");
                card = new PrepaidCard(number, expirationDate.toLocalDate(), Status.valueOf(status), clientId, availableBalance);
                break;
            default:
                throw new IllegalArgumentException("Unknown card type: " + cardType);
        }
        return card;
    }
}
