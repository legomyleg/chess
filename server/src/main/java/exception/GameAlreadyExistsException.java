package exception;

public class GameAlreadyExistsException extends ResponseException {
    public GameAlreadyExistsException(ResponseException.Code code, String message) {
        super(code, message);
    }
}
