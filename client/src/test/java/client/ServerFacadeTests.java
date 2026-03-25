package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import server.Server;

import java.util.UUID;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade clientServer;
    String currentTestUsername;
    String currentTestPassword;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        clientServer = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void setUp() {
        currentTestUsername = UUID.randomUUID().toString();
        currentTestPassword = UUID.randomUUID().toString();
    }

    @Test
    public void registerWorks() throws ResponseException {
        var response = clientServer.register(currentTestUsername, currentTestPassword, "p@gmail.com");
        assertNotNull(response.username(), "Returned empty username.");
        assertEquals(currentTestUsername, response.username(), "Returned username incorrect");

        assertNotNull(response.authToken(), "Returned empty authToken.");
    }

    @Test
    public void registerReturnsErrorUsernameTaken() {
        assertDoesNotThrow(() -> clientServer.register(currentTestUsername, currentTestPassword, "test@gmail.com"),
                "Register should not throw an error.");
        assertThrows(ResponseException.class, () -> clientServer.register(currentTestUsername, currentTestPassword, "test@gmail.com"),
                "Register should throw an error if username is already taken,");
    }

    @Test
    public void loginWorks() {
        assertDoesNotThrow(() -> clientServer.register(currentTestUsername, currentTestPassword, "test@gmail.com"),
                "Register should not throw an error.");

        assertDoesNotThrow(() -> {
            var response = clientServer.login(currentTestUsername, currentTestPassword);
            assertNotNull(response.username(), "Returned empty username.");
            assertEquals(currentTestUsername, response.username(), "Returned username incorrect");

            assertNotNull(response.authToken(), "Returned empty authToken.");
        }, "Login should not throw an error.");
    }

    @Test
    public void loginReturnsErrorBadCredentials() {
        assertDoesNotThrow(() -> clientServer.register(currentTestUsername, currentTestPassword, "test@gmail.com"),
                "Register should not throw an error.");
        assertThrows(ResponseException.class, () -> clientServer.login(currentTestUsername, UUID.randomUUID().toString()),
                "Login should throw an error if credentials are incorrect.");
    }

    @Test
    public void logoutWorks() {
        assertDoesNotThrow(() -> clientServer.register(currentTestUsername, currentTestPassword, "test@gmail.com"),
                "Register should not throw an error.");

        assertDoesNotThrow(() -> {
            var response = clientServer.login(currentTestUsername, currentTestPassword);
            clientServer.logout(response.authToken());
        }, "Logout should not throw an error.");
    }

    @Test
    public void logoutReturnsErrorBadAuthToken() {
        assertThrows(ResponseException.class, () -> clientServer.logout(UUID.randomUUID().toString()),
                "Logout should throw an error if authToken is invalid.");
    }

}
