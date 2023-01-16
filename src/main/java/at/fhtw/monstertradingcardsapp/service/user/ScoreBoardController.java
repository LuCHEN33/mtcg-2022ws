package at.fhtw.monstertradingcardsapp.service.user;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.monstertradingcardsapp.controller.Controller;
import at.fhtw.monstertradingcardsapp.model.User;
import at.fhtw.monstertradingcardsapp.persistence.DBUser;

import java.sql.Connection;
import java.util.Map;

public class ScoreBoardController extends Controller {
    private final DBUser dbUser;

    public ScoreBoardController(Connection dbConn) { this.dbUser = new DBUser(dbConn); }

    public Response showScoreBoard(Request request) {
        if(request.getParams() != null && request.getHeaderMap().getHeader("Authorization") != null) {
            String credentials = request.getHeaderMap().getHeader("Authorization").split("\\s+")[1];
            String[] param = request.getParams().split("=");
            if(param[0].equals("name")) {
                String userName = param[1];
                if (this.dbUser.checkHeaderCredentials(userName, credentials)) {
                    Map<String, Integer> scoreBoard = this.dbUser.showScoreBoard();

                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ message: \"" + scoreBoard + "\" }"
                    );
                } else {
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ message: \"Unauthorized of getting User Stats\" }"
                    );
                }
            }
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\" : \"Getting Score Board failed\" }"
        );
    }
}
