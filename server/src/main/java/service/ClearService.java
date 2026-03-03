package service;

import dataaccess.AuthDOA;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {
    private final GameDAO gameDAO;
    private final AuthDOA authDOA;
    private final UserDAO userDAO;

    public ClearService(GameDAO gameDAO, AuthDOA authDOA, UserDAO userDAO) {
        this.gameDAO = gameDAO;
        this.authDOA = authDOA;
        this.userDAO = userDAO;
    }

    public void deleteAllData() {
        gameDAO.deleteAll();
        authDOA.deleteAll();
        userDAO.deleteAll();
    }
}
