package at.fhtw.monstertradingcardsapp.service.user;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

import java.sql.Connection;

public class LoginService implements Service {
    private final LoginController loginController;

    public LoginService(Connection dbConn) { this.loginController = new LoginController(dbConn);}

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST) {
            return this.loginController.authenticateCredentials(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[Bad Request]"
        );
    }
}
