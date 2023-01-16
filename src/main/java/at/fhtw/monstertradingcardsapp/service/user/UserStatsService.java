package at.fhtw.monstertradingcardsapp.service.user;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

import java.sql.Connection;

public class UserStatsService implements Service {
    private final UserStatsControler userStatsControler;

    public UserStatsService(Connection dbConn) { this.userStatsControler = new UserStatsControler(dbConn); }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET) {
            return this.userStatsControler.showUserStats(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[Bad Request]"
        );
    }
}
