package at.fhtw.monstertradingcardsapp.service.card;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.monstertradingcardsapp.controller.Controller;
import at.fhtw.monstertradingcardsapp.model.Card;
import at.fhtw.monstertradingcardsapp.persistence.DBCard;
import at.fhtw.monstertradingcardsapp.persistence.DBUser;

import java.sql.Connection;
import java.util.List;

public class ShowCardsController extends Controller {
    private final DBCard dbCard;
    private final DBUser dbUser;

    public ShowCardsController (Connection dbConn) {
        this.dbCard = new DBCard(dbConn);
        this.dbUser = new DBUser(dbConn);
    }

    public Response getAllCardsFromUser(Request request){
        if(request.getParams() != null && request.getHeaderMap().getHeader("Authorization") != null) {
            String credentials = request.getHeaderMap().getHeader("Authorization").split("\\s+")[1];
            String[] param = request.getParams().split("=");
            if(param[0].equals("name")) {
                String userName = param[1];
                if (!this.dbUser.checkHeaderCredentials(userName, credentials)) {
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ message: \"Unauthorized of showing Cards\" }"
                    );
                }

                List<Card> acquiredCards = this.dbCard.getAllCardsFromUser(userName);
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ message: \"" + acquiredCards + "\" }"
                );
            }
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\" : \"Getting Card failed\" }"
        );
    }
}
