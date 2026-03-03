package service;

import dataaccess.*;
import exception.NotAuthenticatedException;
import exception.ResponseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.RegisterRequest;
import result.CreateGameResult;

import static org.junit.jupiter.api.Assertions.*;

class CreateGameServiceTest {
    AuthDAO authDAO = new MemoryAuthDAO();
    UserDAO userDAO = new MemoryUserDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    String authToken = null;
    CreateGameService createGameService;

    @BeforeEach
    void setUp() throws ResponseException {
        String username = "pboi";
        String password = "pboi1";
        String email = "asd@gmail.com";

        var registerRequest = new RegisterRequest(username, password, email);
        var registerResult = new RegisterService(authDAO, userDAO).register(registerRequest);
        authToken = registerResult.authToken();

        createGameService = new CreateGameService(authDAO, gameDAO);
    }

    @AfterEach
    void tearDown() {
        authDAO.deleteAll();
        userDAO.deleteAll();
        gameDAO.deleteAll();
    }

    @Test
    void createGameSucceeds() {
        try {
            CreateGameResult result = createGameService.createGame(new CreateGameRequest("myGame"), authToken);
            assertNotNull(result.gameID());
        } catch (ResponseException e) {
            throw new AssertionError("createGame should not throw exception.");
        }
    }

    @Test
    void createGameFails() {
        assertThrowsExactly(NotAuthenticatedException.class, () ->
                createGameService.createGame(new CreateGameRequest("myGame"), "badToken")
        );
    }
}
