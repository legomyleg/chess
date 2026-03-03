package exception;

import dataaccess.DataAccessException;

public class NotAuthenticatedException extends ResponseException {
    public NotAuthenticatedException(Code code, String message) {
        super(code, message);
    }
}
