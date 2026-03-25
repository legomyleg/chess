package client;

public class Client {
    private final ServerFacade server;

    public Client(String url) {
        server = new ServerFacade(url);
    }

    public void run() {
        System.out.println("Welcome to chess!");


    }


}
