package exception;

public class DatabaseErrorException extends ResponseException {
    private static final String DEFAULT_MESSAGE = "Error: database error";

    public DatabaseErrorException(String description) {
        super(500, formatMessage(description));
    }

    private static String formatMessage(String description) {
        if (description == null || description.isBlank()) {
            return DEFAULT_MESSAGE;
        }
        return description.startsWith("Error:") ? description : "Error: " + description;
    }
}
