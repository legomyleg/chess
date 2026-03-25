package client;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;

    public Client(String url) {
        server = new ServerFacade(url);
    }

    public void run() {
        System.out.println(WHITE_KING + " Welcome to CS240 Chess Online!");


    }


}
