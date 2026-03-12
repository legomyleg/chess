package exception;

public class BadRequestException extends ResponseException {
    private static final String MESSAGE = "Error: bad request";

    public BadRequestException() {
        super(400, MESSAGE);
    }

    public BadRequestException(String ignored) {
        this();
    }
}
