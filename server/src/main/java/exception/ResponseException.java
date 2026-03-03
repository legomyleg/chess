package exception;

import com.google.gson.Gson;

import java.util.Map;

public class ResponseException extends Exception {
    private final int statusCode;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getHttpStatusCode() { return statusCode; }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage()));
    }
}
