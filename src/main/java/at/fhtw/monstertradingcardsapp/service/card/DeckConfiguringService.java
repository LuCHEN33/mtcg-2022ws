package at.fhtw.monstertradingcardsapp.service.card;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

import java.sql.Connection;

public class DeckConfiguringService implements Service {
    private final DeckConfiguringController deckConfiguringController;

    public DeckConfiguringService(Connection dbConn) { this.deckConfiguringController = new DeckConfiguringController(dbConn); }
    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET) {
            return this.deckConfiguringController.showDeckFromUser(request);
        } else if (request.getMethod() == Method.PUT) {
            return this.deckConfiguringController.configureDeckFromUser(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[Bad Request]"
        );
    }
}
