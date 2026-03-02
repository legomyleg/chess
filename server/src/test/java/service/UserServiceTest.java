package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void registerSucceeds() {
        var memoryAuthDAO = new MemoryAuthDAO();
        var memoryUserDAO = new MemoryUserDAO();

        var service = new UserService(memoryAuthDAO, memoryUserDAO);

        String username = "pboi";
        String password = "1234";
        String email = "pboi@email.com";
        var registerRequest = new RegisterRequest(username, password, email);

        RegisterResult result = null;

        try {
            result = service.register(registerRequest);
        } catch (DataAccessException e) {
            throw new AssertionError("Register should not throw error.");
        }

        assertEquals(username, result.username());
        assertNotNull(result.authToken());
        assertNotEquals("", result.authToken());
    }

    @Test
    void registerFails() {
        var memoryAuthDAO = new MemoryAuthDAO();
        var memoryUserDAO = new MemoryUserDAO();

        var service = new UserService(memoryAuthDAO, memoryUserDAO);

        String username = "pboi";
        String password = "1234";
        String email = "pboi@email.com";
        var request = new RegisterRequest(username, password, email);

        try {
            service.register(request);
        } catch(Exception e) {
            throw new AssertionError("Register did not work for valid user.");
        }

        assertThrowsExactly(AlreadyTakenException.class, () -> service.register(request));
    }
}