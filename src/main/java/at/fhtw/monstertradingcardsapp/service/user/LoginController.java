package at.fhtw.monstertradingcardsapp.service.user;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.monstertradingcardsapp.controller.Controller;
import at.fhtw.monstertradingcardsapp.model.User;
import at.fhtw.monstertradingcardsapp.persistence.DBUser;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class LoginController extends Controller {
    private final DBUser dbUser;

    public LoginController(Connection dbConn) {
        this.dbUser = new DBUser(dbConn);
    }

    public Response authenticateCredentials(Request request) {
        try {

            User userData = this.getObjectMapper().readValue(request.getBody(), User.class);
            User foundUser = this.dbUser.getUserByName(userData.getUserName());

            if( userData.getUserName().equals(foundUser.getUserName()) && userData.getPassword().equals(foundUser.getPassword())) {
                return new Response(
                        HttpStatus.ACCEPTED,
                        ContentType.JSON,
                        "{ message: \"Login Success\" }"
                );
            }

            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ message: \"Login Failed\" }"
            );

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\" : \"Login failed\" }"
        );
    }
}
