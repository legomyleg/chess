package service;

import dataaccess.*;
import exception.NotAuthenticatedException;
import exception.ResponseException;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;

class LogoutServiceTest {

    AuthDAO authDAO = new MemoryAuthDAO();
    UserDAO userDAO = new MemoryUserDAO();
    String authToken = null;
    LogoutService logoutService;

    @BeforeEach
    void setUp() throws ResponseException {
        String username = "pboi";
        String password = "pboi1";
        String email = "asd@gmail.com";

        var registerRequest = new RegisterRequest(username, password, email);
        var registerResult = new RegisterService(authDAO, userDAO).register(registerRequest);
        authToken = registerResult.authToken();

        logoutService = new LogoutService(authDAO);
    }

    @AfterEach
    void tearDown() {
        authDAO.deleteAll();
        userDAO.deleteAll();
    }

    @Test
    void logoutSuccess() throws ResponseException {

        try {
            new LogoutService(authDAO).logout(authToken);
        } catch (ResponseException e) {
            throw new AssertionError("Logout should not throw exception.");
        }
    }

    @Test
    void logoutFailure() throws ResponseException {

        assertThrowsExactly(NotAuthenticatedException.class, () -> logoutService.logout("a"));

    }
}