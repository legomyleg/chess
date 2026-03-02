package dataaccess;

public class NotAuthenticatedException extends DataAccessException {
    public NotAuthenticatedException(String message) {
        super(message);
    }
}
