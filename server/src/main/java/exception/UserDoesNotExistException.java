package exception;

import dataaccess.DataAccessException;

public class UserDoesNotExistException extends ResponseException {

    public UserDoesNotExistException(Code code, String message) {
        super(code, message);
    }
}
