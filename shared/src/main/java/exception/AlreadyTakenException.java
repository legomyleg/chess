package exception;

public class AlreadyTakenException extends ResponseException {
    private static final String MESSAGE = "Error: already taken";

    public AlreadyTakenException() {
        super(403, MESSAGE);
    }

    public AlreadyTakenException(String ignored) {
        this();
    }
}
