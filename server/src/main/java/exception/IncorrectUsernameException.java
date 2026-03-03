package exception;

public class IncorrectUsernameException extends ResponseException {
    public IncorrectUsernameException(Code code, String message) {
        super(code, message);
    }
}
