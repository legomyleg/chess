package dataaccess;

import exception.UserDoesNotExistException;
import model.UserData;

import java.util.HashMap;
import java.util.Map;


public class MemoryUserDAO implements UserDAO {
    Map<String, UserData> users;

    public MemoryUserDAO() {
        users = new HashMap<>();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.get(user.username()) != null) {
            throw new DataAccessException("Username already taken.");
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (users.get(username) == null) {
            throw new DataAccessException("User does not exist.");
        }
        return users.get(username);
    }

    // TODO: Implement memory password verification.
    @Override
    public boolean verifyPassword(String username, String password) {
        return false;
    }


    @Override
    public void deleteAll() {
        users.clear();
    }

}