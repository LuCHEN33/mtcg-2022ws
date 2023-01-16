package at.fhtw.monstertradingcardsapp.service.user;

import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.utils.RequestBuilder;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.io.BufferedReader;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class UserTest {
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testShowingUserData() throws Exception {
        BufferedReader reader = mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn("GET http://localhost:10001/stats HTTP/1.1",
                "Content-Type: application/json",
                "Content-Length: 0",
                "Accept: */*",
                "",
                "");
        Request request = new RequestBuilder().buildRequest(reader);
        Connection dbConn = Mockito.mock(Connection.class);
        UserDataController userDataController = new UserDataController(dbConn);
        assertEquals("HTTP/1.1 400 Bad Request", userDataController.showUserData(request).get().split("\r\n")[0]);
        assertEquals("{ \"message\" : \"showing User Data failed\" }", userDataController.showUserData(request).get().split("\r\n")[8]);
    }

    @Test
    void testShowingUserStats() throws Exception {
        BufferedReader reader = mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn("GET http://localhost:10001/stats HTTP/1.1",
                "Content-Type: application/json",
                "Content-Length: 0",
                "Accept: */*",
                "",
                "");
        Request request = new RequestBuilder().buildRequest(reader);
        Connection dbConn = Mockito.mock(Connection.class);
        UserStatsControler userStatsControler = new UserStatsControler(dbConn);
        assertEquals("HTTP/1.1 400 Bad Request", userStatsControler.showUserStats(request).get().split("\r\n")[0]);
        assertEquals("{ \"message\" : \"Getting User Stats failed\" }", userStatsControler.showUserStats(request).get().split("\r\n")[8]);
    }

    @Test
    void testShowingScoreBoard() throws Exception {
        BufferedReader reader = mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn("GET http://localhost:10001/score?name=kienboec HTTP/1.1",
                "Content-Type: application/json",
                "Content-Length: 0",
                "Accept: */*",
                "Authorization: Basic 23c496d2ee2494b3f380a2bd7380b811",
                "");
        Request request = new RequestBuilder().buildRequest(reader);
        Connection dbConn = Mockito.mock(Connection.class);
        ScoreBoardController scoreBoardController = new ScoreBoardController(dbConn);
        assertThrows(NullPointerException.class, () -> scoreBoardController.showScoreBoard(request).get());
    }

    @Test
    void testUserLoginStats() throws Exception {
        BufferedReader reader = mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn("POST http://localhost:10001/sessions HTTP/1.1",
                "Content-Type: application/json",
                "Content-Length: 8",
                "Accept: */*",
                "",
                "{'id':1}");
        Request request = new RequestBuilder().buildRequest(reader);
        Connection dbConn = Mockito.mock(Connection.class);
        LoginController loginController = new LoginController(dbConn);
        assertEquals("{ \"message\" : \"Login failed\" }", loginController.authenticateCredentials(request).get().split("\r\n")[8]);
    }
}