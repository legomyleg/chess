package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    boolean verifyPassword(String username, String plainTextPassword) throws DataAccessException;

    void deleteAll();
}
