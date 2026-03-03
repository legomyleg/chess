package exception;

public class IncorrectPasswordException extends ResponseException {
    public IncorrectPasswordException(String message) {
        super(401, message);
    }
}
