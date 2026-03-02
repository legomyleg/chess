package dataaccess;

public class AlreadyAuthenticatedException extends DataAccessException {
    public AlreadyAuthenticatedException(String message) {
        super(message);
    }
}
