package at.fhtw.monstertradingcardsapp.service.user;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.monstertradingcardsapp.controller.Controller;
import at.fhtw.monstertradingcardsapp.model.ToUpdateUserData;
import at.fhtw.monstertradingcardsapp.model.User;
import at.fhtw.monstertradingcardsapp.persistence.DBUser;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.Connection;


public class UserDataController extends Controller {
    private final DBUser dbUser;

    public UserDataController(Connection dbConn) {
        this.dbUser = new DBUser(dbConn);
    }

    public Response addNewUser(Request request) {
        try {
            User userData = this.getObjectMapper().readValue(request.getBody(), User.class);
            userData.setCoins(20);
            userData.setStats(100);
            if(this.dbUser.addUserToDB(userData) == 1){
                return new Response(
                        HttpStatus.CREATED,
                        ContentType.JSON,
                        "{ message: \"Registration Success\" }"
                );
            }

            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ message: \"Registration Failed\" }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\" : \"Registration failed\" }"
        );
    }

    public Response showUserData(Request request) {
        if(request.getParams() != null && request.getHeaderMap().getHeader("Authorization") != null) {
            String credentials = request.getHeaderMap().getHeader("Authorization").split("\\s+")[1];
            String[] param = request.getParams().split("=");
            if(param[0].equals("name")) {
                String userName = param[1];
                if(this.dbUser.checkHeaderCredentials(userName, credentials)) {
                    if ( this.dbUser.getUserByName(userName) == null) {
                        return new Response(
                                HttpStatus.BAD_REQUEST,
                                ContentType.JSON,
                                "{ \"message\" : \" getting User Data failed\" }"
                        );
                    }
                    return new Response(
                            HttpStatus.CREATED,
                            ContentType.JSON,
                            "{ message: \"" + this.dbUser.getUserByName(userName) + "\" }"
                    );
                } else {
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ message: \"Unauthorized of getting User Data\" }"
                    );
                }
            }
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\" : \"showing User Data failed\" }"
        );
    }

    public Response updateUserData(Request request) {
        if(request.getParams() != null && request.getHeaderMap().getHeader("Authorization") != null) {
            String credentials = request.getHeaderMap().getHeader("Authorization").split("\\s+")[1];
            String[] param = request.getParams().split("=");
            if(param[0].equals("name")) {
                String userName = param[1];
                if(this.dbUser.checkHeaderCredentials(userName, credentials)) {
                    User dbUser = this.dbUser.getUserByName(userName);
                    if ( dbUser == null) {
                        return new Response(
                                HttpStatus.BAD_REQUEST,
                                ContentType.JSON,
                                "{ \"message\" : \"Updating failed - User not found\" }"
                        );
                    }

                    int userId = dbUser.getId();
                    ToUpdateUserData toUpdateUserData;
                    try {
                        toUpdateUserData = this.getObjectMapper().readValue(request.getBody(), ToUpdateUserData.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    if(this.dbUser.updateUserByName(userId, toUpdateUserData)) {
                        return new Response(
                                HttpStatus.OK,
                                ContentType.JSON,
                                "{ message: \" User Data updated\" }"
                        );
                    }
                } else {
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ message: \"Unauthorized of updating User Data\" }"
                    );
                }
            }
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\" : \"Updating failed\" }"
        );
    }
}
