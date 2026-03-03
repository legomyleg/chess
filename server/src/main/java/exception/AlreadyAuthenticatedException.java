package exception;

import dataaccess.DataAccessException;

public class AlreadyAuthenticatedException extends ResponseException {
    public AlreadyAuthenticatedException(Code code, String message) {
        super(code, message);
    }
}
