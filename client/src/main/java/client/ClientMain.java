package client;

import chess.*;

import static ui.EscapeSequences.*;

public class ClientMain {
    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";

        if (args.length == 1) {
            serverUrl = args[0];
        }

        try {
            new Client(serverUrl).run();

        } catch (Throwable ex) {
            System.out.println(SET_TEXT_COLOR_RED + "Unable to start server: %s%n".formatted(ex.getMessage()));
        }
    }
}
