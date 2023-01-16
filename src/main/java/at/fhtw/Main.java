package at.fhtw;

import at.fhtw.httpserver.utils.Router;
import at.fhtw.httpserver.server.Server;
import at.fhtw.monstertradingcardsapp.persistence.DBInit;
import at.fhtw.monstertradingcardsapp.service.card.DeckConfiguringService;
import at.fhtw.monstertradingcardsapp.service.card.PackageAcquiringService;
import at.fhtw.monstertradingcardsapp.service.card.PackageAddingService;
import at.fhtw.monstertradingcardsapp.service.card.ShowCardsService;
import at.fhtw.monstertradingcardsapp.service.echo.EchoService;
import at.fhtw.monstertradingcardsapp.service.user.LoginService;
import at.fhtw.monstertradingcardsapp.service.user.ScoreBoardService;
import at.fhtw.monstertradingcardsapp.service.user.UserDataService;
import at.fhtw.monstertradingcardsapp.service.user.UserStatsService;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Server server = new Server(10001, configureRouter());

        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static Router configureRouter()
    {
        Router router = new Router();
        DBInit db = new DBInit();
        router.addService("/echo", new EchoService());
        router.addService("/sessions", new LoginService(db.getDbConn()));
        router.addService("/users", new UserDataService(db.getDbConn()));
        router.addService("/packages", new PackageAddingService(db.getDbConn()));
        router.addService("/transactions/packages", new PackageAcquiringService(db.getDbConn()));
        router.addService("/cards", new ShowCardsService(db.getDbConn()));
        router.addService("/deck", new DeckConfiguringService(db.getDbConn()));
        router.addService("/stats", new UserStatsService(db.getDbConn()));
        router.addService("/score", new ScoreBoardService(db.getDbConn()));
        return router;
    }


}
