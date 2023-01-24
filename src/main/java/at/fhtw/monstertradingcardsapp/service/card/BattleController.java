package at.fhtw.monstertradingcardsapp.service.card;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.monstertradingcardsapp.controller.Controller;
import at.fhtw.monstertradingcardsapp.model.Card;
import at.fhtw.monstertradingcardsapp.persistence.DBBattle;
import at.fhtw.monstertradingcardsapp.persistence.DBCard;
import at.fhtw.monstertradingcardsapp.persistence.DBUser;

import java.sql.Connection;
import java.util.List;

public class BattleController extends Controller {
    private final DBCard dbCard;
    private final DBUser dbUser;
    private final DBBattle dbBattle;

    public BattleController (Connection dbConn){
        this.dbCard = new DBCard(dbConn);
        this.dbUser = new DBUser(dbConn);
        this.dbBattle = new DBBattle(dbConn);
    }

    public Response addToBattle(Request request) {
        if(request.getParams() != null  && request.getHeaderMap().getHeader("Authorization") != null) {
            String credentials = request.getHeaderMap().getHeader("Authorization").split("\\s+")[1];
            String[] param = request.getParams().split("=");
            if(param[0].equals("name")) {
                String userName = param[1];
                ContentType responseContentType = ContentType.JSON;
                if (request.getParams() != null) {
                    if (param[0].equals("format") && param[1].equals("plain")) {
                        responseContentType = ContentType.PLAIN_TEXT;
                    }
                }

                //System.out.println(param.length);
                if (!this.dbUser.checkHeaderCredentials(userName, credentials)) {
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ message: \"Unauthorized of showing Deck\" }"
                    );
                }

                List<Card> playerOnesDeck;
                List<Card> playerTwosDeck;
                String playerOneName=null;
                if(this.dbBattle.isPlayerOneWaiting() > 0) {
                    this.dbBattle.addUserToExistingBattle(userName);
                    playerOneName = this.dbBattle.getNameOfPlayerOneFromExistingBattle();
                    playerOnesDeck = this.dbCard.getDeck(playerOneName);
                    playerTwosDeck = this.dbCard.getDeck(userName);
                    int count = 3;
                    for(Card tmpCard : playerOnesDeck){
                        System.out.println(playerOneName + ": " + tmpCard.getName() + " (" + tmpCard.getDamage()+ ") "
                            + " vs " + userName + ": " + playerTwosDeck.get(count).getName()
                                + " (" + playerTwosDeck.get(count).getDamage()+ ") ");
                        count--;
                    }
                } else {
                    this.dbBattle.addUserToNewBattle(userName);
                }

                return new Response(
                        HttpStatus.CREATED,
                        responseContentType,
                        "{ message: \"" + playerOneName + "\" }"
                );
            }
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\" : \"Getting User Deck failed\" }"
        );
    }
}
