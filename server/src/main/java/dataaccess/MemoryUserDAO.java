package dataaccess;

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
        return users.get(username);
    }

    @Override
    public boolean verifyPassword(String username, String password) throws DataAccessException {
        var user = getUser(username);
        return user != null && user.password().equals(password);
    }


    @Override
    public void deleteAll() {
        users.clear();
    }

}
