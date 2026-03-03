package service;

import dataaccess.*;
import exception.ResponseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.RegisterRequest;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
    AuthDAO authDAO = new MemoryAuthDAO();
    UserDAO userDAO = new MemoryUserDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    ClearService clearService;

    @BeforeEach
    void setUp() throws ResponseException {
        String username = "pboi";
        String password = "pboi1";
        String email = "asd@gmail.com";

        var registerRequest = new RegisterRequest(username, password, email);
        var registerResult = new RegisterService(authDAO, userDAO).register(registerRequest);
        String authToken = registerResult.authToken();

        new CreateGameService(authDAO, gameDAO).createGame(new CreateGameRequest("myGame"), authToken);

        clearService = new ClearService(gameDAO, authDAO, userDAO);
    }

    @AfterEach
    void tearDown() {
        authDAO.deleteAll();
        userDAO.deleteAll();
        gameDAO.deleteAll();
    }

    @Test
    void clearSucceeds() {
        try {
            clearService.deleteAllData();
        } catch (Exception e) {
            throw new AssertionError("Clear should not throw exception.");
        }
    }

    @Test
    void clearDeletesAllData() {
        clearService.deleteAllData();

        assertTrue(gameDAO.listAllGames().isEmpty());
    }
}
