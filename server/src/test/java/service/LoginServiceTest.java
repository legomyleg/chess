package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import exception.NotAuthenticatedException;
import exception.ResponseException;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.RegisterRequest;

class LoginServiceTest {
    AuthDAO authDAO = new MemoryAuthDAO();
    UserDAO userDAO = new MemoryUserDAO();
    String username = "pboi";
    String password = "pboi1";
    String email = "asd@gmail.com";
    LoginService loginService = null;

    @BeforeEach
    void setUp() throws ResponseException {
        var registerRequest = new RegisterRequest(username, password, email);
        String authToken = new RegisterService(authDAO, userDAO).register(registerRequest).authToken();

        new LogoutService(authDAO).logout(authToken);

        loginService = new LoginService(userDAO, authDAO);
    }

    @AfterEach
    void tearDown() {
        userDAO.deleteAll();
        authDAO.deleteAll();
    }

    @Test
    void loginSuccess() {
        try {
            var request = new LoginRequest(username, password);
            loginService.login(request);
        } catch (ResponseException e) {
            throw new AssertionError("Login should not throw error.");
        }
    }

    @Test
    void loginFails() {
        String wrongPassword = "wrong";
        var badRequest = new LoginRequest(username, wrongPassword);

        assertThrowsExactly(NotAuthenticatedException.class, () -> loginService.login(badRequest));

    }
}