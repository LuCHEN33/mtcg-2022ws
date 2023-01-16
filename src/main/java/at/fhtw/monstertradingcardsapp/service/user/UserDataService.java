package at.fhtw.monstertradingcardsapp.service.user;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

import java.sql.Connection;

public class UserDataService implements Service {
    private final UserDataController userDataController;

    public UserDataService(Connection dbConn) { this.userDataController = new UserDataController(dbConn);}

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST) {
            return this.userDataController.addNewUser(request);
        } else if (request.getMethod() == Method.GET) {
            return this.userDataController.showUserData(request);
        } else if (request.getMethod() == Method.PUT) {
            return  this.userDataController.updateUserData(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[Bad Request]"
        );
    }
}
