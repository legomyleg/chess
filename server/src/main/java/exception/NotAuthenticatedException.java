package exception;

public class NotAuthenticatedException extends ResponseException {
    private static final String MESSAGE = "Error: unauthorized";

    public NotAuthenticatedException() {
        super(401, MESSAGE);
    }

    public NotAuthenticatedException(String ignored) {
        this();
    }
}
