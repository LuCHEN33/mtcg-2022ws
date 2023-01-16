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

public class PackageAcquiringController extends Controller {
    private final DBCard dbCard;
    private final DBUser dbUser;

    public PackageAcquiringController(Connection dbConn) {
        this.dbCard = new DBCard(dbConn);
        this.dbUser = new DBUser(dbConn);
    }

    public Response acquirePackage(Request request) {
        if(request.getParams() != null && request.getHeaderMap().getHeader("Authorization") != null) {
            String credentials = request.getHeaderMap().getHeader("Authorization").split("\\s+")[1];
            String[] param = request.getParams().split("=");
            if(param[0].equals("name")) {
                String userName = param[1];
                if(!this.dbUser.checkHeaderCredentials(userName, credentials)) {
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ message: \"Unauthorized Package Acquiring\" }"
                    );
                }

                List<Card> acquiredPackage = this.dbCard.acquirePackageByUser(userName);
                if (acquiredPackage.size() == 5) {
                    return new Response(
                            HttpStatus.ACCEPTED,
                            ContentType.JSON,
                            "{ message: \"" + acquiredPackage + "\" }"
                    );
                }
            }
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "{ \"message\" : \"Package acquiring failed, no money or no package\" }"
        );
    }
}
