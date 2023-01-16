package at.fhtw.monstertradingcardsapp.persistence;

import at.fhtw.monstertradingcardsapp.model.ToUpdateUserData;
import at.fhtw.monstertradingcardsapp.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class DBUser {
    private final Connection dbConn;

    private static final String insertUserSQL = "INSERT INTO appuser(username, password, coins, stats) VALUES(?,?,?,?)";
    private static final String updatetUserSQL = "UPDATE appuser SET username = ?, bio = ?, image = ? WHERE id = ?";
    private static final String insertDeckSQL = "INSERT INTO deck(appuser_id) VALUES(?)";
    private static final String selectUserByNameSQL = "SELECT * FROM appuser WHERE username = ?";
    private static final String selectScoreBoardWithUserNameSQL = "SELECT stats, username FROM appuser ORDER BY stats DESC";

    public DBUser(Connection dbConn) {
        this.dbConn = dbConn;
    }

    public int addUserToDB(User user){
        System.out.println("Adding User: " + user.toString());
        if(this.getUserByName(user.getUserName()) != null){
            return 0;
        }

        try {
            PreparedStatement stmt = this.dbConn.prepareStatement(insertUserSQL);
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPassword());
            stmt.setInt(3, user.getCoins());
            stmt.setInt(4, user.getStats());
            if(!user.getUserName().equals("admin")){
                stmt.executeUpdate();
                stmt = this.dbConn.prepareStatement(insertDeckSQL);
                stmt.setInt(1, this.getUserByName(user.getUserName()).getId());
            }
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User getUserByName(String userName){
        System.out.println("Getting User: " + userName);
        try {
            PreparedStatement stmt = this.dbConn.prepareStatement(selectUserByNameSQL);
            stmt.setString(1, userName);
            ResultSet resultSet = stmt.executeQuery();
            User foundUser = null;
            while (resultSet.next()){
                foundUser = new User(resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getInt("coins"),
                        resultSet.getInt("stats"),
                        resultSet.getString("bio"),
                        resultSet.getString("image"));
            }
            System.out.println("Found User: " + foundUser);
            return foundUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateUserByName(int userId, ToUpdateUserData toUpdateUserData){
        System.out.println("Updating User: " + toUpdateUserData);
        try {
            PreparedStatement stmt = this.dbConn.prepareStatement(updatetUserSQL);
            stmt.setString(1, toUpdateUserData.getUserName());
            stmt.setString(2, toUpdateUserData.getBio());
            stmt.setString(3, toUpdateUserData.getImage());
            stmt.setInt(4, userId);
            if (stmt.executeUpdate() == 1){
                return true;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Integer> showScoreBoard() {
        try {
            PreparedStatement stmt = this.dbConn.prepareStatement(selectScoreBoardWithUserNameSQL);
            ResultSet resultSet = stmt.executeQuery();
            Map<String, Integer> scoreBoard = new HashMap<>();

            while (resultSet.next()){
                scoreBoard.put(resultSet.getString("username"), resultSet.getInt("stats"));
            }

            return scoreBoard;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkHeaderCredentials(String userName, String credentials){
        User currentUser = this.getUserByName(userName);
        if(currentUser == null) {
            return false;
        }

        MessageDigest m;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        m.update(currentUser.getPassword().getBytes());
        byte[] bytes = m.digest();
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < bytes.length; i++){
            s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        String encrytedPW = s.toString();

        if(encrytedPW.equals(credentials)) {
            return true;
        }
        return false;
    }

}
