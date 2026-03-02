package dataaccess;

public class UserDoesNotExistException extends DataAccessException {

    public UserDoesNotExistException(String message) {
        super(message);
    }
}
