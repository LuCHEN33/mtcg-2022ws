package at.fhtw.httpserver.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {

    @Test
    void getPathname() {
        Request request1 = new Request();
        request1.setPathname("/users");
        Request request2 = new Request();
        request2.setPathname("/sessions");
        Request request3 = new Request();
        request3.setPathname("/packages");

        assertEquals("/users", request1.getPathname());
        assertEquals("/sessions", request2.getPathname());
        assertEquals("/packages", request3.getPathname());

    }

    @Test
    void testGetParamsWithId() {
        Request request = new Request();
        request.setPathname("/packages");
        request.setParams("name=kienboec");

        assertEquals("/packages", request.getPathname());
        assertEquals("name=kienboec", request.getParams());
    }

    @Test
    void testGetParamsWithId2() {
        Request request = new Request();
        request.setPathname("/packages");
        request.setParams("name=altenhof");

        assertEquals("/packages", request.getPathname());
        assertEquals("name=altenhof", request.getParams());
    }

    @Test
    void testGetServiceRouteWithSlash() {
        Request request = new Request();
        request.setPathname("/");

        assertNull(request.getServiceRoute());
    }

    @Test
    void testGetServiceRouteWithRoute() {
        Request request = new Request();
        request.setPathname("/score");

        assertEquals("/score", request.getServiceRoute());
    }

    @Test
    void testGetServiceRouteWithSubRoute() {
        Request request = new Request();
        request.setPathname("/transactions/packages");

        assertEquals("/transactions/packages", request.getServiceRoute());
        assertEquals("transactions", request.getPathParts().get(0));
        assertEquals("packages", request.getPathParts().get(1));
    }
}