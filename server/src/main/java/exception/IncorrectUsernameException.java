package exception;

public class IncorrectUsernameException extends ResponseException {
    public IncorrectUsernameException(String message) {
        super(401, message);
    }
}
