package server;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server();
        server.run(0);

        System.out.println("♕ 240 Chess Server");
    }
}