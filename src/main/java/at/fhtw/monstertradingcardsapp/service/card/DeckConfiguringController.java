package at.fhtw.monstertradingcardsapp.service.card;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.monstertradingcardsapp.controller.Controller;
import at.fhtw.monstertradingcardsapp.model.Card;
import at.fhtw.monstertradingcardsapp.persistence.DBCard;
import at.fhtw.monstertradingcardsapp.persistence.DBUser;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class DeckConfiguringController extends Controller {
    private final DBCard dbCard;
    private final DBUser dbUser;

    public DeckConfiguringController (Connection dbConn){
        this.dbCard = new DBCard(dbConn);
        this.dbUser = new DBUser(dbConn);
    }

    public Response showDeckFromUser(Request request) {
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

                List<Card> deck = this.dbCard.getDeck(userName);

                return new Response(
                        HttpStatus.OK,
                        responseContentType,
                        "{ message: \"" + deck + "\" }"
                );
            }
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\" : \"Getting User Deck failed\" }"
        );
    }

    public Response configureDeckFromUser(Request request) {
        if(request.getParams() != null  && request.getHeaderMap().getHeader("Authorization") != null) {
            String credentials = request.getHeaderMap().getHeader("Authorization").split("\\s+")[1];
            String[] param = request.getParams().split("=");
            if(param[0].equals("name")) {
                String userName = param[1];
                if (!this.dbUser.checkHeaderCredentials(userName, credentials)) {
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ message: \"Unauthorized of configuring Deck\" }"
                    );
                }

                try {
                    String[] deckIdArray = this.getObjectMapper().readValue(request.getBody(), String[].class);
                    if (deckIdArray.length == 4) {
                        List<String> deckIdList = Arrays.asList(deckIdArray);
                        List<Card> ownedCards = this.dbCard.getAllCardsFromUser(userName);
                        int validCount = 0;
                        for (Card tempCard : ownedCards) {
                            for (String tempId : deckIdList) {
                                if (tempId.equals(tempCard.getId())) {
                                    validCount++;
                                }
                            }
                        }

                        if (validCount == 4 && this.dbCard.configureDeckFromUser(deckIdList, userName)) {
                            return new Response(
                                    HttpStatus.ACCEPTED,
                                    ContentType.JSON,
                                    "{ message: \"Deck configuring success\" }"
                            );
                        }
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\" : \"Deck configuring failed\" }"
        );
    }
}
