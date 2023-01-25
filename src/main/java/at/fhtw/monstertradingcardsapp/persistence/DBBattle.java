package at.fhtw.monstertradingcardsapp.persistence;

import at.fhtw.monstertradingcardsapp.model.Card;
import at.fhtw.monstertradingcardsapp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBBattle {
    private final Connection dbConn;
    private final DBUser dbUser;
    private final DBCard dbCard;

    private static final String insertUserToNewBattleSQL = "INSERT INTO battle(player1_id) VALUES(?)";
    private static final String insertUserToExistingBattleSQL = "UPDATE battle SET player2_id = ? WHERE id = ?";
    private static final String getLastRowFromBattleSQL = " SELECT id, player1_id, player2_id FROM battle WHERE id = (SELECT max(id) FROM battle)";

    public DBBattle(Connection dbConn) {
        this.dbConn = dbConn;
        this.dbUser = new DBUser(dbConn);
        this.dbCard = new DBCard(dbConn);
    }

    public int isPlayerOneWaiting(){
        try {
            PreparedStatement stmt = this.dbConn.prepareStatement(getLastRowFromBattleSQL);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()){
                if(resultSet.getInt("player1_id") > 0){
                    return resultSet.getInt("id");
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int addUserToNewBattle(String userName) {
        System.out.println("Adding Player to Battle: " + userName);
        User toAddPlayerOne = this.dbUser.getUserByName(userName);
        try {
            PreparedStatement stmt = this.dbConn.prepareStatement(insertUserToNewBattleSQL);
            stmt.setInt(1, toAddPlayerOne.getId());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int addUserToExistingBattle(String userName) {
        System.out.println("Adding Player to Battle: " + userName);
        User toAddPlayerTwo = this.dbUser.getUserByName(userName);
        try {
            PreparedStatement stmt = this.dbConn.prepareStatement(insertUserToExistingBattleSQL);
            stmt.setInt(1, toAddPlayerTwo.getId());
            stmt.setInt(2, this.isPlayerOneWaiting());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNameOfPlayerOneFromExistingBattle() {
        try {
            PreparedStatement stmt = this.dbConn.prepareStatement(getLastRowFromBattleSQL);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()){
                String playerOnesName = this.dbUser.getUserNameById(resultSet.getInt("player1_id"));
                return playerOnesName;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isCard1Effective(Card card1, Card card2) {
        if((card1.getName().contains("water"))&&(card2.getName().contains("fire"))){
            return true;
        } else if ((card1.getName().contains("fire"))&&(card2.getName().contains("normal"))){
            return true;
        } else if ((card1.getName().contains("normal"))&&(card2.getName().contains("water"))) {
            return true;
        }else {
            return false;
        }
    }

    public boolean isCard2Effective(Card card1, Card card2) {
        if((card2.getName().contains("water"))&&(card1.getName().contains("fire"))){
            return true;
        } else if ((card2.getName().contains("fire"))&&(card2.getName().contains("normal"))){
            return true;
        } else if ((card2.getName().contains("normal"))&&(card1.getName().contains("water"))) {
            return true;
        }else {
            return false;
        }
    }



    public String Fights(Card card1, Card card2){
        String winner ="";

            calculateDamage(card1, card2);
            winner = fightLogic(card1, card2);

        return  winner;
    }



    String fightLogic(Card card1, Card card2){
        String winner = "";
        if(card1.getDamage() > card2.getDamage()){
            winner = card1.getName();
        } else if (card1.getDamage() < card2.getDamage()) {
            winner = card2.getName();
        }else{
            winner = "Draw";
        }
        return winner;
    }

    void calculateDamage(Card card1,Card card2){
        if(isCard1Effective(card1, card2)){
            card1.setDamage(card1.getDamage()*2);
            card2.setDamage(card2.getDamage()/2);
        }else if(isCard2Effective(card1, card2)){
            card1.setDamage(card1.getDamage()/2);
            card2.setDamage(card2.getDamage()*2);
        }else{
            card1.setDamage(card1.getDamage());
            card2.setDamage(card2.getDamage());
        }
    }

}
