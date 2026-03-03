package exception;

public class GameNotFoundException extends ResponseException {
    public GameNotFoundException(String message) {
        super(400, message);
    }
}
