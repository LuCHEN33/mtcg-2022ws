package at.fhtw.httpserver.utils;

import at.fhtw.httpserver.server.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import java.io.BufferedReader;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RequestBuilderTest {

    @Test
    void testBuildRequestFromGet() throws Exception {
        BufferedReader reader = mock(BufferedReader.class);
        when(reader.readLine()).thenReturn("GET /transactions/packages?name=kienboec HTTP/1.1",
                "Content-Type: application/json",
                "Content-Length: 8",
                "Accept: */*",
                "",
                "{'id':1}");

        Request request = new RequestBuilder().buildRequest(reader);
        assertEquals("/transactions/packages", request.getPathname());
        assertEquals("/transactions/packages", request.getServiceRoute());
        assertEquals("packages", request.getPathParts().get(1));
        assertEquals(8, request.getHeaderMap().getContentLength());
    }

    @Test
    void testBuildRequestFromPost() throws Exception {
        BufferedReader reader = mock(BufferedReader.class);
        when(reader.readLine()).thenReturn("GET /deck?name=kienboec HTTP/1.1",
                "Content-Type: application/json",
                "Content-Length: 8",
                "Accept: */*",
                "",
                "{'id':1}");

        Request request = new RequestBuilder().buildRequest(reader);
        assertEquals("/deck", request.getPathname());
        assertEquals("/deck", request.getServiceRoute());
        assertEquals("deck", request.getPathParts().get(0));
        assertEquals(8, request.getHeaderMap().getContentLength());
    }
}