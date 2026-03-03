package exception;

import com.google.gson.Gson;

import java.util.Map;

public class ResponseException extends Exception {

    public enum Code {
        ServerError,
        ClientError
    }

    final private Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public int getHttpStatusCode() {
        return switch (code) {
            case ServerError -> 500;
            case ClientError -> 400;
        };
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }
}
