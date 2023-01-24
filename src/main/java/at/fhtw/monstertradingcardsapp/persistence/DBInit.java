package at.fhtw.monstertradingcardsapp.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBInit {
    private Connection dbConn = null;
    private final String pgUrl = "jdbc:postgresql://localhost:5432/";
    private final String pgUser = "postgres";
    private final String pgPassword = "postgres";
    private final String dbName = "mtcgif21b209";
    private String dbDropSQL = "DROP DATABASE IF EXISTS " + dbName;
    private String dbCreationSQL = "CREATE DATABASE " + dbName;

    private String dbUserTableCreationSQL = "CREATE TABLE IF NOT EXISTS appuser" +
            " (" +
            "  id SERIAL PRIMARY KEY," +
            "  username VARCHAR(255) NOT NULL," +
            "  password VARCHAR(255) NOT NULL," +
            "  coins INT NOT NULL," +
            "  stats INT NOT NULL," +
            "  bio VARCHAR(255) NULL," +
            "  image VARCHAR(255) NULL" +
            " );";
    private String dbCardTableCreationSQL = "CREATE TABLE IF NOT EXISTS card" +
            " (" +
            "  id VARCHAR(255) PRIMARY KEY," +
            "  name VARCHAR(255) NOT NULL," +
            "  damage INT," +
            "  appuser_id INT NULL," +
            "  CONSTRAINT fk_appuser " +
            "    FOREIGN KEY (appuser_id)" +
            "    REFERENCES appuser(id) ON DELETE SET NULL" +
            " );";

    private String dbDeckTableCreationSQL = "CREATE TABLE IF NOT EXISTS deck" +
            " (" +
            "  appuser_id INT PRIMARY KEY," +
            "  card1_id VARCHAR(255) NULL," +
            "  card2_id VARCHAR(255) NULL," +
            "  card3_id VARCHAR(255) NULL," +
            "  card4_id VARCHAR(255) NULL," +
            "  CONSTRAINT fk_appuser " +
            "    FOREIGN KEY (appuser_id)" +
            "    REFERENCES appuser(id)," +
            "  CONSTRAINT fk_card1 " +
            "    FOREIGN KEY (card1_id)" +
            "    REFERENCES card(id)," +
            "  CONSTRAINT fk_card2 " +
            "    FOREIGN KEY (card2_id)" +
            "    REFERENCES card(id)," +
            "  CONSTRAINT fk_card3 " +
            "    FOREIGN KEY (card3_id)" +
            "    REFERENCES card(id)," +
            "  CONSTRAINT fk_card4 " +
            "    FOREIGN KEY (card4_id)" +
            "    REFERENCES card(id)" +
            " );";

    private String dbBattleTableCreationSQL = "CREATE TABLE IF NOT EXISTS battle" +
            " (" +
            "  id SERIAL PRIMARY KEY," +
            "  player1_id INT NULL," +
            "  player2_id INT NULL," +
            "  winner_id INT NULL," +
            "  CONSTRAINT fk_player1 " +
            "    FOREIGN KEY (player1_id)" +
            "    REFERENCES appuser(id) ON DELETE SET NULL," +
            "  CONSTRAINT fk_player2 " +
            "    FOREIGN KEY (player2_id)" +
            "    REFERENCES appuser(id) ON DELETE SET NULL," +
            "  CONSTRAINT fk_winner " +
            "    FOREIGN KEY (winner_id)" +
            "    REFERENCES appuser(id) ON DELETE SET NULL" +
            " );";

    public DBInit() {
        this.dbConn = this.connectToDBServer();
        System.out.println("Creating database...");
        try {
            Statement stmt = this.dbConn.createStatement();
            stmt.executeUpdate(dbDropSQL);
            stmt.executeUpdate(dbCreationSQL);
            System.out.println("Database created successfully...");
            this.dbConn = this.reconnectToDataBase();
            stmt = this.dbConn.createStatement();
            stmt.executeUpdate(dbUserTableCreationSQL);
            stmt.executeUpdate(dbCardTableCreationSQL);
            stmt.executeUpdate(dbDeckTableCreationSQL);
            stmt.executeUpdate(dbBattleTableCreationSQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection connectToDBServer() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(this.pgUrl, this.pgUser, this.pgPassword);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    private Connection reconnectToDataBase() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(this.pgUrl+this.dbName, this.pgUser, this.pgPassword);
            System.out.println("Connected to the DataBase " + this.dbName + "successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    public Connection getDbConn() {
        return this.dbConn;
    }
}
