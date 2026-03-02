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
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        if (users.get(username) == null) {
            throw new UserDoesNotExistException("User does not exist.");
        }
        users.remove(username);
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }
}
