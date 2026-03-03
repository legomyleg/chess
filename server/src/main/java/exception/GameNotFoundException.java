package exception;

public class GameNotFoundException extends ResponseException {
    public GameNotFoundException(ResponseException.Code code, String message) {
        super(code, message);
    }
}
