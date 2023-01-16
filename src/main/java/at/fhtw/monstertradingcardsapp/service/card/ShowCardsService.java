package at.fhtw.monstertradingcardsapp.service.card;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

import java.sql.Connection;

public class ShowCardsService implements Service {
    private final ShowCardsController showCardsController;

    public ShowCardsService(Connection dbConn) {
        this.showCardsController = new ShowCardsController(dbConn);
    }
    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET) {
            return this.showCardsController.getAllCardsFromUser(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[Bad Request]"
        );
    }
}
