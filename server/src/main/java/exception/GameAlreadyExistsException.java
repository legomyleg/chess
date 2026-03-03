package exception;

public class GameAlreadyExistsException extends ResponseException {
    public GameAlreadyExistsException(String message) {
        super(403, message);
    }
}
