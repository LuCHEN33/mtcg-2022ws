package at.fhtw.at.fhtw.monstertradingcardsapp.service.card;

import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.utils.RequestBuilder;
import at.fhtw.monstertradingcardsapp.service.card.DeckConfiguringController;
import at.fhtw.monstertradingcardsapp.service.card.PackageAcquiringController;
import at.fhtw.monstertradingcardsapp.service.card.PackageAddingController;
import at.fhtw.monstertradingcardsapp.service.card.ShowCardsController;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class CardTest {
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testShowingUserData() throws Exception {
        BufferedReader reader = mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn("GET http://localhost:10001/deck HTTP/1.1",
                "Content-Type: application/json",
                "Content-Length: 0",
                "Accept: */*",
                "",
                "");
        Request request = new RequestBuilder().buildRequest(reader);
        Connection dbConn = Mockito.mock(Connection.class);
        DeckConfiguringController deckConfiguringController = new DeckConfiguringController(dbConn);
        assertEquals("HTTP/1.1 400 Bad Request", deckConfiguringController.showDeckFromUser(request).get().split("\r\n")[0]);
        assertEquals("{ \"message\" : \"Getting User Deck failed\" }", deckConfiguringController.showDeckFromUser(request).get().split("\r\n")[8]);
    }

    @Test
    void testAcquiringPackage() throws Exception {
        BufferedReader reader = mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn("GET http://localhost:10001/transactions/packages HTTP/1.1",
                "Content-Type: application/json",
                "Content-Length: 0",
                "Accept: */*",
                "",
                "");
        Request request = new RequestBuilder().buildRequest(reader);
        Connection dbConn = Mockito.mock(Connection.class);
        PackageAcquiringController packageAcquiringController = new PackageAcquiringController(dbConn);
        assertEquals("HTTP/1.1 400 Bad Request", packageAcquiringController.acquirePackage(request).get().split("\r\n")[0]);
        assertEquals("{ \"message\" : \"Package acquiring failed, no money or no package\" }", packageAcquiringController.acquirePackage(request).get().split("\r\n")[8]);
    }

    @Test
    void testAddingPackage() throws Exception {
        BufferedReader reader = mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn("GET http://localhost:10001/packages HTTP/1.1",
                "Content-Type: application/json",
                "Content-Length: 0",
                "Accept: */*",
                "Authorization: Basic 23c496d2ee2494b3f380a2bd7380b811",
                "");
        Request request = new RequestBuilder().buildRequest(reader);
        Connection dbConn = Mockito.mock(Connection.class);
        PackageAddingController packageAddingController = new PackageAddingController(dbConn);
        assertThrows(NullPointerException.class, () -> packageAddingController.addPackage(request).get());
    }

    @Test
    void testShowCards() throws Exception {
        BufferedReader reader = mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn("GET http://localhost:10001/transactions/cards HTTP/1.1",
                "Content-Type: application/json",
                "Content-Length: 0",
                "Accept: */*",
                "",
                "");
        Request request = new RequestBuilder().buildRequest(reader);
        Connection dbConn = Mockito.mock(Connection.class);
        ShowCardsController showCardsController = new ShowCardsController(dbConn);
        assertEquals("HTTP/1.1 400 Bad Request", showCardsController.getAllCardsFromUser(request).get().split("\r\n")[0]);
        assertEquals("{ \"message\" : \"Getting Card failed\" }", showCardsController.getAllCardsFromUser(request).get().split("\r\n")[8]);
    }

    @Test
    void testShowDeckFromUser() throws Exception {
        BufferedReader reader = mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn("GET http://localhost:10001/deck HTTP/1.1",
                "Content-Type: application/json",
                "Content-Length: 0",
                "Accept: */*",
                "Authorization: Basic 23c496d2ee2494b3f380a2bd7380b811",
                "");
        Request request = new RequestBuilder().buildRequest(reader);
        Connection dbConn = Mockito.mock(Connection.class);
        DeckConfiguringController deckConfiguringController = new DeckConfiguringController(dbConn);
        assertEquals("HTTP/1.1 400 Bad Request", deckConfiguringController.showDeckFromUser(request).get().split("\r\n")[0]);
        assertEquals("{ \"message\" : \"Getting User Deck failed\" }", deckConfiguringController.showDeckFromUser(request).get().split("\r\n")[8]);
    }

    @Test
    void testConfigureDeckFromUser() throws Exception {
        BufferedReader reader = mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn("GET http://localhost:10001/deck HTTP/1.1",
                "Content-Type: application/json",
                "Content-Length: 0",
                "Accept: */*",
                "Authorization: Basic 23c496d2ee2494b3f380a2bd7380b811",
                "");
        Request request = new RequestBuilder().buildRequest(reader);
        Connection dbConn = Mockito.mock(Connection.class);
        DeckConfiguringController deckConfiguringController = new DeckConfiguringController(dbConn);
        assertEquals("HTTP/1.1 400 Bad Request", deckConfiguringController.configureDeckFromUser(request).get().split("\r\n")[0]);
        assertEquals("{ \"message\" : \"Deck configuring failed\" }", deckConfiguringController.configureDeckFromUser(request).get().split("\r\n")[8]);
    }

}
