package at.fhtw.monstertradingcardsapp.service.card;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

import java.sql.Connection;

public class PackageAddingService implements Service {
    private final PackageAddingController packageAddingController;

    public PackageAddingService(Connection dbConn) { this.packageAddingController = new PackageAddingController(dbConn);}

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST) {
            return this.packageAddingController.addPackage(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[Bad Request]"
        );
    }
}
