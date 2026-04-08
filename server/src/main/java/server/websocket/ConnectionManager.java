package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, List<Session>> connections = new ConcurrentHashMap<>();

    public void addGame(Integer gameID, Session session) {
        connections.;
    }

    public void addSessionToGame(Integer gameID, Session session) {

    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(Session excludeSession, NotificationMessage notification) throws IOException {
        String msg = notification.toString();
        for (Session s : connections.values()) {
            if (s.isOpen() && !s.equals(excludeSession)) {
                s.getRemote().sendString(msg);
            }
        }
    }
}
