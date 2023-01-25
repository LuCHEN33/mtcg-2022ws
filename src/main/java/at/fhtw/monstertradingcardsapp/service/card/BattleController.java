package at.fhtw.monstertradingcardsapp.service.card;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.monstertradingcardsapp.controller.Controller;
import at.fhtw.monstertradingcardsapp.model.Card;
import at.fhtw.monstertradingcardsapp.model.User;
import at.fhtw.monstertradingcardsapp.persistence.DBBattle;
import at.fhtw.monstertradingcardsapp.persistence.DBCard;
import at.fhtw.monstertradingcardsapp.persistence.DBUser;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
                String playerOneName;
                List<String> logs = new ArrayList<>();
                if(this.dbBattle.isPlayerOneWaiting() > 0) {
                    this.dbBattle.addUserToExistingBattle(userName);
                    playerOneName = this.dbBattle.getNameOfPlayerOneFromExistingBattle();
                    playerOnesDeck = this.dbCard.getDeck(playerOneName);
                    playerTwosDeck = this.dbCard.getDeck(userName);

                    int n = 3;
                    for(Card tmpCard : playerOnesDeck){
                        Random rand = new Random();
                        int count = rand.nextInt(n);

                        String winCard = this.dbBattle.Fights(tmpCard,playerTwosDeck.get(count));


                        logs.add(playerOneName + ": " + tmpCard.getName() + " (" + tmpCard.getDamage()+ ") "
                                + " vs " + userName + ": " + playerTwosDeck.get(count).getName()
                                + " (" + playerTwosDeck.get(count).getDamage()+ ") " + "winCard: " + winCard);


                       /* if(winCard.equals(tmpCard.getName()) && playerTwosDeck.size() != 0){
                            playerOnesDeck.remove(0);;
                            n--;
                        }
                        if(winCard.equals(playerTwosDeck.get(count).getName()) && playerOnesDeck.size()!= 0){
                            playerOnesDeck.remove(0);
                        }*/
                    }
                    /*String winner = null;
                    if(playerOnesDeck.size() < playerTwosDeck.size()){
                        winner = userName;
                    } else if (playerOnesDeck.size() > playerTwosDeck.size()) {
                        winner =playerOneName;
                    }else{
                        winner = "Draw";
                    }*/

                    String winner = playerOneName;
                    User playerOne = this.dbUser.getUserByName(playerOneName);
                    int playerOneId = playerOne.getId();

                    playerOne.setStats(103);

                    //this.dbUser.updateUserStatsByName(playerOneId,playerOne);

                    return new Response(
                            HttpStatus.CREATED,
                            responseContentType,
                            "{ message: \"" + logs + winner + " win! " + "\" }"
                    );
                } else {
                    this.dbBattle.addUserToNewBattle(userName);
                    return new Response(
                            HttpStatus.CREATED,
                            responseContentType,
                            "{ message: \" waiting other player to join\" }"
                    );
                }
            }
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\" : \"Getting User Deck failed\" }"
        );
    }
}
