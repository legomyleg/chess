package exception;

public class AlreadyAuthenticatedException extends ResponseException {
    public AlreadyAuthenticatedException(String message) {
        super(403, message);
    }
}
