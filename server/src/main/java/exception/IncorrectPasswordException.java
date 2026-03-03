package exception;

public class IncorrectPasswordException extends ResponseException {
    public IncorrectPasswordException(Code code, String message) {
        super(code, message);
    }
}
