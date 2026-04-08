package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, List<Session>> connections = new ConcurrentHashMap<>();

    public void addGame(Integer gameID) {
        connections.put(gameID, new ArrayList<>());
    }

    public void addSessionToGame(Integer gameID, Session session) {
        connections.get(gameID).add(session);
    }

    public void removeSessionFromGame(Integer gameID, Session session) {
        connections.get(gameID).remove(session);
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
