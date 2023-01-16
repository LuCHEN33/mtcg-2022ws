package at.fhtw.monstertradingcardsapp.persistence;

import at.fhtw.monstertradingcardsapp.model.Card;
import at.fhtw.monstertradingcardsapp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBCard {
    private final Connection dbConn;
    private static final String insertCardSQL = "INSERT INTO card(id, name, damage) VALUES(?,?,?)";
    private static final String getAllCardsByUserNameSQL = "SELECT c.id, c.name, c.damage FROM card c" +
            " JOIN appuser a ON a.id = c.appuser_id" +
            " WHERE a.username = ?";
    private static final String getUnacquiredCardsSQL = "SELECT * FROM card WHERE appuser_id IS NULL";
    private static final String getDeckFromUserSQL = "SELECT * FROM deck WHERE appuser_id = ?";
    private static final String getCardByIdSQL = "SELECT * FROM card WHERE id = ?";
    private static final String updateUserAcquiredCardSQL = "UPDATE card SET appuser_id = ? WHERE id = ?";
    private static final String updateUserCoinsSQL = "UPDATE appuser SET coins = ? WHERE id = ?";
    private static final String updateUserDeckSQL = "UPDATE deck SET card1_id = ?, card2_id = ?," +
                                                    " card3_id = ?, card4_id = ? WHERE appuser_id = ?";
    private final DBUser dbUser;

    public DBCard(Connection dbConn) {
        this.dbConn = dbConn;
        this.dbUser = new DBUser(dbConn);
    }

    public int addPackageToDB(List<Card> cardList){
        System.out.println("Adding Package: " + cardList.toString());

        List<Card> existedCards = new ArrayList<>();
        try {
            for (Card newCard : cardList) {
                PreparedStatement stmt = this.dbConn.prepareStatement(insertCardSQL);
                stmt.setString(1, newCard.getId());
                stmt.setString(2, newCard.getName());
                stmt.setInt(3, newCard.getDamage());
                if (stmt.executeUpdate() != 1){
                    existedCards.add(newCard);
                }
            }
            if (existedCards.size() > 0){
                System.out.println("Already existed Cards in DB: " + existedCards);
            }

            return existedCards.size();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Card> acquirePackageByUser(String userName){
        System.out.println("Acquiring Package from User: " + userName);
        List<Card> unacquiredCards = this.getUnacquiredCards();
        List<Card> acquiredCardsForUser = new ArrayList<>();
        if(this.dbUser.getUserByName(userName).getCoins()<5){
            return acquiredCardsForUser;
        }
        if(unacquiredCards.size() >4){
            for(int i = 0; i < 5; i++){
                User currentUser = this.dbUser.getUserByName(userName);
                int random_int = (int) ((Math.random() * (unacquiredCards.size() - 0)) + 0);
                Card tempCard = unacquiredCards.get(random_int);
                try {
                    PreparedStatement stmt = this.dbConn.prepareStatement(updateUserAcquiredCardSQL);
                    stmt.setInt(1, currentUser.getId());
                    stmt.setString(2, tempCard.getId());
                    if(stmt.executeUpdate() == 1){
                        acquiredCardsForUser.add(tempCard);
                        unacquiredCards.remove(tempCard);
                        stmt = this.dbConn.prepareStatement(updateUserCoinsSQL);
                        stmt.setInt(1, currentUser.getCoins()-1);
                        stmt.setInt(2, currentUser.getId());
                        stmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return acquiredCardsForUser;
    }

    private List<Card> getUnacquiredCards(){
        try {
            List<Card> unacquiredCards = new ArrayList<>();
            PreparedStatement stmt = this.dbConn.prepareStatement(getUnacquiredCardsSQL);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()){
                Card tempCard = new Card(resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("damage"));
                unacquiredCards.add(tempCard);
            }
            return unacquiredCards;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Card getCardById(String id) {
        System.out.println("Getting Card, Id: " + id);
        try {
            PreparedStatement stmt = this.dbConn.prepareStatement(getCardByIdSQL);
            stmt.setString(1, id);
            ResultSet resultSet = stmt.executeQuery();
            Card foundCard = null;
            while (resultSet.next()){
                foundCard = new Card(resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("damage"));
            }
            System.out.println("Found User: " + foundCard);
            return foundCard;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Card> getAllCardsFromUser(String userName) {
        try {
            List<Card> acquiredCards = new ArrayList<>();
            PreparedStatement stmt = this.dbConn.prepareStatement(getAllCardsByUserNameSQL);
            stmt.setString(1, userName);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()){
                Card tempCard = new Card(resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("damage"));
                acquiredCards.add(tempCard);
            }
            return acquiredCards;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean configureDeckFromUser(List<String> deckIdList, String userName) {
        if(deckIdList.size() != 4){
            return  false;
        }

        try {
            int userId = this.dbUser.getUserByName(userName).getId();

            PreparedStatement stmt = this.dbConn.prepareStatement(updateUserDeckSQL);
            stmt.setString(1, deckIdList.get(0));
            stmt.setString(2, deckIdList.get(1));
            stmt.setString(3, deckIdList.get(2));
            stmt.setString(4, deckIdList.get(3));
            stmt.setInt(5, userId);

            if (stmt.executeUpdate() != 1){
                return false;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public List<Card> getDeck(String userName){
        try {
            List<Card> deck = new ArrayList<>();
            int userId = this.dbUser.getUserByName(userName).getId();
            PreparedStatement stmt = this.dbConn.prepareStatement(getDeckFromUserSQL);
            stmt.setInt(1, userId);

            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                for(int i = 0; i < 4; i++){
                    String cardId = resultSet.getString("card" + (i + 1) + "_id");
                    if(cardId != null) {
                        Card cardOfDeck = this.getCardById(cardId);
                        deck.add(cardOfDeck);
                    }
                }
            }

            return deck;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
