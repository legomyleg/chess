package dataaccess;

import com.google.gson.Gson;
import model.UserData;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws DataAccessException {
        ConfigureDatabase.configureDatabase();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO users(username, password, email) VALUES (?, ?, ?)";
        String json = new Gson().toJson(user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAll() {

    }
}
