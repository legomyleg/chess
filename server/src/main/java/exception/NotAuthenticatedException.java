package exception;

public class NotAuthenticatedException extends ResponseException {
    public NotAuthenticatedException(String message) {
        super(401, message);
    }
}
