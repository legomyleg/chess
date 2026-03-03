package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;

class LoginServiceTest {
    AuthDAO authDAO = new MemoryAuthDAO();
    UserDAO userDAO = new MemoryUserDAO();

    @BeforeEach
    void setUp() throws ResponseException {
        String username = "pboi";
        String password = "pboi1";
        String email = "asd@gmail.com";

        var registerRequest = new RegisterRequest(username, password, email);
        String authToken = new RegisterService(authDAO, userDAO).register(registerRequest).authToken();

        new LogoutService(authDAO).logout(authToken);
    }

    @AfterEach
    void tearDown() {
        userDAO.deleteAll();
        authDAO.deleteAll();
    }

    @Test
    void loginSuccess() {
        try {

        }
    }
}