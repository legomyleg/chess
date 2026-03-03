package exception;

public class UserDoesNotExistException extends ResponseException {
    public UserDoesNotExistException(String message) {
        super(401, message);
    }
}
