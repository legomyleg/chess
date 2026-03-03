package exception;

import dataaccess.DataAccessException;

public class AlreadyTakenException extends ResponseException {
    public AlreadyTakenException(Code code, String message) {
        super(code, message);
    }
}
