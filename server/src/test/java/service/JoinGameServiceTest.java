package service;

import chess.ChessGame;
import dataaccess.*;
import exception.AlreadyTakenException;
import exception.ResponseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.RegisterRequest;

import static org.junit.jupiter.api.Assertions.*;

class JoinGameServiceTest {
    AuthDAO authDAO = new MemoryAuthDAO();
    UserDAO userDAO = new MemoryUserDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    String authToken = null;
    Integer gameID = null;
    JoinGameService joinGameService;

    @BeforeEach
    void setUp() throws ResponseException {
        String username = "pboi";
        String password = "pboi1";
        String email = "asd@gmail.com";

        var registerRequest = new RegisterRequest(username, password, email);
        var registerResult = new RegisterService(authDAO, userDAO).register(registerRequest);
        authToken = registerResult.authToken();

        var createGameResult = new CreateGameService(authDAO, gameDAO).createGame(new CreateGameRequest("myGame"), authToken);
        gameID = createGameResult.gameID();

        joinGameService = new JoinGameService(authDAO, gameDAO);
    }

    @AfterEach
    void tearDown() {
        authDAO.deleteAll();
        userDAO.deleteAll();
        gameDAO.deleteAll();
    }

    @Test
    void joinGameSucceeds() {
        try {
            joinGameService.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID), authToken);
        } catch (ResponseException e) {
            throw new AssertionError("joinGame should not throw exception.");
        }
    }

    @Test
    void joinGameFails() throws ResponseException {
        joinGameService.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID), authToken);

        assertThrowsExactly(AlreadyTakenException.class, () ->
                joinGameService.joinGame(new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID), authToken)
        );
    }
}
