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

public class PackageAddingController extends Controller {
    private final DBCard dbCard;
    private final DBUser dbUser;

    public PackageAddingController(Connection dbConn) {
        this.dbCard = new DBCard(dbConn);
        this.dbUser = new DBUser(dbConn);
    }

    public Response addPackage(Request request) {
        try {
            String credentials = request.getHeaderMap().getHeader("Authorization").split("\\s+")[1];
            if(!this.dbUser.checkHeaderCredentials("admin", credentials)) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        "{ message: \"Unauthorized Package Adding\" }"
                );
            }

            Card[] cardArray = this.getObjectMapper().readValue(request.getBody(), Card[].class);
            if(cardArray.length == 5){
                List<Card> cardList = Arrays.asList(cardArray);
                if(this.dbCard.addPackageToDB(cardList) == 0) {
                    return new Response(
                            HttpStatus.CREATED,
                            ContentType.JSON,
                            "{ message: \"Package added\" }"
                    );
                }
            }

            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ message: \"Package adding failed\" }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\" : \"Package adding failed\" }"
        );
    }
}
